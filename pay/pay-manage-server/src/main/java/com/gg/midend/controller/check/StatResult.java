package com.gg.midend.controller.check;

import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：樊亦村
 * 功能：对账统计结果查询
 */
@RestController
public class StatResult {

    @Autowired
    private SqlService sqlService;

    /**
     * 描述：查询对账汇总结果
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    @RequestMapping(value = "/manage/checkResult.getStatResult")
    public Object getStatResult(@RequestBody Map paramMap) {
        Map<String, Object> returnMap = new HashMap<>();
        //查询商户对账结果
        List statusList = sqlService.selectList("chkResultStatus", "selectStatus", paramMap);
        returnMap.put("checkStatusList", statusList);
        returnMap.put("inOutStat", getInOutStat(paramMap));
        if (!statusList.isEmpty()) {
            //查询三方的多帐条目
            paramMap.put("accountSource", "1");
            List thirdMoreList = sqlService.selectList("chkErrorRecord", "selectAll", paramMap);
            returnMap.put("thirdMoreList", thirdMoreList);
            //查询业务（HIS）的多帐条目
            paramMap.put("accountSource", "2");
            List bizMoreList = sqlService.selectList("chkErrorRecord", "selectAll", paramMap);
            returnMap.put("bizMoreList", bizMoreList);
        }
        return returnMap;
    }

    /**
     * 描述：查询三方和业务（HIS）的收费、退费的笔数、金额
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    private Map getInOutStat(Map paramMap) {
        Map<String, Object> statMap = new HashMap<>();
        //查询三方收费、退费的笔数、金额
        List thirdStatList = sqlService.selectList("chkThirdTrans", "selectStat", paramMap);
        for (Object o : thirdStatList) {
            Map thirdMap = (Map) o;
            int transType = (int) thirdMap.get("transType");
            if (transType == 1) {
                //三方收费信息，包含笔数和金额
                statMap.put("thirdIn", thirdMap);
            } else if (transType == -1) {
                //三方退费信息，包含笔数和金额
                statMap.put("thirdOut", thirdMap);
            }
        }
        //查询业务（HIS）收费、退费的笔数、金额
        List bizStatList = sqlService.selectList("chkBizTrans", "selectStat", paramMap);
        for (Object o : bizStatList) {
            Map bizMap = (Map) o;
            int transType = (int) bizMap.get("transType");
            if (transType == 1) {
                //业务（HIS）收费信息，包含笔数和金额
                statMap.put("bizIn", bizMap);
            } else if (transType == -1) {
                //业务（HIS）退费信息，包含笔数和金额
                statMap.put("bizOut", bizMap);
            }
        }
        return statMap;
    }

    /**
     * 描述：调账
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    @RequestMapping(value = "/manage/checkResult.adjust")
    public Object adjust(@RequestBody Map paramMap) {
        Map<String, Object> returnMap = new HashMap<>();
        try{
            Map queryMap = new HashMap();
            queryMap.put("orderTrace",paramMap.get("orderTrace"));
            List queryList = sqlService.selectList("payOperateRecord", "selectById", queryMap);
            if(queryList.size() > 0){
                sqlService.selectList("payOperateRecord", "deleteById", queryMap);
            }

            //增加操作记录并且将errorType改为0
            sqlService.selectList("payOperateRecord", "insert", paramMap);
            sqlService.selectList("chkErrorRecord", "updateErrorType", paramMap);
            //去查当前商户、当前日期、三方和业务账单是否全为0
            Integer count = (Integer) sqlService.selectOne("chkErrorRecord", "selectCount", paramMap);
            //当count为0时，说明没有错账了，需要去修改账单状态
            if(count == 0){
                sqlService.selectList("chkResultStatus", "updateStatus", paramMap);
            }
            returnMap.put("success",true);
        }catch (Exception e){
            returnMap.put("success",false);
        }
        return returnMap;
    }

}
