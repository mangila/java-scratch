package io.github.mangila.java2.internal;

import io.github.mangila.ensure4j.Ensure;
import java.net.URI;

public record HttpUri(URI value) {

  public HttpUri {
    Ensure.notNull(value);
    final String scheme = value.getScheme();
    Ensure.notNull(scheme, "scheme must not be null");
    if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
      throw new IllegalArgumentException(
          "Invalid scheme: " + scheme + ". Expected 'http' or 'https'.");
    }
  }

  public int getPort() {
    int port = value.getPort();
    return switch (port) {
      case -1 -> isHttps() ? 443 : 80;
      default -> port;
    };
  }

  public boolean isHttps() {
    final String scheme = value.getScheme();
    return "https".equals(scheme);
  }

  public String getHost() {
    return value.getHost();
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
