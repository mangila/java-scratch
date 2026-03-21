package io.github.mangila.java2.model;

public class HttpRequestBuilder {

  private HttpMethod method;
  private String path;
  private final HttpVersion version;
  private HttpHeaders headers;
  private String body;

  public HttpRequestBuilder() {
    this.version = HttpVersion.HTTP_1_1;
  }

  public HttpRequestBuilder body(String body) {
    this.body = body;
    return this;
  }

  public HttpRequest build() {
    return new HttpRequest(method, path, version, headers, body);
  }

  public HttpRequestBuilder headers(HttpHeaders headers) {
    this.headers = headers;
    return this;
  }

  public HttpRequestBuilder method(HttpMethod method) {
    this.method = method;
    return this;
  }

  public HttpRequestBuilder path(String path) {
    this.path = path;
    return this;
  }
}
