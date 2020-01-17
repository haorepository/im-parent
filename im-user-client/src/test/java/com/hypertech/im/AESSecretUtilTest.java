package com.hypertech.im;

import org.junit.Test;

import com.hypertech.im.jwt.AESSecretUtil;
import com.hypertech.im.jwt.SecretKeyUtil;

public class AESSecretUtilTest {
	@Test
	public void encryptToStr(){
		String key = AESSecretUtil.encryptToStr("123456", SecretKeyUtil.ID_KEY);
		System.out.println(key);
	}
}
