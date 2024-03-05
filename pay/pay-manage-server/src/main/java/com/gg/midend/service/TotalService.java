package com.gg.midend.service;

import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TotalService {

    @Autowired
    private SqlService sqlService;

    //首页统计信息
    public Object selectTotal(Map paramMap) {
        Map retMap = new HashMap();
        // 饼图
        List pieAmt = sqlService.selectList("payTotal", "selectPieAmt", paramMap);  // 交易金额占比
        List pieConut = sqlService.selectList("payTotal", "selectPieCount", paramMap);  // 交易笔数占比

        // 折线图
        List lineAmt = sqlService.selectList("payTotal", "selectLineAmt", paramMap);  // 交易量走势(单位:笔)
        List lineCount = sqlService.selectList("payTotal", "selectLineCount", paramMap);  // 交易量走势(单位:笔)

        // 卡片
        List<Map> total = sqlService.selectList("payTotal", "selectTotal", paramMap);  // 交易量总量(单位:元)
        Map totalMap = total.get(0);
        double totalAmt = Double.parseDouble(totalMap.get("payAmt").toString());    //总金额
        int totalCount = Integer.valueOf(totalMap.get("payCount").toString());  //总笔数

        List<Map> totalType = sqlService.selectList("payTotal", "selectTotalByType", paramMap);  // 交易量总量(类型)(单位:元)
        List totalList = new ArrayList();
        for (Map map : totalType) {
            double count = Integer.valueOf(map.get("payCount").toString());
            double amt = Double.parseDouble(map.get("payAmt").toString());
            if (!(totalAmt == 0) || !(totalCount == 0)) {
                map.put("payCountPercent", (count * 100) / totalCount);
                map.put("payAmtPercent", (amt * 100) / totalAmt);
            } else {
                map.put("payCountPercent", 0);
                map.put("payAmtPercent", 0);
            }
            totalList.add(map);
        }
        retMap.put("pieAmt", pieAmt);
        retMap.put("pieConut", pieConut);
        retMap.put("lineAmt", lineAmt);
        retMap.put("lineCount", lineCount);
        retMap.put("card", totalList);
        return retMap;
    }
}
