package com.ivanfrias.myturn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableScheduling
public class MyturnApplication {

  public static void main(String[] args) {
    SpringApplication.run(MyturnApplication.class, args);
  }
}
