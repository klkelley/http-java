package me.karakelley.http.handlers;

import me.karakelley.http.filesystem.PublicDirectory;

import java.io.File;

public interface ContentPresenter {
  String displayResources(File fileName, PublicDirectory publicDirectory);
}
