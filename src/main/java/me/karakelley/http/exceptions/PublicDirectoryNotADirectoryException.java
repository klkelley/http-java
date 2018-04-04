package me.karakelley.http.exceptions;

public class PublicDirectoryNotADirectoryException extends RuntimeException {
  public PublicDirectoryNotADirectoryException(String message) {
    super(message);
  }
}
