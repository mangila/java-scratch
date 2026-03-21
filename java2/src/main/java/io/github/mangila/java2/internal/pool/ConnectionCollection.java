package io.github.mangila.java2.internal.pool;

import io.github.mangila.java2.HttpClientConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionCollection {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionCollection.class);

  private final List<ConnectionPoolEntry> connections;

  public ConnectionCollection(HttpClientConfig.ConnectionPoolConfig connectionPoolConfig) {
    this.connections = new ArrayList<>(connectionPoolConfig.maxConnections());
  }

  public void add(ConnectionPoolEntry connectionPoolEntry) {
    connections.add(connectionPoolEntry);
  }

  public void clear() {
    connections.clear();
  }

  public void closeAllConnections() {
    for (ConnectionPoolEntry entry : connections) {
      while (!entry.acquireState()) {
        Thread.onSpinWait();
      }
      try {
        entry.closeConnection();
      } catch (IOException _) {
        // do nothing
      }
    }
  }

  public List<ConnectionPoolEntry> getConnections() {
    return Collections.unmodifiableList(connections);
  }
}
