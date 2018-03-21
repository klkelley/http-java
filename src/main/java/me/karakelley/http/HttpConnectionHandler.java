package me.karakelley.http;

import me.karakelley.http.controllers.Controller;
import me.karakelley.http.utility.BufferedLineReader;
import me.karakelley.http.utility.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

class HttpConnectionHandler {
  private final Socket clientSocket;
  final Logger logger = LoggerFactory.getLogger(HttpConnectionHandler.class);

  public HttpConnectionHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void start() {
    CompletableFuture.runAsync(() -> {
      try (LineReader reader = new BufferedLineReader(new InputStreamReader(clientSocket.getInputStream()));
           PrintWriter out = new PrintWriter(clientSocket.getOutputStream())) {
        Request newRequest = new Request(reader, clientSocket.getLocalPort());
        sendResponse(newRequest, out);
      } catch (Exception e) {
        logger.info("Ouch!", e);
      }
    });
  }

  public void sendResponse(Request newRequest, PrintWriter out) {
    Controller controller = Router.dispatch(newRequest);
    Response response = controller.respond();
    out.write(response.deliver());
  }
}
