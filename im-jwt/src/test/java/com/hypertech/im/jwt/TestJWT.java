package com.hypertech.im.jwt;

import java.util.UUID;

import cn.hutool.crypto.digest.DigestUtil;

public class TestJWT {
	
	public static void main(String[] args) {
		String uuid1 = uuid();
		String key1 = DigestUtil.md5Hex(uuid1);
		System.out.println("gateway key "+key1);
//		String uuid2 = uuid();
//		String key2 = DigestUtil.md5Hex(uuid2);
//		System.out.println("server key "+key2);
//		String uuid3 = uuid();
//		String key3 = DigestUtil.md5Hex(uuid3);
//		System.out.println("client key "+key3);
	}
	
	public static String uuid(){
		return UUID.randomUUID().toString().replaceAll("-","");
	}
}
