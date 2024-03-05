package com.gg.midend.service;

import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MenuService {

    @Autowired
    private SqlService sqlService;

    public List<Map> getAll(Map paramMap) {
        List all = sqlService.selectList("menu", "selectByPrimaryKey", paramMap);
        return build(1, all, (String) paramMap.get("childName"));
    }

    public List<Map> build(int id, List<Map> treeNodes, String childName) {
        List<Map> trees = new ArrayList<Map>();
        for (Map treeNode : treeNodes) {
            if (id == (int) treeNode.get("key")) {
                trees.add(treeNode);
            }
            for (Map it : treeNodes) {
                if (it.get("parentId").equals(treeNode.get("key"))) {
                    if (treeNode.get(childName) == null) {
                        treeNode.put(childName, new ArrayList<Map>());
                    }
                    List<Map> routes = (List<Map>) treeNode.get(childName);
                    routes.add(it);
                }
            }
        }
        return trees;
    }

}
