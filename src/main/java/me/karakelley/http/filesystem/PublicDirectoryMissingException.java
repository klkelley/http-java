package me.karakelley.http.filesystem;

public class PublicDirectoryMissingException extends RuntimeException{
  public PublicDirectoryMissingException(String message) {
    super(message);
  }
}
