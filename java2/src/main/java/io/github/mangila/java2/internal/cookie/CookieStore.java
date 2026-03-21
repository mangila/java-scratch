package io.github.mangila.java2.internal.cookie;

import io.github.mangila.java2.model.HttpHeaders;
import io.github.mangila.java2.model.HttpRequest;
import io.github.mangila.java2.model.HttpResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CookieStore {

  private final Map<String, Cookie> cookieJar;

  public CookieStore() {
    this.cookieJar = new ConcurrentHashMap<>();
  }

  public void add(Cookie cookie) {
    final String name = cookie.name();
    cookieJar.put(name, cookie);
  }

  public HttpRequest apply(HttpRequest httpRequest) {
    if (cookieJar.isEmpty()) {
      return httpRequest;
    }
    HttpHeaders httpHeaders =
        httpRequest.headers().toBuilder()
            .addHeader(
                "cookie",
                cookieJar.values().stream()
                    .map(Cookie::asCookieHeader)
                    .collect(Collectors.joining("; ")))
            .build();
    return httpRequest.toBuilder().headers(httpHeaders).build();
  }

  public void apply(HttpResponse response) {
    response
        .headers()
        .getCookies()
        .forEach(
            setCookie -> {
              Cookie c = Cookie.from(setCookie);
              add(c);
            });
  }

  public boolean hasCookies() {
    return !cookieJar.isEmpty();
  }
}
