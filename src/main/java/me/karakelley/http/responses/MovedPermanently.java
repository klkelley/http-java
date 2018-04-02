package me.karakelley.http.responses;

import me.karakelley.http.Response;

public class MovedPermanently extends Response {
  public String getStatus() {
    return "301 Moved Permanently";
  }
}
