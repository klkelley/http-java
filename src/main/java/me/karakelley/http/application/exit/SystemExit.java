package me.karakelley.http.application.exit;

import me.karakelley.http.server.Exit;

public class SystemExit implements Exit {
  public void exit(int status) {
    System.exit(status);
  }
}
