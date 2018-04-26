package me.karakelley.http.application.handlers;

import me.karakelley.http.server.http.responses.Ok;
import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.http.Response;
import me.karakelley.http.server.Handler;

public class HelloWorldHandler implements Handler {
  private final String DEFAULT_RESPONSE = "Hello World";
  private final String CONTENT_TYPE = "Content-Type";
  private final String MIME_TYPE = "text/plain";

  @Override
  public Response respond(Request request) {
      Response response = new Ok();
      response.setHeaders(CONTENT_TYPE, MIME_TYPE);
      response.setBody(DEFAULT_RESPONSE);
      return response;
  }
}
