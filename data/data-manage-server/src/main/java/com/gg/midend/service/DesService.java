package com.gg.midend.service;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * TODO
 *
 * @Description
 * @Auther: Administrator
 * @Date: 2023/4/7 16:24
 */
@Service
public class DesService {

    @Autowired
    private SqlService sqlService;

    /**
     * TODO
     *
     * @description:启用设备监控
     * @author: lufm
     * @date: 2023/4/7 16:29
     * @param: [paramMap]
     * @return: java.lang.Object
     **/
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Object startDes(Map paramMap) throws ExceptionCenter {
        // 检查设备是否启动
        if (!Objects.equals("0", paramMap.get("useStat"))) {
            throw new ExceptionCenter("500","设备未启用，请先启用设备");
        }

        //删除原先记录
        int deleteNum = sqlService.update("devState", "deleteById", "devId",paramMap.get("id"));

        Map map = new HashMap();
        map.put("id",paramMap.get("typeId"));
        Map devTypeMap = (Map) sqlService.selectOne("devType","selectDevType",map);

        if(devTypeMap == null || devTypeMap.isEmpty()){
            throw new ExceptionCenter("500","设备类型设置错误，请确认");
        }

        String devId = (String) paramMap.get("id");
        String hospitalId = (String) paramMap.get("hospitalId");
        //部件字符串
        List list = new ArrayList();
        String part = String.valueOf(devTypeMap.get("parts")).trim();
        char[] parts = part.toCharArray();
        for (int i = 0; i < parts.length; i++) {
            String datetime = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
            String catId = Integer.toString(2000 + i + 1);
            if (Objects.equals('1', parts[i])) {
                Map pMap = new HashMap();
                pMap.put("hospitalId", hospitalId);
                pMap.put("devId", devId);
                pMap.put("catId", catId);
                pMap.put("value", "0");
                pMap.put("wType", "0");
                pMap.put("err", "");
                pMap.put("dTime", datetime);
                pMap.put("pad1", "");
                list.add(pMap);
            }
        }
        if(list.size() > 0){
            sqlService.update("devState","initDevState","list",list);
        }

        return 1;
    }


    /**
     * TODO
     *
     * @description:关闭监控后删除des_dev_state表对应数据
     * @author: lufm
     * @date: 2023/4/10 10:19
     * @param: [paramMap]
     * @return: java.lang.Object
     **/
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Object stopDes(Map paramMap) throws ExceptionCenter {
        // 检查设备是否启动
        if (!Objects.equals("0", paramMap.get("useStat"))) {
            throw new ExceptionCenter("500","设备未启用，请先启用设备");
        }
        //删除原先记录
        int deleteNum = sqlService.update("devState", "deleteById", "devId",paramMap.get("id"));

        return 1;
    }

}
