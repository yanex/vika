package rimx.network;

import net.rim.device.api.servicebook.ServiceRecord;

public class URLFactory {
  private String url = "";
  private String protocol = "";
  private String host = "";
  private String port = "";
  private String absPath = "";
  private boolean isSecured = false;

  private int portBeginsAt = -1;
  private int absolutePathBeginsAt = -1;
  private int httpParamIndex = -1;

  public URLFactory(String url) {
    this.url = url.trim();

    if (this.url.indexOf("://") != -1) {
      protocol = this.url.substring(0, this.url.indexOf("://") + 3);
      this.url = this.url.substring(this.url.indexOf("://") + 3, this.url.length());
    } else {
      protocol = "http://";
    }
    protocol = protocol.toLowerCase();

    if (protocol.indexOf("https") != -1 || protocol.indexOf("ssl") != -1
        || protocol.indexOf("tls") != -1) {
      isSecured = true;
    }

    httpParamIndex = this.url.indexOf('?');

    if (httpParamIndex != -1 && this.url.indexOf(':') != -1
        && this.url.indexOf(':') < httpParamIndex) {
      portBeginsAt = this.url.indexOf(':');
    } else if (httpParamIndex == -1 && this.url.indexOf(':') != -1) {
      portBeginsAt = this.url.indexOf(':');
    }

    if (httpParamIndex != -1 && this.url.indexOf('/') != -1
        && this.url.indexOf('/') < httpParamIndex) {
      absolutePathBeginsAt = this.url.indexOf('/');
    } else if (httpParamIndex == -1 && this.url.indexOf('/') != -1) {
      absolutePathBeginsAt = this.url.indexOf('/');
    }

    if (portBeginsAt != -1) {
      host = this.url.substring(0, portBeginsAt);
    } else if (absolutePathBeginsAt != -1) {
      host = this.url.substring(0, absolutePathBeginsAt);
    } else {
      host = this.url.substring(0, this.url.length());
    }

    if (portBeginsAt != -1) {
      if (absolutePathBeginsAt != -1) {
        port = this.url.substring(portBeginsAt + 1, absolutePathBeginsAt);
      } else {
        port = this.url.substring(portBeginsAt + 1, this.url.length());
      }
    } else {
      if (isSecured) {
        port = "443";
      } else {
        port = "80";
      }
    }

    if (absolutePathBeginsAt != -1) {
      absPath = this.url.substring(absolutePathBeginsAt, this.url.length());
    } else {
      absPath = "/";
    }

  }

  public String getAbsolutePath() {
    return absPath;
  }

  public String getHost() {
    return host;
  }

  public String getHttpBisUrl() {
    if (isSecured()) {
      return "https://" + host + ":" + port + absPath
          + ";deviceside=false;ConnectionType=mds-public";
    } else {
      return "http://" + host + ":" + port + absPath
          + ";deviceside=false;ConnectionType=mds-public";
    }
  }

  public String getHttpBisUrl(ServiceRecord bisServiceRecord) {
    if (isSecured()) {
      return "https://" + host + ":" + port + absPath + ";deviceside=false" + ";ConnectionUID="
          + bisServiceRecord.getUid();
    } else {
      return "http://" + host + ":" + port + absPath + ";deviceside=false" + ";ConnectionUID="
          + bisServiceRecord.getUid();
    }
  }

  public String getHttpDefaultTcpCellularUrl(ServiceRecord tcpServiceRecord) {
    if (isSecured()) {
      return "https://" + host + ":" + port + absPath + ";deviceside=true" + ";ConnectionUID="
          + tcpServiceRecord.getUid();
    } else {
      return "http://" + host + ":" + port + absPath + ";deviceside=true" + ";ConnectionUID="
          + tcpServiceRecord.getUid();
    }
  }

  public String getHttpDefaultUrl() {
    if (isSecured()) {
      return "https://" + host + ":" + port + absPath;
    } else {
      return "http://" + host + ":" + port + absPath;
    }
  }

  public String getHttpMdsUrl(boolean e2eRequired) {
    String e2e = "";
    if (e2eRequired) {
      e2e = ";EndToEndRequired";
    }

    if (isSecured()) {
      return "https://" + host + ":" + port + absPath + ";deviceside=false" + e2e;
    } else {
      return "http://" + host + ":" + port + absPath + ";deviceside=false";
    }
  }

  public String getHttpTcpCellularUrl(String apn, String apnUser, String apnPassword) {
    if (apn != null && apn.length() > 0) {
      apn = ";apn=" + apn;
    } else {
      apn = "";
    }

    if (apnUser != null && apnUser.length() > 0) {
      apnUser = ";TunnelAuthUsername=" + apnUser;
    } else {
      apnUser = "";
    }

    if (apnPassword != null && apnPassword.length() > 0) {
      apnPassword = ";TunnelAuthPassword=" + apnPassword;
    } else {
      apnPassword = "";
    }

    if (isSecured()) {
      return "https://" + host + ":" + port + absPath + ";deviceside=true" + apn + apnUser
          + apnPassword;
    } else {
      return "http://" + host + ":" + port + absPath + ";deviceside=true" + apn + apnUser
          + apnPassword;
    }
  }

  public String getHttpTcpWiFiUrl() {
    if (isSecured()) {
      return "https://" + host + ":" + port + absPath + ";interface=wifi";
    } else {
      return "http://" + host + ":" + port + absPath + ";interface=wifi";
    }
  }

  public String getHttpWap1Url(String gatewayAPN, String gatewayIP, String gatewayPort,
                               String sourceIP, String sourcePort, String username, String password, boolean enableWTLS) {
    boolean paramsValid = true;
    String result;

    if (isSecured()) {
      result = "https://" + host + ":" + port + absPath + ";deviceside=true";
    } else {
      result = "http://" + host + ":" + port + absPath + ";deviceside=true";
    }

    if (gatewayAPN != null && gatewayAPN.length() > 0) {
      result = result + ";WapGatewayAPN=" + gatewayAPN;
    } else {
      paramsValid = false;
    }
    if (gatewayIP != null && gatewayIP.length() > 0) {
      result = result + ";WapGatewayIP=" + gatewayIP;
    } else {
      paramsValid = false;
    }
    if (gatewayPort != null && gatewayPort.length() > 0) {
      result = result + ";WapGatewayPort=" + gatewayPort;
    }
    if (sourceIP != null && sourceIP.length() > 0) {
      result = result + ";WapSourceIP=" + sourceIP;
    }
    if (sourcePort != null && sourcePort.length() > 0) {
      result = result + ";WapSourcePort=" + sourcePort;
    }
    if (username != null && username.length() > 0) {
      result = result + ";TunnelAuthUsername=" + username;
    }
    if (password != null && password.length() > 0) {
      result = result + ";TunnelAuthPassword=" + password;
    }
    if (enableWTLS) {
      result = result + ";WapEnableWTLS=true";
    }

    if (paramsValid) {
      return result;
    } else {
      return null;
    }
  }

  public String getHttpWap2Url(ServiceRecord wap2ServiceRecord) {
    if (isSecured()) {
      return "https://" + host + ":" + port + absPath + ";deviceside=true" + ";ConnectionUID="
          + wap2ServiceRecord.getUid();
    } else {
      return "http://" + host + ":" + port + absPath + ";deviceside=true" + ";ConnectionUID="
          + wap2ServiceRecord.getUid();
    }
  }

  public String getPort() {
    return port;
  }

  public String getProtocol() {
    return protocol;
  }

  public String getSocketBisUrl() {
    if (isSecured()) {
      return "ssl://" + host + ":" + port + ";deviceside=false;ConnectionType=mds-public";
    } else {
      return "socket://" + host + ":" + port + ";deviceside=false;ConnectionType=mds-public";
    }
  }

  public String getSocketDefaultTcpCellularUrl(ServiceRecord tcpServiceRecord) {
    if (isSecured()) {
      return "ssl://" + host + ":" + port + ";deviceside=true" + ";ConnectionUID="
          + tcpServiceRecord.getUid();
    } else {
      return "socket://" + host + ":" + port + ";deviceside=true" + ";ConnectionUID="
          + tcpServiceRecord.getUid();
    }
  }

  public String getSocketDefaultUrl() {
    if (isSecured()) {
      return "ssl://" + host + ":" + port;
    } else {
      return "socket://" + host + ":" + port;
    }
  }

  public String getSocketMdsUrl(boolean e2eRequired) {
    String e2e = "";
    if (e2eRequired) {
      e2e = ";EndToEndRequired";
    }

    if (isSecured()) {
      return "ssl://" + host + ":" + port + ";deviceside=false" + e2e;
    } else {
      return "socket://" + host + ":" + port + ";deviceside=false";
    }
  }

  public String getSocketTcpCellularUrl(String apn, String apnUser, String apnPassword) {
    if (apn != null && apn.length() > 0) {
      apn = ";apn=" + apn;
    } else {
      apn = "";
    }

    if (apnUser != null && apnUser.length() > 0) {
      apnUser = ";TunnelAuthUsername=" + apnUser;
    } else {
      apnUser = "";
    }

    if (apnPassword != null && apnPassword.length() > 0) {
      apnPassword = ";TunnelAuthPassword=" + apnPassword;
    } else {
      apnPassword = "";
    }

    if (isSecured()) {
      return "ssl://" + host + ":" + port + ";deviceside=true" + apn + apnUser + apnPassword;
    } else {
      return "socket://" + host + ":" + port + ";deviceside=true" + apn + apnUser + apnPassword;
    }
  }

  public String getSocketTcpWiFiUrl() {
    if (isSecured()) {
      return "ssl://" + host + ":" + port + ";interface=wifi";
    } else {
      return "socket://" + host + ":" + port + ";interface=wifi";
    }
  }

  public String getSocketWap1Url(String gatewayAPN, String gatewayIP, String gatewayPort,
                                 String sourceIP, String sourcePort, String username, String password, boolean enableWTLS) {
    boolean paramsValid = true;
    String result;

    if (isSecured()) {
      result = "ssl://" + host + ":" + port + ";deviceside=true";
    } else {
      result = "socket://" + host + ":" + port + ";deviceside=true";
    }

    if (gatewayAPN != null && gatewayAPN.length() > 0) {
      result = result + ";WapGatewayAPN=" + gatewayAPN;
    } else {
      paramsValid = false;
    }
    if (gatewayIP != null && gatewayIP.length() > 0) {
      result = result + ";WapGatewayIP=" + gatewayIP;
    } else {
      paramsValid = false;
    }
    if (gatewayPort != null && gatewayPort.length() > 0) {
      result = result + ";WapGatewayPort=" + gatewayPort;
    }
    if (sourceIP != null && sourceIP.length() > 0) {
      result = result + ";WapSourceIP=" + sourceIP;
    }
    if (sourcePort != null && sourcePort.length() > 0) {
      result = result + ";WapSourcePort=" + sourcePort;
    }
    if (username != null && username.length() > 0) {
      result = result + ";TunnelAuthUsername=" + username;
    }
    if (password != null && password.length() > 0) {
      result = result + ";TunnelAuthPassword=" + password;
    }
    if (enableWTLS) {
      result = result + ";WapEnableWTLS=true";
    }

    if (paramsValid) {
      return result;
    } else {
      return null;
    }
  }

  public String getSocketWap2Url(ServiceRecord wap2ServiceRecord) {
    if (wap2ServiceRecord == null) {
      return null;
    }

    if (isSecured()) {
      return "ssl://" + host + ":" + port + ";deviceside=true" + ";ConnectionUID="
          + wap2ServiceRecord.getUid();
    } else {
      return "socket://" + host + ":" + port + ";deviceside=true" + ";ConnectionUID="
          + wap2ServiceRecord.getUid();
    }
  }

  public String getUrl() {
    return url;
  }

  public boolean isSecured() {
    return isSecured;
  }

  public void setAbsPath(String absPath) {
    this.absPath = absPath;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public void setSecured(boolean isSecured) {
    this.isSecured = isSecured;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}
