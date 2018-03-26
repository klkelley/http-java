package me.karakelley.http.ContentGeneration;

import me.karakelley.http.FileSystem.PublicDirectory;

import java.io.File;

public interface ContentGenerator {
  String displayDirectories(File fileName, PublicDirectory publicDirectory);
  String displayFiles(String fileName);
}
