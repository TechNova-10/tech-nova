package com.tech_nova.hub;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableFeignClients
@SpringBootApplication
@EnableJpaAuditing
public class HubApplication {

  public static void main(String[] args) {
    SpringApplication.run(HubApplication.class, args);
  }

}
