package me.karakelley.http.controllers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.Status;

public class RootController implements Controller {
  private final Request request;
  private final String DEFAULT_RESPONSE = "Hello World";

  public RootController(Request request) {
    this.request = request;
  }

  public Response respond() {
    Response response = new Response();
    if (request.validRequestLine() && request.getMethod().equals("GET")) {
      response.setStatus(Status.OK);
      response.setHeaders("Content-Type", "text/plain");
      response.setHeaders("Content-Length", String.valueOf(DEFAULT_RESPONSE.getBytes().length));
      response.setBody(DEFAULT_RESPONSE);
    } else {
      response.setStatus(Status.NOT_FOUND);
    }
    return response;
  }
}
