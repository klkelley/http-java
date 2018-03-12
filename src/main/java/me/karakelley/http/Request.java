package me.karakelley.http;

import me.karakelley.http.utility.LineReader;

import java.util.HashMap;
import java.util.Map;

public class Request {
  private final LineReader reader;
  private String requestMethod;
  private String requestPath;
  private String requestProtocol;
  Map headers = new HashMap<String, String>();

  public Request(LineReader reader) {
    this.reader = reader;
    setRequestLine();
    setHeaders();
  }

  public String getMethod() {
    return requestMethod;
  }

  public String getPath() {
    return requestPath;
  }

  public String getProtocol() {
    return requestProtocol;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getHeader(String header) {
    return (String) headers.get(header);
  }

  private void setHeaders() {
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

  private void setRequestLine() {
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

  public boolean validRequestLine() {
    try {
      return requestMethod.equals("GET") && requestProtocol.equals("HTTP/1.1");
    } catch (NullPointerException e) {
      return false;
    }
  }
}
