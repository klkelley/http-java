package me.karakelley.http;

import java.io.*;
import java.net.ServerSocket;

import static java.lang.String.format;

class Server {
  private final int port;
  private ServerSocket serverSocket;
  private final ServerLogger logger;

  public Server(int port, ServerLogger logger) {
    this.port = port;
    this.logger = logger;
  }

  public void start() {
    try {
      serverSocket = new ServerSocket(port);
      logger.info("Started on port " + serverSocket.getLocalPort());
      while (true)
        new ClientHandler(serverSocket.accept()).start();
    } catch (IOException | IllegalArgumentException e) {
      logger.info(e.getMessage());
    } finally {
      shutDown();
    }
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

