package com.gg.midend.basic;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DictRegionController {
	
    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/manage/dictRegion.select")
    public Object querySchTempDtl() throws ExceptionCenter {
        //查询患者信息列表
        List<Map> list = sqlService.selectList("dictRegion", "selectAll");
        String province = "";
        String city = "";
        Map<String, Object> pMap = new HashMap<String, Object>();//省份
        Map<String, Object> cMap = new HashMap<String, Object>();//城市

        List pList = new ArrayList();//省份
        List cList = new ArrayList();//城市
        List aList = new ArrayList();//区县
        for(Map map : list) {
        	if(province.equals(map.get("province_code"))) {
        		if(city.equals(map.get("city_code"))) {
    				Map<String, String> aMap = new HashMap<String, String>();//区县
        			aMap.put("value", (String) map.get("area_code"));
        			aMap.put("label", (String) map.get("area_name"));
        			aList.add(aMap);
            	}
        		else {
        			if(!"".equals(city)) {
        				
            			cMap.put("children", aList);
            			aList = new ArrayList();
            			cList.add(cMap);
        			}
        			city = (String) map.get("city_code");
        			cMap = new HashMap<String, Object>();
        			cMap.put("value", (String) map.get("city_code"));
        			cMap.put("label", (String) map.get("city_name"));
        		}
        	}
        	else{
        		if(!"".equals(province)) {
        			cMap.put("children", aList);
        			aList = new ArrayList();
        			cList.add(cMap);
        			
            		pMap.put("children", cList);
            		cList = new ArrayList();
            		pList.add(pMap);
        		}
        		province = (String) map.get("province_code");
	        	pMap = new HashMap<String, Object>();
	        	pMap.put("value", (String) map.get("province_code"));
	        	pMap.put("label", (String) map.get("province_name"));
	        	

    			city = (String) map.get("city_code");
	        	cMap = new HashMap<String, Object>();
    			cMap.put("value", (String) map.get("city_code"));
    			cMap.put("label", (String) map.get("city_name"));
    			
	        	Map<String, String> aMap = new HashMap<String, String>();//区县
    			aMap.put("value", (String) map.get("area_code"));
    			aMap.put("label", (String) map.get("area_name"));
    			aList.add(aMap);
    			
    			
        	}
        }
        cMap.put("children", aList);
		aList = new ArrayList();
		cList.add(cMap);
		
		pMap.put("children", cList);
		cList = new ArrayList();
		pList.add(pMap);
		
        Map<String, List> returnMap = new HashMap<>();
        returnMap.put("list", pList);
        return returnMap;
    }

}
