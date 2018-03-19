package me.karakelley.http.controllers;

import me.karakelley.http.Response;
import me.karakelley.http.Status;

public class InvalidRequestController implements Controller {

  public Response respond() {
    Response response = new Response();
    response.setStatus(Status.NOT_FOUND);
    return response;
  }
}
