package me.karakelley.http.server;

import me.karakelley.http.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseWriter {

  private final static Logger logger = LoggerFactory.getLogger(ResponseWriter.class);
  private final OutputStream out;
  private final Response response;

  ResponseWriter(OutputStream out, Response response) {
    this.out = out;
    this.response = response;
  }

  public void deliver() {
    try {
      out.write(formatResponse());
    } catch (IOException e) {
      logger.info("Ouch!", e);
    }
  }

  private byte[] formatResponse() {
    return new ResponseFormatter(response).convertToBytes();
  }
}
