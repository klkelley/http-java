package me.karakelley.http.server;

import me.karakelley.http.http.Request;
import me.karakelley.http.http.InvalidRequestException;
import me.karakelley.http.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class HttpRequestReader {

  private HttpMethod method;
  private String path;
  private String protocol;
  private Map<String, String> headers;
  private String CRLF = "\r\n";

  private InputStream reader;
  private final static Logger logger = LoggerFactory.getLogger(HttpRequestReader.class);

  public HttpRequestReader(InputStream reader) {
    this.reader = reader;
  }

  public Request read(int port) {
    String rawRequest = "";
    try {
      rawRequest = readRequestLineAndHeaders();
    } catch (IOException e) {
      logger.info("Ouch!", e);
    }

    parseRequestLine(rawRequest);
    headers = parseHeaders(rawRequest);

    byte[] bodyBytes = new byte[0];
    try {
      bodyBytes = readBody();
    } catch (IOException e) {
      logger.info("Ouch!", e);
    }

    return new Request.Builder()
            .setMethod(method)
            .setPath(path)
            .setProtocol(protocol)
            .setHeaders(headers)
            .setBody(bodyBytes)
            .setPort(port)
            .build();
  }

  private String readRequestLineAndHeaders() throws IOException {
    String rawRequest = "";

    while (!rawRequest.endsWith(CRLF + CRLF)) {
      rawRequest += (char) (reader.read());
    }
    return rawRequest;
  }

  private byte[] readBody() throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    String bodyPresent = headers.get("Content-Length");
    byte[] bodyBytes;

    if (bodyPresent != null) {
      bodyBytes = new byte[Integer.parseInt(bodyPresent)];
      int content;
      while ((content = reader.read(bodyBytes, 0, bodyBytes.length)) != -1) {
        buffer.write(bodyBytes, 0, content);
        if (buffer.size() >= bodyBytes.length) break;
      }
    }
    return buffer.toByteArray();
  }

  private String validPath(String path) {
    if (path == null) throw new InvalidRequestException("Invalid Path!");
    return path;
  }

  private String validProtocol(String protocol) {
    if (!protocol.equals("HTTP/1.1")) throw new InvalidRequestException("Invalid Protocol!");
    return protocol;
  }

  private void parseRequestLine(String requestString) {
    try {
      int index = requestString.indexOf("\r\n");
      String rawRequestLine = requestString.substring(0, index);
      String[] requestLine = rawRequestLine.split(" ");

      this.method = HttpMethod.fromString(requestLine[0]);
      this.path = validPath(requestLine[1]);
      this.protocol = validProtocol(requestLine[2]);
    } catch (Exception e) {
      throw new InvalidRequestException("Invalid Request!");
    }
  }

  private Map<String, String> parseHeaders(String rawRequest) {
    String rawHeaders;
    String requestLine = rawRequest.trim();
    if (requestLine.contains(CRLF)) {
      int index = requestLine.indexOf(CRLF);
      rawHeaders = requestLine.substring(index);

      return Arrays.stream(rawHeaders.split(CRLF))
              .filter(header -> !header.isEmpty())
              .map(header -> Arrays.asList(header.split(":\\s+")))
              .collect(Collectors.toMap(line -> line.get(0), line -> line.get(1)));
    } else return new HashMap<>();
  }
}

