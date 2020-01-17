package com.hypertech.im.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 令牌桶
 * @author     ： terry
 * @date       ： Created in 2020/1/15 14:50
 * @param:
 * @return：
 */
//@Configuration
public class GatewayRoutes {

	//@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder){
		return routeLocatorBuilder.routes().route(
				route->route.path("/user/**").filters(
						filter->filter.stripPrefix(0)
				).uri("lb://im-user-client")
		).build();
	}
}
