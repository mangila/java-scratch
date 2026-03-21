package io.github.mangila.java1.internal.tokenizer;

import static io.github.mangila.java1.internal.JsonStreamReader.END_OF_STREAM;

import io.github.mangila.java1.internal.JsonStreamReader;
import io.github.mangila.java1.internal.tokenizer.model.JsonToken;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenType;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenizeException;
import java.nio.CharBuffer;
import java.util.HexFormat;

public non-sealed class JsonStringTokenizer implements Tokenizer {

  private final JsonStreamReader jsonStreamReader;
  private final StringBuilder jsonStringBuilder;

  public JsonStringTokenizer(JsonStreamReader jsonStreamReader) {
    this.jsonStreamReader = jsonStreamReader;
    this.jsonStringBuilder = new StringBuilder(1024);
  }

  @Override
  public JsonToken tokenize(int characterRead) throws JsonTokenizeException {
    jsonStringBuilder.setLength(0);
    while (true) {
      final int nextCharacterRead = jsonStreamReader.readCharacter();
      if (nextCharacterRead == END_OF_STREAM) {
        throw new JsonTokenizeException(
            "unexpected end of stream at position: " + jsonStreamReader.getPosition());
      }
      if (Character.isISOControl(nextCharacterRead)) {
        final String message =
            "unescaped control character: "
                + nextCharacterRead
                + " at position: "
                + jsonStreamReader.getPosition();
        throw new JsonTokenizeException(message);
      }
      if (nextCharacterRead == '"') {
        break;
      }
      if (nextCharacterRead == '\\') {
        final int escape = readEscape();
        jsonStringBuilder.appendCodePoint(escape);
      } else {
        jsonStringBuilder.appendCodePoint(nextCharacterRead);
      }
    }
    return new JsonToken(JsonTokenType.STRING, jsonStringBuilder.toString());
  }

  private int readEscape() throws JsonTokenizeException {
    final int escapeCharacterRead = jsonStreamReader.readCharacter();
    return switch (escapeCharacterRead) {
      case '"', '\\', '/' -> escapeCharacterRead;
      case 'b' -> '\b';
      case 'f' -> '\f';
      case 'n' -> '\n';
      case 'r' -> '\r';
      case 't' -> '\t';
      case 'u' -> readUnicodeEscape();
      case END_OF_STREAM ->
          throw new JsonTokenizeException(
              "unexpected end of stream at position: " + jsonStreamReader.getPosition());
      default ->
          throw new JsonTokenizeException(
              "unexpected escape sequence: '"
                  + (char) escapeCharacterRead
                  + "' at position: "
                  + jsonStreamReader.getPosition());
    };
  }

  private int readUnicodeEscape() throws JsonTokenizeException {
    final CharBuffer charBufferRead = jsonStreamReader.readCharacter(4);
    try {
      return HexFormat.fromHexDigits(charBufferRead.toString());
    } catch (IllegalArgumentException e) {
      throw new JsonTokenizeException(
          "invalid unicode escape: '"
              + charBufferRead
              + "' at position: "
              + jsonStreamReader.getPosition(),
          e);
    }
  }
}
