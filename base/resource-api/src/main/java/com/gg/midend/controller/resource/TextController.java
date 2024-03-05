package com.gg.midend.controller.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.core.service.SqlService;
import com.gg.midend.utils.MapUtil;

import cn.hutool.core.convert.Convert;

@RestController
public class TextController extends GGBaseController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SqlService sqlService;

	@RequestMapping(value = "/getTextList")
	public Object getTextList(@RequestBody Map<String, Object> paramMap) throws Exception {

		Map<String, Object> respBodyMap = new HashMap<String, Object>(); // 定义返回正文

        Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));   //获取正文

        // 校验必传字段
        String[] needParams = {"typeId"};
        MapUtil.checkParams(needParams, reqMap);

		List<?> textList = sqlService.selectList("text", "selectTextList", reqMap); // 获取医生图文列表

		if (textList.size() <= 0) { // 判断记录是否为空
			return retBack("1", "未查询到图文概要信息，请确认入参是否正确！");
		}
		respBodyMap.put("List", textList);
		return retBack("0", "交易成功", respBodyMap);
	}

	@RequestMapping(value = "/getTextInfo")
	public Object getTextInfo(@RequestBody Map<String, Object> paramMap) throws Exception {

		Map<String, Object> respBodyMap = new HashMap<String, Object>(); // 定义返回正文

        Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));   //获取正文

        // 校验必传字段
        String[] needParams = {"id"};
        MapUtil.checkParams(needParams, reqMap);

		respBodyMap = Convert.toMap(String.class, Object.class, sqlService.selectOne("text", "selectTextInfo", reqMap)); // 图文详细信息

		log.info("出参:" + respBodyMap);
		if (respBodyMap == null) { // 判断记录是否为空
			return retBack("1", "未查询到图文具体信息，请确认入参是否正确！");
		}
		return retBack("0", "交易成功", respBodyMap);
	}
}
