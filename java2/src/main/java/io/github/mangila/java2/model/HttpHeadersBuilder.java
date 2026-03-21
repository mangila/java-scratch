package io.github.mangila.java2.model;

import java.util.*;

public class HttpHeadersBuilder {

  private final TreeMap<String, List<String>> headersMap = new TreeMap<>();

  public HttpHeadersBuilder() {
    accept("*/*");
    addHeader("connection", "keep-alive");
    addHeader("user-agent", "github.com/mangila/java-scratch");
  }

  public HttpHeadersBuilder(Map<String, List<String>> headers) {
    headers.forEach((key, value) -> headersMap.put(key.toLowerCase(Locale.ROOT), value));
  }

  public HttpHeadersBuilder accept(String value) {
    return addHeader("accept", value);
  }

  public HttpHeadersBuilder acceptEncoding(String value) {
    return addHeader("accept-encoding", value);
  }

  public HttpHeadersBuilder addHeader(String key, String value) {
    final String headerKey = key.toLowerCase(Locale.ROOT);
    headersMap.computeIfAbsent(headerKey, s -> new ArrayList<>(1)).add(value);
    return this;
  }

  public HttpHeaders build() {
    return new HttpHeaders(headersMap);
  }

  public HttpHeadersBuilder contentType(String value) {
    return addHeader("content-type", value);
  }
}
