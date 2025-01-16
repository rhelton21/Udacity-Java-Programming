package com.udacity.webcrawler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class that sorts the map of word counts.
 */
final class WordCounts {

  /**
   * Given an unsorted map of word counts, returns a new map whose word counts are sorted according
   * to the provided {@link WordCountComparator}, and includes only the top
   * {@param popluarWordCount} words and counts.
   *
   * <p>TODO: Reimplement this method using only the Stream API and lambdas and/or method
   *          references.
   *
   * @param wordCounts       the unsorted map of word counts.
   * @param popularWordCount the number of popular words to include in the result map.
   * @return a map containing the top {@param popularWordCount} words and counts in the right order.
   */
  static Map<String, Integer> sort(Map<String, Integer> wordCounts, int popularWordCount) {
    // Convert the entry set to a stream, sort using the custom comparator, limit the result, and collect to a LinkedHashMap
    return wordCounts.entrySet().stream()
        .sorted(new WordCountComparator()) // Sort entries based on word frequency, length, and alphabetical order
        .limit(popularWordCount)            // Limit to the top popularWordCount entries
        .collect(Collectors.toMap(
            Map.Entry::getKey,              // Key mapper: word
            Map.Entry::getValue,            // Value mapper: count
            (e1, e2) -> e1,                 // Merge function (won't be needed since we limit results)
            LinkedHashMap::new              // Collect into LinkedHashMap to preserve order
        ));
  }

  /**
   * A {@link Comparator} that sorts word count pairs correctly:
   *
   * <p>
   * <ol>
   *   <li>First sorting by word count, ranking more frequent words higher.</li>
   *   <li>Then sorting by word length, ranking longer words higher.</li>
   *   <li>Finally, breaking ties using alphabetical order.</li>
   * </ol>
   */
  private static final class WordCountComparator implements Comparator<Map.Entry<String, Integer>> {
    @Override
    public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
      if (!a.getValue().equals(b.getValue())) {
        return b.getValue() - a.getValue();  // Sort by count descending
      }
      if (a.getKey().length() != b.getKey().length()) {
        return b.getKey().length() - a.getKey().length();  // Sort by length descending
      }
      return a.getKey().compareTo(b.getKey());  // Sort alphabetically ascending
    }
  }

  private WordCounts() {
    // Prevent instantiation
  }
}
