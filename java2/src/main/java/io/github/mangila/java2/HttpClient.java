package io.github.mangila.java2;

import io.github.mangila.java2.internal.*;
import io.github.mangila.java2.internal.pool.ConnectionPool;
import io.github.mangila.java2.internal.pool.ConnectionPoolEntry;
import io.github.mangila.java2.model.HttpRequest;
import io.github.mangila.java2.model.HttpResponse;
import java.io.*;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

  private final HttpClientConfig httpClientConfig;
  private final ConnectionPool connectionPool;

  public HttpClient(HttpClientConfig httpClientConfig) {
    this.httpClientConfig = httpClientConfig;
    final HttpUri uri = new HttpUri(httpClientConfig.host());
    this.connectionPool = new ConnectionPool(uri, httpClientConfig.connectionPoolConfig());
  }

  @Override
  public void close() {
    connectionPool.close();
  }

  public CompletableFuture<HttpResponse> fetchAsync(HttpRequest httpRequest) {
    final Executor executor = httpClientConfig.executor();
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            return fetch(httpRequest);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
          }
        },
        executor);
  }

  public void init() {
    try {
      connectionPool.init();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private HttpResponse fetch(HttpRequest httpRequest) throws InterruptedException {
    LOGGER.info("HTTP request: {}", httpRequest);
    final ConnectionPoolEntry entry = connectionPool.acquire();
    try {
      return entry.exchange(httpRequest);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } finally {
      connectionPool.release(entry);
    }
  }
}
