package me.karakelley.http.server;

import me.karakelley.http.exceptions.InvalidRequestException;
import me.karakelley.http.Request;
import me.karakelley.http.RequestParser;
import me.karakelley.http.Response;
import me.karakelley.http.responses.BadRequest;
import me.karakelley.http.handlers.Handler;
import me.karakelley.http.utility.BufferedLineReader;
import me.karakelley.http.utility.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler {
  private final static Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

  public void startConnection(Socket clientSocket, Handler handler, RequestParser requestParser) {
    try (LineReader reader = new BufferedLineReader(new InputStreamReader(clientSocket.getInputStream()));
         OutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
      handleRequest(reader, out, handler, requestParser, clientSocket);
    } catch (Exception e) {
      logger.info("Ouch!", e);
    }
  }

  private void handleRequest(LineReader reader, OutputStream out, Handler handler, RequestParser requestParser, Socket clientSocket) {
    Response response;
    try {
      Request request = requestParser.parse(reader).buildRequest(clientSocket.getLocalPort());
      response = handler.respond(request);
      new ResponseWriter(out, response).deliver();
    } catch (InvalidRequestException e) {
      new ResponseWriter(out, new BadRequest()).deliver();
    }
  }
}
