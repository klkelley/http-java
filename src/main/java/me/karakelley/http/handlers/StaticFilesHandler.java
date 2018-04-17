package me.karakelley.http.handlers;

import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.http.responses.NotFound;
import me.karakelley.http.http.responses.Ok;
import me.karakelley.http.server.Handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StaticFilesHandler implements Handler {
  private final ContentPresenter contentPresenter;
  private final PublicDirectory publicDirectory;
  private final String CONTENT_TYPE = "Content-Type";
  private final String TEXT_HTML = "text/html";

  public StaticFilesHandler(PublicDirectory publicDirectory, ContentPresenter contentPresenter) {
    this.contentPresenter = contentPresenter;
    this.publicDirectory = publicDirectory;
  }

  public Response respond(Request request) {
    String requestedResource = request.getPath();
    Response response;
    if (publicDirectory.resourceExists(requestedResource)) {
      response = new Ok();
      return serveResponse(response, requestedResource);
    } else {
      return new NotFound();
    }
  }

  private Response serveResponse(Response response, String requestedResource) {
    if (publicDirectory.isFile(requestedResource)) {
      return serveFilesResponse(response, requestedResource);
    } else {
      return serveDirectoryResponse(response, requestedResource);
    }
  }

  private Response serveDirectoryResponse(Response response, String requestedResource) {
    response.setBody(String.join("", getFilesAndDirectoryLinks(requestedResource)));
    response.setHeaders(CONTENT_TYPE, TEXT_HTML);
    return response;
  }

  private Response serveFilesResponse(Response response, String requestedResource) {
    response.setBody(publicDirectory.getFileContents(requestedResource));
    response.setHeaders(CONTENT_TYPE, publicDirectory.getMimeType(requestedResource));
    return response;
  }

  private ArrayList<String> getFilesAndDirectoryLinks(String resource) {
    List<File> files = publicDirectory.getDirectoriesAndFiles(resource);
    ArrayList<String> resources = new ArrayList<>();
    for (File file : files) {
      resources.add(contentPresenter.displayResources(file, publicDirectory));
    }
    return resources;
  }
}

