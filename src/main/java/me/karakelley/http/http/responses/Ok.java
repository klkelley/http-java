package me.karakelley.http.http.responses;

import me.karakelley.http.http.Response;

public class Ok extends Response {
  public String getStatus() {
    return "200 OK";
  }
}
