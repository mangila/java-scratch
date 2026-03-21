package io.github.mangila.java2.internal.cookie;

import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public record Cookie(
    String name,
    String value,
    URI domain,
    String path,
    Instant expires,
    Long maxAge,
    boolean secure,
    boolean httpOnly,
    String sameSite) {

  public static Cookie from(String setCookie) {
    String name = null;
    String value = null;
    URI domain = null;
    String path = null;
    Instant expires = null;
    Long maxAge = null;
    boolean secure = false;
    boolean httpOnly = false;
    String sameSite = null;
    final String[] parts = setCookie.split(";");
    for (int i = 0; i < parts.length; i++) {
      final String cookiePart = parts[i];
      final String[] pair = cookiePart.split("=");
      if (i == 0) {
        name = pair[0].trim();
        value = pair[1].trim();
        continue;
      }
      final String attributeKey = pair[0].trim().toLowerCase(Locale.ROOT);
      final String attributeValue = pair.length == 1 ? "" : pair[1].trim();
      switch (attributeKey) {
        case "domain" -> domain = URI.create(attributeValue);
        case "path" -> path = attributeValue;
        case "max-age" -> {
          try {
            maxAge = Long.parseLong(attributeValue);
          } catch (NumberFormatException _) {
            // do nothing
          }
        }
        case "expires" -> {
          try {
            expires = Instant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(attributeValue));
          } catch (DateTimeParseException _) {
            // do nothing
          }
        }
        case "secure" -> secure = true;
        case "httponly" -> httpOnly = true;
        case "samesite" -> sameSite = attributeValue;
        default -> throw new IllegalArgumentException("Invalid cookie attribute: " + attributeKey);
      }
    }
    return new Cookie(name, value, domain, path, expires, maxAge, secure, httpOnly, sameSite);
  }

  public String asCookieHeader() {
    return name + "=" + value;
  }
}
