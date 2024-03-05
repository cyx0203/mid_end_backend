package com.gg.midend.controller.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gg.core.service.SqlService;
import com.gg.midend.utils.MapUtil;
import com.mysql.cj.util.StringUtils;

import cn.hutool.core.convert.Convert;

@RestController
public class DoctorController  extends GGBaseController{

    @Value("${file.url}")
    private String url;
    
    @Autowired
    private SqlService sqlService;
    
    @RequestMapping(value = "/getDoctrorList")
    public Object getDoctrorList(@RequestBody Map<String, Object> paramMap) throws Exception {
    	
    	Map<String, Object> respBodyMap = new HashMap<String, Object>();	//定义返回正文
    	Map<String, Object> reqMap = new HashMap<String, Object>();	//定义请求参数
    	
    	Map<String, Object> bodyMap = Convert.toMap(String.class, Object.class, paramMap.get("body")); //获取正文
    	
    	if(StringUtils.isNullOrEmpty((String) bodyMap.get("hospitalId"))) {//判断必输字段
    		return retBack("1", "hospitalId不能为空");
    	}

        // 校验必传字段
        String[] needParams = {"hospitalId"};
        MapUtil.checkParams(needParams, bodyMap);
        
    	reqMap.put("deptCode", !StringUtils.isNullOrEmpty((String) bodyMap.get("deptCode")) ? 
    			bodyMap.get("deptCode") : null);			//科室ID（传入查询指定科室信息）
    	reqMap.put("docCode", !StringUtils.isNullOrEmpty((String) bodyMap.get("docCode")) ? 
    			bodyMap.get("docCode") : null);			//医生ID（传入查询指定医生信息）
        reqMap.put("docName", !StringUtils.isNullOrEmpty((String) bodyMap.get("docName")) ? 
                bodyMap.get("docName") : null); //医生姓名 支持模糊查询
    	reqMap.put("docLevel", bodyMap.get("docLevel")); //医生级别 0-所有医生 1-专家
    	
    	List<?> docList = sqlService.selectList("doc", "selectByHosId", reqMap);	//获取医生简介
    	
    	if(docList.size() <= 0) {	//判断记录是否为空
    		return retBack("1", "未查询到医生信息，请确认入参是否正确！");
    	}
    	List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
    	for(Object doctor : docList) {
    	    Map<String, Object> item = Convert.toMap(String.class, Object.class, doctor);
    	    if(item.get("imageUrl") != null) {
                item.put("imageUrl", url + item.get("imageUrl"));
    	    }
    	    resultList.add(item);
    	}
    	respBodyMap.put("list", resultList);
    	
    	return  retBack("0", "交易成功", respBodyMap);
    }
}
