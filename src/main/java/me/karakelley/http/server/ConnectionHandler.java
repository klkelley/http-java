package me.karakelley.http.server;

import me.karakelley.http.server.http.InvalidRequestException;
import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.http.Response;
import me.karakelley.http.server.http.responses.BadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;

public class ConnectionHandler {
  private final static Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

  public void startConnection(Socket clientSocket, Handler handler, RequestReaderFactory readerFactory) {
    try (InputStream reader = new BufferedInputStream(clientSocket.getInputStream(), clientSocket.getSendBufferSize());
         OutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())) {
      HttpRequestReader requestReader = readerFactory.getReader(reader);
      handleRequest(requestReader, out, handler, clientSocket.getLocalPort());
    } catch (Exception e) {
      logger.info("Ouch!", e);
    }
  }

  private void handleRequest(HttpRequestReader reader, OutputStream out, Handler handler, int port) {
    Response response;
    try {
      Request request = reader.read(port);
      response = handler.respond(request);
      new ResponseWriter(out, response).deliver();
    } catch (InvalidRequestException e) {
      new ResponseWriter(out, new BadRequest()).deliver();
    }
  }
}
