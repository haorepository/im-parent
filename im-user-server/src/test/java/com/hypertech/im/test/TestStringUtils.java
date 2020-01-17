package com.hypertech.im.test;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class TestStringUtils {
	
	@Test
	public void equals(){
		assertTrue(StringUtils.equals("张三","张三"));
	}
}
