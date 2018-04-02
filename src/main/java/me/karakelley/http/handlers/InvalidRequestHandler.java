package me.karakelley.http.handlers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.responses.NotFound;

public class InvalidRequestHandler implements Handler {

  public Response respond(Request request) {
    Response response = new NotFound();
    return response;
  }
}
