package me.karakelley.http.server.filesystem;

public class PublicDirectoryMissingException extends RuntimeException{
  public PublicDirectoryMissingException(String message) {
    super(message);
  }
}
