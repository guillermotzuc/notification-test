package com.gtzuc.projects.notification.discovery.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class NotificationDiscoveryServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationDiscoveryServerApplication.class, args);
	}

}
