package io.github.mangila.java2.model;

import io.github.mangila.java2.internal.HttpUri;
import java.util.Objects;

public final class HttpRequest {

  public static HttpRequestBuilder builder() {
    return new HttpRequestBuilder();
  }

  private final HttpMethod method;
  private final String path;
  private final int contentLength;
  private final HttpVersion version;
  private final HttpHeaders headers;
  private final String body;

  public HttpRequest(
      HttpMethod method, String path, HttpVersion version, HttpHeaders headers, String body) {
    this.method = Objects.requireNonNull(method, "method cannot be null");
    this.path = Objects.requireNonNull(path, "path cannot be null");
    this.version = Objects.requireNonNull(version, "version cannot be null");
    this.headers = Objects.requireNonNull(headers, "headers cannot be null");
    this.body = body;
    this.contentLength = body == null ? 0 : body.length();
  }

  public HttpRequestBuilder toBuilder() {
    return new HttpRequestBuilder().method(method).path(path).headers(headers).body(body);
  }

  public String toHttp(HttpUri uri) {
    final StringBuilder sb = new StringBuilder();
    final String host = uri.getHost();
    sb.append(method).append(' ').append(path).append(' ').append(version).append("\r\n");
    sb.append("host: ").append(host).append("\r\n");
    sb.append("content-length: ").append(contentLength).append("\r\n");
    sb.append(headers.toString().replace("\n", "\r\n")).append("\r\n");
    if (body != null) {
      sb.append(body).append("\r\n");
    }
    return sb.toString();
  }
}
