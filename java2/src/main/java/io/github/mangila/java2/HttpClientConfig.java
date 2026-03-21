package io.github.mangila.java2;

import io.github.mangila.java2.internal.HttpUri;
import java.time.Duration;
import java.util.concurrent.Executor;

public record HttpClientConfig(
    HttpUri host,
    boolean followRedirect,
    ConnectionPoolConfig connectionPoolConfig,
    Executor executor) {

  public record ConnectionPoolConfig(
      int maxConnections, Duration connectionTimeout, Duration idleTimeout) {}
}
