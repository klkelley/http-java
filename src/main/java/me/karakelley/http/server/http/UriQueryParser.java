package me.karakelley.http.server.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

class UriQueryParser {

  public static Map<String, List<String>> parse(String requestPath) {
    String queries = getQueryString(requestPath);
    if (queries == null) {
      return Collections.emptyMap();
    }

    return Arrays.stream(queries.split("&"))
            .map(query -> query.split("="))
            .collect(Collectors.toMap(pair -> decode(parsePair(pair, 0)),
                    pair -> Arrays.asList(decode(parsePair(pair, 1)))));

  }

  private static String getQueryString(String requestPath) {
    return createURI(requestPath).getRawQuery();
  }

  private static URI createURI(String requestPath) {
    try {
      return URI.create(requestPath);
    } catch (Exception e) {
      throw new InvalidRequestException("Invalid");
    }
  }

  private static String parsePair(String[] array, int index) {
    return index >= array.length ? null : array[index];
  }

  private static String decode(String encoded) {
    try {
      return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
