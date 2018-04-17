package me.karakelley.http.exit;

public class SystemExit implements Exit {
  public void exit(int status) {
    System.exit(status);
  }
}
