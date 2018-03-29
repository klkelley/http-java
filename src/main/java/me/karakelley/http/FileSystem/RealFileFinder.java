package me.karakelley.http.FileSystem;

import java.io.File;
import java.nio.file.Paths;

public class RealFileFinder implements FileFinder {
  @Override
  public boolean resourceExists(String path) {
    File resource = Paths.get(path).toFile();
    return (resource.exists()) && resource.isDirectory() || resource.isFile();
  }
}
