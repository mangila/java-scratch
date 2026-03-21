package io.github.mangila.java2.internal.cookie;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CookieTest {

  @Test
  void from() {
    String setCookie =
        "session_id=xyz123abc; Domain=example.com; Path=/dashboard; "
            + "Expires=Wed, 21 Oct 2026 07:28:00 GMT; Max-Age=3600; Secure; HttpOnly; SameSite=Lax";
    final Cookie cookie = Cookie.from(setCookie);
    assertNotNull(cookie);
    assertEquals("session_id", cookie.name());
    assertEquals("xyz123abc", cookie.value());
    assertEquals(URI.create("example.com"), cookie.domain());
    assertEquals("/dashboard", cookie.path());
    assertEquals(Instant.parse("2026-10-21T07:28:00Z"), cookie.expires());
    assertEquals(3600, cookie.maxAge());
    assertTrue(cookie.secure());
    assertTrue(cookie.httpOnly());
    assertEquals("Lax", cookie.sameSite());
  }
}
