package io.github.mangila.java2.model;

public record HttpResponseStatus(String version, int code, String reasonPhrase) {

  public HttpResponseStatus {
    if (version == null || version.isEmpty()) {
      throw new IllegalArgumentException("Empty version");
    }
    if (reasonPhrase == null) {
      reasonPhrase = "";
    }
    if (code < 100 || code > 599) {
      throw new IllegalArgumentException(
          "Not valid status code: %s must be in range 100 - 599" + code);
    }
  }

  public static HttpResponseStatus parse(String line) {
    if (line == null || line.isEmpty()) {
      throw new IllegalArgumentException("Empty status line");
    }
    String[] parts = line.split(" ", 3);
    if (parts.length < 2) {
      throw new IllegalArgumentException("Malformed status line: " + line);
    }
    String version = parts[0];
    int code = Integer.parseInt(parts[1]);
    String reasonPhrase = (parts.length == 3) ? parts[2] : "";
    return new HttpResponseStatus(version, code, reasonPhrase);
  }

  public boolean is1xx() {
    return code >= 100 && code < 200;
  }

  public boolean is2xx() {
    return code >= 200 && code < 300;
  }

  public boolean is3xx() {
    return code >= 300 && code < 400;
  }

  public boolean is4xx() {
    return code >= 400 && code < 500;
  }

  public boolean is5xx() {
    return code >= 500 && code < 600;
  }

  public boolean isError() {
    return is4xx() || is5xx();
  }

  @Override
  public String toString() {
    return version + " " + code + " " + reasonPhrase;
  }
}
