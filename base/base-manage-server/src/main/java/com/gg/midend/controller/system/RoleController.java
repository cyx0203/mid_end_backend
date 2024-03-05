package com.gg.midend.controller.system;

import com.gg.core.controller.BaseController;
import com.gg.midend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @RequestMapping({"/manage/role/role.{sqlid}"})
    public Object controller(@PathVariable String sqlid, @RequestBody Map paramMap) {
        Map retMap = new HashMap();
        if ("insertSelective".equals(sqlid)) {
            roleService.insert("role", "insertSelective", paramMap, retMap);
        }
        if ("updateByPrimaryKeySelective".equals(sqlid)) {
            roleService.update("role", "updateByPrimaryKeySelective", paramMap, retMap);
        }
        if ("deleteByPrimaryKey".equals(sqlid)) {
            roleService.delete("role", "deleteByPrimaryKey", paramMap, retMap);
        }
        return success(retMap);
    }
}
