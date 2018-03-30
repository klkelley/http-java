package me.karakelley.http.ContentGeneration;

import me.karakelley.http.FileSystem.PublicDirectory;

import java.io.File;

public interface ContentGenerator {
  String displayResources(File fileName, PublicDirectory publicDirectory);
}
