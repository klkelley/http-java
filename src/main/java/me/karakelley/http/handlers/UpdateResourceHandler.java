package me.karakelley.http.handlers;


import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.responses.Conflict;
import me.karakelley.http.responses.Created;
import me.karakelley.http.responses.NoContent;

import java.io.IOException;

public class UpdateResourceHandler implements Handler {
  private PublicDirectory publicDirectory;

  public UpdateResourceHandler(PublicDirectory publicDirectory) {
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
