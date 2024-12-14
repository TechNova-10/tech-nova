package com.tech_nova.movementInfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

@EnableFeignClients
@SpringBootApplication
public class MovementInfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovementInfoApplication.class, args);
	}

}
