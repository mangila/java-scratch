package io.github.mangila.java2.internal;

import io.github.mangila.java2.internal.pool.Connection;
import io.github.mangila.java2.internal.pool.ConnectionInputStream;
import io.github.mangila.java2.model.HttpHeaders;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HttpHeaderParser implements Parser<HttpHeaders> {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpHeaderParser.class);

  private final Connection connection;

  HttpHeaderParser(Connection connection) {
    this.connection = connection;
  }

  @Override
  public HttpHeaders parse() throws IOException {
    final List<String> headers = new ArrayList<>(32);
    final ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream(256);
    while (true) {
      String headerLine = readHeaderLine(lineBuffer);
      if (headerLine.isBlank()) {
        break;
      }
      headers.add(headerLine);
    }
    if (headers.isEmpty()) {
      throw new IllegalArgumentException("Empty headers");
    }
    if (headers.getFirst().isBlank()) {
      throw new IllegalArgumentException("Empty Status line");
    }
    return HttpHeaders.parse(headers);
  }

  private String readHeaderLine(ByteArrayOutputStream lineBuffer) throws IOException {
    lineBuffer.reset();
    while (true) {
      int byteRead = connection.read();
      if (byteRead == ConnectionInputStream.EOF) {
        throw new EOFException("Unexpected end of stream");
      }
      if (byteRead == '\n') {
        break;
      }
      if (byteRead != '\r') {
        lineBuffer.write(byteRead);
      }
    }
    return lineBuffer.toString(StandardCharsets.UTF_8);
  }
}
