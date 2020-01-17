package com.hypertech.im.vo;

import java.io.Serializable;

public class ResultDataVO<T> implements Serializable{
	private static final long serialVersionUID = -2125555804173928827L;
	private Integer code;
	private String msg;
	private String status;
	private T data;
	private String token;
	private String userKey;
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getUserKey() {
		return userKey;
	}
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}
	@Override
	public String toString() {
		return "ResultDataVO [code=" + code + ", msg=" + msg + ", status=" + status + ", data=" + data + ", token="
				+ token + ", userKey=" + userKey + "]";
	}
	
	
}
