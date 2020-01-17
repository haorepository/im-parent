package com.hypertech.im.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hypertech.im.config.Redis;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTTokenUtil {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(JWTTokenUtil.class);
	
	@Autowired
	private Redis redis;
	
	/**
     * 从token中获取用户
     */
    public String getUserFromToken(final String token) {
        return getClaimFromToken(token).getSubject();
    }

    /**
     * <pre>
     *  验证token是否失效
     *  true:过期   false:没过期
     * </pre>
     */
    public Boolean isTokenExpired(final String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
        	logger.error(e.getMessage());
        	e.fillInStackTrace();
            return true;
        }
    }

    /**
     * 获取可用的token
     * 如该用户当前token可用，即返回
     * 当前token不可用，则返回一个新token
     * @param userId
     * @return
     */
    public String getGoodToken(final String userId){
        String token = (String)redis.get("userJwtToken_"+userId);
        boolean flag = this.checkToken(token);
        //校验当前token能否使用，不能使用则生成新token
        if(flag){
            return token;
        }else{
            String newToken = this.createToken(userId);
            //初始化新token
            this.initNewToken(userId, newToken);
            return newToken;
        }
    }

    /**
     * 判断过期token是否合法
     * @param token
     * @return
     */
    public String checkExpireToken(final String token){
        //判断token是否需要更新
        boolean expireFlag = this.checkToken(token);
        //false：不建议使用
        if(!expireFlag){
            String userId = (String)redis.get(token);
            if(StringUtils.isNotEmpty(userId)){
                return userId + "-1";
            }
        }else{
            String userId = this.getUserFromToken(token);
            return userId;
        }
        return "";
    }

    /**
     * 检查当前token是否还能继续使用
     * true：可以  false：不建议
     * @param token
     * @return
     */
    public boolean checkToken(final String token){
        SecretKey secretKey = this.createSecretKey();
        try {
            // jwt正常情况 则判断失效时间是否大于5分钟
        	//得到DefaultJwtParser
            long expireTime = Jwts.parser()   
            		//设置签名的秘钥
                    .setSigningKey(secretKey)  
                    .parseClaimsJws(token.replace("jwt_", ""))
                    .getBody().getExpiration().getTime();
            long diff = expireTime - System.currentTimeMillis();
            //如果有效期小于5分钟，则不建议继续使用该token
            if (diff < TimeUtil.ADVANCE_EXPIRE_TIME) {
                return false;
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
        	e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 创建新token
     * @param userId 用户ID
     * @return
     */
    public String createToken(final String userId){
    	//指定签名的时候使用的签名算法，也就是header那部分，
    	//jwt已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; 
        //生成JWT的时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，
        //一般是需要和jwt的接收方提前沟通好验证方式的）
        Map<String,Object> claims = new HashMap<String,Object>();
        claims.put("userId", AESSecretUtil.encryptToStr(userId,SecretKeyUtil.ID_KEY));
		//claims.put("userName",userName);
        //生成签名的时候使用的秘钥secret,这个方法本地封装了的，
        //一般可以从本地配置文件中读取，切记这个秘钥不能外露哦。
        //它就是你服务端的私钥，在任何场景都不应该流露出去。
        //一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。
        SecretKey secretKey = createSecretKey();
        //下面就是在为payload添加各种标准声明和私有声明了
        //这里其实就是new一个JwtBuilder，设置jwt的body
        JwtBuilder builder = Jwts.builder() 
        		//如果有私有声明，一定要先设置这个自己创建的私有的声明，
        		//这个是给builder的claim赋值，一旦写在标准的声明赋值之后，
        		//就是覆盖了那些标准的声明的
        		.setClaims(claims)          
        		//设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，
        		//这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(UUID.randomUUID().toString())                  
                //iat: jwt的签发时间
                .setIssuedAt(now)           
                //sub(Subject)：代表这个JWT的主体，即它的所有人，
                //这个是一个json格式的字符串，可以存放什么userid，roldid之类的，
                //作为什么用户的唯一标志。
                //.setSubject(userId + "-" + jwtVersion)        
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, secretKey);
        //设置过期时间
        if (TimeUtil.JWT_EXPIRE_TIME_LONG >= 0) {
            long expMillis = nowMillis + TimeUtil.JWT_EXPIRE_TIME_LONG;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        String newToken = "jwt_" + builder.compact();
        return newToken;
    }

    /**
     * 生成新token时，初始化token
     * @param userId
     * @param newToken
     */
    public void initNewToken(final String userId,final String newToken){
        String token = (String)redis.get("userJwtToken_"+userId);
        if(StringUtils.isNotEmpty(token)){
            //老token设置过期时间 5分钟
            redis.set(token, userId, TimeUtil.OLD_TOKEN_EXPIRE_TIME);
        }
        //新token初始化
        redis.set(newToken, userId);
        redis.set("userJwtToken_"+userId, newToken);
    }

    /**
     * 获取jwt失效时间
     */
    public Date getExpirationDateFromToken(final String token) {
        return getClaimFromToken(token).getExpiration();
    }

    /**
     * 获取jwt的payload部分
     */
    public Claims getClaimFromToken(final String token) {
        SecretKey secretKey = createSecretKey();
        //得到DefaultJwtParser
        return Jwts.parser()   
        		//设置签名的秘钥
                .setSigningKey(secretKey)  
                .parseClaimsJws(token.replace("jwt_", ""))
                .getBody();
    }

    // 签名私钥
    private SecretKey createSecretKey(){
    	//本地的密码解码
        byte[] encodedKey = DatatypeConverter.parseBase64Binary(SecretKeyUtil.JWT_SECRET_KEY);
        // 根据给定的字节数组使用AES加密算法构造一个密钥，
        //使用 encodedKey中的始于且包含 0 到前 leng 个字节这是当然是所有。
        SecretKey secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return secretKey;
    }

}
