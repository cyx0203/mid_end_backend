package com.gg.midend.service;

import com.gg.core.service.SqlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IdnAddressServices {

    @Autowired
    private SqlService sqlService;
    
    public int update(Map<String, Object> paramMap) {
    	int ret = 0;
    	if(paramMap.get("address") != null || paramMap.get("full_addr") != null ) {
            List addressList = sqlService.selectList("idnAdress", "selectById", paramMap);
        	if(addressList.size() > 0) {//更新
        		Map<String, Object> reqMap = new HashMap<String, Object>();
        		reqMap.put("id", paramMap.get("id"));
        		if(paramMap.get("address") != null) {
        			reqMap.put("province", ((ArrayList) paramMap.get("address")).get(0));
        			reqMap.put("city", ((ArrayList) paramMap.get("address")).get(1));
        			reqMap.put("area", ((ArrayList) paramMap.get("address")).get(2));
        		}
        		if(paramMap.get("full_addr") != null) {
        			reqMap.put("fullAddr", (String) paramMap.get("full_addr"));
        		}
        		ret = sqlService.update("idnAdress", "updateById", reqMap);
        	}
        	else {//插入
        		Map<String, Object> reqMap = new HashMap<String, Object>();
        		reqMap.put("id",  paramMap.get("id"));
        		reqMap.put("addressType", "02");
        		if(paramMap.get("address") != null) {
        			reqMap.put("province", ((ArrayList) paramMap.get("address")).get(0));
        			reqMap.put("city", ((ArrayList) paramMap.get("address")).get(1));
        			reqMap.put("area", ((ArrayList) paramMap.get("address")).get(2));
        		}
        		if(paramMap.get("full_addr") != null) {
        			reqMap.put("fullAddr", (String) paramMap.get("full_addr"));
        		}
        		
        		ret = sqlService.update("idnAdress", "insert", reqMap);
        	}
        }
    	return ret;
    }

}
