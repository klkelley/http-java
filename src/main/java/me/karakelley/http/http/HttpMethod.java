package me.karakelley.http.http;

public enum HttpMethod {
  GET,
  POST,
  PUT,
  DELETE;

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
