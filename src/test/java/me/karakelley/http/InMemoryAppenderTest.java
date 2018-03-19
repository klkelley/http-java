package me.karakelley.http;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.BasicStatusManager;
import me.karakelley.http.utility.InMemoryAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryAppenderTest {

  private LoggerContext context;
  private final InMemoryAppender inMemoryAppender = new InMemoryAppender();

  private LoggingEvent event;

  @BeforeEach
  void setUp() {
    context = new LoggerContext();
    context.setName("context");
    context.setStatusManager(new BasicStatusManager());
    inMemoryAppender.setContext(context);
    inMemoryAppender.setPrefix("test");
    event = new LoggingEvent("fcqn", context.getLogger("logger"), Level.INFO, "Test message", null, new Object[0]);
    context.start();
  }

  @AfterEach
  void tearDown() {
    context.stop();
    inMemoryAppender.stop();
  }

  @Test
  void testAppendsMessage() {
    inMemoryAppender.append(event);
    assertTrue(inMemoryAppender.getEvents().get(0).contains("[INFO] Test message"));
  }
}