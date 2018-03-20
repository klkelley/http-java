package me.karakelley.http.utility;

public class SystemExit implements Exit {
  public void exit(int status) {
    System.exit(status);
  }
}
