package io.github.mangila.java2.internal.pool;

import io.github.mangila.java2.internal.HttpResponseParser;
import io.github.mangila.java2.internal.HttpUri;
import io.github.mangila.java2.model.HttpRequest;
import io.github.mangila.java2.model.HttpResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPoolEntry {

  public static final int STATE_AVAILABLE = 0;
  public static final int STATE_IN_USE = 1;

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPoolEntry.class);

  private final Connection connection;
  private final HttpResponseParser httpResponseParser;
  private final AtomicInteger state;
  private Instant releasedAt;

  ConnectionPoolEntry(Connection connection) {
    this.connection = connection;
    this.httpResponseParser = new HttpResponseParser(connection);
    this.state = new AtomicInteger(STATE_AVAILABLE);
  }

  public boolean acquireState() {
    return state.compareAndSet(STATE_AVAILABLE, STATE_IN_USE);
  }

  public void closeConnection() throws IOException {
    connection.close();
  }

  public ConnectionPoolEntry ensureConnected() throws IOException {
    if (connection.isNew()) {
      LOGGER.info("Opening new connection");
      connection.open();
    } else if (connection.isClosed()) {
      LOGGER.info("Reopening closed connection");
      connection.create();
      connection.open();
    }
    return this;
  }

  public HttpResponse exchange(HttpRequest httpRequest) throws IOException {
    final HttpUri uri = connection.getUri();
    final String http = httpRequest.toHttp(uri);
    connection.write(http);
    return httpResponseParser.parse();
  }

  public boolean isAvailable() {
    return state.get() == STATE_AVAILABLE;
  }

  public boolean isIdle(Duration duration) {
    if (releasedAt == null) {
      return false;
    }
    Instant cutoff = Instant.now().minus(duration);
    return releasedAt.isBefore(cutoff);
  }

  public boolean releaseState() {
    final boolean cas = state.compareAndSet(STATE_IN_USE, STATE_AVAILABLE);
    if (cas) {
      this.releasedAt = Instant.now();
      return true;
    }
    return false;
  }
}
