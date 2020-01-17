package com.hypertech.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


/*
 * @EnableDiscoveryClient和@EnableEurekaClient
 * 选其一即可ek则可选@EnableEurekaClient，其他
 * 则选择@EnableDiscoveryClient
 */
@EnableEurekaClient
@SpringBootApplication
public class ImUserServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ImUserServerApplication.class, args);
	}
}
