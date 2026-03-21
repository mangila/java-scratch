package io.github.mangila.java2.model;

import java.util.*;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;

public record HttpHeaders(Map<String, String> headers) {

  public HttpHeaders(Map<String, String> headers) {
    this.headers =
        headers == null
            ? Collections.emptyMap()
            : Collections.unmodifiableMap(new LinkedHashMap<>(headers));
  }

  @Override
  public Map<String, String> headers() {
    return headers == null ? Collections.emptyMap() : Collections.unmodifiableMap(headers);
  }

  public static HttpHeadersBuilder builder() {
    return new HttpHeadersBuilder();
  }

  public static HttpHeaders parse(List<String> headers) {
    Map<String, String> responseHeaders =
        headers.stream()
            .skip(1)
            .map(line -> line.split(":", 2))
            .map(
                strings -> {
                  if (strings.length != 2) {
                    throw new IllegalArgumentException(
                        "Invalid header line: " + Arrays.toString(strings));
                  }
                  return strings;
                })
            .collect(
                Collectors.toMap(
                    parts -> parts[0].trim().toLowerCase(Locale.ROOT), parts -> parts[1].trim()));
    String statusLine = headers.getFirst();
    responseHeaders.put("status-line", statusLine);
    return new HttpHeaders(responseHeaders);
  }

  public HttpHeadersBuilder toBuilder() {
    return new HttpHeadersBuilder(headers);
  }

  @Override
  public @NonNull String toString() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      sb.append(key).append(": ").append(value).append("\n");
    }
    return sb.toString();
  }

  public HttpResponseStatus getStatusLine() {
    final String statusLine = headers.get("status-line");
    Objects.requireNonNull(statusLine, "status line cannot be null");
    return HttpResponseStatus.parse(statusLine);
  }

  public String getContentLength() {
    return headers.get("content-length");
  }

  public boolean isGzip() {
    final String contentEncoding = headers.get("content-encoding");
    if (contentEncoding == null) {
      return false;
    }
    return "gzip".contains(contentEncoding);
  }

  public boolean isChunked() {
    final String transferEncoding = headers.get("transfer-encoding");
    if (transferEncoding == null) {
      return false;
    }
    return "chunked".contains(transferEncoding);
  }
}
