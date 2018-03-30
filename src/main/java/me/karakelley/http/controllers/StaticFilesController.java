package me.karakelley.http.controllers;

import me.karakelley.http.ContentGeneration.ContentGenerator;
import me.karakelley.http.FileSystem.PublicDirectory;
import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.Status;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StaticFilesController implements Controller {

  private final ArrayList availableActions = new ArrayList<>(Collections.singleton("GET"));
  private final ContentGenerator contentGenerator;
  private final PublicDirectory publicDirectory;

  public StaticFilesController(PublicDirectory publicDirectory, ContentGenerator contentGenerator) {
    this.contentGenerator = contentGenerator;
    this.publicDirectory = publicDirectory;
  }

  public Response respond(Request request) {
    String requestedResource = request.getPath();

    Response response = new Response();
    if (request.validRequestLine() && availableActions.contains(request.getMethod())) {
      return serveResponse(response, requestedResource);
    } else {
      return new InvalidRequestController().respond(request);
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
    response.setStatus(Status.OK);
    response.setBody(String.join("", getFilesAndDirectoryLinks(requestedResource)));
    response.setHeaders("Content-Type", "text/html");
    response.setHeaders("Content-Length", String.valueOf(response.getBody().length));
    return response;
  }

  private Response serveFilesResponse(Response response, String requestedResource) {
    response.setStatus(Status.OK);
    response.setBody(publicDirectory.getFileContents(requestedResource));
    response.setHeaders("Content-Type", publicDirectory.getMimeType(requestedResource));
    response.setHeaders("Content-Length", String.valueOf(response.getBody().length));
    return response;
  }

  private ArrayList<String> getFilesAndDirectoryLinks(String resource) {
    List<File> files = publicDirectory.getDirectoriesAndFiles(resource);
    ArrayList<String> resources = new ArrayList<>();
    for (File file : files) {
      resources.add(contentGenerator.displayResources(file, publicDirectory));
    }
    return resources;
  }
}

