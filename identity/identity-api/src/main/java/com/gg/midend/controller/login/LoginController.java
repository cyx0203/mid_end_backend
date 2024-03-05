package com.gg.midend.controller.login;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class LoginController {

    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = {"/currentUser", "/api/currentUser"})
    public Object currentUser(@RequestBody Map paramMap) throws ExceptionCenter {
        if (paramMap.get("account") == null) {
            throw new ExceptionCenter("500", "参数有误");
        }
        List<Map> list = sqlService.selectList("user", "selectByPrimaryKey", "accountRef", (String) paramMap.get("account"));

        if (list.size() != 1) {
            throw new ExceptionCenter("500", "请重新登录");
        }
        Map userMap = list.get(0);
        return userMap;

    }

    @RequestMapping(value = {"/login.account", "/api/login.account"})
    public Object login(@RequestBody Map paramMap) throws ExceptionCenter {
        Map retMap = new HashMap();
        List<Map> list = sqlService.selectList("user", "login",
                "account", paramMap.get("account"),
                "password", paramMap.get("password"));

        if (list.size() != 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "用户名或密码错误");
        }
        Map map = list.get(0);
        if (Objects.equals(map.get("state"), 2)) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "用户被加入黑名单");
        }
        if (Objects.equals(map.get("state"), 4)) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "用户被停用");
        }
        if (map.get("role_id") == null) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "用户角色未定义");
        }
        int roleid = (int) list.get(0).get("role_id");
        int userid = (int) list.get(0).get("id");
        retMap.put("success", true);
        retMap.put("userid", userid);
        retMap.put("roleid", roleid);
        retMap.put("returnCode", "0000");
        retMap.putAll(map);
        List menu = sqlService.selectList("menu", "selectByRoleid", "role_id", roleid);
        List<Map> rootMenu = build(1, menu, "routes");
        retMap.put("menu", rootMenu);
        return retMap;
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
