package com.gg.midend.controller.pay;

import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：ml
 * 功能：支付管理管理
 */
@RestController
public class PayController {

    @Autowired
    private SqlService sqlService;

    /**
     * 描述：分表查询数据库并组装数据
     *
     * @param paramMap 入参
     * @return Object 返回值
     */
    @RequestMapping(value = "/manage/merchantPay.queryAll")
    public Object getStatResult(@RequestBody Map paramMap) {
        Map<String, Object> returnMap = new HashMap<>();
        List merchantList=new ArrayList();
        if(paramMap.containsKey("keywords")){
            merchantList = sqlService.selectList("merMerchant", "selectLike", paramMap); //模糊查询
        }else{
            merchantList = sqlService.selectList("merMerchant", "selectAll", paramMap); //所有商户
        }
        List merChannelList = sqlService.selectList("merChannel", "selectAll", paramMap); //支付渠道
        List merPayTypeList = sqlService.selectList("merPayType", "selectJoinPayType", paramMap); //支付方式
        List merParamAlipayList = sqlService.selectList("merParamAlipay", "selectAll", paramMap); //支付宝参数
        List merParamWechatList = sqlService.selectList("merParamWechat", "selectAll", paramMap); //微信参数
        List merHospitalList = sqlService.selectList("merHospital", "selectAll", paramMap); //医院


        for (Object merchantO : merchantList) {
            Map merchantMap = (Map) merchantO;
            String merchantId = (String) merchantMap.get("id");
            //查询支付渠道
            List<Map> channelInfo = new ArrayList<>();
            for (Object channelO : merChannelList) {
                Map channelMap = (Map) channelO;
                if (merchantId.equals(channelMap.get("merchantId"))) {
                    channelInfo.add(channelMap);
                }
            }
            //查询支付方式
            List<Map> payTypeInfo = new ArrayList<>();
            for (Object modelO : merPayTypeList) {
                Map modelMap = (Map) modelO;
                if (merchantId.equals(modelMap.get("merchantId"))) {
                    payTypeInfo.add(modelMap);
                }
            }
            //支付宝支付参数
            List<Map> alipayInfo = new ArrayList<>();
            for (Object paramO : merParamAlipayList) {
                Map paramsMap = (Map) paramO;
                if (merchantId.equals(paramsMap.get("merchantId"))) {
                    alipayInfo.add(paramsMap);
                }
            }

            //微信支付参数
            List<Map> wechatInfo = new ArrayList<>();
            for (Object paramO : merParamWechatList) {
                Map paramsMap = (Map) paramO;
                if (merchantId.equals(paramsMap.get("merchantId"))) {
                    Map wxMap = new HashMap();
                    wxMap.put("serviceMerchantId",paramsMap.get("merchantId"));
                    List wxList = sqlService.selectList("merParamWechatSub", "selectById", wxMap); //微信参数
                    paramsMap.put("wechatSub",wxList);
                    wechatInfo.add(paramsMap);
                }
            }

            //医院
            List<Map> hospitalInfo = new ArrayList<>();
            for (Object paramO : merHospitalList) {
                Map paramsMap = (Map) paramO;
                if (merchantId.equals(paramsMap.get("merchantId"))) {
                    hospitalInfo.add(paramsMap);
                }
            }
            merchantMap.put("channelList", channelInfo);
            merchantMap.put("payTypeList", payTypeInfo);
            merchantMap.put("alipayList", alipayInfo);
            merchantMap.put("wechatList", wechatInfo);
            merchantMap.put("hospitalList", hospitalInfo);
        }
        returnMap.put("payMerchantList", merchantList);
        return returnMap;
    }


    @RequestMapping(value = "/manage/WxParam.update")
    public Object updateWxParam(@RequestBody Map paramMap) {
        Map<String, Boolean> returnMap = new HashMap<>();
        List wechatSub = (List) paramMap.get("wechatSub");

        try{
            sqlService.selectList("merParamWechat", "update", paramMap);
            sqlService.selectList("merParamWechatSub", "delete", paramMap);
            //有子商户参数
            if(wechatSub.size() > 0){
                Map wxMap = new HashMap();
                wxMap.put("wechatSub",wechatSub);
                sqlService.selectList("merParamWechatSub", "insert", wxMap);
            }

            returnMap.put("success",true);
        }catch(Exception e){
            returnMap.put("success",false);
        }
        return returnMap;
    }

    @RequestMapping(value = "/manage/WxParam.insert")
    public Object insertWxParam(@RequestBody Map paramMap) {
        Map<String, Boolean> returnMap = new HashMap<>();
        List wechatSub = (List) paramMap.get("wechatSub");

        try{
            sqlService.selectList("merParamWechat", "insert", paramMap);
            //有子商户参数
            if(wechatSub.size() > 0){
                Map wxMap = new HashMap();
                wxMap.put("wechatSub",wechatSub);
                sqlService.selectList("merParamWechatSub", "insert", wxMap);
            }

            returnMap.put("success",true);
        }catch(Exception e){
            returnMap.put("success",false);
        }
        return returnMap;
    }

    @RequestMapping(value = "/manage/WxParam.delete")
    public Object deleteWxParam(@RequestBody Map paramMap) {
        Map<String, Boolean> returnMap = new HashMap<>();
        try{
            sqlService.selectList("merParamWechat", "delete", paramMap);
            sqlService.selectList("merParamWechatSub", "delete", paramMap);

            returnMap.put("success",true);
        }catch(Exception e){
            returnMap.put("success",false);
        }
        return returnMap;
    }

}
