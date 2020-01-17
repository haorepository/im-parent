package com.hypertech.im.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTUtil {
	private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);
	/**
	 * 指定签名算法
	 */
	private static SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	
	/**
	 * 将SECRET常量字符串使用base64解码成字节数组
	 * @return apiKeySecretBytes
	 */
	public static byte[] apiKeySecretBytes(){
		byte[] apiKeySecretBytes = 
				DatatypeConverter.parseBase64Binary(SecretKeyUtil.JWT_SECRET_KEY);
		return apiKeySecretBytes;
	}
	
	
	public static Key signingKey(){
		//将SECRET常量字符串使用base64解码成字节数组
		//byte[] apiKeySecretBytes = apiKeySecretBytes();
		//使用HmacSHA256签名算法生成一个HS256的签名秘钥Key
		Key signingKey = new SecretKeySpec(
				apiKeySecretBytes(),signatureAlgorithm.getJcaName());
		return signingKey;
	}
	
	public static String creatJWT(final Map<String,Object> claims,final Long expirationTime){
		//设置签名算法
		//SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		//获取当前时间
		long currentTimeMillis = System.currentTimeMillis();
		Date now = new Date(currentTimeMillis);
		//将SECRET常量字符串使用base64解码成字节数组
		//byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SecretCommon.SECRET);
		//使用HmacSHA256签名算法生成一个HS256的签名秘钥Key
		Key signingKey = signingKey();
		Map<String,Object> header = new HashMap<String, Object>();
		header.put("alg", signatureAlgorithm.getValue());
		//header.put("typ", "JWT");
		//Map<String,Object> claims = new HashMap<String, Object>();
		//claims.put("userId", AESSecretUtil.encryptToStr(userId,SecretKeyUtil.KEY));
		//claims.put("userName",userName);
		JwtBuilder builder = Jwts.builder()
				.setHeader(header)
				.setClaims(claims)
				//.setId(UUID.randomUUID().toString())
				//.setSubject(AESSecretUtil.encryptToStr(userId,SecretCommon.KEY)+"-"+userName)
				//.setIssuedAt(now)
				//.setIssuer(AESSecretUtil.encryptToStr(userId,SecretKeyUtil.KEY))
				//.claim("userId",AESSecretUtil.encryptToStr(userId,SecretCommon.KEY))
                //.claim("userName",userName)
				// 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, signingKey);
              	//添加Token过期时间
                if (TimeUtil.EXPIRESSECOND >= 0) {
                    long expMillis = currentTimeMillis + TimeUtil.EXPIRESSECOND;
                    Date expDate = new Date(expMillis);
                    builder.setExpiration(expDate).setNotBefore(now);
                }
		return builder.compact();
	}
	 /**
     * @Description: 解析JWT
     * 返回Claims对象
     * @param jsonWebToken - JWT
     * @Data: 2018/7/28 19:25
     * @Modified By:
     */
    public static Claims parseJWT(final String jsonWebToken) {
        Claims claims = null;
        try {
            if (StringUtils.isNotBlank(jsonWebToken)) {
                //解析jwt
                claims = Jwts.parser().setSigningKey(apiKeySecretBytes())
                        	.parseClaimsJws(jsonWebToken).getBody();
            }else {
                logger.warn("[JWTHelper]-json web token 为空");
            }
        } catch (Exception e) {
            logger.error("[JWTHelper]-JWT解析异常：可能因为token已经超时或非法token");
        }
        return claims;
    }

    /**
     * @Description: 校验JWT是否有效
     * 返回json字符串的demo:
     * freshToken-刷新后的jwt
     * userName-客户名称
     * userId-客户编号
     * @param jsonWebToken - JWT
     * @Modified By:
     */
    public static String validateLogin(final String jsonWebToken) {
        Map<String, Object> retMap = null;
        Map<String,Object> claims2 = null;
        Claims claims = parseJWT(jsonWebToken);
        if (claims != null) {
            //解密客户编号
            String decryptUserId = AESSecretUtil
            		.decryptToStr((String)claims.get("userId"), SecretKeyUtil.ID_KEY);
            retMap = new HashMap<>();
            //加密后的客户编号
            retMap.put("userId", decryptUserId);
            //客户名称
            retMap.put("userName", claims.get("userName"));
            //刷新JWT
            retMap.put("freshToken", creatJWT(claims2,null));
        }else {
            logger.warn("[JWTHelper]-JWT解析出claims为空");
        }
        return retMap!=null?JSONObject.toJSONString(retMap):null;
    }

    public static void main(String[] args) {
       String jsonWebKey = creatJWT(null,null);
       System.out.println(jsonWebKey);
       Claims claims =  parseJWT(jsonWebKey);
       System.out.println(claims);
       System.out.println(validateLogin(jsonWebKey));
    }
}
