package io.github.mangila.java2.internal;

import io.github.mangila.ensure4j.Ensure;
import java.net.URI;
import org.jspecify.annotations.NonNull;

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

  public static HttpUri from(String uri) {
    final URI u = URI.create(uri);
    return new HttpUri(u);
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
  public @NonNull String toString() {
    return value.toString();
  }
}
