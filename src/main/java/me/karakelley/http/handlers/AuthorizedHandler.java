package me.karakelley.http.handlers;

import me.karakelley.http.http.Authorization;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.http.responses.Unauthorized;
import me.karakelley.http.server.Handler;

public class AuthorizedHandler implements Handler {
  private Handler authorizedHandler;
  private Authorization authorizer;

  public AuthorizedHandler(Handler authorizedHandler, Authorization authorizer) {
    this.authorizedHandler = authorizedHandler;
    this.authorizer = authorizer;
  }

  @Override
  public Response respond(Request request) {
    return authorizer.isAuthorized(request) ? authorizedHandler.respond(request) : new Unauthorized();
  }
}
