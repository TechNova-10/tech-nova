package com.tech_nova.notificaion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class NotificaionApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificaionApplication.class, args);
	}

}
