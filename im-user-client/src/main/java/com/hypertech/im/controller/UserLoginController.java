package com.hypertech.im.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.hypertech.im.config.Redis;
import com.hypertech.im.entity.UserInfo;
import com.hypertech.im.jwt.AESSecretUtil;
import com.hypertech.im.jwt.JWTUtil;
import com.hypertech.im.jwt.JwtHelper;
import com.hypertech.im.jwt.SecretKeyUtil;
import com.hypertech.im.service.UserService;
import com.hypertech.im.vo.ResultDataVO;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;

@RestController
@RequestMapping(value="/user")
@SuppressWarnings(value="all")
public class UserLoginController {
	private static final Logger logger = 
			LoggerFactory.getLogger(UserLoginController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private Redis redis;
	
	@Value("${jwt.expiration.time}")
	private String expirationTime; 
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	@HystrixCommand(commandKey="loginKey",
	fallbackMethod="loginError",
	commandProperties={
			@HystrixProperty(name =HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS,value = "15000")
	})
	public String login(String name,String password,HttpServletResponse response){
		logger.info("用户名："+name);
		ResultDataVO<UserInfo> resultData = new ResultDataVO<UserInfo>();
		String userJson = userService.login(name, password);
		UserInfo u = new Gson().fromJson(userJson,UserInfo.class);
		String userId = AESSecretUtil.encryptToStr(u.getUserId(), SecretKeyUtil.ID_KEY);
		Map<String,Object> claims = new HashMap<String, Object>();
		claims.put("userId",userId);
		//生成token
		String token = JwtHelper.generateToken(claims, Long.valueOf(expirationTime));
		//String token = JWTUtil.creatJWT(claims,expirationTime);
		//redi缓存token
//		redis.set(AESSecretUtil.encryptToStr(
//				u.getUserId(), SecretKeyUtil.KEY), token,120);
		resultData.setData(u);
		resultData.setToken(token);
		response.setHeader("Authorization", token);
		return new Gson().toJson(resultData);
	}
	
	@RequestMapping(value="/query",method=RequestMethod.POST)
	@HystrixCommand(commandKey="queryAllKey",fallbackMethod="queryError")
	public String queryAll(){
		return userService.queryAll();
	}
	
	@RequestMapping(value="/fallback",method=RequestMethod.POST)
	public String fallback(){
		logger.info("into fallback test");
		ResultDataVO<String> resultData = new ResultDataVO<String>();
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}
		resultData.setData("fallback test");
		resultData.setCode(200);
		resultData.setMsg("fallback test");
		resultData.setStatus("success");
		return new Gson().toJson(resultData);
	}
	
	public String loginError(String name,String password,HttpServletResponse response){
		logger.info("服务暂时不可用");
		return "服务暂时不可用";
	}
	
	public String queryError(){
		logger.info("查询数据出错");
		return "查询数据出错";
	}
}
