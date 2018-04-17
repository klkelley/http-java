package me.karakelley.http;

import me.karakelley.http.http.Response;
import me.karakelley.http.server.ResponseFormatter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseFormatterTest {

  @Test
  void convertToBytes() {
    Response response = new Response();
    response.setStatus("200 OK");
    ResponseFormatter formatter = new ResponseFormatter(response);
    assertEquals(new String(formatter.convertToBytes()), "HTTP/1.1 200 OK\r\n\r\n");
  }

  @Test
  void testConvertToBytesWithHeaders() {
    Response response = new Response();
    response.setStatus("200 OK");
    response.setHeaders("Host", "test.com");
    ResponseFormatter formatter = new ResponseFormatter(response);
    assertEquals(new String(formatter.convertToBytes()), "HTTP/1.1 200 OK\r\nHost: test.com\r\n\r\n");
  }


  @Test
  void testAddContentLengthIfBodyPresent() {
    Response response = new Response();
    response.setStatus("200 OK");
    response.setHeaders("Host", "test.com");
    response.setBody("testing");
    ResponseFormatter formatter = new ResponseFormatter(response);
    assertEquals(new String(formatter.convertToBytes()), "HTTP/1.1 200 OK\r\nHost: test.com\r\nContent-Length: 7\r\n\r\ntesting");
  }

  @Test
  void testNoContentLengthIfNoBodyPresent() {
    Response response = new Response();
    response.setStatus("200 OK");
    ResponseFormatter formatter = new ResponseFormatter(response);
    assertEquals(new String(formatter.convertToBytes()), "HTTP/1.1 200 OK\r\n\r\n");
  }
}