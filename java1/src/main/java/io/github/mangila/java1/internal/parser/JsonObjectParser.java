package io.github.mangila.java1.internal.parser;

import io.github.mangila.java1.internal.JsonTokenQueue;
import io.github.mangila.java1.internal.parser.model.JsonParserException;
import io.github.mangila.java1.internal.tokenizer.model.JsonToken;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenType;
import io.github.mangila.java1.model.JsonNode;
import io.github.mangila.java1.model.JsonObject;

public non-sealed class JsonObjectParser implements Parser<JsonObject> {

  private final JsonNodeParser jsonNodeParser;

  public JsonObjectParser(JsonNodeParser jsonNodeParser) {
    this.jsonNodeParser = jsonNodeParser;
  }

  @Override
  public JsonObject parse(JsonTokenQueue queue) throws JsonParserException {
    queue.expect(JsonTokenType.OPEN_OBJECT);
    JsonToken jsonToken = queue.ensurePeek();
    if (jsonToken.hasType(JsonTokenType.CLOSE_OBJECT)) {
      queue.expect(JsonTokenType.CLOSE_OBJECT);
      return JsonObject.EMPTY;
    }
    final JsonObject jsonObject = new JsonObject();
    while (true) {
      jsonToken = queue.expect(JsonTokenType.STRING);
      final String jsonKey = jsonToken.value().toString();
      queue.expect(JsonTokenType.COLON);
      final JsonNode jsonNode = jsonNodeParser.parse(queue);
      jsonObject.put(jsonKey, jsonNode);
      jsonToken = queue.ensurePeek();
      if (jsonToken.hasType(JsonTokenType.CLOSE_OBJECT)) {
        queue.expect(JsonTokenType.CLOSE_OBJECT);
        return jsonObject;
      }
      queue.expect(JsonTokenType.COMMA);
    }
  }
}
