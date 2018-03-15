package me.karakelley.http;

class Main {
  public static void main(String[] args) {
    Integer port = new ServerConfiguration(args).getPort();
    Server server = new Server(port);
    server.start();
  }
}
