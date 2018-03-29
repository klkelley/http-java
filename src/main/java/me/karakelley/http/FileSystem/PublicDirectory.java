package me.karakelley.http.FileSystem;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class PublicDirectory {

  private final FileFinder fileFinderProxy;
  public static class PublicDirectoryMissingException extends RuntimeException {}
  public static class PublicDirectoryNotADirectoryException extends RuntimeException {}

  public static PublicDirectory create(String path, FileFinder fileFinderProxy) {
    if (!Files.exists(Paths.get(path)))
      throw new PublicDirectoryMissingException();

    if (!Files.isDirectory(Paths.get(path)))
      throw new PublicDirectoryNotADirectoryException();

    return new PublicDirectory(path, fileFinderProxy);
  }

  private static File documentRoot;
  private static String path;

  private PublicDirectory(String path, FileFinder fileFinderProxy) {
    this.fileFinderProxy = fileFinderProxy;
    this.documentRoot = Paths.get(path).toFile();
    this.path = path;
  }

  public List<File> getDirectoriesAndFiles(String requestedResource) {
    File directory = Paths.get(documentRoot + requestedResource).toFile();
    if (resourceExists(requestedResource)) {
      return Arrays.stream(directory.listFiles())
              .filter(file -> file.isDirectory() || file.isFile())
              .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  public boolean resourceExists(String path) {
    return fileFinderProxy.resourceExists(documentRoot+path);
  }

  public String relativePath(File file) {
    return documentRoot.toURI().relativize(file.toURI()).getPath();
  }

}
