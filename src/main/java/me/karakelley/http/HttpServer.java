package me.karakelley.http;

import me.karakelley.http.controllers.Controller;
import me.karakelley.http.utility.BufferedLineReader;
import me.karakelley.http.utility.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class HttpServer {
  private final int port;
  private final static Logger logger = LoggerFactory.getLogger(HttpServer.class);
  private final Controller controller;
  private ServerSocket serverSocket;

  public HttpServer(ServerConfiguration serverConfiguration) {
    this.port = serverConfiguration.getPort();
    this.controller = serverConfiguration.getController();
  }

  public void start() {
    try {
      serverSocket = new ServerSocket(port);
      logger.info("Started on port {}", getPortNumber());
      while (true) {
        Socket socket = serverSocket.accept();
        CompletableFuture.runAsync(() -> startConnection(socket));
      }
    } catch (Exception e) {
      logger.info("Ouch!", e);
    } finally {
      shutDown();
    }
  }

  private void startConnection(Socket clientSocket) {
    try (LineReader reader = new BufferedLineReader(new InputStreamReader(clientSocket.getInputStream()));
         OutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
      Request newRequest = new Request(reader, clientSocket.getLocalPort());
      sendResponse(newRequest, out);
    } catch (Exception e) {
      logger.info("Ouch!", e);
    }
  }

  private void sendResponse(Request newRequest, OutputStream out) {
    Response response = controller.respond(newRequest);
    try {
      out.write(response.convertToBytes());
    } catch (IOException e) {
      logger.info("Ouch!", e);
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

