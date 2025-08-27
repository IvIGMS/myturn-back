package com.ivanfrias.myturn.notifications.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProps(Mail mail) {
  public record Mail(boolean isEmailMocked, String smtpFromName, String smtpFromEmail) {}
}
