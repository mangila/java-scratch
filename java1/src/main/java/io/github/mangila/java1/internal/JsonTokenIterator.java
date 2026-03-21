package io.github.mangila.java1.internal;

import static io.github.mangila.java1.internal.JsonStreamReader.END_OF_STREAM;

import io.github.mangila.java1.internal.tokenizer.JsonTokenizer;
import io.github.mangila.java1.internal.tokenizer.model.JsonToken;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class JsonTokenIterator implements Iterator<JsonToken>, AutoCloseable {

  private final JsonStreamReader jsonStreamReader;
  private final JsonTokenizer jsonTokenizer;

  public JsonTokenIterator(JsonStreamReader jsonStreamReader) {
    this.jsonStreamReader = jsonStreamReader;
    this.jsonTokenizer = new JsonTokenizer(jsonStreamReader);
  }

  @Override
  public void close() {
    jsonStreamReader.close();
  }

  @Override
  public boolean hasNext() {
    final int characterRead = jsonStreamReader.readCharacter();
    if (characterRead == END_OF_STREAM) {
      return false;
    }
    jsonStreamReader.unread(characterRead);
    return true;
  }

  @Override
  public JsonToken next() {
    if (!hasNext()) {
      throw new NoSuchElementException("No more tokens available");
    }
    final int characterRead = jsonStreamReader.readCharacter();
    return jsonTokenizer.tokenize(characterRead);
  }
}
