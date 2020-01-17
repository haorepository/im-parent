package com.hypertech.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 *  @EnableDiscoveryClient和@EnableEurekaClient
 * 选其一即可ek则可选@EnableEurekaClient，其他
 * 则选择@EnableDiscoveryClient
 * @author     ： terry
 * @date       ： Created in 2020/1/15 14:49
 * @param:
 * @return：
 */
@EnableEurekaClient
@SpringBootApplication
public class ImGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(ImGatewayApplication.class, args);
	}
}
