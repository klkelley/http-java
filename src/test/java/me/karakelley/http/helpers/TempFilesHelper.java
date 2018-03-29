package me.karakelley.http.helpers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class TempFilesHelper {

  public static void withTempDirectory(Consumer<Path> f) {
    try {
      Path path = Files.createTempDirectory("test");
      try {
        f.accept(path);
      } finally {
        Files.list(path).forEach(filePath -> {
          try {
            Files.delete(filePath);
          } catch (IOException ignored) {}
        });
        Files.delete(path);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Path createTempFile(Path directory) {
    try {
      return Files.createTempFile(directory, "test", ".temp");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
