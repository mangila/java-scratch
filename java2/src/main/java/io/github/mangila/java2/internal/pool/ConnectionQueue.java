package io.github.mangila.java2.internal.pool;

import io.github.mangila.java2.HttpClientConfig;
import io.github.mangila.java2.internal.HttpUri;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionQueue {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionQueue.class);

  private final HttpUri uri;
  private final BlockingQueue<ConnectionPoolEntry> queue;
  private final Duration timeout;

  public ConnectionQueue(HttpUri uri, HttpClientConfig.ConnectionPoolConfig connectionPoolConfig) {
    this.uri = uri;
    this.timeout = connectionPoolConfig.connectionTimeout();
    this.queue = new ArrayBlockingQueue<>(connectionPoolConfig.maxConnections(), true);
  }

  public ConnectionPoolEntry acquire() throws InterruptedException {
    final ConnectionPoolEntry entry = queue.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
    if (entry == null) {
      throw new ConnectionPoolException("Pool is exhausted");
    }
    try {
      while (!entry.acquireState()) {
        Thread.onSpinWait();
      }
      return entry.ensureConnected();
    } catch (IOException e) {
      release(entry);
      throw new ConnectionPoolException("Failed to acquire connection", e);
    }
  }

  public void add(ConnectionPoolEntry connectionPoolEntry) {
    queue.add(connectionPoolEntry);
  }

  public void clear() {
    queue.clear();
  }

  public void release(ConnectionPoolEntry connectionPoolEntry) {
    while (!connectionPoolEntry.releaseState()) {
      Thread.onSpinWait();
    }
    add(connectionPoolEntry);
  }
}
