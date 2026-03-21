package io.github.mangila.java2.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import org.brotli.dec.BrotliInputStream;

public final class CompressionUtil {

  public static byte[] decompress(String contentEncoding, byte[] content) throws IOException {
    final ByteArrayInputStream contentAsStream = new ByteArrayInputStream(content);
    return switch (contentEncoding) {
      case "gzip" -> Gzip.decompress(contentAsStream);
      case "deflate" -> Deflate.decompress(contentAsStream);
      case "br" -> Brotli.decompress(contentAsStream);
      default -> content;
    };
  }

  private CompressionUtil() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static final class Brotli {

    public static byte[] decompress(ByteArrayInputStream contentAsStream) throws IOException {
      try (final BrotliInputStream brotliInputStream = new BrotliInputStream(contentAsStream)) {
        return brotliInputStream.readAllBytes();
      }
    }

    private Brotli() {
      throw new UnsupportedOperationException("Utility class");
    }
  }

  public static final class Deflate {

    public static byte[] decompress(ByteArrayInputStream contentAsStream) throws IOException {
      try (final InflaterInputStream inflaterInputStream =
          new InflaterInputStream(contentAsStream)) {
        return inflaterInputStream.readAllBytes();
      }
    }

    private Deflate() {
      throw new UnsupportedOperationException("Utility class");
    }
  }

  public static final class Gzip {

    public static byte[] decompress(ByteArrayInputStream contentAsStream) throws IOException {
      try (final GZIPInputStream gzipInputStream = new GZIPInputStream(contentAsStream)) {
        return gzipInputStream.readAllBytes();
      }
    }

    private Gzip() {
      throw new UnsupportedOperationException("Utility class");
    }
  }
}
