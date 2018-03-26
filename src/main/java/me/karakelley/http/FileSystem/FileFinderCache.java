package me.karakelley.http.FileSystem;

import java.util.HashMap;
import java.util.Map;

public class FileFinderCache implements FileFinder {
  private final FileFinder fileFinder;
  private Map<String, Boolean> cache = new HashMap();

  public FileFinderCache(FileFinder fileFinder) {
    this.fileFinder = fileFinder;
  }

  @Override
  public boolean resourceExists(String path) {
    Boolean requestedFiles = cache.getOrDefault(path, null);
    if (requestedFiles == null) {
      cache.put(path, fileFinder.resourceExists(path));
    }
    return cache.get(path);
  }
}
