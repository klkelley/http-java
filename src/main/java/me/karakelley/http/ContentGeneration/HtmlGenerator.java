package me.karakelley.http.ContentGeneration;

import me.karakelley.http.FileSystem.PublicDirectory;

import java.io.File;

public class HtmlGenerator implements ContentGenerator {

  public String displayResources(File fileName, PublicDirectory publicDirectory) {
    return "<p><a href=\"/" + publicDirectory.relativePath(fileName) + "\">" + fileName.getName() + "</a></p>";
  }
}
