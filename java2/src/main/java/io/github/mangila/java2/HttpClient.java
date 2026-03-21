package io.github.mangila.java2;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.java2.internal.HttpUri;
import io.github.mangila.java2.internal.pool.ConnectionPool;
import io.github.mangila.java2.internal.pool.ConnectionPoolEntry;
import io.github.mangila.java2.model.HttpRequest;
import io.github.mangila.java2.model.HttpResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

  public static Builder builder() {
    return new Builder();
  }

  private final HttpClientConfig httpClientConfig;
  private final HttpUri uri;
  private final boolean followRedirect;
  private final ConnectionPool connectionPool;
  private final AtomicBoolean initialized;

  public HttpClient(HttpClientConfig httpClientConfig) {
    this.httpClientConfig = httpClientConfig;
    this.followRedirect = httpClientConfig.followRedirect();
    this.uri = httpClientConfig.host();
    this.connectionPool = new ConnectionPool(httpClientConfig);
    this.initialized = new AtomicBoolean(false);
  }

  @Override
  public void close() {
    if (initialized.compareAndSet(true, false)) {
      connectionPool.close();
    }
  }

  public HttpResponse fetch(HttpRequest httpRequest) {
    try {
      return exchange(httpRequest);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  public CompletableFuture<HttpResponse> fetchAsync(HttpRequest httpRequest) {
    final Executor executor = httpClientConfig.executor();
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            return exchange(httpRequest);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
          }
        },
        executor);
  }

  public void init() {
    if (initialized.compareAndSet(false, true)) {
      try {
        connectionPool.init();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  private HttpResponse exchange(HttpRequest httpRequest) throws InterruptedException {
    final ConnectionPoolEntry entry = connectionPool.acquire();
    try {
      return entry.exchange(httpRequest, followRedirect);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } finally {
      connectionPool.release(entry);
    }
  }

  public static class Builder {
    private HttpUri host;
    private boolean followRedirect = true;
    private Duration connectTimeout = Duration.ofSeconds(30);
    private Duration idleTimeout = Duration.ofMinutes(1);
    private int maxConnections = 10;
    private Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    public HttpClient build() {
      if (host == null) {
        throw new IllegalStateException("host must be configured");
      }
      HttpClientConfig.ConnectionPoolConfig poolConfig =
          new HttpClientConfig.ConnectionPoolConfig(maxConnections, connectTimeout, idleTimeout);
      HttpClientConfig config = new HttpClientConfig(host, followRedirect, poolConfig, executor);
      HttpClient client = new HttpClient(config);
      client.init();
      return client;
    }

    public Builder connectTimeout(Duration duration) {
      this.connectTimeout = Objects.requireNonNull(duration, "connectTimeout cannot be null");
      return this;
    }

    public Builder executor(Executor executor) {
      this.executor = Objects.requireNonNull(executor, "executor cannot be null");
      return this;
    }

    public Builder followRedirect(boolean followRedirect) {
      this.followRedirect = followRedirect;
      return this;
    }

    public Builder host(URI host) {
      Objects.requireNonNull(host, "host cannot be null");
      this.host = new HttpUri(host);
      return this;
    }

    public Builder idleTimeout(Duration duration) {
      this.idleTimeout = Objects.requireNonNull(duration, "idleTimeout cannot be null");
      return this;
    }

    public Builder maxConnections(int maxConnections) {
      Ensure.positive(maxConnections);
      this.maxConnections = maxConnections;
      return this;
    }
  }
}
