package me.karakelley.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;

class HttpServer {
  private final int port;
  private final Logger logger = LoggerFactory.getLogger("Server");
  private ServerSocket serverSocket;

  public HttpServer(int port) {
    this.port = port;
  }

  public void start() {
    try {
      serverSocket = new ServerSocket(port);
      logger.info("Started on port {}", getPortNumber());
      while (true)
        new HttpConnectionHandler(serverSocket.accept()).start();
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
    if (serverSocket != null && !serverSocket.isClosed()) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        logger.info("Ouch!", e);
      }
    }
  }
}

