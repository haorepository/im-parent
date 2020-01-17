package com.hypertech.im;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.junit.Test;

import com.hypertech.im.jwt.SaltUtil;

import cn.hutool.crypto.digest.DigestUtil;

public class HutoolUtilTest {
	
	@Test
	public void digester(){
		
//		String s = DigestUtil.md5Hex("123456");
//		
//		System.out.println(s);
		//e10adc3949ba59abbe56e057f20f883e
		String uuid = UUID.randomUUID().toString().replaceAll("-","");
		StringBuffer nonceBuffer = new StringBuffer(uuid);
		try {
			nonceBuffer.append(SaltUtil.createSalt());
			System.out.println(nonceBuffer);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String nonce = DigestUtil.md5Hex(nonceBuffer.toString());
		System.out.println(nonce);
		if(!false && !false){
			System.out.println(123);
		}
	}
}
