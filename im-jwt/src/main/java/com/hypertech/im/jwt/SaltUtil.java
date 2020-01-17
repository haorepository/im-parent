package com.hypertech.im.jwt;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SaltUtil {
	public static String createSalt() throws NoSuchAlgorithmException{
		 // Use a SecureRandom generator, SHA1PRNG算法是基于SHA-1实现且保密性较强的随机数生成器
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        // Create array for salt
        byte[] salt = new byte[16];
        // Get a random salt
        secureRandom.nextBytes(salt);
        // 将十进制数转换成十六进制(使用&运算，正数部分没变，负数部分二进制从右往左第9位及以上都为0
        StringBuilder builder = new StringBuilder();
        for (byte num : salt) {
            builder.append(Integer.toString((num & 0xff) + 0x100, 16).substring(1));
        }
        return builder.toString();
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(createSalt());
	}
}
