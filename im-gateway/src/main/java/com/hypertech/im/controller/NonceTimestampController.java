package com.hypertech.im.controller;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hypertech.im.entity.JWTEntity;
import com.hypertech.im.jwt.AESSecretUtil;
import com.hypertech.im.jwt.SaltUtil;
import com.hypertech.im.jwt.SecretKeyUtil;

import cn.hutool.crypto.digest.DigestUtil;

@RestController
@RequestMapping(value="/nonce_timestamp")
public class NonceTimestampController {
	
//	@Autowired
//	private Redis redis;
	
	@RequestMapping(value="/nonce_timestamp")
	public ResponseEntity<JWTEntity> nonceTimestamp(String userId,String userKey) throws NoSuchAlgorithmException{
		JWTEntity jwtEntity = new JWTEntity();
		String uuid = UUID.randomUUID().toString().replaceAll("-","");
		StringBuffer nonceBuffer = new StringBuffer(uuid);
		nonceBuffer.append(SaltUtil.createSalt());
		String nonce = DigestUtil.md5Hex(nonceBuffer.toString());
		String timestamp = String.valueOf(System.currentTimeMillis());
		String sign = AESSecretUtil.encryptToStr(
				nonce+"-"+timestamp,SecretKeyUtil.NONCE_TIMESTAMP_KEY);
		jwtEntity.setTimestamp(timestamp);
		jwtEntity.setNonce(nonce);
		jwtEntity.setSign(sign);
//		验证用户是否有权获取系统时间戳和随机数
//		if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userKey)){
//			String decryptUserId = AESSecretUtil.decryptToStr(userId, SecretKeyUtil.ID_KEY);
//			String redisUserKey = (String)redis.get(decryptUserId);
//			if(StringUtils.equals(userKey, redisUserKey)){
//				jwtEntity.setTimestamp(timestamp);
//				jwtEntity.setNonce(nonce);
//				jwtEntity.setSign(sign);
//			}
//		}
		//return String.valueOf(timestamp);
		return new ResponseEntity<JWTEntity>(jwtEntity,HttpStatus.OK);
	}
}
