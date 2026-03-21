package io.github.mangila.java2.internal.pool;

import java.io.IOException;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConnectionIdleProbe implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionIdleProbe.class);

  private final ConnectionCollection connections;

  ConnectionIdleProbe(ConnectionCollection connections) {
    this.connections = connections;
  }

  @Override
  public void run() {
    final Duration duration = Duration.ofMinutes(1);
    for (ConnectionPoolEntry entry : connections.getConnections()) {
      if (entry.isIdle(duration) && entry.isAvailable()) {
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
