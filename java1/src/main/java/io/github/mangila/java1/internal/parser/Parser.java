package io.github.mangila.java1.internal.parser;

import io.github.mangila.java1.internal.JsonTokenQueue;
import io.github.mangila.java1.internal.parser.model.JsonParserException;

public sealed interface Parser<T> permits JsonArrayParser, JsonNodeParser, JsonObjectParser {

  T parse(JsonTokenQueue queue) throws JsonParserException;
}
