package me.karakelley.http.server.handlers;

import me.karakelley.http.server.Handler;
import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.http.Response;
import me.karakelley.http.server.http.responses.MethodNotAllowed;
import me.karakelley.http.server.http.responses.NotFound;
import me.karakelley.http.server.RequestMatcher;

import java.util.*;

public class Router implements Handler {
  private final Map<RequestMatcher, Handler> routes = new LinkedHashMap<>();

  @Override
  public Response respond(Request request) {
    boolean pathMatched = false;

    for (Map.Entry<RequestMatcher, Handler> entry : routes.entrySet()) {
      RequestMatcher matcher = entry.getKey();
      Handler handler = entry.getValue();
      if (matcher.pathMatches(request)) {
        pathMatched = true;
        if (matcher.methodMatches(request)) {
          return handler.respond(request);
        }
      }
    }

    if (pathMatched) {
      return new MethodNotAllowed();
    } else {
      return new NotFound();
    }
  }


  public Router route(RequestMatcher matcher, Handler handler) {
    routes.put(matcher, handler);
    return this;
  }
}
