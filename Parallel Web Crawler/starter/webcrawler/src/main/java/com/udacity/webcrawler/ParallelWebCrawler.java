package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * A concrete implementation of {@link WebCrawler} that runs multiple threads on a
 * {@link ForkJoinPool} to fetch and process multiple web pages in parallel.
 */
final class ParallelWebCrawler implements WebCrawler {
  private final Clock clock;  // Clock for managing timeout
  private final Duration timeout;  // Maximum allowed crawl duration
  private final int popularWordCount;  // Number of popular words to return in results
  private final ForkJoinPool pool;  // ForkJoinPool for parallel crawling
  private final List<Pattern> ignoredUrls;  // Regex patterns for URLs to ignore
  private final int maxDepth;  // Maximum depth for recursive crawling
  private final PageParserFactory parserFactory;  // Factory for page parsers

  /**
   * Constructs a {@link ParallelWebCrawler} with injected dependencies.
   */
  @Inject
  ParallelWebCrawler(
      Clock clock,
      @Timeout Duration timeout,
      @PopularWordCount int popularWordCount,
      @TargetParallelism int threadCount,
      @IgnoredUrls List<Pattern> ignoredUrls,
      @MaxDepth int maxDepth,
      PageParserFactory parserFactory) {
    this.clock = clock;
    this.timeout = timeout;
    this.popularWordCount = popularWordCount;
    this.pool = new ForkJoinPool(Math.min(threadCount, getMaxParallelism()));  // Thread pool size is capped at max parallelism
    this.ignoredUrls = ignoredUrls;
    this.maxDepth = maxDepth;
    this.parserFactory = parserFactory;
  }

  /**
   * Initiates the web crawling process from the given starting URLs.
   *
   * @param startingUrls the list of starting URLs for the crawl
   * @return a {@link CrawlResult} containing the results of the crawl
   */
  @Override
  public CrawlResult crawl(List<String> startingUrls) {
    Instant deadline = clock.instant().plus(timeout);
    ConcurrentMap<String, Integer> wordCounts = new ConcurrentHashMap<>();
    ConcurrentSkipListSet<String> visitedUrls = new ConcurrentSkipListSet<>();

    System.out.println("[INFO] Starting crawl with " + startingUrls.size() + " URLs.");

    List<ForkJoinTask<Void>> tasks = new ArrayList<>();
    for (String url : startingUrls) {
      if (url != null && !url.trim().isEmpty()) {  // Check for null or blank URLs
        tasks.add(pool.submit(createCrawlTask(url, maxDepth, deadline, wordCounts, visitedUrls)));
      } else {
        System.out.println("[WARNING] Ignoring null or blank URL.");
      }
    }

    for (ForkJoinTask<Void> task : tasks) {
      try {
        task.join();  // Ensure all tasks finish
      } catch (Exception e) {
        System.err.println("[ERROR] Exception while joining task: " + e.getMessage());
      }
    }

    System.out.println("[INFO] Crawl completed. Visited " + visitedUrls.size() + " URLs.");

    // Handle empty word counts to avoid IllegalArgumentException
    Map<String, Integer> sortedWordCounts = wordCounts.isEmpty() ? Collections.emptyMap() : WordCounts.sort(wordCounts, popularWordCount);

    return new CrawlResult.Builder()
      .setWordCounts(sortedWordCounts)
      .setUrlsVisited(visitedUrls.size())
      .build();
  }

  /**
   * Creates a {@link RecursiveTask} to crawl the given URL.
   *
   * @param url         the URL to crawl
   * @param depth       the remaining crawl depth
   * @param deadline    the time limit for the crawl
   * @param wordCounts  the map of word counts to update
   * @param visitedUrls the set of visited URLs
   * @return a {@link RecursiveTask} representing the crawl task for the URL
   */
  private RecursiveTask<Void> createCrawlTask(
      String url,
      int depth,
      Instant deadline,
      ConcurrentMap<String, Integer> wordCounts,
      ConcurrentSkipListSet<String> visitedUrls) {

    return new RecursiveTask<>() {
      @Override
      protected Void compute() {
        // Terminate if max depth reached or the deadline has passed
        if (depth == 0 || clock.instant().isAfter(deadline)) {
          System.out.println("[DEBUG] Skipping URL due to depth 0 or timeout: " + url);
          return null;
        }

        // Ignore the URL if it matches any ignored pattern
        for (Pattern pattern : ignoredUrls) {
          if (pattern.matcher(url).matches()) {
            System.out.println("[DEBUG] Ignoring URL due to matching pattern: " + url);
            return null;
          }
        }

        // If the URL was already visited, skip it
        if (!visitedUrls.add(url)) {
          System.out.println("[DEBUG] URL already visited: " + url);
          return null;
        }

        System.out.println("[INFO] Crawling URL: " + url + " at depth " + depth);

        // Parse the page and get the result (word counts and links).
        PageParser.Result result = parserFactory.get(url).parse();

        // Merge word counts into the shared map.
        result.getWordCounts().forEach((word, count) -> {
          String normalizedWord = word.trim().toLowerCase().replaceAll("[^a-z]", "");  // Normalize and remove non-alphabetic characters
          if (!normalizedWord.isEmpty()) {  // Skip blank words after normalization
            wordCounts.merge(normalizedWord, count, Integer::sum);  // Merge word counts atomically
          } else {
            System.out.println("[DEBUG] Skipping empty or invalid word during merge.");
          }
        });

        // Create crawl tasks for each link found on the page
        List<RecursiveTask<Void>> subtasks = new ArrayList<>();
        for (String link : result.getLinks()) {
          subtasks.add(createCrawlTask(link, depth - 1, deadline, wordCounts, visitedUrls));
        }

        // Execute all subtasks in parallel
        invokeAll(subtasks);

        System.out.println("[INFO] Finished crawling URL: " + url);
        return null;
      }
    };
  }

  /**
   * Returns the maximum parallelism supported by the system.
   *
   * @return the number of available processor cores
   */
  @Override
  public int getMaxParallelism() {
    return Runtime.getRuntime().availableProcessors();  // Return the number of available processors
  }
}
