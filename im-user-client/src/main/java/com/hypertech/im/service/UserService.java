package com.hypertech.im.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(value="im-user-server")
public interface UserService {
	
	@RequestMapping(value="/im-user-server/user/login",method=RequestMethod.POST)
	String login(@RequestParam("name")String name,@RequestParam("password")String password);
	
	@RequestMapping(value="/im-user-server/user/query",method=RequestMethod.POST)
	String queryAll();
}
