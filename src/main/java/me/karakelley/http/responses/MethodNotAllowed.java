package me.karakelley.http.responses;

import me.karakelley.http.Response;

public class MethodNotAllowed extends Response {
  public String getStatus() {
    return "405 Method Not Allowed";
  }
}
