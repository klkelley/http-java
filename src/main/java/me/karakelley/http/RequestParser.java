package me.karakelley.http;

import me.karakelley.http.exceptions.InvalidRequestException;
import me.karakelley.http.utility.LineReader;

import java.util.HashMap;
import java.util.Map;

public class RequestParser {
  private final RequestValidator requestValidator;
  private Map<String, String> headers = new HashMap<>();
  private String requestMethod;
  private String requestPath;
  private String requestProtocol;

  public RequestParser(RequestValidator requestValidator) {
    this.requestValidator = requestValidator;
  }

  public RequestParser parse(LineReader reader) {
    parseRequestLine(reader);
    parseHeaders(reader);
    return this;
  }

  public Request buildRequest(int port) throws InvalidRequestException {
    Request request = RequestBuilder.build(requestMethod, requestPath, requestProtocol, headers, port);

    if (requestValidator.isValid(request)) {
      return request;
    } else throw new InvalidRequestException("Invalid request");
  }

  private void parseHeaders(LineReader reader) {
    try {
      String lines;
      while ((lines = reader.readLine()) != null && !lines.isEmpty()) {
        String[] splitHeaders = lines.split(":", 2);
        headers.put(splitHeaders[0], splitHeaders[1].trim());
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void parseRequestLine(LineReader reader) {
    String requestLine;
    String[] parsedLine;
    try {
      requestLine = reader.readLine();
      parsedLine = requestLine.replaceAll("\\s+", " ").split(" ");
      if (parsedLine.length == 3) {
        requestMethod = parsedLine[0];
        requestPath = parsedLine[1];
        requestProtocol = parsedLine[2];
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
