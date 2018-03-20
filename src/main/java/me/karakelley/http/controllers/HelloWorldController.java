package me.karakelley.http.controllers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.Status;

import java.util.ArrayList;
import java.util.Collections;

public class HelloWorldController implements Controller {
  private final ArrayList availableActions = new ArrayList<>(Collections.singleton("GET"));
  private final String DEFAULT_RESPONSE = "Hello World";

  @Override
  public Response respond(Request request) {
    if (availableActions.contains(request.getMethod()) && request.validRequestLine()) {
      Response response = new Response();
      response.setStatus(Status.OK);
      response.setHeaders("Content-Type", "text/plain");
      response.setHeaders("Content-Length", String.valueOf(DEFAULT_RESPONSE.getBytes().length));
      response.setBody(DEFAULT_RESPONSE);
      return response;
    } else {
      return new InvalidRequestController().respond(request);
    }
  }
}
