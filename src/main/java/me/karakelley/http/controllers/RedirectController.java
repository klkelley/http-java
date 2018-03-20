package me.karakelley.http.controllers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.Status;

import java.util.ArrayList;
import java.util.Collections;

public class RedirectController implements Controller {
  private final ArrayList availableActions = new ArrayList<>(Collections.singleton("GET"));

  public Response respond(Request request) {
    Response response = new Response();

    if (request.validRequestLine() && availableActions.contains(request.getMethod())) {
      return setResponse(request, response);
    } else {
      return new InvalidRequestController().respond(request);
    }
  }

  private Response setResponse(Request request, Response response) {
    response.setStatus(Status.MOVED_PERMANENTLY);
    response.setHeaders("Location", "http://" + request.getHostAndPort() + "/");
    return response;
  }
}
