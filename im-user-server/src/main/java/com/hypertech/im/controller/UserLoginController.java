package com.hypertech.im.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import com.hypertech.im.entity.UserInfo;
import com.hypertech.im.service.UserService;
import com.hypertech.im.vo.ResultDataVO;

/**
 * @author terry
 */
@RestController
@RequestMapping(value="/user")
public class UserLoginController {
	
	@Autowired(required=true)
	private UserService userService;
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public String login(String name,String password){
		if(userService.login(name, password)){
			UserInfo user = new UserInfo();
			user.setUserId("123456");
			user.setUserName("admin");
			return new Gson().toJson(user);
		}else{
			return new Gson().toJson(null);
		}
	}
	
	@RequestMapping(value="/query",method=RequestMethod.POST)
	public String queryAll(){
		ResultDataVO<List<UserInfo>> data = new ResultDataVO<List<UserInfo>>();
		List<UserInfo> list = userService.queryAll();
		data.setData(list);
		data.setCode(200);
		data.setMsg("Query all data success");
		data.setStatus("success");
		return new Gson().toJson(data);
	}
}
