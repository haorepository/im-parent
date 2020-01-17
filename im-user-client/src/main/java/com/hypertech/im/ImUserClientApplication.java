package com.hypertech.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

/*
 * @EnableDiscoveryClient和@EnableEurekaClient
 * 选其一即可ek则可选@EnableEurekaClient，其他
 * 则选择@EnableDiscoveryClient
 */
@EnableHystrix
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class ImUserClientApplication {
	public static void main(String[] args) {
		SpringApplication.run(ImUserClientApplication.class, args);
	}
}
