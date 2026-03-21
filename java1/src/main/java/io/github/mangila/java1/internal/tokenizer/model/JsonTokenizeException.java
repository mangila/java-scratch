package io.github.mangila.java1.internal.tokenizer.model;

public class JsonTokenizeException extends RuntimeException {

  public JsonTokenizeException(JsonTokenType jsonTokenType, long position) {
    super("Not valid tokenization for token: " + jsonTokenType + " at position: " + position);
  }

  public JsonTokenizeException(long position) {
    super("Not valid tokenization at position: " + position);
  }

  public JsonTokenizeException(String message) {
    super(message);
  }

  public JsonTokenizeException(String message, Throwable cause) {
    super(message, cause);
  }
}
