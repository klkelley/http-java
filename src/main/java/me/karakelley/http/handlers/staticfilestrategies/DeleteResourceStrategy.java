package me.karakelley.http.handlers.staticfilestrategies;

import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.http.responses.NoContent;
import me.karakelley.http.server.Handler;

public class DeleteResourceStrategy implements Handler {
  private PublicDirectory publicDirectory;

  public DeleteResourceStrategy(PublicDirectory publicDirectory) {
    this.publicDirectory = publicDirectory;
  }

  @Override
  public Response respond(Request request) {
    publicDirectory.deleteResource(request.getPath());
    return new NoContent();
  }
}
