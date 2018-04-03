package me.karakelley.http.helpers;

import me.karakelley.http.server.HttpServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientHelper {

  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;
  int count = 0;
  final int maxTries = 5;

  public void connectWithTry(String host, HttpServer server) throws InterruptedException {
    boolean connected = false;
    while (count < maxTries) {
      try {
        Thread.sleep(50);
        connected = connect(host, server.getPortNumber());
        if (connected == true) ;
        break;
      } catch (Exception ex) {
        count++;
        if (count > maxTries) {
          throw ex;
        }
      }
    }
  }

  public boolean connect(String host, int port) {
    try {
      clientSocket = new Socket(host, port);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public List<String> sendMessage(String message) {
    out.println(message);
    ArrayList<String> messages = new ArrayList<>();
    String content = "";
    try {
      while (in.ready() || content.length() == 0) {
        content += (char) in.read();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Arrays.asList(content.split("\r\n"));
  }
}
