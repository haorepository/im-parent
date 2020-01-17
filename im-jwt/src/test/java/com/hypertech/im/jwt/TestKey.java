package com.hypertech.im.jwt;

public class TestKey {
	
	public static void main(String[] args) {
		String uuid = "54321";
		long millis = System.currentTimeMillis();
		String data = uuid+"-"+millis;
		System.out.println(data);
		String b = AESSecretUtil.encryptToStr(data, SecretKeyUtil.NONCE_TIMESTAMP_KEY);
		System.out.println(b);
		String s = AESSecretUtil.decryptToStr(b, SecretKeyUtil.NONCE_TIMESTAMP_KEY);
		System.out.println(s);
	}
}
