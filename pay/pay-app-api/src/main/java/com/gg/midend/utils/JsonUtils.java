package com.gg.midend.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * 处理JSON数据格式
 * @author asus
 *
 */
public class JsonUtils {

	
	/**
	 * map转json
	 * 
	 * @param data
	 * @return
	 */
	public static String MapToJson(Map<String, Object> data) {
		ObjectMapper mapper = new ObjectMapper();
		String mjson = null;
		try {
			mjson = mapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mjson;
	}
	
	/**
	 * json转map
	 * 
	 * @param json
	 * @return
	 */
	public static Map<String, Object> JsonToMapObj(String json) {
		ObjectMapper mapper = new ObjectMapper();
		//mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = mapper.readValue(json, Map.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}


}
