package io.github.mangila.java2.internal.pool;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;

public class ConnectionOutputStream extends BufferedOutputStream {

  public static final int BUFFER_SIZE = 8192;

  public ConnectionOutputStream(OutputStream out) {
    super(out, BUFFER_SIZE);
  }

  @Override
  public void flush() throws IOException {
    super.flush();
  }

  @Override
  public void write(byte @NotNull [] b) throws IOException {
    super.write(b);
  }

  @Override
  public void write(byte @NotNull [] b, int off, int len) throws IOException {
    super.write(b, off, len);
  }

  public void writeAndFlush(String httpRequest) throws IOException {
    final byte[] httpBytes = httpRequest.getBytes(StandardCharsets.UTF_8);
    write(httpBytes);
    flush();
  }
}
