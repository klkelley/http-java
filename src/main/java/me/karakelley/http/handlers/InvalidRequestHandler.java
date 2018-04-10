package me.karakelley.http.handlers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;

public class InvalidRequestHandler implements Handler {

  private final Response response;

  public InvalidRequestHandler(Response response) {
    this.response = response;
  }

  public Response respond(Request request) {
    return response;
  }
}
