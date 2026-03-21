package io.github.mangila.java1.internal.tokenizer;

import io.github.mangila.java1.internal.JsonStreamReader;
import io.github.mangila.java1.internal.tokenizer.model.JsonToken;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenType;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenizeException;
import java.nio.CharBuffer;

public non-sealed class JsonBooleanTokenizer implements Tokenizer {

  private static final JsonToken FALSE_JSON_TOKEN =
      JsonToken.JSON_TOKEN_MAP.get(JsonTokenType.FALSE);
  private static final JsonToken TRUE_JSON_TOKEN = JsonToken.JSON_TOKEN_MAP.get(JsonTokenType.TRUE);

  private final JsonStreamReader jsonStreamReader;

  public JsonBooleanTokenizer(JsonStreamReader jsonStreamReader) {
    this.jsonStreamReader = jsonStreamReader;
  }

  @Override
  public JsonToken tokenize(int characterRead) throws JsonTokenizeException {
    return switch (characterRead) {
      case 't' -> tokenizeTrue();
      case 'f' -> tokenizeFalse();
      default -> throw new JsonTokenizeException(jsonStreamReader.getPosition());
    };
  }

  private JsonToken tokenizeFalse() throws JsonTokenizeException {
    final CharBuffer buffer = jsonStreamReader.readCharacter(4);
    final String estimateFalse = buffer.toString();
    if ("alse".equals(estimateFalse)) {
      return FALSE_JSON_TOKEN;
    }
    throw new JsonTokenizeException(JsonTokenType.FALSE, jsonStreamReader.getPosition());
  }

  private JsonToken tokenizeTrue() throws JsonTokenizeException {
    final CharBuffer buffer = jsonStreamReader.readCharacter(3);
    final String estimateTrue = buffer.toString();
    if ("rue".equals(estimateTrue)) {
      return TRUE_JSON_TOKEN;
    }
    throw new JsonTokenizeException(JsonTokenType.TRUE, jsonStreamReader.getPosition());
  }
}
