package io.github.mangila.java2.internal.pool;

import io.github.mangila.java2.internal.HttpUri;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connection {

  private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);

  private final HttpUri uri;
  private final boolean isHttps;
  private SocketAddress socketAddress;
  private Socket socket;
  private ConnectionInputStream inputStream;
  private ConnectionOutputStream outputStream;

  public Connection(HttpUri uri) {
    this.uri = uri;
    this.isHttps = uri.isHttps();
  }

  public void close() throws IOException {
    if (hasSocket()) {
      socket.close();
    }
  }

  public void create() throws IOException {
    final String host = uri.getHost();
    final int port = uri.getPort();
    socketAddress = new InetSocketAddress(host, port);
    socket = ConnectionSocketFactory.createSocket(isHttps);
  }

  public HttpUri getUri() {
    return uri;
  }

  public boolean hasSocket() {
    return socket != null;
  }

  public boolean isClosed() {
    return socket.isClosed();
  }

  public boolean isNew() {
    return !isOpen() && !isClosed();
  }

  public boolean isOpen() {
    return socket.isConnected() && !socket.isClosed();
  }

  public void open() throws IOException {
    socket.connect(socketAddress);
    inputStream = new ConnectionInputStream(socket.getInputStream());
    outputStream = new ConnectionOutputStream(socket.getOutputStream());
  }

  public int read() throws IOException {
    return inputStream.read();
  }

  public byte[] readNBytes(int length) throws IOException {
    return inputStream.readNBytes(length);
  }

  public void skipNBytes(int length) throws IOException {
    inputStream.skipNBytes(length);
  }

  @Override
  public String toString() {
    return socketAddress.toString();
  }

  public void write(String httpRequest) throws IOException {
    outputStream.writeAndFlush(httpRequest);
  }
}
