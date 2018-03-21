package me.karakelley.http;

import java.util.Map;
import java.util.stream.Collectors;

public class ResponseFormatter {
  private final static String PROTOCOL = "HTTP/1.1 ";
  private final static String CRLF = "\r\n";

  public static String build(String statusLine, Map<String, String> headers, String body) {
    StringBuilder response = new StringBuilder();
    response.append(formatStatusLine(statusLine));
    response.append(formatHeaders(headers));
    response.append(CRLF);
    response.append(body);

    return String.valueOf(response);
  }

  public static String formatHeaders(Map<String, String> headers) {
    return headers.entrySet()
            .stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue() + "\r\n")
            .collect(Collectors.joining());
  }

  public static String formatStatusLine(String statusLine) {
    return PROTOCOL + statusLine + "\r\n";
  }
}
