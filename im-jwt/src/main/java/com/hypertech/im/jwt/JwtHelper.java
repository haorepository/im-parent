package com.hypertech.im.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.codec.Base64;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtHelper {

	private static final Logger logger = 
			LoggerFactory.getLogger(JwtHelper.class);
    /**
	 * 指定签名算法
	 */
    private static final SignatureAlgorithm SA = SignatureAlgorithm.HS256;
    
    /**
     * 创建JWT
     */
    public static String createJWT(final Map<String, Object> claims, Long expirationTime) {
         //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        Date now = new Date(System.currentTimeMillis());

        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();//生成JWT的时间
        //下面就是在为payload添加各种标准声明和私有声明了
        JwtBuilder builder = Jwts.builder() //这里其实就是new一个JwtBuilder，设置jwt的body
        		//如果有私有声明，一定要先设置这个自己创建的私有的声明，
        		//这个是给builder的claim赋值，一旦写在标准的声明赋值之后，
        		//就是覆盖了那些标准的声明的
                .setClaims(claims) 
                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，
                //这个可以设置为一个不重复的值，主要用来作为一次性token,
                //从而回避重放攻击。
                .setId(UUID.randomUUID().toString())                  
				//.setSubject(AESSecretUtil.encryptToStr(userId,SecretCommon.KEY)+"-"+userName)
				//设置用户
                //.setIssuer(AESSecretUtil.encryptToStr(userId,SecretKeyUtil.KEY))
                //iat: jwt的签发时间
                .setIssuedAt(now)           
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(SA, secretKey);
        if (expirationTime >= 0) {
            long expMillis = nowMillis + expirationTime.longValue();
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);     //设置过期时间
        }
        return builder.compact();
    }

    /**
     * 验证jwt
     */
    public static Claims verifyJwt(final String token) {
        //签名秘钥，和生成的签名的秘钥一模一样
        SecretKey key = generalKey();
        Claims claims;
        try {
            claims = Jwts.parser()  //得到DefaultJwtParser
                    .setSigningKey(key)         //设置签名的秘钥
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
        	logger.error(e.getMessage());
        	e.printStackTrace();
            claims = null;
        }
        //设置需要解析的jwt
        return claims;
    }
    
    
    /**
     * 验证jwt
     */
    public static boolean verifyJWT(final String token) {
        //签名秘钥，和生成的签名的秘钥一模一样
        SecretKey key = generalKey();
        try {
        	//得到DefaultJwtParser
            Jwts.parser()  
	            //设置签名的秘钥
                .setSigningKey(key)        
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
        	e.printStackTrace();
        	logger.error(e.getMessage());
        	return false;
        }
        return true;
    }


    /**
     * 由字符串生成加密key
     *
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.decode(SecretKeyUtil.JWT_SECRET_KEY);
        SecretKey key = new SecretKeySpec(encodedKey,SA.getJcaName());
        return key;
    }

    /**
     * 根据userId和openid生成token
     */
    public static String generateToken(Map<String, Object> claims,Long expirationTime) {
        return createJWT(claims, expirationTime);
    }
    
    public static void main(String[] args) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	
    	String token = generateToken(map,3*60*1000l);
		System.out.println(token);
	}
}
