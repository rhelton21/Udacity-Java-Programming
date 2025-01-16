package com.udacity.webcrawler.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utility class to write a {@link CrawlResult} to file.
 */
public final class CrawlResultWriter {
  private final CrawlResult result;

  /**
   * Creates a new {@link CrawlResultWriter} that will write the given {@link CrawlResult}.
   */
  public CrawlResultWriter(CrawlResult result) {
    this.result = Objects.requireNonNull(result);
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Path}.
   *
   * <p>If a file already exists at the path, the existing file should not be deleted; new data
   * should be appended to it.
   *
   * @param path the file path where the crawl result data should be written.
   * @throws RuntimeException if an I/O error occurs while writing.
   */
  public void write(Path path) {
    Objects.requireNonNull(path);

    // Open a BufferedWriter and call the write method.
    try (Writer writer = Files.newBufferedWriter(path)) {
      write(writer);
    } catch (IOException e) {
      throw new RuntimeException("Error writing crawl result to file: " + path, e);
    }
  }

  /**
   * Formats the {@link CrawlResult} as JSON and writes it to the given {@link Writer}.
   *
   * @param writer the destination where the crawl result data should be written.
   * @throws RuntimeException if an I/O error occurs during serialization.
   */
  public void write(Writer writer) {
    Objects.requireNonNull(writer);

    ObjectMapper objectMapper = new ObjectMapper(); // Create an ObjectMapper instance.
    objectMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET); // Prevents the writer from closing.

    try {
      objectMapper.writeValue(writer, result); // Serialize the CrawlResult object to the writer.
    } catch (IOException e) {
      throw new RuntimeException("Error writing crawl result to writer", e);
    }
  }
}
