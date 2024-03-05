package com.gg.midend.controller.resource;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.core.service.SqlService;
import com.gg.midend.utils.MapUtil;

import cn.hutool.core.convert.Convert;

@RestController
public class HospitalController  extends GGBaseController{

    @Autowired
    private SqlService sqlService;
    
    @RequestMapping(value = "/getHospitalInfo")
    public Object getHospitalInfo(@RequestBody Map<String, Object> paramMap) throws Exception {
    	
    	Map<String, Object> respBodyMap = new HashMap<String, Object>();			//定义返回正文
    	
    	Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body")); //获取正文
    	
        // 校验必传字段
        String[] needParams = {"hospitalId"};
        MapUtil.checkParams(needParams, reqMap);
    	
    	respBodyMap = Convert.toMap(String.class, Object.class, sqlService.selectOne("hosp", "selectByHosId", reqMap));	//返回医院简介结果
    	
    	if(respBodyMap == null) {	//判断是否有返回结果
    		return retBack("1", "未查询到医院信息，请确认参数是否正确！");
    	}
    	return  retBack("0", "交易成功", respBodyMap);
    }
}
