package com.gg.midend.controller.menu;

import com.gg.core.controller.BaseController;
import com.gg.midend.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
public class MenuController extends BaseController {
    @Autowired
    private MenuService menuService;

    @PostMapping(value = "/api/menu/{domain}.{sqlid}")
    public Object controller(@RequestBody Map paramMap) {

        HashMap retMap = new HashMap();

        if (paramMap.get("listKey") != null) {
            retMap.put(paramMap.get("listKey"), menuService.getAll(paramMap));
        } else {
            retMap.put("list", menuService.getAll(paramMap));
        }
        return success(retMap);


    }
}
