package me.karakelley.http.handlers.staticfilestrategies;

import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.http.responses.Conflict;
import me.karakelley.http.http.responses.Created;
import me.karakelley.http.http.responses.NoContent;
import me.karakelley.http.server.Handler;

import java.io.IOException;

public class UpdateResourceStrategy implements Handler {
  private PublicDirectory publicDirectory;

  public UpdateResourceStrategy(PublicDirectory publicDirectory) {
    this.publicDirectory = publicDirectory;
  }

  @Override
  public Response respond(Request request) {
    Response response = null;
    try {
      if (publicDirectory.resourceExists(request.getPath())) {
        publicDirectory.updateFileContents(request.getPath(), request.getBody());
        response = new NoContent();
      } else {
        response = createNewResource(response, request);
      }
    } catch (Exception e) {
      response = new Conflict();
    }
    return response;
  }


  private Response createNewResource(Response response, Request request) throws IOException {
    publicDirectory.createFile(request.getPath(), request.getBody());
    response = new Created();
    response.setHeaders("Location", publicDirectory.getPath(request.getPath()).toString());
    return response;
  }
}