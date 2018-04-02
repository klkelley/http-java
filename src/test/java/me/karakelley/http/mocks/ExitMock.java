package me.karakelley.http.mocks;

import me.karakelley.http.utility.Exit;

public class ExitMock implements Exit {
  public static int exitCalled = 0;

  public void exit(int status) {
    exitCalled += 1;
  }
}