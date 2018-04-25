package me.karakelley.http.server.handlers.staticfilestrategies;

import me.karakelley.http.server.filesystem.PublicDirectory;
import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.http.Response;
import me.karakelley.http.server.http.responses.NoContent;
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
