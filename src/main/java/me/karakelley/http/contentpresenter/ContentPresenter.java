package me.karakelley.http.contentpresenter;

import me.karakelley.http.filesystem.PublicDirectory;

import java.io.File;

public interface ContentPresenter {
  String displayResources(File fileName, PublicDirectory publicDirectory);
}
