package io.github.mangila.java1.internal.tokenizer;

import static io.github.mangila.java1.internal.JsonStreamReader.END_OF_STREAM;

import io.github.mangila.java1.internal.JsonStreamReader;
import io.github.mangila.java1.internal.tokenizer.model.JsonToken;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenType;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenizeException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public non-sealed class JsonNumberTokenizer implements Tokenizer {

  private static final Pattern JSON_NUMBER =
      Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");

  private final JsonStreamReader jsonStreamReader;
  private final StringBuilder jsonNumberBuilder;
  private final Matcher jsonNumberMatcher;

  public JsonNumberTokenizer(JsonStreamReader jsonStreamReader) {
    this.jsonStreamReader = jsonStreamReader;
    this.jsonNumberBuilder = new StringBuilder(1024);
    this.jsonNumberMatcher = JSON_NUMBER.matcher("");
  }

  @Override
  public JsonToken tokenize(int characterRead) throws JsonTokenizeException {
    jsonNumberBuilder.setLength(0);
    jsonNumberBuilder.appendCodePoint(characterRead);
    while (true) {
      final int nextCharacterRead = jsonStreamReader.readCharacter();
      if (nextCharacterRead == END_OF_STREAM) {
        break;
      }
      if (isDelimiter(nextCharacterRead)) {
        jsonStreamReader.unread(nextCharacterRead);
        break;
      }
      jsonNumberBuilder.appendCodePoint(nextCharacterRead);
    }
    final String value = jsonNumberBuilder.toString();
    final boolean isJsonNumber = jsonNumberMatcher.reset(value).matches();
    if (isJsonNumber) {
      return new JsonToken(JsonTokenType.NUMBER, value);
    }
    throw new JsonTokenizeException("Not a valid JSON number: " + value);
  }

  private boolean isDelimiter(int characterRead) {
    return characterRead == ','
        || characterRead == ']'
        || characterRead == '}'
        || characterRead == ' '
        || characterRead == '\t'
        || characterRead == '\n'
        || characterRead == '\r';
  }
}
