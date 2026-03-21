package io.github.mangila.java1.internal;

import io.github.mangila.java1.internal.tokenizer.model.JsonToken;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenType;

public class JsonLexer {

  private final JsonStreamReader jsonStreamReader;

  public JsonLexer(JsonStreamReader jsonStreamReader) {
    this.jsonStreamReader = jsonStreamReader;
  }

  public JsonTokenQueue lexJson() {
    final int jsonLength = jsonStreamReader.getLength();
    if (jsonLength == 0) {
      return JsonTokenQueue.EMPTY;
    }
    final JsonTokenQueue queue = new JsonTokenQueue(jsonLength);
    try (JsonTokenIterator iterator = new JsonTokenIterator(jsonStreamReader)) {
      while (iterator.hasNext()) {
        final JsonToken jsonToken = iterator.next();
        if (jsonToken.hasType(JsonTokenType.WHITESPACE)) {
          continue;
        }
        queue.add(jsonToken);
      }
      return queue;
    }
  }
}
