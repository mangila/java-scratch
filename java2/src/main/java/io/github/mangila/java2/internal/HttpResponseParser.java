package io.github.mangila.java2.internal;

import io.github.mangila.java2.internal.pool.Connection;
import io.github.mangila.java2.model.HttpHeaders;
import io.github.mangila.java2.model.HttpResponse;
import io.github.mangila.java2.model.HttpResponseStatus;
import java.io.*;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseParser implements Parser<HttpResponse> {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseParser.class);

  private final HttpHeaderParser httpHeaderParser;
  private final HttpBodyParser httpBodyParser;

  public HttpResponseParser(Connection connection) {
    this.httpHeaderParser = new HttpHeaderParser(connection);
    this.httpBodyParser = new HttpBodyParser(connection);
  }

  @Override
  public HttpResponse parse() throws IOException {
    HttpHeaders httpHeaders = httpHeaderParser.parse();
    HttpResponseStatus httpResponseStatus = httpHeaders.getStatusLine();
    ByteBuffer body = httpBodyParser.parse(httpHeaders);
    return new HttpResponse(httpResponseStatus, httpHeaders, body);
  }
}
