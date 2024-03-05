package com.gg.midend.controller.system;

import com.gg.core.controller.BaseController;
import com.gg.core.service.SqlService;
import com.gg.midend.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.*;

@RestController
public class MenuController extends BaseController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private SqlService sqlService;

    @PostMapping(value = "/manage/menu/{domain}.{sqlid}")
    public Object controller(@RequestBody Map paramMap) {
        System.out.println("sss");
        HashMap retMap = new HashMap();

        if (paramMap.get("listKey") != null) {
            retMap.put(paramMap.get("listKey"), menuService.getAll(paramMap));
        } else {
            retMap.put("list", menuService.getAll(paramMap));
        }
        return success(retMap);
    }

    @PostMapping(value = "/manage/menu/delete")
    public Object delete(@RequestBody Map paramMap) {
        Map<String, Boolean> returnMap = new HashMap<>();
        List<Integer> firstIds = new ArrayList();
        List<Integer> deleteIds = new ArrayList();

        try{
            //通过id查到id或par_id等于id的数据
            List<Map> menuList = sqlService.selectList("menu", "selectById", paramMap);
            for (Map map : menuList){
                firstIds.add((Integer) map.get("id"));
                deleteIds.add((Integer) map.get("id"));
            }

            //查找子ID
            for(int i=1;i<firstIds.size();i++){
                Map idMap = new HashMap();
                idMap.put("id",firstIds.get(i));
                List<Map> menuList2 = sqlService.selectList("menu", "selectById", idMap);
                for (Map map : menuList2){
                    deleteIds.add((Integer) map.get("id"));
                }
            }
            //去重
            HashSet<Integer> set = new HashSet<>(deleteIds);
            //去重后转成List进行批量删除
            List<Integer> deleteList = new ArrayList<Integer>(set);

            Map deleteMap = new HashMap();
            deleteMap.put("deleteList",deleteList);
            sqlService.selectList("menu", "delete", deleteMap);

            returnMap.put("success",true);

        }catch (Exception e){
            returnMap.put("success",false);
        }

        return returnMap;
    }
}
