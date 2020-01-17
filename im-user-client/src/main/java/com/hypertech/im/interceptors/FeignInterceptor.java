package com.hypertech.im.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.hypertech.im.config.Redis;
import com.hypertech.im.jwt.SecretKeyUtil;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Configuration
public class FeignInterceptor implements RequestInterceptor {
	
	@Autowired
	private Redis redis;

	@Override
	public void apply(RequestTemplate template) {
		//获取微服务调用认证的密钥
		String key = SecretKeyUtil.CLOUD_SECRET_KEY;
		template.header(key, (String)redis.get(key));
	}

}
