package me.karakelley.http.handlers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.responses.Conflict;
import me.karakelley.http.responses.Created;

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
