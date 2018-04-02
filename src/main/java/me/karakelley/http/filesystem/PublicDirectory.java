package me.karakelley.http.filesystem;

import me.karakelley.http.exceptions.PublicDirectoryMissingException;
import me.karakelley.http.exceptions.PublicDirectoryNotADirectoryException;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class PublicDirectory {

  public static PublicDirectory create(String path, FileFinder fileFinderProxy) {
    if (!Files.exists(Paths.get(path)))
      throw new PublicDirectoryMissingException("Directory does not exist!");

    if (!Files.isDirectory(Paths.get(path)))
      throw new PublicDirectoryNotADirectoryException("Not a directory!");

    return new PublicDirectory(path, fileFinderProxy);
  }

  private File documentRoot;
  private String path;
  private final FileFinder fileFinderCache;

  private PublicDirectory(String path, FileFinder fileFinderCache) {
    this.fileFinderCache = fileFinderCache;
    this.documentRoot = Paths.get(path).toFile();
    this.path = path;
  }

  public List<File> getDirectoriesAndFiles(String requestedResource) {
    File directory = getPath(requestedResource).toFile();
    if (resourceExists(requestedResource)) {
      return Arrays.stream(directory.listFiles())
              .filter(file -> file.isDirectory() || file.isFile())
              .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  public boolean resourceExists(String path) {
    return fileFinderCache.resourceExists(documentRoot+path);
  }

  public String relativePath(File file) {
    return documentRoot.toURI().relativize(file.toURI()).getPath();
  }

  public String getMimeType(String requestedResource) {
    File path = getPath(requestedResource).toFile();
    return URLConnection.guessContentTypeFromName(path.getName());
  }

  public boolean isFile(String requestedResource) {
    return getPath(requestedResource).toFile().isFile();
  }

  public byte[] getFileContents(String requestedResource) {
    Path file = getPath(requestedResource);
    try {
      return Files.readAllBytes(file);
    } catch (IOException e) {
      return "".getBytes();
    }
  }

  private Path getPath(String requestedResource) {
    return Paths.get(documentRoot + requestedResource);
  }
}
