package me.karakelley.http.application.presenters;

import me.karakelley.http.server.filesystem.PublicDirectory;
import me.karakelley.http.server.FilePresenter;
import me.karakelley.http.helpers.TempFilesHelper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlFilePresenterTest {

  @Test
  void displaysHtmlBoilerPlate() {
    TempFilesHelper.withTempDirectory(directory ->  {
      List<File> files = Collections.singletonList(TempFilesHelper.createTempFile(directory, "/test.txt").toFile());
      FilePresenter presenter = new HtmlFilePresenter(PublicDirectory.create(directory.toString()));
      String htmlPage = presenter.displayFiles(files);
      assertTrue(htmlPage.contains("<!DOCTYPE html>"));
      assertTrue(htmlPage.contains("<html lang=\"en\">"));
      assertTrue(htmlPage.contains("<meta charset=\"utf-8\">"));
      assertTrue(htmlPage.contains("<head>") && htmlPage.contains("</head>"));
    });
  }

  @Test
  void displaysFilesAsLinks() {
    TempFilesHelper.withTempDirectory(directory ->  {
      List<File> files = Collections.singletonList(TempFilesHelper.createTempFile(directory, "/test.txt").toFile());
      FilePresenter presenter = new HtmlFilePresenter(PublicDirectory.create(directory.toString()));
      String htmlPage = presenter.displayFiles(files);
      assertTrue(htmlPage.contains("<a href=\"/test.txt\">test.txt</a>"));
    });
  }
}
