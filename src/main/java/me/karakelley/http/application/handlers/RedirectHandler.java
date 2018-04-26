package me.karakelley.http.application.handlers;

import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.http.Response;
import me.karakelley.http.server.http.responses.MovedPermanently;
import me.karakelley.http.server.Handler;

public class RedirectHandler implements Handler {

  public Response respond(Request request) {
    Response response = new MovedPermanently();
    return setResponse(request, response);
  }

  private Response setResponse(Request request, Response response) {
    response.setHeaders("Location", "http://" + request.getHostAndPort() + "/");
    return response;
  }
}
