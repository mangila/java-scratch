package io.github.mangila.java2;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.mangila.java2.model.HttpHeaders;
import io.github.mangila.java2.model.HttpMethod;
import io.github.mangila.java2.model.HttpRequest;
import io.github.mangila.java2.model.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

class HttpClientTest {

  static final HttpClient client =
      HttpClient.builder()
          .host(URI.create("https://httpbin.org"))
          .followRedirect(true)
          .connectTimeout(Duration.ofSeconds(30))
          .idleTimeout(Duration.ofMinutes(1))
          .maxConnections(5)
          .executor(Executors.newVirtualThreadPerTaskExecutor())
          .build();

  @AfterAll
  static void after_all() {
    client.close();
  }

  @Test
  void should_handle_br() {
    HttpHeaders headers =
        HttpHeaders.builder()
            .accept("application/json")
            .acceptEncoding("br")
            .contentType("application/json")
            .build();
    HttpRequest request =
        HttpRequest.builder().path("/brotli").method(HttpMethod.GET).headers(headers).build();
    HttpResponse response = client.fetch(request);
    assertThat(response.status().is2xx()).isTrue();
    assertThat(response.headers().getContentEncoding()).isEqualTo("br");
    assertThat(response.bodyAsString()).isNotBlank().contains("\"brotli\": true,");
  }

  @Test
  void should_handle_chunked() {
    HttpHeaders headers =
        HttpHeaders.builder().accept("application/json").contentType("application/json").build();
    HttpRequest request =
        HttpRequest.builder().path("/stream/5").method(HttpMethod.GET).headers(headers).build();
    HttpResponse response = client.fetch(request);
    assertThat(response.status().is2xx()).isTrue();
    assertThat(response.headers().isChunked()).isTrue();
    assertThat(response.bodyAsString()).isNotBlank();
  }

  @Test
  void should_handle_deflate() {
    HttpHeaders headers =
        HttpHeaders.builder()
            .accept("application/json")
            .acceptEncoding("deflate")
            .contentType("application/json")
            .build();
    HttpRequest request =
        HttpRequest.builder().path("/deflate").method(HttpMethod.GET).headers(headers).build();
    HttpResponse response = client.fetch(request);
    assertThat(response.status().is2xx()).isTrue();
    assertThat(response.headers().getContentEncoding()).isEqualTo("deflate");
    assertThat(response.bodyAsString()).isNotBlank().contains("\"deflated\": true,");
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
    HttpResponse response = client.fetch(request);
    assertThat(response.status().is2xx()).isTrue();
    assertThat(response.headers().getContentEncoding()).isEqualTo("gzip");
    assertThat(response.bodyAsString()).isNotBlank().contains("\"gzipped\": true,");
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
    HttpResponse response = client.fetch(request);
    assertThat(response.status().is2xx()).isTrue();
    assertThat(response.bodyAsString()).isNotBlank().contains("\"a\": \"b\"");
  }

  @Test
  void should_redirect() {
    HttpHeaders headers =
        HttpHeaders.builder().accept("application/json").contentType("application/json").build();
    HttpRequest request =
        HttpRequest.builder()
            .path("/redirect-to?url=/get")
            .method(HttpMethod.GET)
            .headers(headers)
            .build();
    HttpResponse response = client.fetch(request);
    assertThat(response.status().is2xx()).isTrue();
    assertThat(response.bodyAsString()).isNotBlank();
  }

  @Test
  void should_set_cookies() {
    HttpHeaders headers = HttpHeaders.builder().build();
    HttpRequest request =
        HttpRequest.builder()
            .path("/cookies/set/hello/world")
            .method(HttpMethod.GET)
            .headers(headers)
            .build();
    HttpResponse response = client.fetch(request);
    assertThat(response.status().is2xx()).isTrue();
    assertThat(response.bodyAsString()).isNotBlank().contains("\"hello\": \"world\"");
  }
}
