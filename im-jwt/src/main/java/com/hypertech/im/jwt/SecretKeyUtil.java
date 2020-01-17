package com.hypertech.im.jwt;

public class SecretKeyUtil {
	/**
	 * 自定义JWT签名秘钥 
	 */
	public static final String JWT_SECRET_KEY="9bba86f6231e17d95897af248dd7f446";
	
    /**
     * 自定义用于加密ID的密匙 
     */
    public static final String ID_KEY = "e40127633177a13c66f6a6a442cd76ac";
    
    /**
     * 用于服务调用认证的密钥
     */
    public static final String CLOUD_SECRET_KEY = "fd49b9e7217bb1ff7a2aee4def8282d7";
    
    /**
     * 用于随机数加时间戳加密的密钥
     */
    public static final String NONCE_TIMESTAMP_KEY = "d3aebe7f923dc7f82c4698550128b538";
}
