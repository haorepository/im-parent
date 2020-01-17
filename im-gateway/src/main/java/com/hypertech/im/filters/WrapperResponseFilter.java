package com.hypertech.im.filters;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.hypertech.im.config.Redis;
import com.hypertech.im.entity.UserInfo;
import com.hypertech.im.jwt.AESSecretUtil;
import com.hypertech.im.jwt.JWTUtil;
import com.hypertech.im.jwt.JwtHelper;
import com.hypertech.im.jwt.SecretKeyUtil;
import com.hypertech.im.vo.ResultDataVO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@SuppressWarnings("all")
public class WrapperResponseFilter implements GlobalFilter,Ordered{
	private static final Logger logger = 
			LoggerFactory.getLogger(WrapperResponseFilter.class);
	
	@Autowired
	private Redis redis;
	
	@Value(value="${spring.redis.expiredTime}")
	private String expiredTime;
	
	private List<String> skipAuthUrls(){
		return Arrays.asList("/im-user-client/user/login","/get/timestamp");
	}

	@Override
	public int getOrder() {
		return -2;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.info("Into WrapperResponseFilter");
		String url = exchange.getRequest().getURI().getPath();
		String gatewayRequestKey = redis.create(SecretKeyUtil.CLOUD_SECRET_KEY);
		ServerHttpRequest request = exchange.getRequest();
		request.mutate().headers(
				h->h.remove(SecretKeyUtil.CLOUD_SECRET_KEY)).build();
		//获取token
		String token = request.getHeaders().getFirst("Authorization");
		//获取token
		String nonce = request.getHeaders().getFirst("nonce");
		//获取token
		String timestamp = request.getHeaders().getFirst("timestamp");
		//获取token
		String sign = request.getHeaders().getFirst("sign");
        //exchange.getRequest().getHeaders().getFirst("Authorization");
		ServerHttpRequest newRequest = request.mutate()
				.header(SecretKeyUtil.CLOUD_SECRET_KEY, gatewayRequestKey).build();
		
        ServerHttpResponse resp = exchange.getResponse();
        
        
        ServerWebExchange newExchange =  exchange.mutate().request(newRequest.mutate().build()).build();
		if(!CollectionUtils.isEmpty(skipAuthUrls()) 
				&& skipAuthUrls().contains(url)){
			return chain.filter(newExchange);
		}
		
		if(!StringUtils.isEmpty(token)){
			//String newToken = JWTUtil.validateLogin(token);
			boolean jwtFlag = JwtHelper.verifyJWT(token);
			boolean signFlag = verifyIsReplayAttack(nonce, timestamp, sign);
			logger.info("Verify JWT token is "+jwtFlag);
			logger.info("Verify sign token is "+jwtFlag);
			//if(!StringUtils.isEmpty(newToken)){
				//return success(newExchange, chain);
			//}
			if(jwtFlag && signFlag){
				return success(newExchange, chain);
			}
		}
        return authErro(resp); 
	}
	private Mono<Void> success(ServerWebExchange exchange, GatewayFilterChain chain){
		ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
		ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        // probably should reuse buffers
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        // 释放掉内存
                        DataBufferUtils.release(dataBuffer);
                        String rs = new String(content, Charset.forName("UTF-8"));
                        
                        ResultDataVO result = new Gson().fromJson(rs, ResultDataVO.class);
                        //System.out.println(result.toString());
                        logger.info(result.toString());
                        byte[] newRs = new String(content, Charset.forName("UTF-8")).getBytes();
                        //如果不重新设置长度则收不到消息。
                        originalResponse.getHeaders().setContentLength(newRs.length);
                        return bufferFactory.wrap(newRs);
                    }));
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };
        // replace response with decorator
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
	}
	
	
	/**
     * 认证错误输出
     * @param resp 响应对象
     * @param mess 错误信息
     * @return
     */
    private Mono<Void> authErro(ServerHttpResponse resp) {
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        ResultDataVO<UserInfo> data = new ResultDataVO<UserInfo>();
        data.setCode(0);
        data.setStatus("error");
        data.setMsg("Get data fail...");
        String responseJson = new Gson().toJson(data);
        DataBuffer buffer = resp.bufferFactory().wrap(responseJson.getBytes());
        return resp.writeWith(Flux.just(buffer));
    }
    
//    private boolean verifyIsReplayAttack(String nonce,String timestamp,String sign){
//    	String str1 = verifySign(nonce, timestamp, sign);
//    	String str2 = verifyTimestamp(timestamp);
//    	if(StringUtils.equals(str1, str2)){
//    		return true;
//    	}
//    	return false;
//    }
    
    private boolean verifyIsReplayAttack(String nonce,String timestamp,String sign){
    	StringBuffer sb = new StringBuffer();
    	String oldSign;
    	String newSign;
    	if(StringUtils.isNotBlank(nonce)){
    		sb.append(nonce);
    	}else{
    		logger.error("Nonce is null.");
    		return false;
    	}
    	if(StringUtils.isNotBlank(timestamp)){
    		sb.append("-");
    		sb.append(timestamp);
    		oldSign = (String)redis.get(sb.toString());
    		newSign = AESSecretUtil.encryptToStr(sb.toString(), SecretKeyUtil.NONCE_TIMESTAMP_KEY);
     	}else{
     		logger.error("Timestamp is null.");
    		return false;
    	}
    	if(!(StringUtils.isNotBlank(sign) && StringUtils.equals(sign, newSign))){
    		logger.error("Sign is null or it is bad.");
    		return false;
    	}
    	if(verifyTimestamp(timestamp) && !StringUtils.equals(sign, oldSign)){
    		boolean flag = redis.set(sb.toString(),sign,Integer.valueOf(expiredTime));
    		if(flag){
    			logger.info("Sign is insert redis.");
    		}else{
    			logger.error("Insert sgin has been error.");
    			return false;
    		}
    	}else{
    		logger.error("Timestamp has expired or sign already exists.");
    		return false;
    	}
    	return true;
    }
    
    /**
     * 验证时间是否过期
     * @param timestamp
     * @return
     */
    private boolean verifyTimestamp(String timestamp){
    	long currentTimeMillis = System.currentTimeMillis();
    	long oldTimestamp = Long.valueOf(timestamp).longValue();
    	if(currentTimeMillis-oldTimestamp<60*1000){
    		return true;
    	}
    	return false;
    }

}
