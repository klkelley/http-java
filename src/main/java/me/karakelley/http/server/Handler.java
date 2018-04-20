package me.karakelley.http.server;

import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;

public interface Handler {
  Response respond(Request request);
}
