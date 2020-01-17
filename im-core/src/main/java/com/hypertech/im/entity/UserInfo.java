package com.hypertech.im.entity;

import java.io.Serializable;

/**
 * @author terry
 */
public class UserInfo implements Serializable{
	private static final long serialVersionUID = -5567212680430747449L;
	private String userId;
	private String userName;
	private int userAge;
	private int sex;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getUserAge() {
		return userAge;
	}
	public void setUserAge(int userAge) {
		this.userAge = userAge;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public UserInfo(String userId, String userName, int userAge, int sex) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.userAge = userAge;
		this.sex = sex;
	}
	public UserInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
