package com.gg.midend.controller.basic;

import com.gg.core.controller.BaseController;
import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class InstitutionController extends BaseController {

    @Autowired
    private SqlService sqlService;

    @PostMapping(value = "/manage/hospBranch/delete")
    public Object delete(@RequestBody Map paramMap) {
        Map<String, Boolean> returnMap = new HashMap<>();
        List<String> firstIds = new ArrayList();
        List<String> deleteIds = new ArrayList();

        try{
            //通过id查到id或par_id等于id的数据
            List<Map> institutionList = sqlService.selectList("hospBranch", "selectById", paramMap);
            for (Map map : institutionList){
                firstIds.add((String) map.get("id"));
                deleteIds.add((String) map.get("id"));
            }

            //查找子ID
            for(int i=1;i<firstIds.size();i++){
                Map idMap = new HashMap();
                idMap.put("id",firstIds.get(i));
                List<Map> institutionList2 = sqlService.selectList("hospBranch", "selectById", idMap);
                for (Map map : institutionList2){
                    deleteIds.add((String) map.get("id"));
                }
            }
            //去重
            HashSet<String> set = new HashSet<>(deleteIds);
            //去重后转成List进行批量删除
            List<String> deleteList = new ArrayList<String>(set);

            Map deleteMap = new HashMap();
            deleteMap.put("deleteList",deleteList);
            sqlService.selectList("hospBranch", "delete", deleteMap);

            returnMap.put("success",true);

        }catch (Exception e){
            returnMap.put("success",false);
        }

        return returnMap;
    }
}
