package com.udacity.webcrawler.profiler;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import java.lang.reflect.Proxy;

/**
 * Concrete implementation of the {@link Profiler}.
 */
final class ProfilerImpl implements Profiler {

  private final Clock clock;
  private final ProfilingState state = new ProfilingState();
  private final ZonedDateTime startTime;

  @Inject
  ProfilerImpl(Clock clock) {
    this.clock = Objects.requireNonNull(clock);
    this.startTime = ZonedDateTime.now(clock);
  }

  @Override
  public <T> T wrap(Class<T> klass, T delegate) {
    Objects.requireNonNull(klass);
    Objects.requireNonNull(delegate);

    // Validate that at least one method in the class has the @Profiled annotation
    boolean hasProfiledMethod = false;
    for (var method : klass.getMethods()) {
      if (method.isAnnotationPresent(Profiled.class)) {
        hasProfiledMethod = true;
        break;
      }
    }

    if (!hasProfiledMethod) {
      throw new IllegalArgumentException("Class " + klass.getName() + " does not have any @Profiled methods.");
    }

    // Create and return a dynamic proxy
    return (T) Proxy.newProxyInstance(
        klass.getClassLoader(),
        new Class<?>[]{klass},
        new ProfilingMethodInterceptor(clock, delegate, state, klass.getName())
    );
  }

  @Override
  public void writeData(Path path) {
    Objects.requireNonNull(path);

    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      writeData(writer);
    } catch (IOException e) {
      System.err.println("[ERROR] Failed to write profiling data to file: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Override
  public void writeData(Writer writer) throws IOException {
    writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
    writer.write(System.lineSeparator());
    state.write(writer);
    writer.write(System.lineSeparator());
  }
}
