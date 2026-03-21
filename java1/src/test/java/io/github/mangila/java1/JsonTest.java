package io.github.mangila.java1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.mangila.java1.model.JsonException;
import io.github.mangila.java1.model.JsonNode;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JsonTest {

  final Json json = new Json();

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/invalid/unclosed_object.json",
        "/invalid/unclosed_array.json",
        "/invalid/unclosed_string.json",
        "/invalid/trailing_comma_object.json",
        "/invalid/trailing_comma_array.json",
        "/invalid/missing_colon.json",
        "/invalid/missing_value.json",
        "/invalid/missing_key.json",
        "/invalid/unquoted_key.json",
        "/invalid/single_quoted_string.json",
        "/invalid/invalid_number_leading_zero.json",
        "/invalid/invalid_number_trailing_dot.json",
        "/invalid/invalid_number_multiple_dots.json",
        "/invalid/invalid_number_plus_sign.json",
        "/invalid/invalid_escape.json",
        "/invalid/invalid_unicode_escape.json",
        "/invalid/invalid_keyword_true.json",
        "/invalid/invalid_keyword_false.json",
        "/invalid/invalid_keyword_null.json",
        "/invalid/unexpected_token.json",
        "/invalid/multiple_values_without_comma.json",
        "/invalid/unescaped_newline_in_string.json",
        "/invalid/no_brackets.json"
      })
  void testInvalidJson(String resourcePath) {
    @Language("JSON")
    final String jsonString = ResourceUtil.readResource(resourcePath);
    assertThatThrownBy(() -> json.jsonToObject(jsonString)).isInstanceOf(JsonException.class);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/valid/empty_object.json",
        "/valid/primitives.json",
        "/valid/numbers_edge_cases.json",
        "/valid/escapes_and_unicode.json",
        "/valid/arrays_and_nesting.json",
        "/valid/whitespace_and_formatting.json",
        "/valid/special_keys.json",
        "/valid/deeply_nested.json",
        "/valid/top_level_array.json",
        "/valid/top_level_boolean.json",
        "/valid/top_level_null.json",
        "/valid/top_level_number.json",
        "/valid/top_level_string.json",
      })
  void testValidJson(String resourcePath) {
    @Language("JSON")
    final String readResource = ResourceUtil.readResource(resourcePath);
    JsonNode jsonNode = json.jsonToObject(readResource);
    assertThat(jsonNode).isNotNull();
    final String jsonString = jsonNode.toString();
    assertThat(jsonString).isNotBlank();
  }
}
