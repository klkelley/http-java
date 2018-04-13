package me.karakelley.http.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class RealFileFinder implements FileFinder {
  @Override
  public boolean resourceExists(String path) {
    File resource = null;
    try {
      resource = Paths.get(path).toFile().getCanonicalFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return (resource.exists()) && resource.isDirectory() || resource.isFile();
  }
}
