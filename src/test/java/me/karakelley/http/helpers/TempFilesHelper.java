package me.karakelley.http.helpers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.function.Consumer;

public class TempFilesHelper {

  public static void withTempDirectory(Consumer<Path> f) {
    try {
      Path dir = Files.createDirectory(Paths.get("./src/testing"));
      try {
        f.accept(dir);
      } finally {
        Files.list(dir).forEach(filePath -> {
          try {
            Files.walk(filePath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
          } catch (IOException ignored) {
          }
        });
        Files.deleteIfExists(dir);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Path createTempFile(Path directory, String name) {
    try {
      return Files.createFile(Paths.get(directory + name + ".txt"));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static void createContents(String text, Path file) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(file)));
      writer.write(text);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
