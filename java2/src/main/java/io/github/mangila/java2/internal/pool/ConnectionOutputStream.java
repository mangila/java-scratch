package io.github.mangila.java2.internal.pool;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ConnectionOutputStream extends BufferedOutputStream {

  public static final int BUFFER_SIZE = 16384;

  public ConnectionOutputStream(OutputStream out) {
    super(out, BUFFER_SIZE);
  }

  public void writeAndFlush(String httpRequest) throws IOException {
    final byte[] httpBytes = httpRequest.getBytes(StandardCharsets.UTF_8);
    super.write(httpBytes);
    super.flush();
  }
}
