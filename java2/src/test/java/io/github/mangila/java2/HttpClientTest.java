package io.github.mangila.java2;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.mangila.java2.model.*;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HttpClientTest {

  static final HttpClientConfig CONFIG =
      new HttpClientConfig(
          URI.create("https://httpbin.org"),
          new HttpClientConfig.ConnectionPoolConfig(
              1, Duration.ofSeconds(30), Executors.newSingleThreadScheduledExecutor()),
          Executors.newVirtualThreadPerTaskExecutor());

  static final HttpClient client = new HttpClient(CONFIG);

  @AfterAll
  static void after_all() {
    client.close();
  }

  @BeforeAll
  static void before_all() {
    client.init();
  }

  @Test
  void should_handle_chunked() {
    HttpHeaders headers =
        HttpHeaders.builder().accept("application/json").contentType("application/json").build();
    HttpRequest request =
        HttpRequest.builder().path("/stream/5").method(HttpMethod.GET).headers(headers).build();
    HttpResponse response = client.fetchAsync(request).join();
    assertThat(response.status().code()).isEqualTo(200);
    assertThat(response.headers().isChunked()).isTrue();
    assertThat(response.bodyAsString()).isNotBlank();
  }

  @Test
  void should_handle_gzip() {
    HttpHeaders headers =
        HttpHeaders.builder()
            .accept("application/json")
            .acceptEncoding("gzip")
            .contentType("application/json")
            .build();
    HttpRequest request =
        HttpRequest.builder().path("/gzip").method(HttpMethod.GET).headers(headers).build();
    HttpResponse response = client.fetchAsync(request).join();
    assertThat(response.status().code()).isEqualTo(200);
    assertThat(response.headers().isGzip()).isTrue();
    assertThat(response.bodyAsString()).isNotBlank();
  }

  @Test
  void should_post_body() {
    HttpHeaders headers =
        HttpHeaders.builder().accept("application/json").contentType("application/json").build();
    HttpRequest request =
        HttpRequest.builder()
            .path("/post")
            .method(HttpMethod.POST)
            .headers(headers)
            .body("{\"a\": \"b\"}")
            .build();
    HttpResponse response = client.fetchAsync(request).join();
    assertThat(response.status().code()).isEqualTo(200);
    assertThat(response.bodyAsString()).isNotBlank();
  }

  @Test
  void should_redirect() {}
}
