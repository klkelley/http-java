package me.karakelley.http.server.http.responses;

import me.karakelley.http.server.http.Response;

public class MovedPermanently extends Response {
  public String getStatus() {
    return "301 Moved Permanently";
  }
}
