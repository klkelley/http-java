package me.karakelley.http;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

public class Appender extends AppenderBase<ILoggingEvent> {
  private ArrayList<String> events = new ArrayList<>();
  private String prefix;

  @Override
  protected void append(ILoggingEvent eventObject) {
    events.add(eventObject.toString());
  }

  public List<String> getEvents() {
    return events;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

}
