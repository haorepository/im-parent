package com.hypertech.im;

import org.junit.Test;

import cn.hutool.crypto.digest.DigestUtil;

public class HutoolUtilTest {
	
	@Test
	public void digester(){
		
		String s = DigestUtil.md5Hex("123456");
		
		System.out.println(s);
		//e10adc3949ba59abbe56e057f20f883e
	}
}
