package io.github.mangila.java1.internal.tokenizer.model;

public enum JsonTokenType {
  WHITESPACE,
  OPEN_OBJECT,
  CLOSE_OBJECT,
  OPEN_ARRAY,
  CLOSE_ARRAY,
  COMMA,
  COLON,
  STRING,
  NUMBER,
  TRUE,
  FALSE,
  NULL
}
