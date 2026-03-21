package io.github.mangila.java1;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ResourceUtil {

  public static String readResource(String resourceName) {
    try (InputStream inputStream = ResourceUtil.class.getResourceAsStream(resourceName)) {
      if (inputStream == null) {
        throw new IllegalArgumentException("Resource not found: " + resourceName);
      }
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read resource: " + resourceName, e);
    }
  }

  private ResourceUtil() {
    throw new UnsupportedOperationException("Utility class");
  }
}
