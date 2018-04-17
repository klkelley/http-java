package me.karakelley.http.handlers;

import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.http.responses.Conflict;
import me.karakelley.http.http.responses.Created;
import me.karakelley.http.server.Handler;

public class NewResourceHandler implements Handler {

  private final PublicDirectory publicDirectory;

  NewResourceHandler(PublicDirectory publicDirectory) {
    this.publicDirectory = publicDirectory;
  }

  @Override
  public Response respond(Request request) {
    Response response;
    try {
      publicDirectory.createFile(request.getPath(), request.getBody());
      response = new Created();
      response.setHeaders("Location", publicDirectory.getPath(request.getPath()).toString());
    } catch (Exception e) {
      response = new Conflict();
    }
    return response;
  }
}
