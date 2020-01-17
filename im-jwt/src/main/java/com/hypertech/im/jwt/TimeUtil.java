package com.hypertech.im.jwt;

public class TimeUtil {
	/**
	 * 超时毫秒数（默认30分钟）1800000
	 */
    public static final int EXPIRESSECOND = 2*60*1000;
    
    /**
	 * JWT超时毫秒数（默认30分钟）1800000
	 */
    public static final int JWT_EXPIRE_TIME_LONG = 30*60*1000;
    
    /**
     * 距离过期时间
     */
    public static final int ADVANCE_EXPIRE_TIME = 5*60*1000;
    
    /**
     * 旧的token过期时间(单位：秒)
     */
    public static final int OLD_TOKEN_EXPIRE_TIME = 5*60;
}
