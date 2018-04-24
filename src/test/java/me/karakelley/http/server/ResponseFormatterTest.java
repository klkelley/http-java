package me.karakelley.http.server;

import me.karakelley.http.http.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseFormatterTest {
  Response response;

  @BeforeEach
  void setUp() {
    response = new Response();
    response.setStatus("200 OK");
  }

  @Test
  void convertToBytes() {
    ResponseFormatter formatter = new ResponseFormatter(response);
    assertEquals(new String(formatter.convertToBytes()), "HTTP/1.1 200 OK\r\n\r\n");
  }

  @Test
  void testConvertToBytesWithHeaders() {
    response.setHeaders("Host", "test.com");
    ResponseFormatter formatter = new ResponseFormatter(response);
    assertEquals(new String(formatter.convertToBytes()), "HTTP/1.1 200 OK\r\nHost: test.com\r\n\r\n");
  }


  @Test
  void testAddContentLengthIfBodyPresent() {
    response.setHeaders("Host", "test.com");
    response.setBody("testing");
    ResponseFormatter formatter = new ResponseFormatter(response);
    assertEquals(new String(formatter.convertToBytes()), "HTTP/1.1 200 OK\r\nHost: test.com\r\nContent-Length: 7\r\n\r\ntesting");
  }

  @Test
  void testNoContentLengthIfNoBodyPresent() {
    ResponseFormatter formatter = new ResponseFormatter(response);
    assertEquals(new String(formatter.convertToBytes()), "HTTP/1.1 200 OK\r\n\r\n");
  }
}