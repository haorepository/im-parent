package com.hypertech.im.entity;

import java.io.Serializable;

/**
 * @author terry
 */
public class JWTInfo implements Serializable{
	private static final long serialVersionUID = 3529536911692354635L;
	private String userId;
	private String userName;
	private String role;
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@Override
	public String toString() {
		return "JWTInfo [userId=" + userId + ", userName=" + userName + ", role=" + role + "]";
	}
}
