package com.hypertech.im.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;

/**
 * @author     ： terry
 * @date       ： Created in 2020/1/15 14:58
 * @param: 
 * @return： 
 */
@Configuration
public class WebMvcConfig extends WebFluxConfigurationSupport {
	private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);
	@Override
	public void addCorsMappings(CorsRegistry registry){
		super.addCorsMappings(registry);
		logger.info("WebMVC configuration : addCorsMappings");
        registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
	}
}
