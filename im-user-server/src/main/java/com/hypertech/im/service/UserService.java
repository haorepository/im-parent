package com.hypertech.im.service;

import java.util.List;

import com.hypertech.im.entity.UserInfo;

public interface UserService {
	boolean login(String name,String password);
	
	List<UserInfo> queryAll();
}
