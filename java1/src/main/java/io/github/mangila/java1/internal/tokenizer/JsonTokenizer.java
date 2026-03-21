package io.github.mangila.java1.internal.tokenizer;

import io.github.mangila.java1.internal.JsonStreamReader;
import io.github.mangila.java1.internal.tokenizer.model.JsonToken;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenType;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenizeException;

public non-sealed class JsonTokenizer implements Tokenizer {

  private final JsonStreamReader jsonStreamReader;
  private final JsonStringTokenizer jsonStringTokenizer;
  private final JsonNumberTokenizer jsonNumberTokenizer;
  private final JsonBooleanTokenizer jsonBooleanTokenizer;
  private final JsonNullTokenizer jsonNullTokenizer;

  public JsonTokenizer(JsonStreamReader jsonStreamReader) {
    this.jsonStreamReader = jsonStreamReader;
    this.jsonStringTokenizer = new JsonStringTokenizer(jsonStreamReader);
    this.jsonNumberTokenizer = new JsonNumberTokenizer(jsonStreamReader);
    this.jsonBooleanTokenizer = new JsonBooleanTokenizer(jsonStreamReader);
    this.jsonNullTokenizer = new JsonNullTokenizer(jsonStreamReader);
  }

  @Override
  public JsonToken tokenize(int characterRead) throws JsonTokenizeException {
    if (Character.isWhitespace(characterRead)) {
      return JsonToken.JSON_TOKEN_MAP.get(JsonTokenType.WHITESPACE);
    }
    return switch (characterRead) {
      case '{' -> JsonToken.JSON_TOKEN_MAP.get(JsonTokenType.OPEN_OBJECT);
      case '}' -> JsonToken.JSON_TOKEN_MAP.get(JsonTokenType.CLOSE_OBJECT);
      case '[' -> JsonToken.JSON_TOKEN_MAP.get(JsonTokenType.OPEN_ARRAY);
      case ']' -> JsonToken.JSON_TOKEN_MAP.get(JsonTokenType.CLOSE_ARRAY);
      case ',' -> JsonToken.JSON_TOKEN_MAP.get(JsonTokenType.COMMA);
      case ':' -> JsonToken.JSON_TOKEN_MAP.get(JsonTokenType.COLON);
      case '"' -> jsonStringTokenizer.tokenize(characterRead);
      case 't', 'f' -> jsonBooleanTokenizer.tokenize(characterRead);
      case 'n' -> jsonNullTokenizer.tokenize(characterRead);
      case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' ->
          jsonNumberTokenizer.tokenize(characterRead);
      default -> {
        final String message =
            "unexpected character: '"
                + (char) characterRead
                + "' at position: "
                + jsonStreamReader.getPosition();
        throw new JsonTokenizeException(message);
      }
    };
  }
}
