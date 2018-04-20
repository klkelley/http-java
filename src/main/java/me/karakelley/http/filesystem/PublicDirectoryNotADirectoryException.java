package me.karakelley.http.filesystem;

public class PublicDirectoryNotADirectoryException extends RuntimeException {
  public PublicDirectoryNotADirectoryException(String message) {
    super(message);
  }
}
