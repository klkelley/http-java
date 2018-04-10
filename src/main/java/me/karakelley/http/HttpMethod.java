package me.karakelley.http;

import me.karakelley.http.exceptions.InvalidRequestException;

public enum HttpMethod {
  GET,
  POST,
  PUT;

  public static HttpMethod fromString(String s) {
    switch (s) {
      case "GET":
        return GET;

      case "POST":
        return POST;

      case "PUT":
        return PUT;

      default:
        throw new InvalidRequestException("ouch");
    }
  }
}
