package me.karakelley.http.controllers;

import me.karakelley.http.PublicDirectory;
import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.Status;

import java.util.ArrayList;
import java.util.Collections;

public class StaticFilesController implements Controller {
  private final ArrayList availableActions = new ArrayList<>(Collections.singleton("GET"));

  public Response respond(Request request) {
    Response response = new Response();
    if (request.validRequestLine() && availableActions.contains(request.getMethod())) {
      return displayFilesResponse(response);
    } else {
      return new InvalidRequestController().respond(request);
    }
  }

  private Response displayFilesResponse(Response response) {
    response.setStatus(Status.OK);
    response.setBody(String.join("\n", PublicDirectory.getFiles()));
    response.setHeaders("Content-Type", "text/plain");
    response.setHeaders("Content-Length", String.valueOf(response.getBody().getBytes().length));
    return response;
  }
}
