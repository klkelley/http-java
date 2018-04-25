package me.karakelley.http.server;

import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.http.Response;

public interface Handler {
  Response respond(Request request);
}
