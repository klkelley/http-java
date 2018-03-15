package me.karakelley.http;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.BasicStatusManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class AppenderTest {

  private LoggerContext context;
  private Appender appender = new Appender();

  private LoggingEvent event;

  @BeforeEach
  void setUp() {
    context = new LoggerContext();
    context.setName("context");
    context.setStatusManager(new BasicStatusManager());
    appender.setContext(context);
    appender.setPrefix("test");
    event = new LoggingEvent("fcqn", context.getLogger("logger"), Level.INFO, "Test message", null, new Object[0]);
    context.start();
  }

  @AfterEach
  void tearDown() {
    context.stop();
    appender.stop();
  }

  @Test
  void testAppendsMessage() {
    appender.append(event);
    assertTrue(appender.getEvents().get(0).contains("[INFO] Test message"));
  }
}