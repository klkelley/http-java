package me.karakelley.http.server.filesystem;

public class PublicDirectoryNotADirectoryException extends RuntimeException {
  public PublicDirectoryNotADirectoryException(String message) {
    super(message);
  }
}
