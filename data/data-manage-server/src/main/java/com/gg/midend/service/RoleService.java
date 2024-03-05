package com.gg.midend.service;

import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {
    
    @Autowired
    private SqlService sqlService;

    public void select(Map<String, Object> paramMap, Map retMap) {
        List retList = sqlService.selectList("role", "selectByPrimaryKey");
        retMap.put("list", retList);
        Map pagination = new HashMap();
        String pageSize;
        if (paramMap.get("currentPage") != null) {
            pageSize = paramMap.get("currentPage").toString();
            pagination.put("current", Integer.valueOf(pageSize));
        }

        if (paramMap.get("pageSize") != null) {
            pageSize = paramMap.get("pageSize").toString();
            pagination.put("pageSize", Integer.valueOf(pageSize));
        }

        pagination.put("total", retList.size());
        retMap.put("pagination", pagination);
        retMap.put("list", retList);
    }

    public void insert(String namespace, String sqlid, Map<String, Object> paramMap, Map retMap) {
        sqlService.update(namespace, sqlid, paramMap);
        int maxId = (Integer)sqlService.selectOne("role", "selectMaxId");
        paramMap.put("roleId", maxId);
        int ret = sqlService.update("rolemenu", "insertRoleMenu", paramMap, new Object[0]);
        select(paramMap, retMap);
    }

    public void update(String namespace, String sqlid, Map<String, Object> paramMap, Map retMap) {
        sqlService.update(namespace, sqlid, paramMap);
        paramMap.put("roleId", paramMap.get("id"));
        paramMap.remove("id");
        int ret = sqlService.update("rolemenu", "deleteByPrimaryKey", paramMap);
        ret = sqlService.update("rolemenu", "insertRoleMenu", paramMap);
        select(paramMap, retMap);
    }

    public void delete(String namespace, String sqlid, Map<String, Object> paramMap, Map retMap) {
        paramMap.put("roleId", paramMap.get("id"));
        int ret = sqlService.update("rolemenu", "deleteByPrimaryKey", paramMap);
        sqlService.update(namespace, sqlid, paramMap);
        this.select(paramMap, retMap);
    }

}
