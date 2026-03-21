package io.github.mangila.java1.internal;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public class JsonStreamReader implements AutoCloseable {

  public static final int END_OF_STREAM = -1;

  private final PushbackReader reader;
  private final int length;
  private long position;

  public JsonStreamReader(byte[] json) {
    this.reader =
        new PushbackReader(
            new InputStreamReader(new ByteArrayInputStream(json), StandardCharsets.UTF_8));
    this.length = json.length;
    position = 0;
  }

  @Override
  public void close() {
    try {
      reader.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public int getLength() {
    return length;
  }

  public long getPosition() {
    return position;
  }

  public int readCharacter() {
    try {
      final int characterRead = reader.read();
      position++;
      return characterRead;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public CharBuffer readCharacter(int length) {
    try {
      final CharBuffer buffer = CharBuffer.allocate(length);
      final int _ = reader.read(buffer.array(), 0, length);
      position += length;
      return buffer;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void unread(int character) {
    try {
      reader.unread(character);
      position--;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
