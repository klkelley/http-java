package me.karakelley.http.controllers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.Status;

public class RedirectController implements Controller {

  private final Request request;

  public RedirectController(Request request) {
    this.request = request;
  }

  public Response respond() {
    Response response = new Response();

    if (request.validRequestLine() && request.getMethod().equals("GET")) {
      setResponse(response);
    } else {
      response.setStatus(Status.NOT_FOUND);
    }
    return response;
  }

  public Response setResponse(Response response) {
    response.setStatus(Status.MOVED_PERMANENTLY);
    response.setHeaders("Location", "http://" + findHostAndPort() + "/");
    return response;
  }

  public String findHostAndPort() {
    String host = findHost();
    if (!host.contains(":")) {
      host += ":" + request.getPort();
    }
    return host;
  }

  private String findHost() {
    String host = request.getHeaders().get("Host");
    if (host == null) {
      host = "localhost";
    }
    return host;
  }
}
