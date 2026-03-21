package io.github.mangila.java2.internal.pool;

import io.github.mangila.java2.HttpClientConfig;
import io.github.mangila.java2.internal.HttpUri;
import io.github.mangila.java2.internal.cookie.CookieStore;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPool implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);

  private final HttpUri uri;
  private final CookieStore cookieStore;
  private final HttpClientConfig.ConnectionPoolConfig connectionPoolConfig;
  private final ConnectionCollection connections;
  private final ConnectionQueue queue;
  private final ScheduledExecutorService scheduler;

  public ConnectionPool(HttpClientConfig httpClientConfig) {
    this.uri = httpClientConfig.host();
    this.cookieStore = new CookieStore();
    this.connectionPoolConfig = httpClientConfig.connectionPoolConfig();
    this.queue = new ConnectionQueue(connectionPoolConfig);
    this.connections = new ConnectionCollection(connectionPoolConfig);
    this.scheduler = Executors.newSingleThreadScheduledExecutor();
  }

  public ConnectionPoolEntry acquire() throws InterruptedException {
    return queue.acquire();
  }

  @Override
  public void close() {
    LOGGER.info("Closing connection pool");
    scheduler.shutdown();
    queue.clear();
    connections.closeAllConnections();
    connections.clear();
  }

  public void init() throws IOException {
    LOGGER.info("Initializing connection pool");
    for (int i = 0; i < connectionPoolConfig.maxConnections(); i++) {
      final Connection connection = new Connection(uri);
      connection.create();
      final ConnectionPoolEntry entry = new ConnectionPoolEntry(connection, cookieStore);
      connections.add(entry);
      queue.add(entry);
    }
    final Duration idleTimeout = connectionPoolConfig.idleTimeout();
    final ConnectionIdleProbe probe = new ConnectionIdleProbe(idleTimeout, connections);
    scheduler.scheduleAtFixedRate(probe, 0, idleTimeout.toMillis(), TimeUnit.MILLISECONDS);
  }

  public void release(ConnectionPoolEntry entry) {
    queue.release(entry);
  }
}
