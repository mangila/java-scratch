package io.github.mangila.java1.internal.parser;

import io.github.mangila.java1.internal.JsonTokenQueue;
import io.github.mangila.java1.internal.parser.model.JsonParserException;
import io.github.mangila.java1.internal.tokenizer.model.JsonToken;
import io.github.mangila.java1.model.*;

public non-sealed class JsonNodeParser implements Parser<JsonNode> {

  private final JsonArrayParser jsonArrayParser;
  private final JsonObjectParser jsonObjectParser;

  public JsonNodeParser() {
    this.jsonArrayParser = new JsonArrayParser(this);
    this.jsonObjectParser = new JsonObjectParser(this);
  }

  @Override
  public JsonNode parse(JsonTokenQueue queue) throws JsonParserException {
    JsonToken jsonToken = queue.ensurePeek();
    return switch (jsonToken.type()) {
      case NULL -> {
        queue.ensurePoll();
        yield JsonNode.NULL_NODE;
      }
      case STRING -> {
        jsonToken = queue.ensurePoll();
        yield new JsonNode(JsonType.STRING, jsonToken.value());
      }
      case NUMBER -> {
        jsonToken = queue.ensurePoll();
        yield new JsonNode(JsonType.NUMBER, jsonToken.value());
      }
      case TRUE, FALSE -> {
        jsonToken = queue.ensurePoll();
        yield new JsonNode(JsonType.BOOLEAN, jsonToken.value());
      }
      case OPEN_ARRAY -> {
        final JsonArray jsonArray = jsonArrayParser.parse(queue);
        yield new JsonNode(JsonType.ARRAY, jsonArray);
      }
      case OPEN_OBJECT -> {
        final JsonObject jsonObject = jsonObjectParser.parse(queue);
        yield new JsonNode(JsonType.OBJECT, jsonObject);
      }
      default -> throw new JsonParserException("Unexpected token type: " + jsonToken.type());
    };
  }
}
