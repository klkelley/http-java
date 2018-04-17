package me.karakelley.http.server;

import me.karakelley.http.http.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseFormatter {
  private final static String PROTOCOL = "HTTP/1.1 ";
  private final static String CRLF = "\r\n";
  private final Response response;
  private final String CONTENT_LENGTH = "Content-Length";

  public ResponseFormatter(Response response) {
    this.response = response;
  }

  public byte[] convertToBytes() {
    setContentLengthHeader();
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      out.write(formatStatusLine(response.getStatus()));
      out.write(formatHeaders(response.getHeaders()));
      out.write(CRLF.getBytes());
      out.write(response.getBody());

      out.close();
      return out.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private byte[] formatHeaders(Map<String, String> headers) {
    return headers.entrySet()
            .stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue() + CRLF)
            .collect(Collectors.joining()).getBytes();
  }

  private void setContentLengthHeader() {
    byte[] body = response.getBody();
    if (body.length > 0) {
      response.setHeaders(CONTENT_LENGTH, String.valueOf(body.length));
    }
  }

  private byte[] formatStatusLine(String statusLine) {
    return (PROTOCOL + statusLine + CRLF).getBytes();
  }
}
