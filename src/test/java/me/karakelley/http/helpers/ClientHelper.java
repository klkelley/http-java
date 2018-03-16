package me.karakelley.http.helpers;

import me.karakelley.http.utility.BufferedLineReader;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHelper {

  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;
  int count = 0;
  int maxTries = 4;

  public void connectWithTry(String host, Integer port) throws InterruptedException, IOException {
    try {
      Thread.sleep(10);
      connect(host, port);
    } catch (Exception ex) {
      count++;
      if (count > maxTries) {
        throw ex;
      }
    }
  }

  public void connect(String host, int port) throws IOException {
    clientSocket = new Socket(host, port);
    out = new PrintWriter(clientSocket.getOutputStream(), true);
    in = new BufferedLineReader(new InputStreamReader(clientSocket.getInputStream()));
  }

  public ArrayList<String> sendMessage(String message) throws IOException {
    out.println(message);
    ArrayList<String> messages = new ArrayList<>();
    String lines;
    while ((lines = in.readLine()) != null) {
      messages.add(lines);
    }
    return messages;
  }
}
