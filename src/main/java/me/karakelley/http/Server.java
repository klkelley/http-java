package me.karakelley.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;

import static java.lang.String.format;

class Server {
  private final int port;
  private ServerSocket serverSocket;
  private final Logger logger = LoggerFactory.getLogger("Server");

  public Server(int port) {
    this.port = port;
  }

  public void start() {
    try {
      serverSocket = new ServerSocket(port);
      logger.info("Started on port {}", serverSocket.getLocalPort());
      while (true)
        new ClientHandler(serverSocket.accept()).start();
    } catch (IOException | IllegalArgumentException e) {
      logger.info(e.getMessage());
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
        logger.info(e.getMessage());
      }
    }
  }
}

