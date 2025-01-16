package com.udacity.webcrawler.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A static utility class that loads a JSON configuration file.
 */
public final class ConfigurationLoader {

  private final Path path;

  /**
   * Create a {@link ConfigurationLoader} that loads configuration from the given {@link Path}.
   */
  public ConfigurationLoader(Path path) {
    this.path = Objects.requireNonNull(path);
  }

  /**
   * Loads configuration from this {@link ConfigurationLoader}'s path.
   *
   * @return the loaded {@link CrawlerConfiguration}.
   * @throws RuntimeException if an I/O error occurs while reading the file.
   */
  public CrawlerConfiguration load() {
    // Open a reader for the JSON file at the given path.
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      // Use the read method to deserialize the JSON configuration from the reader.
      return read(reader);
    } catch (IOException e) {
      throw new RuntimeException("Error reading configuration file: " + path, e);
    }
  }

  /**
   * Loads crawler configuration from the given reader.
   *
   * @param reader a Reader pointing to a JSON string that contains crawler configuration.
   * @return a crawler configuration
   * @throws RuntimeException if an I/O error occurs during deserialization.
   */
  public static CrawlerConfiguration read(Reader reader) {
    // Ensure the reader is not null to avoid null pointer exceptions.
    Objects.requireNonNull(reader);
    
    // Create Jackson ObjectMapper for JSON deserialization.
    ObjectMapper objectMapper = new ObjectMapper();
    // Prevent the ObjectMapper from closing the reader automatically.
    objectMapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

    try {
      // Deserialize the JSON string from the reader into a CrawlerConfiguration.Builder object.
      CrawlerConfiguration.Builder builder = objectMapper.readValue(reader, CrawlerConfiguration.Builder.class);
      // Build the final CrawlerConfiguration object from the builder.
      return builder.build();
    } catch (IOException e) {
      throw new RuntimeException("Error reading configuration from reader", e);
    }
  }
}
