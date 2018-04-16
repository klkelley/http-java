package me.karakelley.http.handlers;

import me.karakelley.http.contentpresenter.HtmlPresenter;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Response;
import me.karakelley.http.http.Request;
import me.karakelley.http.server.Handler;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StaticFilesHandlerTest {

  @Test
  void testGetDisplayFiles() {
    TempFilesHelper.withTempDirectory(directory -> {
      TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createTempFile(directory, "/test2");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new StaticFilesHandler(publicDirectory, new HtmlPresenter());
      Response response = handler.respond(new Request.Builder()
              .setMethod(HttpMethod.GET)
              .setPath("/")
              .setProtocol("HTTP/1.1")
              .setPort(0)
              .build());

      assertTrue(new String(response.getBody()).split("", 2).length == 2);
    });
  }

  @Test
  void testCreatesLinksForDirectories() {
    TempFilesHelper.withTempDirectory(directory -> {
      TempFilesHelper.createTempFile(directory, "/test1");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new StaticFilesHandler(publicDirectory, new HtmlPresenter());
      Response response = handler.respond(new Request.Builder()
              .setMethod(HttpMethod.GET)
              .setPath("/")
              .setProtocol("HTTP/1.1")
              .setPort(0)
              .build());

      assertTrue(new String(response.getBody()).contains("<p><a href=\"/test1.txt\">test1.txt</a></p>"));
    });
  }

  @Test
  void testServesTextFile() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello World", file);

      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      ContentPresenter contentPresenter = new HtmlPresenter();
      Handler handler = new StaticFilesHandler(publicDirectory, contentPresenter);
      Response response = handler.respond(new Request.Builder()
              .setMethod(HttpMethod.GET)
              .setPath("/test1.txt")
              .setProtocol("HTTP/1.1")
              .setPort(0)
              .build());

      assertEquals("Hello World", new String(response.getBody()));
    });
  }
}

