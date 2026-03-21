package io.github.mangila.java2.internal.pool;

import java.io.IOException;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConnectionIdleProbe implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionIdleProbe.class);

  private final Duration idleTimeout;
  private final ConnectionCollection connections;

  ConnectionIdleProbe(Duration idleTimeout, ConnectionCollection connections) {
    this.idleTimeout = idleTimeout;
    this.connections = connections;
  }

  @Override
  public void run() {
    for (ConnectionPoolEntry entry : connections.getConnections()) {
      if (entry.isIdle(idleTimeout) && entry.isAvailable()) {
        try {
          LOGGER.info("Closing idle connection");
          entry.closeConnection();
        } catch (IOException _) {
          // do nothing
        }
      }
    }
  }
}
