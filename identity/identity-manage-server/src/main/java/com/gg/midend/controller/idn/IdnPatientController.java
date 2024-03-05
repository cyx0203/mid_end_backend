package com.gg.midend.controller.idn;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.midend.service.IdnAddressServices;

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
public class IdnPatientController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SqlService sqlService;
    @Autowired
    private IdnAddressServices idnAddressServices;

    @RequestMapping(value = "/identity/idnPatientInfo.select")
    public Object queryIdnPatientInfo(@RequestBody Map paramMap) throws ExceptionCenter {
        //查询患者信息列表
        List patientList = sqlService.selectList("idnPatient", "selectAll", paramMap);
 
        Map<String, List> returnMap = new HashMap<>();
        returnMap.put("list", patientList);
        return returnMap;
    }

    @RequestMapping(value = "identity/idnPatientInfo.selectGuardian")
    public Object queryGuardianInfo(@RequestBody Map paramMap) throws ExceptionCenter {
        //查询患者信息列表
        List<Map> guardianList = sqlService.selectList("idnGuardian", "selectById", paramMap);
    	Map m = new HashMap();
        int i = 1;//组装编辑数据
        for(Map map : guardianList) {
        	m.put("relation" + i, map.get("relation"));
        	m.put("name" + i, map.get("name"));
        	m.put("phone" + i, map.get("phone"));
        	i++;
        }
        Map returnMap = new HashMap();
        returnMap.put("list", guardianList);
        returnMap.put("editdata", m);
        return returnMap;
    }
    
    @RequestMapping(value = "/identity/idnPatientInfo.edit")
    @Transactional(rollbackFor = {Exception.class})
    public Object editIdnPatientInfo(@RequestBody Map paramMap) throws ExceptionCenter {
    	System.out.println("paramMap="+paramMap);
        //更新患者信息列表
        int ret = sqlService.update("idnPatient", "updateById", paramMap);
        //更新地址信息
        idnAddressServices.update(paramMap);
        //更新联系人
        ret = sqlService.update("idnGuardian", "deleteById", paramMap);//删除原有联系人
        List list = new ArrayList(); //组装新联系人
        if(paramMap.get("relation1")!= null) {
        	Map map = new HashMap();
        	map.put("id", paramMap.get("id"));
        	map.put("link_relation", paramMap.get("relation1"));
        	map.put("link_name", paramMap.get("name1"));
        	map.put("link_phone", paramMap.get("phone1"));
        	map.put("link_addr", "");
        	list.add(map);
        }
        if(paramMap.get("relation2")!= null) {
        	Map map = new HashMap();
        	map.put("id", paramMap.get("id"));
        	map.put("link_relation", paramMap.get("relation2"));
        	map.put("link_name", paramMap.get("name2"));
        	map.put("link_phone", paramMap.get("phone2"));
        	map.put("link_addr", "");
        	list.add(map);
        }

        if(paramMap.get("relation3")!= null) {
        	Map map = new HashMap();
        	map.put("id", paramMap.get("id"));
        	map.put("link_relation", paramMap.get("relation3"));
        	map.put("link_name", paramMap.get("name3"));
        	map.put("link_phone", paramMap.get("phone3"));
        	map.put("link_addr", "");
        	list.add(map);
        }
        if (!list.isEmpty()) {
        	Map reqMap = new HashMap();
            reqMap.put("list", list);
            ret = sqlService.update("idnGuardian", "insert", reqMap);//添加联系人
        }
        
        return ret;
    }

    
}
