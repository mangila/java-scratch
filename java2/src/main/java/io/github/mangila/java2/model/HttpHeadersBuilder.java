package io.github.mangila.java2.model;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class HttpHeadersBuilder {

  private final TreeMap<String, String> headersMap;

  public HttpHeadersBuilder() {
    this(new TreeMap<>());
  }

  public HttpHeadersBuilder(Map<String, String> headers) {
    this.headersMap = new TreeMap<>();
    headers.forEach((key, value) -> headersMap.put(key.toLowerCase(Locale.ROOT), value));
    headersMap.put("connection", "keep-alive");
    headersMap.put("user-agent", "github.com/mangila/java-scratch");
  }

  public HttpHeadersBuilder accept(String value) {
    return addHeader("accept", value);
  }

  public HttpHeadersBuilder acceptEncoding(String value) {
    return addHeader("accept-encoding", value);
  }

  public HttpHeadersBuilder addHeader(String key, String value) {
    final String headerKey = key.toLowerCase(Locale.ROOT);
    headersMap.put(headerKey, value);
    return this;
  }

  public HttpHeaders build() {
    return new HttpHeaders(headersMap);
  }

  public HttpHeadersBuilder contentType(String value) {
    return addHeader("content-type", value);
  }
}
