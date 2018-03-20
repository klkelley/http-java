package me.karakelley.http;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PublicDirectory {
  public static class PublicDirectoryMissingException extends RuntimeException {}
  public static class PublicDirectoryNotADirectoryException extends RuntimeException {}

  public static PublicDirectory create(String path) {
    if (!Files.exists(Paths.get(path)))
      throw new PublicDirectoryMissingException();

    if (!Files.isDirectory(Paths.get(path)))
      throw new PublicDirectoryNotADirectoryException();

    return new PublicDirectory(path);
  }

  private static File documentRoot;
  private static String path;

  public PublicDirectory(String path) {
    this.documentRoot = Paths.get(path).toFile();
    this.path = path;
  }

  public static List<String> getFiles() {
    File[] files = documentRoot.listFiles();
    return Arrays.stream(files)
            .filter(File::isFile)
            .map(File::getName)
            .collect(Collectors.toList());
  }
}
