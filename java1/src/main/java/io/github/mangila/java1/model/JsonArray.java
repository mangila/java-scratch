package io.github.mangila.java1.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JsonArray {

  public static final JsonArray EMPTY = new JsonArray();

  private final List<JsonNode> list = new ArrayList<>();

  public void add(JsonNode value) {
    list.add(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JsonArray jsonArray = (JsonArray) o;
    return Objects.equals(list, jsonArray.list);
  }

  public JsonNode get(int index) {
    return list.get(index);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(list);
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public int size() {
    return list.size();
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

  public List<JsonNode> values() {
    return Collections.unmodifiableList(list);
  }

  void toJson(StringBuilder sb) {
    sb.append('[');
    boolean first = true;
    for (JsonNode item : list) {
      if (!first) {
        sb.append(',');
      }
      if (item == null) {
        sb.append("null");
      } else {
        item.toJson(sb);
      }
      first = false;
    }
    sb.append(']');
  }
}
