package io.github.mangila.java1.model;

import java.math.BigDecimal;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public record JsonNode(JsonType type, Object value) {

  public static final JsonNode NULL_NODE = new JsonNode(JsonType.NULL, null);

  private static final HexFormat HEX_FORMAT = HexFormat.of();
  private static final CharsetEncoder CHARSET_ENCODER = StandardCharsets.US_ASCII.newEncoder();

  public JsonObject asObject() {
    return (JsonObject) value;
  }

  public JsonArray asArray() {
    return (JsonArray) value;
  }

  public BigDecimal asBigDecimal() {
    return new BigDecimal(value.toString());
  }

  public long asLong() {
    return Long.parseLong(value.toString());
  }

  public double asDouble() {
    return Double.parseDouble(value.toString());
  }

  public Boolean asBoolean() {
    return (Boolean) value;
  }

  public boolean isNull() {
    return type == JsonType.NULL;
  }

  public String toJson() {
    StringBuilder sb = new StringBuilder();
    toJson(sb);
    return sb.toString();
  }

  public void toJson(StringBuilder sb) {
    switch (type) {
      case NULL -> sb.append("null");
      case BOOLEAN -> sb.append(asBoolean());
      case NUMBER -> sb.append(value);
      case STRING -> appendJsonString(sb);
      case OBJECT -> asObject().toJson(sb);
      case ARRAY -> asArray().toJson(sb);
    }
  }

  private void appendJsonString(StringBuilder sb) {
    sb.append('"');
    String string = asString();
    for (int i = 0; i < string.length(); i++) {
      char c = string.charAt(i);
      switch (c) {
        case '"' -> sb.append("\\\"");
        case '\\' -> sb.append("\\\\");
        case '\b' -> sb.append("\\b");
        case '\f' -> sb.append("\\f");
        case '\n' -> sb.append("\\n");
        case '\r' -> sb.append("\\r");
        case '\t' -> sb.append("\\t");
        default -> {
          if (Character.isISOControl(c) || !CHARSET_ENCODER.canEncode(c)) {
            sb.append("\\u");
            String hex = HEX_FORMAT.toHexDigits((short) c);
            sb.append(hex);
          } else {
            sb.append(c);
          }
        }
      }
    }
    sb.append('"');
  }

  @Override
  public String toString() {
    return toJson();
  }

  public String asString() {
    return value.toString();
  }
}
