package io.github.mangila.java2.model;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public record HttpResponse(HttpResponseStatus status, HttpHeaders headers, ByteBuffer body) {

  public HttpResponse {
    if (body == null) {
      body = ByteBuffer.allocate(0).asReadOnlyBuffer();
    }
    body = body.asReadOnlyBuffer();
  }

  @Override
  public ByteBuffer body() {
    return body.duplicate();
  }

  public String bodyAsString() {
    return StandardCharsets.UTF_8.decode(body()).toString();
  }
}
