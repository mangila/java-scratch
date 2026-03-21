package io.github.mangila.java2.internal.pool;

import io.github.mangila.java2.HttpClientConfig;
import io.github.mangila.java2.internal.HttpUri;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPool implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);

  private final HttpUri uri;
  private final HttpClientConfig.ConnectionPoolConfig connectionPoolConfig;
  private final ConnectionCollection connections;
  private final ConnectionQueue queue;

  public ConnectionPool(HttpUri uri, HttpClientConfig.ConnectionPoolConfig connectionPoolConfig) {
    this.uri = uri;
    this.connectionPoolConfig = connectionPoolConfig;
    this.queue = new ConnectionQueue(uri, connectionPoolConfig);
    this.connections = new ConnectionCollection(connectionPoolConfig);
  }

  public ConnectionPoolEntry acquire() throws InterruptedException {
    return queue.acquire();
  }

  @Override
  public void close() {
    LOGGER.info("Closing connection pool");
    queue.clear();
    connections.closeAllConnections();
    connections.clear();
  }

  public void init() throws IOException {
    for (int i = 0; i < connectionPoolConfig.maxConnections(); i++) {
      final Connection connection = new Connection(uri);
      connection.create();
      final ConnectionPoolEntry entry = new ConnectionPoolEntry(connection);
      connections.add(entry);
      queue.add(entry);
    }
    final ScheduledExecutorService scheduler = connectionPoolConfig.scheduler();
    LOGGER.info("Scheduling connection idle probe");
    final ConnectionIdleProbe probe = new ConnectionIdleProbe(connections);
    scheduler.scheduleAtFixedRate(probe, 0, 1, TimeUnit.MINUTES);
  }

  public void release(ConnectionPoolEntry entry) {
    queue.release(entry);
  }
}
