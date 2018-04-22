package me.karakelley.http.handlers;

import java.io.File;
import java.util.List;

public interface FilePresenter {
  String displayFiles(List<File> paths);
}
