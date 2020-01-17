package com.hypertech.im.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.hypertech.im.config.Redis;

@Configuration
public class WebMvcInterceptor implements WebMvcConfigurer {

//    @Autowired
//    private WebApplicationContext applicationContext;

	@Autowired
	private Redis redis;
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	RequestInterceptor interceptor = new RequestInterceptor();
    	interceptor.setRedis(redis);
        registry.addInterceptor(interceptor).addPathPatterns("/**");
    }
}
