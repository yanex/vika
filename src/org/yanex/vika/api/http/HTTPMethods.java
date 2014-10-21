package org.yanex.vika.api.http;

import org.yanex.vika.api.APIException;
import org.yanex.vika.api.ErrorCodes;
import org.yanex.vika.util.network.Network;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HTTPMethods {

  public static String get(String url) throws APIException {
    return HTTPMethods.http(url, "GET", null);
  }

  public static String post(String url, byte[] data) throws APIException {
    return HTTPMethods.http(url, "POST", data);
  }

  public static byte[] downloadFile(String url) {
    return HTTPMethods.download(url, "GET", null);
  }

  private static String http(String url, String method, byte[] data) throws APIException {
    url = url + LinkHelper.getBBLink(url);

    if (!Network.test()) {
      return null;
    }

    HttpConnection connection = null;
    try {
      connection = (HttpConnection)Connector.open(url);
      connection.setRequestMethod(method);

      if (method.equals("POST") && data != null) {
        writePostData(connection, data);
      }

      int code = connection.getResponseCode();

      if (isRedirect(code)) {
        String location = connection.getHeaderField("location").trim();
        connection.close();
        return HTTPMethods.http(location, method, data);
      } else if (code == HttpConnection.HTTP_UNAUTHORIZED) {
        throw new APIException(ErrorCodes.UNAUTHORIZED);
      } else if (!isOk(code)) {
        connection.close();
        return null;
      }

      return new String(readAndClose(connection).toByteArray(), "UTF-8");
    } catch (Exception e) {
      if (e instanceof APIException) {
        throw new APIException((APIException) e);
      }
      return null;
    } finally {
      closeConnection(connection);
    }
  }

  private static byte[] download(String url, String method, byte[] data) {
    url = url + LinkHelper.getBBLink(url);

    if (!Network.test()) {
      return null;
    }

    HttpConnection connection = null;
    try {
      connection = (HttpConnection) Connector.open(url);
      connection.setRequestMethod(method);

      if (method.equals("POST") && data != null) {
        writePostData(connection, data);
      }

      int code = connection.getResponseCode();

      if (isRedirect(code)) {
        String location = connection.getHeaderField("location").trim();
        connection.close();
        return HTTPMethods.downloadFile(location);
      } else if (!isOk(code)) {
        connection.close();
        return null;
      }

      return readAndClose(connection).toByteArray();
    } catch (Exception e) {
      return null;
    } finally {
      closeConnection(connection);
    }
  }

  private static void closeConnection(HttpConnection connection) {
    if (connection!=null) {
      try {
        connection.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static void writePostData(HttpConnection connection, byte[] data) throws IOException {
    connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
    OutputStream requestOutput = connection.openOutputStream();
    requestOutput.write(data);
    requestOutput.close();
  }

  private static ByteArrayOutputStream readAndClose(HttpConnection connection) throws IOException {
    InputStream responseData = connection.openInputStream();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    byte[] buffer = new byte[10000];
    int bytesRead = responseData.read(buffer);
    while (bytesRead > 0) {
      baos.write(buffer, 0, bytesRead);
      bytesRead = responseData.read(buffer);
    }
    baos.close();
    connection.close();

    return baos;
  }

  private static boolean isRedirect(int responseCode) {
    return responseCode == HttpConnection.HTTP_TEMP_REDIRECT
        || responseCode == HttpConnection.HTTP_MOVED_TEMP
        || responseCode == HttpConnection.HTTP_MOVED_PERM;
  }

  private static boolean isOk(int responseCode) {
    return (responseCode == HttpConnection.HTTP_OK
        || responseCode == HttpConnection.HTTP_ACCEPTED
        || responseCode == HttpConnection.HTTP_CREATED);
  }

}