package me.karakelley.http.utility;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAppender extends AppenderBase<ILoggingEvent> {
  private final ArrayList<String> events = new ArrayList<>();
  private String prefix;

  @Override
  public void append(ILoggingEvent eventObject) {
    if (eventObject.getThrowableProxy() != null) {
      events.add(eventObject.getThrowableProxy().getMessage());
    } else events.add(eventObject.toString());
  }

  public List<String> getEvents() {
    return events;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

}
