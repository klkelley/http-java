package me.karakelley.http.exceptions;

public class PublicDirectoryMissingException extends RuntimeException{
  public PublicDirectoryMissingException(String message) {
    super(message);
  }
}
