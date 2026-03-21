package io.github.mangila.java1.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class JsonObject {

  public static final JsonObject EMPTY = new JsonObject();

  private final Map<String, JsonNode> tree = new LinkedHashMap<>();

  public boolean containsKey(String key) {
    return tree.containsKey(key);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JsonObject that = (JsonObject) o;
    return Objects.equals(tree, that.tree);
  }

  public JsonNode get(String key) {
    return tree.get(key);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(tree);
  }

  public boolean isEmpty() {
    return tree.isEmpty();
  }

  public void put(String key, JsonNode jsonNode) {
    tree.put(key, jsonNode);
  }

  public int size() {
    return tree.size();
  }

  public String toJson() {
    StringBuilder sb = new StringBuilder();
    toJson(sb);
    return sb.toString();
  }

  @Override
  public String toString() {
    return toJson();
  }

  public Map<String, JsonNode> values() {
    return Collections.unmodifiableMap(tree);
  }

  void toJson(StringBuilder sb) {
    sb.append('{');
    boolean first = true;
    for (Map.Entry<String, JsonNode> entry : tree.entrySet()) {
      if (!first) {
        sb.append(',');
      }
      sb.append("\"").append(entry.getKey()).append("\"");
      sb.append(':');
      if (entry.getValue() == null) {
        sb.append("null");
      } else {
        entry.getValue().toJson(sb);
      }
      first = false;
    }
    sb.append('}');
  }
}
