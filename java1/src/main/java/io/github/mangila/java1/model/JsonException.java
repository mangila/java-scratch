package io.github.mangila.java1.model;

public class JsonException extends RuntimeException {

  public JsonException(String message) {
    super(message);
  }

  public JsonException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public JsonException(Throwable throwable) {
    super(throwable);
  }
}
