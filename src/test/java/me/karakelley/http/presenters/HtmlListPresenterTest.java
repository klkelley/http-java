package me.karakelley.http.presenters;

import me.karakelley.http.handlers.ListPresenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HtmlListPresenterTest {

  ListPresenter presenter;
  HashMap<String, List<String>> fakeQuery;

  @BeforeEach
  void setUp() {
    presenter = new HtmlListPresenter();
    fakeQuery = new HashMap<>();
    fakeQuery.put("hola", Arrays.asList("hello", "there"));
  }

  @Test
  void testDisplaysHtmlBoilerPlate() {
    String htmlPage = presenter.displayList(fakeQuery);

    assertTrue(htmlPage.contains("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\">"));
  }

  @Test
  void testDisplaysQuerysAsDetailedList() {
    String htmlPage = presenter.displayList(fakeQuery);

    assertTrue(htmlPage.contains("<body><dl><dt>hola</dt><dd>hello</dd></dl></body>"));
  }
}