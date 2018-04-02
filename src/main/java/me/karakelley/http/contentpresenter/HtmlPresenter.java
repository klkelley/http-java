package me.karakelley.http.contentpresenter;

import me.karakelley.http.filesystem.PublicDirectory;

import java.io.File;

public class HtmlPresenter implements ContentPresenter {

  public String displayResources(File fileName, PublicDirectory publicDirectory) {
    return "<p><a href=\"/" + publicDirectory.relativePath(fileName) + "\">" + fileName.getName() + "</a></p>";
  }
}
