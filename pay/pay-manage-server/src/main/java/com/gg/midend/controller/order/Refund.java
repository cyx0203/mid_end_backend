package com.gg.midend.controller.order;

import com.gg.core.service.SqlService;
import com.gg.midend.utils.ConnHttp;
import com.gg.midend.utils.MD5;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: 樊亦村
 * Created time: 2023-02-25
 * Description: 处理退款流程
 */
@RestController
public class Refund {

    @Autowired
    private SqlService sqlService;

    @Autowired
    ConnHttp connHttp;

    private static final Logger logger = LoggerFactory.getLogger(Refund.class);

    @Value("${payRefundUrl.createRefundOrder}")
    String createRefundOrder; // 0005

    @Value("${payRefundUrl.refundOrder}")
    String refundOrder; // 0006

    @Value("${payRefundUrl.refundQuery}")
    String refundQuery; // 0007

    @Value("${payRefundUrl.queryPayOrder}")
    String queryPayOrder; // 0008

    @Value("${payRefundUrl.queryRefundOrder}")
    String queryRefundOrder; // 0009

    @RequestMapping(value = "/manage/payOrder.refund")
    @Transactional(rollbackFor = {Exception.class})
    public Object refund(@RequestBody Map<String, Object> paramMap) throws Exception {
        //校验退款超级密码 start
        String password = (String) paramMap.get("password");
        List payUserPasswordList = sqlService.selectList("payUserPassword" , "selectPassword", paramMap);
        if (payUserPasswordList == null || payUserPasswordList.size() == 0) {
            throw new Exception("你还没有设置退款密码。");
        }
        if (!password.equals(((Map) payUserPasswordList.get(0)).get("password"))) {
            throw new Exception("退款密码错误。");
        }
        //校验退款超级密码 end
        //调用退款接口 start
        //查询医院md5key
//        Map md5KeyMap = sqlService.selectOne("payMerchant.selectMD5", paramMap);
//        String md5Key = md5KeyMap.get("md5Key").toString();
//        if (md5Key == null || "".equals(md5Key)) {
//            throw new Exception("管理尚未设置交易密钥。");
//        }
        String md5Key = "";
        //步骤一：创建退费订单
        Map retMap = new HashMap();
        String listKey = (String) paramMap.get("listKey");
        Map croMap = sendUrl(paramMap, "0005", md5Key, createRefundOrder);
        Map dataMap = (Map) croMap.get("data");
        //步骤二：创建退费订单请求返回结果
        paramMap.put("refundorderId", dataMap.get("refundorderId"));
        Map roMap = sendUrl(paramMap, "0006", md5Key, refundOrder);
        Map dataMap1 = (Map) roMap.get("data");
        logger.debug("本次退款请求退了 {} 分", dataMap1.get("refundAmt"));
        paramMap.remove("refundAmt");
        paramMap.remove("refundReason");
        //步骤三：记录操作员的退费操作
//        Map param = new HashMap();
//        param.put("fOperid", paramMap.get("account"));
//        param.put("fOrderid", paramMap.get("refundorderId"));
//        param.put("fOrdertrace", paramMap.get("orderId"));
//        param.put("fOpertype", "1");
//        sqlService.update("payOperRecord.insert", param);
        //步骤四：退费查询
        Map aMap = sendUrl(paramMap, "0007", md5Key, refundQuery);
        retMap.put(listKey, aMap);

        //添加操作记录
        paramMap.put("orderTrace",paramMap.get("orderId"));
        paramMap.remove("orderId");
        paramMap.put("orderId",paramMap.get("refundorderId"));
        Map queryMap = new HashMap();
        queryMap.put("orderTrace",paramMap.get("orderTrace"));
        List queryList = sqlService.selectList("payOperateRecord", "selectById", queryMap);
        if(queryList.size() > 0){
            sqlService.selectList("payOperateRecord", "deleteById", queryMap);
        }
        sqlService.selectList("payOperateRecord", "insert", paramMap);

        //调用退款接口 end
        return 1;
    }

    private Map sendUrl(Map paramMap, String tradeType, String md5Key, String refundUrl) throws Exception {
        logger.debug("============" + tradeType + " ==" + "start" + "== " + refundUrl + "=================");
        Map<String, Object> map = new HashMap<>();
        String paramMapStr = Json.toJson(paramMap, JsonFormat.compact());
        String paramMapMd5 = paramMapStr + "" + md5Key;
        String sign = MD5.MD5Encode(paramMapMd5);
        map.put("tradeParam", paramMap);
        map.put("tradeType", tradeType);
        map.put("sign", sign);
        logger.debug("向 {} 发送参数: {}", tradeType, map);
        Map rspMap = (Map) connHttp.send(refundUrl, map);
        logger.debug("{} 返回参数: {}", tradeType, rspMap);
        //判断返回结果
        if (rspMap == null) {
            throw new Exception("退款前置服务异常");
        }
        String returnCode = (String) rspMap.get("returnCode");
        //已经存在退费订单，直接进行返回
        if("2010".equals(returnCode) && "0005".equals(tradeType)){
            logger.debug("============" + tradeType + " ==" + "end" + "== " + refundUrl + "=================");
            return rspMap;
        }
        if (!"0000".equals(returnCode)) {
            throw new Exception(rspMap.get("returnInfo").toString());
        }
        logger.debug("============" + tradeType + " ==" + "end" + "== " + refundUrl + "=================");
        return rspMap;
    }
}
