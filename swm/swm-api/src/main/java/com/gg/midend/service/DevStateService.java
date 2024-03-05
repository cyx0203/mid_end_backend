package com.gg.midend.service;

import com.alibaba.fastjson.JSON;
import com.gg.core.service.SqlService;
import com.gg.midend.utils.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DevStateService {

    @Autowired
    private SqlService sqlService;

    /**
     * TODO
     *
     * @description: agent更新状态，只更新des_dev_state表里有的数据，在设备监控页面有启用监控和关闭监控，分别在启用和关闭时插入或删除des_dev_state数据
     * @author: lufm
     * @date: 2023/4/6 14:52
     * @param: [paramMap]
     * @return: java.lang.Object
     **/
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Object update(Map paramMap) throws Exception {
        String devid = (String) paramMap.get("devid");
        String hospitalId = MapUtil.getMapParamValue(paramMap, "hospitalId", "H001");
        //查询设备部件
        Map fidMap = new HashMap();
        fidMap.put("id", devid);
        fidMap.put("hospitalid", hospitalId);
        List<Map> devTypeList = sqlService.selectList("desdevstate", "selectDevStateByDevid", fidMap);

        //设备部件映射
        Map reqMap = new HashMap();
        reqMap.put("N2011", paramMap.get("CardReader"));
        reqMap.put("N2012", paramMap.get("CardSender"));
        reqMap.put("N2013", paramMap.get("cj201"));
        reqMap.put("N2014", paramMap.get("ReceiptPrinter"));
        reqMap.put("N2015", paramMap.get("IDCardReader"));
        reqMap.put("N2016", paramMap.get("Pinpad"));
        reqMap.put("N2017", paramMap.get("CashReceiver"));
        reqMap.put("N2018", paramMap.get("LaserPrinter"));
        reqMap.put("N2020", paramMap.get("invoice"));

        String totalState = "0";//总状态2001
        String errFlag = "0";//故障标志位
        int successCount = 0;//正常部件数量
        List upList = new ArrayList();
        List eventsInsertList = new ArrayList();
        List eventsUpdateList = new ArrayList();

        for (Map m : devTypeList) {
            // 统计正常组件数量
            if (!m.get("stateId").equals("2001") && "0".equals(reqMap.get("N" + m.get("stateId")))) {
                successCount++;
            }
            //3:故障  1、2维护
            if (!m.get("stateId").equals("2001") && "3".equals(reqMap.get("N" + m.get("stateId")))) {
                errFlag = "1";
            }
            //2001设备总状态  || 上送的状态和数据库存的状态一致
            if (m.get("stateId").equals("2001") || m.get("value").equals(reqMap.get("N" + m.get("stateId")))) {
                continue;
            }
            //不存在部件
            if (Objects.equals(reqMap.get("N" + m.get("stateId")), null)) {
                continue;
            } else {
                Map updateMap = new HashMap();
                updateMap.put("value", reqMap.get("N" + m.get("stateId")));
                updateMap.put("id", devid);
                updateMap.put("stateId", m.get("stateId"));
                updateMap.put("hospitalId", hospitalId);
                upList.add(updateMap);

                String errName = (String) sqlService.selectOne("desstate", "selectBJStatus", updateMap);
                if (errName == null || errName.equals("null")) {
                    errName = "未知故障";
                }

                updateMap.put("title", "设备" + devid + "的" + m.get("bjName") + "发生"
                        + errName + "," + m.get("zhName") + "/" + m.get("wdName") + "/" + m.get("instAddr"));

                //原先正常的部件如果异常后新插入一条记录
                if (m.get("value").equals("0")) {
                    eventsInsertList.add(updateMap);
                } else {
                    //原先不正常的记录，正常后更新状态
                    eventsUpdateList.add(updateMap);
                }
            }
        }


        if (successCount == devTypeList.size() - 1) {//所有状态都是0，则总状态是0
            totalState = "0";
        }else{
            if (errFlag.equals("1")) {
                //故障可用
                totalState = "7";
            } else {
                //其他如1、2都是维护
                totalState = "4";
            }
        }
        Map updateMap = new HashMap();
        updateMap.put("value", totalState);
        updateMap.put("id", devid);
        updateMap.put("stateId", "2001");
        updateMap.put("hospitalId", hospitalId);
        upList.add(updateMap);

        Map listMap = new HashMap();
        listMap.put("list", upList);
        //更新设备状态
        sqlService.update("desdevstate", "updateDevStates", listMap);
        if (!eventsInsertList.isEmpty()) {
            Map eventsInsertListMap = new HashMap();
            eventsInsertListMap.put("list", eventsInsertList);
            sqlService.update("desevent", "insertErrEvents", eventsInsertListMap);
        }
        if (!eventsUpdateList.isEmpty()) {
            HashMap<Object, Object> eventsUpdateListMap = new HashMap<>();
            eventsUpdateListMap.put("list", eventsUpdateList);
            sqlService.update("desevent", "updateEvents", eventsUpdateListMap);
        }
        return 1;
    }

    public Object statusUpdate(Map paramMap){
        List list = (List) paramMap.get("diskUse");
        String l = JSON.toJSONString(list);
        paramMap.put("diskUse", l);
        sqlService.update("desdevuse","insertSelective", paramMap);
        return 1;
    }

}
