package me.karakelley.http;

import java.util.Arrays;

public class RequestValidator {
  private String[] methods = new String[]{"GET", "POST"};

  public boolean isValid(Request request) {
    return hasAllRequiredItems(request) &&
            validProtocol(request) &&
            validMethod(request);
  }

  private boolean hasAllRequiredItems(Request request) {
    return request.getMethod() != null &&
            request.getPath() != null &&
            request.getProtocol() != null;
  }

  private boolean validProtocol(Request request) {
    return request.getProtocol().equals("HTTP/1.1");
  }

  private boolean validMethod(Request request) {
    return Arrays.asList(methods).contains(request.getMethod());
  }
}
