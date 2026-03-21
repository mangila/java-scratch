package io.github.mangila.java2.internal.pool;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class ConnectionInputStream extends BufferedInputStream {

  public static final int EOF = -1;
  public static final int BUFFER_SIZE = 8192;

  public ConnectionInputStream(InputStream in) {
    super(in, BUFFER_SIZE);
  }
}
