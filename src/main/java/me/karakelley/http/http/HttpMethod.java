package me.karakelley.http.http;

import java.util.Arrays;
import java.util.List;

public enum HttpMethod {
  GET,
  POST,
  PUT,
  DELETE;

  public static List<HttpMethod> modifiableMethods = Arrays.asList(PUT, DELETE, POST);

  public static HttpMethod fromString(String s) {
    switch (s) {
      case "GET":
        return GET;

      case "POST":
        return POST;

      case "PUT":
        return PUT;

      case "DELETE":
          return DELETE;

      default:
        throw new InvalidRequestException("ouch");
    }
  }
}
