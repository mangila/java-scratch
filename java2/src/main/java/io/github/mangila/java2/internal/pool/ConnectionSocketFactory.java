package io.github.mangila.java2.internal.pool;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.time.Duration;
import javax.net.SocketFactory;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConnectionSocketFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionSocketFactory.class);

  static {
    // System.setProperty("javax.net.debug", "all");
  }

  static Socket createSocket(boolean isHttps) throws IOException {
    final Socket socket;
    if (isHttps) {
      final SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
      final SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket();
      configureSslSocket(sslSocket);
      socket = sslSocket;
    } else {
      final SocketFactory socketFactory = SocketFactory.getDefault();
      socket = socketFactory.createSocket();
    }
    configureSocket(socket);
    return socket;
  }

  private static void configureSocket(Socket socket) throws SocketException {
    socket.setTcpNoDelay(true);
    socket.setKeepAlive(true);
    socket.setReuseAddress(true);
    socket.setSoTimeout((int) Duration.ofSeconds(5).toMillis());
    socket.setSoLinger(true, (int) Duration.ofSeconds(2).toSeconds());
  }

  private static void configureSslSocket(SSLSocket sslSocket) {
    sslSocket.setUseClientMode(true);
    final SSLParameters sslParameters = sslSocket.getSSLParameters();
    sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
    sslParameters.setProtocols(new String[] {"TLSv1.2", "TLSv1.3"});
    sslParameters.setApplicationProtocols(new String[] {"http/1.1"});
    sslSocket.setSSLParameters(sslParameters);
  }
}
