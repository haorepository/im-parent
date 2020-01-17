package com.hypertech.im.entity;

import java.io.Serializable;

/**
 * @author     ： terry
 * @date       ： Created in 2020/1/16 10:23
 * @param:
 * @return： 
 */
public class JWTEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String nonce;
	
	private String timestamp;
	
	private String sign;

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
}
