package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Clock clock;  // Clock instance for tracking time.
  private final Object target;  // The object being proxied.
  private final ProfilingState profilingState;  // Holds profiling data.
  private final String profilerClassName;  // Name of the class for method recording.

  /**
   * Constructor for ProfilingMethodInterceptor.
   *
   * @param clock          the clock used for measuring elapsed time.
   * @param target         the target object being profiled.
   * @param profilingState the state that records method call durations.
   * @param profilerClassName the class name of the profiled object.
   */
  ProfilingMethodInterceptor(Clock clock, Object target, ProfilingState profilingState, String profilerClassName) {
    this.clock = Objects.requireNonNull(clock);
    this.target = Objects.requireNonNull(target);
    this.profilingState = Objects.requireNonNull(profilingState);
    this.profilerClassName = profilerClassName;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // Check if the method has the @Profiled annotation.
    if (!method.isAnnotationPresent(Profiled.class)) {
      // If not profiled, just call the original method.
      return method.invoke(target, args);
    }

    // Record the start time.
    Instant start = clock.instant();

    try {
      // Invoke the actual method.
      return method.invoke(target, args);
    } catch (InvocationTargetException e) {
      // Log and rethrow the original cause of the exception.
      System.err.println("[ERROR] Exception in profiled method: " + e.getCause());
      throw e.getCause();
    } catch (IllegalAccessException e) {
      System.err.println("[ERROR] Illegal access when invoking method: " + method.getName());
      throw new RuntimeException(e);
    } finally {
      // Record the end time and calculate the elapsed time.
      Instant end = clock.instant();
      Duration duration = Duration.between(start, end);

      // Record the method call details into ProfilingState.
      profilingState.record(target.getClass(), method, duration);
      System.out.println("[DEBUG] Profiled method: " + method.getName() + " took " + duration.toMillis() + " ms.");
    }
  }
}
