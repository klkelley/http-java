package me.karakelley.http.helpers;

import java.io.*;
import java.net.Socket;

public class EchoClient {

  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;

  public void connect(String host, int port) throws IOException {
    clientSocket = new Socket(host, port);
    out = new PrintWriter(clientSocket.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  }

  public String sendMessage(String message) throws IOException {
    out.println(message);
    return in.readLine();
  }
}
