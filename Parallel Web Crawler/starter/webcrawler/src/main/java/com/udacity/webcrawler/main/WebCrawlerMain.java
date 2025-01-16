package com.udacity.webcrawler.main;

import com.google.inject.Guice;
import com.udacity.webcrawler.WebCrawler;
import com.udacity.webcrawler.WebCrawlerModule;
import com.udacity.webcrawler.json.ConfigurationLoader;
import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.json.CrawlResultWriter;
import com.udacity.webcrawler.json.CrawlerConfiguration;
import com.udacity.webcrawler.profiler.Profiler;
import com.udacity.webcrawler.profiler.ProfilerModule;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * The main entry-point for the web crawler application.
 */
public final class WebCrawlerMain {

  private final CrawlerConfiguration config;

  /**
   * Constructs a {@link WebCrawlerMain} object with the given configuration.
   *
   * @param config the {@link CrawlerConfiguration} object.
   */
  private WebCrawlerMain(CrawlerConfiguration config) {
    this.config = Objects.requireNonNull(config);
  }

  @Inject
  private WebCrawler crawler;

  @Inject
  private Profiler profiler;

  /**
   * Runs the web crawler with the specified configuration.
   *
   * @throws Exception if an error occurs during execution.
   */
  private void run() throws Exception {
    // Use Guice for dependency injection.
    Guice.createInjector(new WebCrawlerModule(config), new ProfilerModule()).injectMembers(this);

    // Perform the web crawl.
    CrawlResult result = crawler.crawl(config.getStartPages());
    CrawlResultWriter resultWriter = new CrawlResultWriter(result);

    // Write the crawl results to a JSON file (or System.out if the file name is empty).
    if (!config.getResultPath().isEmpty()) {
      // If a file path is specified, write the crawl results to that file.
      Path resultPath = Path.of(config.getResultPath());
      resultWriter.write(resultPath);
      System.out.println("[DEBUG] Crawl results written to file: " + resultPath);
    } else {
      // If the path is empty, write the crawl results to standard output.
      System.out.println("[DEBUG] Writing crawl results to standard output.");
      resultWriter.write(new OutputStreamWriter(System.out));
    }

    // Write the profile data to a text file (or System.out if the file name is empty).
    if (!config.getProfileOutputPath().isEmpty()) {
      // If a file path is specified, write the profiling data to that file.
      Path profilePath = Path.of(config.getProfileOutputPath());
      try (BufferedWriter fileWriter = Files.newBufferedWriter(profilePath)) {
        System.out.println("[DEBUG] Writing profile data to file: " + profilePath);
        profiler.writeData(fileWriter);
      }
    } else {
      // If the path is empty, write the profiling data to standard output.
      System.out.println("[DEBUG] Writing profile data to standard output.");
      profiler.writeData(new OutputStreamWriter(System.out));
      System.out.flush();  // Flush the stream to ensure all data is printed
    }
  }

  /**
   * The main entry-point for the application.
   *
   * @param args command-line arguments.
   * @throws Exception if an error occurs during execution.
   */
  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("Usage: WebCrawlerMain [config-file-path]");
      return;
    }

    // Load the configuration file.
    CrawlerConfiguration config = new ConfigurationLoader(Path.of(args[0])).load();
    System.out.println("[DEBUG] Loaded configuration from: " + args[0]);
    // Create an instance of WebCrawlerMain and run the crawler.
    new WebCrawlerMain(config).run();
  }
}
