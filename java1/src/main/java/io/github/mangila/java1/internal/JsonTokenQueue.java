package io.github.mangila.java1.internal;

import io.github.mangila.java1.internal.parser.model.JsonParserException;
import io.github.mangila.java1.internal.tokenizer.model.JsonToken;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenType;
import java.util.ArrayDeque;

public final class JsonTokenQueue {

  public static final JsonTokenQueue EMPTY = new JsonTokenQueue(0);

  private final ArrayDeque<JsonToken> queue;

  public JsonTokenQueue(int jsonLength) {
    final int capacity = estimateQueueCapacity(jsonLength);
    this.queue = new ArrayDeque<>(capacity);
  }

  public void add(JsonToken token) {
    queue.add(token);
  }

  public JsonToken ensurePeek() throws JsonParserException {
    final JsonToken jsonToken = queue.peek();
    if (jsonToken == null) {
      throw new JsonParserException("Unexpected end of tokens");
    }
    return jsonToken;
  }

  public JsonToken ensurePoll() throws JsonParserException {
    final JsonToken jsonToken = queue.poll();
    if (jsonToken == null) {
      throw new JsonParserException("Unexpected end of tokens");
    }
    return jsonToken;
  }

  public JsonToken expect(JsonTokenType jsonTokenType) throws JsonParserException {
    final JsonToken jsonToken = ensurePoll();
    if (!jsonToken.hasType(jsonTokenType)) {
      final String message =
          "Unexpected token type: " + jsonToken.type() + ", expected: " + jsonTokenType;
      throw new JsonParserException(message);
    }
    return jsonToken;
  }

  public boolean isEmpty() {
    return queue.isEmpty();
  }

  private int estimateQueueCapacity(int jsonLength) {
    if (jsonLength <= 0) {
      return 16;
    }
    return Math.max(16, (int) (jsonLength * 0.5));
  }
}
