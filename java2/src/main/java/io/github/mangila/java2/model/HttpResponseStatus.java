package io.github.mangila.java2.model;

public record HttpResponseStatus(String version, int code, String reasonPhrase) {

  public static HttpResponseStatus parse(byte[] buffer) {
    String line = new String(buffer);
    return parse(line);
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

  @Override
  public String toString() {
    return version + " " + code + " " + reasonPhrase;
  }
}
