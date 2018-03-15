package me.karakelley.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

class ClientHandler {
  private final Socket clientSocket;
  Logger logger = LoggerFactory.getLogger(ClientHandler.class);

  public ClientHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void start() {
    CompletableFuture.runAsync(() -> {
      try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
           BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
        echoBack(in, out);
      } catch (IOException e) {
        logger.info(e.getMessage());
      }
    });
  }

  private void echoBack(BufferedReader in, PrintWriter out) throws IOException {
    String input;
    while ((input = in.readLine()) != null) {
      out.println("echo: " + input);
    }
  }
}
