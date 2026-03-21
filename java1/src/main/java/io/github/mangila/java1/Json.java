package io.github.mangila.java1;

import io.github.mangila.ensure4j.Ensure;
import io.github.mangila.java1.internal.JsonLexer;
import io.github.mangila.java1.internal.JsonStreamReader;
import io.github.mangila.java1.internal.JsonTokenQueue;
import io.github.mangila.java1.internal.parser.JsonNodeParser;
import io.github.mangila.java1.internal.parser.Parser;
import io.github.mangila.java1.model.JsonException;
import io.github.mangila.java1.model.JsonNode;
import java.nio.charset.StandardCharsets;
import org.intellij.lang.annotations.Language;

public class Json {

  private final Parser<JsonNode> jsonParser;

  public Json() {
    this.jsonParser = new JsonNodeParser();
  }

  public JsonNode jsonToObject(byte[] json) throws JsonException {
    try {
      Ensure.notNull(json, "json cannot be null");
      final JsonStreamReader jsonStreamReader = new JsonStreamReader(json);
      final JsonLexer jsonLexer = new JsonLexer(jsonStreamReader);
      final JsonTokenQueue jsonTokenQueue = jsonLexer.lexJson();
      if (jsonTokenQueue.isEmpty()) {
        return JsonNode.NULL_NODE;
      }
      final JsonNode jsonNode = jsonParser.parse(jsonTokenQueue);
      if (!jsonTokenQueue.isEmpty()) {
        throw new JsonException("JSON token queue is not empty after parsing");
      }
      return jsonNode;
    } catch (Exception e) {
      throw new JsonException(e);
    }
  }

  public JsonNode jsonToObject(@Language("JSON") String json) throws JsonException {
    Ensure.notBlank(json, "json cannot be blank");
    final byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
    return jsonToObject(bytes);
  }
}
