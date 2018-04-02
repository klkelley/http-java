package me.karakelley.http.responses;

import me.karakelley.http.Response;

public class Ok extends Response {
  public String getStatus() {
    return "200 OK";
  }
}
