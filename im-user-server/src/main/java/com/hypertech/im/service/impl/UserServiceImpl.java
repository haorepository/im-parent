package com.hypertech.im.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.hypertech.im.entity.UserInfo;
import com.hypertech.im.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Override
	public boolean login(String name, String password) {
		if(StringUtils.equals(name,"admin") && StringUtils.equals(password,"admin") ){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public List<UserInfo> queryAll() {
		List<UserInfo> list = new ArrayList<UserInfo>();
		for (int i=0; i<5; i++) {
			UserInfo user = new UserInfo("userid"+i,"name"+i,18+i,1);
			list.add(user);
		}
		return list;
	}

}
