package me.karakelley.http.presenters;

import me.karakelley.http.handlers.ListPresenter;

import java.util.List;
import java.util.Map;

public class HtmlListPresenter implements ListPresenter {
  @Override
  public String displayList(Map<String, List<String>> query) {
    StringBuilder htmlPage = new StringBuilder();
    htmlPage.append("<!DOCTYPE html>");
    htmlPage.append("<html lang=\"en\">");
    htmlPage.append("<head>");
    htmlPage.append("<meta charset=\"utf-8\">");
    htmlPage.append("</head>");
    htmlPage.append("<body>");
    htmlPage.append("<dl>");
    for (Map.Entry<String, List<String>> entry : query.entrySet()) {
      htmlPage.append("<dt>" + entry.getKey() + "</dt><dd>" + entry.getValue().get(0) + "</dd>");
    }
    htmlPage.append("</dl>");
    htmlPage.append("</body>");
    htmlPage.append("</html>");
    return htmlPage.toString();
  }
}
