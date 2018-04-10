package me.karakelley.http.server;

import java.io.InputStream;

public class RequestReaderFactory {
  public HttpRequestReader getReader(InputStream inputStream) {
    return new HttpRequestReader(inputStream);
  }
}
