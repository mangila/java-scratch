package io.github.mangila.java1.internal.tokenizer.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public record JsonToken(JsonTokenType type, Object value) {

  public static final Map<JsonTokenType, JsonToken> JSON_TOKEN_MAP;

  static {
    Map<JsonTokenType, JsonToken> map = new EnumMap<>(JsonTokenType.class);
    map.put(JsonTokenType.WHITESPACE, new JsonToken(JsonTokenType.WHITESPACE, ""));
    map.put(JsonTokenType.OPEN_OBJECT, new JsonToken(JsonTokenType.OPEN_OBJECT, "{"));
    map.put(JsonTokenType.CLOSE_OBJECT, new JsonToken(JsonTokenType.CLOSE_OBJECT, "}"));
    map.put(JsonTokenType.OPEN_ARRAY, new JsonToken(JsonTokenType.OPEN_ARRAY, "["));
    map.put(JsonTokenType.CLOSE_ARRAY, new JsonToken(JsonTokenType.CLOSE_ARRAY, "]"));
    map.put(JsonTokenType.COMMA, new JsonToken(JsonTokenType.COMMA, ","));
    map.put(JsonTokenType.COLON, new JsonToken(JsonTokenType.COLON, ":"));
    map.put(JsonTokenType.NULL, new JsonToken(JsonTokenType.NULL, null));
    map.put(JsonTokenType.TRUE, new JsonToken(JsonTokenType.TRUE, true));
    map.put(JsonTokenType.FALSE, new JsonToken(JsonTokenType.FALSE, false));
    JSON_TOKEN_MAP = Collections.unmodifiableMap(map);
  }

  public boolean hasType(JsonTokenType type) {
    return type == this.type;
  }
}
