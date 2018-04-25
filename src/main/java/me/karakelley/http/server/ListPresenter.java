package me.karakelley.http.server;

import java.util.List;
import java.util.Map;

public interface ListPresenter {
  String displayList(Map<String, List<String>> query);
}
