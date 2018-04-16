package me.karakelley.http.presenters;

import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.handlers.FilePresenter;

import java.io.File;
import java.util.List;

public class HtmlFilePresenter implements FilePresenter {

  private PublicDirectory publicDirectory;

  public HtmlFilePresenter(PublicDirectory publicDirectory) {
    this.publicDirectory = publicDirectory;
  }

  @Override
  public String displayFiles(List<File> paths) {
    StringBuilder htmlPage = new StringBuilder();
    htmlPage.append("<!DOCTYPE html>");
    htmlPage.append("<html lang=\"en\">");
    htmlPage.append("<head>");
    htmlPage.append("<meta charset=\"utf-8\">");
    htmlPage.append("</head>");
    htmlPage.append("<body>");
    for (File path : paths) {
      htmlPage.append("<p><a href=\"/" + publicDirectory.relativePath(path) + "\">" + path.getName() + "</a></p>");
    }
    htmlPage.append("</body>");
    htmlPage.append("</html>");
    return htmlPage.toString();
  }
}
