package me.karakelley.http.server.http.responses;

import me.karakelley.http.server.http.Response;

public class MethodNotAllowed extends Response {
  public String getStatus() {
    return "405 Method Not Allowed";
  }
}
