package com.hypertech.im.interceptors;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import com.hypertech.im.config.Redis;
import com.hypertech.im.jwt.SecretKeyUtil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

public class RequestInterceptor implements HandlerInterceptor {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(RequestInterceptor.class);
	
	private Redis redis;
	
    public Redis getRedis() {
		return redis;
	}

	public void setRedis(Redis redis) {
		this.redis = redis;
	}



	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        logger.info("request vlidate");
    	String secretKey = request.getHeader(SecretKeyUtil.CLOUD_SECRET_KEY);
        if(StrUtil.isNotBlank(secretKey)){
            String key = (String)redis.get(SecretKeyUtil.CLOUD_SECRET_KEY);
            if(!StrUtil.isBlank(key) && secretKey.equals(key)){
                return true;
            }
        }
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        logger.info("illegal request");
        writer.write(JSONUtil.toJsonStr("illegal request"));
        return false;
    }

}
