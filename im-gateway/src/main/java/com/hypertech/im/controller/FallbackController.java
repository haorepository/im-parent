package com.hypertech.im.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.hypertech.im.vo.ResultDataVO;

@RestController
@RequestMapping(value = "/gateway")
public class FallbackController {
	private static final Logger logger = 
			LoggerFactory.getLogger(FallbackController.class);
	
	@RequestMapping(value = "/fallback")
	public String fallback(){
		logger.info("Gateway服务降级...");
		ResultDataVO<Object> data = new ResultDataVO<Object>();
		data.setCode(202);
		data.setStatus("exception");
		data.setMsg("Gateway服务降级...");
		String jsonData = new Gson().toJson(data);
		return jsonData;
	}
}
