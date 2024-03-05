package com.gg.midend.controller.basic;

import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class DevBranchController {

    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/manage/devBranch.selectAll")
    public Object selectTree(@RequestBody Map paramMap) {
        Map<Object, Object> returnMap = new HashMap<>();
        List<Map> all = sqlService.selectList("devBranch", "selectAll", paramMap);
        List tree = buildTreeAll("0000", all);
        returnMap.put(paramMap.get("listKey"), tree);
        return returnMap;
    }

    // 查询树  与原有区别：传入顶层id为String类型
    private List<Map> buildTreeAll(String id, List<Map> treeNodes) {
        List<Map> trees = new ArrayList<>();
        Iterator var5 = treeNodes.iterator();
        while (var5.hasNext()) {
            Map treeNode = (Map) var5.next();
            if (id.equals(treeNode.get("id"))) {
                trees.add(treeNode);
            }
            Iterator var7 = treeNodes.iterator();
            while (var7.hasNext()) {
                Map it = (Map) var7.next();
                if (it.get("parId").equals(treeNode.get("id"))) {
                    if (treeNode.get("children") == null) {
                        treeNode.put("children", new ArrayList());
                    }
                    List<Map> routes = (List) treeNode.get("children");
                    routes.add(it);
                }
            }
        }
        return trees;
    }

    // 只可以选子节点
    @RequestMapping(value = "/manage/devBranch.selectForEdit")
    public Object selectForEdit(@RequestBody Map paramMap) {
        Map<Object, Object> returnMap = new HashMap<>();
        List<Map> all = sqlService.selectList("devBranch", "selectForEdit", paramMap);
        boolean onlyCheckChild = (boolean) paramMap.get("onlyCheckChild");
        List tree = buildTreeForEdit("0000", all, "children", "key", onlyCheckChild);
        if (!"".equals(paramMap.get("listKey"))) {
            returnMap.put(paramMap.get("listKey"), tree);
        }
        return returnMap;
    }

    // 该树只能选子节点
    private List<Map> buildTreeForEdit(String id, List<Map> treeNodes, String childName, String key, boolean onlyCheckChild) {
        List<Map> trees = new ArrayList<>();
        Iterator var5 = treeNodes.iterator();
        while (var5.hasNext()) {
            Map treeNode = (Map) var5.next();
            if (onlyCheckChild) {
                treeNode.put("disabled", !Objects.equals(treeNode.get("level"), "2"));
            }
            if (id.equals(treeNode.get(key))) {
                trees.add(treeNode);
            }
            Iterator var7 = treeNodes.iterator();
            while (var7.hasNext()) {
                Map it = (Map) var7.next();
                if (onlyCheckChild) {
                    it.put("disabled", !Objects.equals(it.get("level"), "3"));
                }
                if (it.get("parId").equals(treeNode.get(key))) {
                    if (treeNode.get(childName) == null) {
                        treeNode.put(childName, new ArrayList());
                    }
                    List<Map> routes = (List) treeNode.get(childName);
                    routes.add(it);
                }
            }
        }
        return trees;
    }
}
