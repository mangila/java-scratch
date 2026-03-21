package io.github.mangila.java1.internal.parser;

import io.github.mangila.java1.internal.JsonTokenQueue;
import io.github.mangila.java1.internal.parser.model.JsonParserException;
import io.github.mangila.java1.internal.tokenizer.model.JsonToken;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenType;
import io.github.mangila.java1.model.JsonArray;
import io.github.mangila.java1.model.JsonNode;

public non-sealed class JsonArrayParser implements Parser<JsonArray> {

  private final JsonNodeParser jsonNodeParser;

  public JsonArrayParser(JsonNodeParser jsonNodeParser) {
    this.jsonNodeParser = jsonNodeParser;
  }

  @Override
  public JsonArray parse(JsonTokenQueue queue) throws JsonParserException {
    queue.expect(JsonTokenType.OPEN_ARRAY);
    JsonToken jsonToken = queue.ensurePeek();
    if (jsonToken.hasType(JsonTokenType.CLOSE_ARRAY)) {
      queue.expect(JsonTokenType.CLOSE_ARRAY);
      return JsonArray.EMPTY;
    }
    final JsonArray jsonArray = new JsonArray();
    while (true) {
      final JsonNode jsonNode = jsonNodeParser.parse(queue);
      jsonArray.add(jsonNode);
      jsonToken = queue.ensurePeek();
      if (jsonToken.hasType(JsonTokenType.CLOSE_ARRAY)) {
        queue.expect(JsonTokenType.CLOSE_ARRAY);
        return jsonArray;
      }
      queue.expect(JsonTokenType.COMMA);
    }
  }
}
