package io.github.mangila.java2.model;

import java.util.*;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record HttpHeaders(Map<String, List<String>> headers) {

  public HttpHeaders(Map<String, List<String>> headers) {
    this.headers = headers == null ? Collections.emptyMap() : Map.copyOf(headers);
  }

  public static HttpHeadersBuilder builder() {
    return new HttpHeadersBuilder();
  }

  @Override
  public Map<String, List<String>> headers() {
    return Map.copyOf(headers);
  }

  public static HttpHeaders parse(List<String> headers) {
    if (headers == null || headers.isEmpty()) {
      throw new IllegalArgumentException("Headers list cannot be null or empty");
    }
    Map<String, List<String>> responseHeaders =
        headers.stream()
            .skip(1) // Skip status line
            .filter(line -> line != null && !line.isBlank())
            .map(line -> line.split(":", 2))
            .map(
                parts -> {
                  if (parts.length != 2) {
                    throw new IllegalArgumentException(
                        "Invalid header line: " + Arrays.toString(parts));
                  }
                  return parts;
                })
            .collect(
                Collectors.groupingBy(
                    parts -> parts[0].trim().toLowerCase(Locale.ROOT),
                    Collectors.mapping(parts -> parts[1].trim(), Collectors.toList())));
    String statusLine = headers.getFirst();
    responseHeaders.put(":status-line", List.of(statusLine));
    return new HttpHeaders(responseHeaders);
  }

  public HttpHeadersBuilder toBuilder() {
    return new HttpHeadersBuilder(headers);
  }

  @Override
  public @NonNull String toString() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
      String key = entry.getKey();
      // skip if pseudo-header (e.g. :status-line))
      if (key.startsWith(":")) {
        continue;
      }
      for (String value : entry.getValue()) {
        sb.append(key).append(": ").append(value).append("\r\n");
      }
    }
    return sb.toString();
  }

  public HttpResponseStatus getStatusLine() {
    final String statusLine = getSingleValue(":status-line");
    Objects.requireNonNull(statusLine, "status line cannot be null");
    return HttpResponseStatus.parse(statusLine);
  }

  public @Nullable String getContentLength() {
    return getSingleValue("content-length");
  }

  public @Nullable String getLocation() {
    return getSingleValue("location");
  }

  public @Nullable String getContentEncoding() {
    return getSingleValue("content-encoding");
  }

  public boolean isChunked() {
    final String transferEncoding = getSingleValue("transfer-encoding");
    if (transferEncoding == null) {
      return false;
    }
    return transferEncoding.equals("chunked");
  }

  private @Nullable String getSingleValue(String key) {
    final List<String> values = headers.get(key);
    if (values == null || values.isEmpty()) {
      return null;
    }
    return values.getFirst();
  }

  public List<String> getCookies() {
    final List<String> cookies = headers.get("set-cookie");
    return cookies == null ? Collections.emptyList() : cookies;
  }

  public boolean hasCookies() {
    return headers.containsKey("set-cookie");
  }
}
