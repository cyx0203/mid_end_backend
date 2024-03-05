package com.gg.midend.controller.basic;

import com.gg.core.service.SqlService;
import com.gg.core.exception.ExceptionCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class DeviceController {

    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/manage/dev.startDev")
    @Transactional(rollbackFor = {Exception.class})
    public Object startDev(@RequestBody Map paramMap) throws ExceptionCenter {
        String hospitalId = (String) paramMap.get("hospitalId");
        String devId = (String) paramMap.get("devId");
        String devTypeId = (String) paramMap.get("devTypeId");
        // 删除des_dev_state表 设备组件信息
        sqlService.update("devState", "deleteByDevId", paramMap);
        //查询设备类型的parts
        Map<String, String> queryDevTypeMap = new HashMap<>();
        queryDevTypeMap.put("id", devTypeId);
        List devTypeList = sqlService.selectList("devType", "selectAll", queryDevTypeMap);
        if (devTypeList.size() == 0) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        Map devTypeMap = (Map) devTypeList.get(0);
        String parts = String.valueOf(devTypeMap.get("parts")).trim();
        // 拼装插入des_dev_state的数据,插表 start
        List insertDesDevStateList = new ArrayList();
        char[] states = parts.toCharArray();
        for (int i = 0; i < 30; i++) {
            String catId = Integer.toString(2000 + i + 1);
            if (Objects.equals('1', states[i])) {
                Map<String, Object> desDevStateMap = new HashMap<>();
                desDevStateMap.put("hospitalId", hospitalId);
                desDevStateMap.put("devId", devId);
                desDevStateMap.put("catId", catId);
                desDevStateMap.put("value", "0");
                insertDesDevStateList.add(desDevStateMap);
            }
        }
        Map<String, List> insertMap = new HashMap<>();
        insertMap.put("insertList", insertDesDevStateList);
        int ret = sqlService.update("devState", "insertBatch", insertMap);
        if (ret < 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        // 拼装插入des_dev_state的数据,插表 end
        // 更新dev_device的启用状态
        Map<String, String> updateDevMap = new HashMap<>();
        updateDevMap.put("hospitalId", hospitalId);
        updateDevMap.put("oid", devId);
        updateDevMap.put("useStat", "0");
        ret = sqlService.update("dev", "update", updateDevMap);
        if (ret < 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        return 1;
    }

    @RequestMapping(value = "/manage/dev.stopDev")
    @Transactional(rollbackFor = {Exception.class})
    public Object stopDev(@RequestBody Map paramMap) throws Exception {
        String hospitalId = (String) paramMap.get("hospitalId");
        String devId = (String) paramMap.get("devId");
        // 删除des_dev_state表 设备组件信息
        sqlService.update("devState", "deleteByDevId", paramMap);
        // 更新dev_device的启用状态
        Map<String, String> updateDevMap = new HashMap<>();
        updateDevMap.put("hospitalId", hospitalId);
        updateDevMap.put("oid", devId);
        updateDevMap.put("useStat", "2");
        int ret = sqlService.update("dev", "update", updateDevMap);
        if (ret < 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        return 1;
    }

    @RequestMapping(value = "/manage/dev.deleteDev")
    @Transactional(rollbackFor = {Exception.class})
    public Object deleteDev(@RequestBody Map paramMap) throws Exception {
        String hospitalId = (String) paramMap.get("hospitalId");
        String devId = (String) paramMap.get("devId");
        // 删除des_dev_state表 设备组件信息
        sqlService.update("devState", "deleteByDevId", paramMap);
        // 删除设备
        Map<String, String> deleteDevMap = new HashMap<>();
        deleteDevMap.put("hospitalId", hospitalId);
        deleteDevMap.put("id", devId);
        int ret = sqlService.update("dev", "deleteById", deleteDevMap);
        if (ret < 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        return 1;
    }

    // startDev
    private char getRandomWithoutRepeat(char c) {
        char ret = getRandomUpperCaseLetter();
        if (ret == c)
            return getRandomWithoutRepeat(c);
        return ret;
    }

    // startDev
    public static char getRandomUpperCaseLetter() {
        return getRandomCharacter('A', 'Z');
    }

    // startDev
    public static char getRandomCharacter(char ch1, char ch2) {
        return (char) (int) (ch1 + Math.random() * (ch2 - ch1 + 1));
    }
}
