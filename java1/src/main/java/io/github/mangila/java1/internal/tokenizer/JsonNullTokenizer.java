package io.github.mangila.java1.internal.tokenizer;

import io.github.mangila.java1.internal.JsonStreamReader;
import io.github.mangila.java1.internal.tokenizer.model.JsonToken;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenType;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenizeException;
import java.nio.CharBuffer;

public non-sealed class JsonNullTokenizer implements Tokenizer {

  private static final JsonToken NULL_JSON_TOKEN = JsonToken.JSON_TOKEN_MAP.get(JsonTokenType.NULL);

  private final JsonStreamReader jsonStreamReader;

  public JsonNullTokenizer(JsonStreamReader jsonStreamReader) {
    this.jsonStreamReader = jsonStreamReader;
  }

  @Override
  public JsonToken tokenize(int characterRead) throws JsonTokenizeException {
    final CharBuffer buffer = jsonStreamReader.readCharacter(3);
    final String estimateNull = buffer.toString();
    if ("ull".equals(estimateNull)) {
      return NULL_JSON_TOKEN;
    }
    throw new JsonTokenizeException(JsonTokenType.NULL, jsonStreamReader.getPosition());
  }
}
