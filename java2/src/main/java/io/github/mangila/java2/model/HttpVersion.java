package io.github.mangila.java2.model;

public enum HttpVersion {
  HTTP_1_1("HTTP/1.1");

  private final String version;

  HttpVersion(String version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return version;
  }
}
