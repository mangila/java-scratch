package io.github.mangila.java2;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

public record HttpClientConfig(
    URI host, ConnectionPoolConfig connectionPoolConfig, Executor executor) {

  public record ConnectionPoolConfig(
      int maxConnections, Duration connectionTimeout, ScheduledExecutorService scheduler) {}
}
