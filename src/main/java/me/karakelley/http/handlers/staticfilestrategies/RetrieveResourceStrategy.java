package me.karakelley.http.handlers.staticfilestrategies;

import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.handlers.FilePresenter;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.http.responses.NotFound;
import me.karakelley.http.http.responses.Ok;
import me.karakelley.http.server.Handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RetrieveResourceStrategy implements Handler {
  private final PublicDirectory publicDirectory;
  private final String CONTENT_TYPE = "Content-Type";
  private final String TEXT_HTML = "text/html";
  private final String ROOT_INDEX_FILE = "index.html";
  private FilePresenter filePresenter;

  public RetrieveResourceStrategy(PublicDirectory publicDirectory, FilePresenter filePresenter) {
    this.filePresenter = filePresenter;
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
    Optional<String> indexFile = lookForIndexFile(requestedResource);
    if (indexFile.isPresent()) {
      return serveFilesResponse(response, requestedResource + "/" +indexFile.get());
    }
    response.setBody(getFilesAndDirectoryLinks(requestedResource));
    response.setHeaders(CONTENT_TYPE, TEXT_HTML);
    return response;
  }

  private Response serveFilesResponse(Response response, String requestedResource) {
    response.setBody(publicDirectory.getFileContents(requestedResource));
    response.setHeaders(CONTENT_TYPE, publicDirectory.getMimeType(requestedResource));
    return response;
  }

  private String getFilesAndDirectoryLinks(String resource) {
    List<File> files = publicDirectory.getDirectoriesAndFiles(resource);
    ArrayList<File> resources = new ArrayList<>(files);
    return filePresenter.displayFiles(resources);
  }

  private Optional<String> lookForIndexFile(String requestedResource) {
    List<File> files = publicDirectory.getDirectoriesAndFiles(requestedResource);

    List<String> result = files.stream()
            .map(File::getName)
            .filter(fileName -> fileName.equals(ROOT_INDEX_FILE))
            .collect(Collectors.toList());
    return result.stream().findFirst();
  }
}
