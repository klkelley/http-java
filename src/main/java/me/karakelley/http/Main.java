package me.karakelley.http;

class Main {
  public static void main(String[] args) {
    Integer port = new ServerConfiguration(args).getPort();
    HttpServer server = new HttpServer(port);
    server.start();
  }
}
