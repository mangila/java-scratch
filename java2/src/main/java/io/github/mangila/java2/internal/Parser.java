package io.github.mangila.java2.internal;

import java.io.IOException;

public interface Parser<T> {

  T parse() throws IOException;
}
