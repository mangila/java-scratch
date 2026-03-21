package io.github.mangila.java2.internal;

import static io.github.mangila.java2.internal.pool.ConnectionInputStream.BUFFER_SIZE;
import static io.github.mangila.java2.internal.pool.ConnectionInputStream.EOF;

import io.github.mangila.java2.internal.pool.Connection;
import io.github.mangila.java2.model.HttpHeaders;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class HttpBodyParser implements Parser<ByteBuffer> {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpBodyParser.class);
  private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);

  private static byte[] decompress(byte[] content) throws IOException {
    try (final ByteArrayInputStream bais = new ByteArrayInputStream(content);
        final GZIPInputStream gzipInputStream = new GZIPInputStream(bais)) {
      return gzipInputStream.readAllBytes();
    }
  }

  private final Connection connection;

  HttpBodyParser(Connection connection) {
    this.connection = connection;
  }

  @Override
  public ByteBuffer parse() {
    throw new UnsupportedOperationException("Not implemented");
  }

  public ByteBuffer parse(HttpHeaders headers) throws IOException {
    final String cl = headers.getContentLength();
    final int contentLength = cl == null ? 0 : Integer.parseInt(cl);
    final boolean gzip = headers.isGzip();
    final boolean chunked = headers.isChunked();
    byte[] content;
    if (chunked) {
      content = readChunked();
    } else {
      if (contentLength == 0) {
        return EMPTY_BUFFER;
      }
      content = connection.readNBytes(contentLength);
    }
    content = gzip ? decompress(content) : content;
    return ByteBuffer.wrap(content);
  }

  private byte[] readChunked() throws IOException {
    final ByteArrayOutputStream chunkBuffer = new ByteArrayOutputStream(BUFFER_SIZE);
    while (true) {
      int chunkSize = readChunkSize();
      LOGGER.debug("Read chunk size: {}", chunkSize);
      if (chunkSize == 0) {
        connection.skipNBytes(2);
        break;
      }
      byte[] chunk = connection.readNBytes(chunkSize);
      if (chunkSize != chunk.length) {
        throw new EOFException("Unexpected end of chunk");
      }
      chunkBuffer.write(chunk);
      connection.skipNBytes(2);
    }
    return chunkBuffer.toByteArray();
  }

  private int readChunkSize() throws IOException {
    int chunkSize = 0;
    while (true) {
      int byteRead = connection.read();
      if (byteRead == EOF) {
        throw new EOFException("Unexpected end of chunk");
      }
      if (byteRead == '\n') {
        break;
      }
      if (byteRead == '\r') {
        continue;
      }
      int digit = Character.digit((char) byteRead, 16);
      if (digit == -1) {
        throw new IllegalArgumentException("Invalid hex digit in chunk size: " + (char) byteRead);
      }
      chunkSize = (chunkSize << 4) | digit;
    }
    return chunkSize;
  }
}
