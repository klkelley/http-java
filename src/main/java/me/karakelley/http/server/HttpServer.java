package me.karakelley.http.server;

import me.karakelley.http.RequestParser;
import me.karakelley.http.handlers.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class HttpServer {
  private final int port;
  private final static Logger logger = LoggerFactory.getLogger(HttpServer.class);
  private final Handler handler;
  private final ConnectionHandler connectionHandler;
  private final RequestParser requestParser;
  private ServerSocket serverSocket;

  public HttpServer(ServerConfiguration serverConfiguration, RequestParser requestParser, ConnectionHandler connectionHandler) {
    this.requestParser = requestParser;
    this.connectionHandler = connectionHandler;
    this.port = serverConfiguration.getPort();
    this.handler = serverConfiguration.getHandler();
  }

  public void start() {
    try {
      serverSocket = new ServerSocket(port);
      logger.info("Started on port {}", getPortNumber());
      while (true) {
        Socket socket = serverSocket.accept();
        CompletableFuture.runAsync(() -> connectionHandler.startConnection(socket, handler, requestParser));
      }
    } catch (Exception e) {
      logger.info("Ouch!", e);
    } finally {
      shutDown();
    }
  }

  public Integer getPortNumber() {
    return serverSocket.getLocalPort();
  }

  private void shutDown() {
    if (serverSocket != null && !serverSocket.isClosed()) try {
      serverSocket.close();
    } catch (IOException e) {
      logger.info("Ouch!", e);
    }
  }
}

