package com.hypertech.im;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.hypertech.im.config.Redis;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private Redis redisUtil;
	
	@Test
	public void add(){
		redisUtil.set("t1","t1", 30);
		String value = stringRedisTemplate.opsForValue().get("t1");
		System.out.println(value);
	}
	
	@Test
	public void redisUtilGet(){
		String value = (String)redisUtil.get("test");
		System.out.println(value);
	}
	
	@Test
	public void del(){
		redisUtil.del("test");
		String value = (String)redisUtil.get("test");
		System.out.println(value);
	}
}
