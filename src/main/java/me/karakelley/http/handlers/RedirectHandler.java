package me.karakelley.http.handlers;

import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.http.responses.MovedPermanently;
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
