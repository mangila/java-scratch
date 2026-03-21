package io.github.mangila.java1.internal.tokenizer;

import io.github.mangila.java1.internal.tokenizer.model.JsonToken;
import io.github.mangila.java1.internal.tokenizer.model.JsonTokenizeException;

public sealed interface Tokenizer
    permits JsonBooleanTokenizer,
        JsonNullTokenizer,
        JsonNumberTokenizer,
        JsonStringTokenizer,
        JsonTokenizer {

  JsonToken tokenize(int characterRead) throws JsonTokenizeException;
}
