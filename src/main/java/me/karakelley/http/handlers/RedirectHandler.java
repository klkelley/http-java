package me.karakelley.http.handlers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.responses.MovedPermanently;

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
