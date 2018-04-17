package me.karakelley.http.http.responses;

import me.karakelley.http.http.Response;

public class MethodNotAllowed extends Response {
  public String getStatus() {
    return "405 Method Not Allowed";
  }
}
