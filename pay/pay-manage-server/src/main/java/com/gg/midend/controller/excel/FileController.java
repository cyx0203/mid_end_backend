package com.gg.midend.controller.excel;

import cn.hutool.db.sql.Order;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.midend.beans.*;
import com.gg.midend.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class FileController {

    @Autowired
    private SqlService sqlService;

    //生成报表访问地址
    @Value("${file.fileUrl}")
    private String fileUrl;

    //报表模板存放本地路径
    @Value("${file.templateDir}")
    private String templateDir;

    //生成报表存放本地路径
    @Value("${file.fileDir}")
    private String fileDir;

    //生成对账明细数据
    @RequestMapping({"/downloadCheckDetail"})
    public Object downloadCheckDetail(@RequestBody Map paramMap) throws ExceptionCenter {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        String date_start = (String) paramMap.get("startDate");
        String date_end = (String) paramMap.get("endDate");
        String startdate = LocalDate.parse(DateUtils.foramtLocaldate(date_start)).format(fmt);
        String enddate = LocalDate.parse(DateUtils.foramtLocaldate(date_end)).format(fmt);
        String fileName = "对账明细" + "-" + date_start + "-" + date_end + ".xlsx";
        if (Objects.equals(date_start, date_end)) {
            fileName = "对账明细" + "-" + date_start + ".xlsx";
        }

        paramMap.put("startdate", date_start);
        paramMap.put("enddate", date_end);
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        ExcelWriter excelWriter = EasyExcel.write(fileDir + fileName).withTemplate(templateDir + "checkDetail.xlsx").build();
        excelWriter.fill(createCheckData(paramMap), writeSheet);
        // 写入list之前的数据
        Map map = new HashMap();
        Map h = (Map) sqlService.selectOne("hospital", "selectNameByHospitalId", paramMap);
        map.put("account", (String) paramMap.get("account"));
        map.put("startdate", startdate);
        map.put("enddate", enddate);
        map.put("hospitalName", h.get("name").toString());
        excelWriter.fill(map, writeSheet);
        excelWriter.finish();
        Map retMap = new HashMap();
        retMap.put("url", fileUrl + fileName);
        return retMap;
    }


    //生成订单明细数据
    @RequestMapping({"/downloadOrderDetail"})
    public Object downloadOrderDetail(@RequestBody Map paramMap) throws ExceptionCenter {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        String date_start = (String) paramMap.get("startDate");
        String date_end = (String) paramMap.get("endDate");
        String startdate = LocalDate.parse(DateUtils.foramtLocaldate(date_start)).format(fmt);
        String enddate = LocalDate.parse(DateUtils.foramtLocaldate(date_end)).format(fmt);
        String fileName = "订单明细" + "-" + date_start + "-" + date_end + ".xlsx";
        if (Objects.equals(date_start, date_end)) {
            fileName = "订单明细" + "-" + date_start + ".xlsx";
        }

        paramMap.put("startdate", date_start);
        paramMap.put("enddate", date_end);
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        ExcelWriter excelWriter = EasyExcel.write(fileDir + fileName).withTemplate(templateDir + "orderDetail.xlsx").build();
        excelWriter.fill(createOrderData(paramMap), writeSheet);
        // 写入list之前的数据
        Map map = new HashMap();
        Map o = (Map) sqlService.selectOne("payOrderPay", "selectPayTotal", paramMap);
        map.put("account", (String) paramMap.get("account"));
        map.put("startdate", startdate);
        map.put("enddate", enddate);
        map.put("hospitalName", (String) paramMap.get("hospitalName"));
        map.put("incount", o.get("incount").toString());
        map.put("income", o.get("income").toString());
        excelWriter.fill(map, writeSheet);
        excelWriter.finish();
        Map retMap = new HashMap();
        retMap.put("url", fileUrl + fileName);
        return retMap;
    }

    //生成订单
    @RequestMapping({"/downloadTransDetail"})
    public Object downloadTransDetail(@RequestBody Map paramMap) throws ExceptionCenter {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        String date_start = (String) paramMap.get("startDate");
        String date_end = (String) paramMap.get("endDate");
        String startdate = LocalDate.parse(DateUtils.foramtLocaldate(date_start)).format(fmt);
        String enddate = LocalDate.parse(DateUtils.foramtLocaldate(date_end)).format(fmt);
        String fileName = "订单汇总" + "-" + date_start + "-" + date_end + ".xlsx";
        if (Objects.equals(date_start, date_end)) {
            fileName = "订单汇总" + "-" + date_start + ".xlsx";
        }

        paramMap.put("startdate", date_start);
        paramMap.put("enddate", date_end);
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        ExcelWriter excelWriter = EasyExcel.write(fileDir + fileName).withTemplate(templateDir + "transDetail.xlsx").build();
        excelWriter.fill(createTransData(paramMap), writeSheet);
        // 写入list之前的数据
        Map map = new HashMap();
        Map o = (Map) sqlService.selectOne("payOrder", "selectForStat", paramMap);
        Map h = (Map) sqlService.selectOne("hospital", "selectNameByHospitalId", paramMap);
        map.put("account", (String) paramMap.get("account"));
        map.put("startdate", startdate);
        map.put("enddate", enddate);
        map.put("hospitalName", h.get("name").toString());
        map.put("incount", o.get("incount").toString());
        map.put("income", Fen2Yuan(o.get("income").toString()));
        excelWriter.fill(map, writeSheet);
        excelWriter.finish();
        Map retMap = new HashMap();
        retMap.put("url", fileUrl + fileName);
        return retMap;
    }


    //生成his账单数据
    @RequestMapping({"/downloadHisDetail"})
    public Object downloadHisDetail(@RequestBody Map paramMap) throws ExceptionCenter {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        String date_start = (String) paramMap.get("startDate");
        String date_end = (String) paramMap.get("endDate");
        String startdate = LocalDate.parse(DateUtils.foramtLocaldate(date_start)).format(fmt);
        String enddate = LocalDate.parse(DateUtils.foramtLocaldate(date_end)).format(fmt);
        String fileName = "业务账单明细" + "-" + date_start + "-" + date_end + ".xlsx";
        if (Objects.equals(date_start, date_end)) {
            fileName = "业务账单明细" + "-" + date_start + ".xlsx";
        }

        paramMap.put("startdate", date_start);
        paramMap.put("enddate", date_end);
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        ExcelWriter excelWriter = EasyExcel.write(fileDir + fileName).withTemplate(templateDir + "hisDetail.xlsx").build();
        excelWriter.fill(createHisData(paramMap), writeSheet);
        // 写入list之前的数据
        Map o = (Map) sqlService.selectOne("hospital", "selectNameByHospitalId", paramMap);

        Map map = new HashMap();
        map.put("account", (String) paramMap.get("account"));
        map.put("startdate", startdate);
        map.put("enddate", enddate);
        map.put("hospitalName", o.get("name").toString());
        excelWriter.fill(map, writeSheet);
        excelWriter.finish();
        Map retMap = new HashMap();
        retMap.put("url", fileUrl + fileName);
        return retMap;
    }

    //生成三方账单数据
    @RequestMapping({"/downloadThirdDetail"})
    public Object downloadThirdDetail(@RequestBody Map paramMap) throws ExceptionCenter {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        String date_start = (String) paramMap.get("startDate");
        String date_end = (String) paramMap.get("endDate");
        String startdate = LocalDate.parse(DateUtils.foramtLocaldate(date_start)).format(fmt);
        String enddate = LocalDate.parse(DateUtils.foramtLocaldate(date_end)).format(fmt);
        String fileName = "三方账单明细" + "-" + date_start + "-" + date_end + ".xlsx";
        if (Objects.equals(date_start, date_end)) {
            fileName = "三方账单明细" + "-" + date_start + ".xlsx";
        }

        paramMap.put("startdate", date_start);
        paramMap.put("enddate", date_end);
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        ExcelWriter excelWriter = EasyExcel.write(fileDir + fileName).withTemplate(templateDir + "thirdDetail.xlsx").build();
        excelWriter.fill(createThirdData(paramMap), writeSheet);
        // 写入list之前的数据
        Map o = (Map) sqlService.selectOne("hospital", "selectNameByHospitalId", paramMap);
        Map map = new HashMap();
        map.put("account", (String) paramMap.get("account"));
        map.put("startdate", startdate);
        map.put("enddate", enddate);
        map.put("hospitalName", o.get("name").toString());
        excelWriter.fill(map, writeSheet);
        excelWriter.finish();
        Map retMap = new HashMap();
        retMap.put("url", fileUrl + fileName);
        return retMap;
    }

    // 创建对账明细数据
    private List<CheckDetails> createCheckData(Map paramMap) throws ExceptionCenter {
        List<Map> detailList = (List<Map>) sqlService.selectList("chkTransDetail", "selectAll", paramMap);
        List<CheckDetails> list = new ArrayList<>();
        for (Map detailMap : detailList) {
            CheckDetails checkDetails = new CheckDetails();
            checkDetails.setAccountDate((String)detailMap.get("accountDate"));
            checkDetails.setCheckId((String)detailMap.get("checkId"));
            checkDetails.setOrderId((String)detailMap.get("orderId"));
            checkDetails.setRefundId((String)detailMap.get("refundId"));
            checkDetails.setMerchantName((String)detailMap.get("merchantName"));
            checkDetails.setChannelName((String)detailMap.get("channelName"));
            checkDetails.setPayTypeName((String)detailMap.get("payTypeName"));
            Integer transType = (Integer) detailMap.get("transType");
            if(transType == 1){
                checkDetails.setTransType("入账");
                checkDetails.setThirdTransAmt(Fen2Yuan(detailMap.get("thirdTransAmt").toString()));
                checkDetails.setBizTransAmt(Fen2Yuan(detailMap.get("bizTransAmt").toString()));
            }else{
                checkDetails.setTransType("出账");
                if("0.00".equals(Fen2Yuan(detailMap.get("thirdTransAmt").toString()))){
                    checkDetails.setThirdTransAmt(Fen2Yuan(detailMap.get("thirdTransAmt").toString()));
                }else{
                    checkDetails.setThirdTransAmt("-"+Fen2Yuan(detailMap.get("thirdTransAmt").toString()));
                }
                if("0.00".equals(Fen2Yuan(detailMap.get("bizTransAmt").toString()))){
                    checkDetails.setBizTransAmt(Fen2Yuan(detailMap.get("bizTransAmt").toString()));
                }else{
                    checkDetails.setBizTransAmt("-"+Fen2Yuan(detailMap.get("bizTransAmt").toString()));
                }
            }
            String errorType = (String)detailMap.get("errorType");
            if("0".equals(errorType)){
                checkDetails.setErrorType("平账");
            }else if("1".equals(errorType)){
                checkDetails.setErrorType("多账");
            }else{
                checkDetails.setErrorType("金额不一致");
            }

            String accountSource = (String) detailMap.get("accountSource");
            if("0".equals(accountSource)){
                checkDetails.setHisAccountSource("有");
                checkDetails.setThirdAccountSource("有");
            }else if("1".equals(accountSource)){
                checkDetails.setHisAccountSource("无");
                checkDetails.setThirdAccountSource("有");
            }else{
                checkDetails.setHisAccountSource("有");
                checkDetails.setThirdAccountSource("无");
            }
            list.add(checkDetails);
        }
        return list;
    }


    // 创建订单明细数据
    private List<OrderDetails> createOrderData(Map paramMap) throws ExceptionCenter {
        List<Map> orderList = (List<Map>) sqlService.selectList("payOrderPay", "selectPayDetail", paramMap);
        List<OrderDetails> list = new ArrayList<>();
        for (Map detailMap : orderList) {
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setAccountDate(DateUtils.parseDate((String)detailMap.get("date"),"yyyy-MM-dd HH:mm:ss"));
            orderDetails.setOrderTrace((String)detailMap.get("orderTrace"));
            orderDetails.setOrderId((String)detailMap.get("orderId"));
            orderDetails.setBuyerName((String)detailMap.get("buyerName"));
            orderDetails.setBuyerPhone((String)detailMap.get("buyerPhone"));
            orderDetails.setMerchantName((String)detailMap.get("merchantName"));
            orderDetails.setChannelName((String)detailMap.get("channelName"));
            orderDetails.setPayTypeName((String)detailMap.get("payTypeName"));
            orderDetails.setGoodsName((String)detailMap.get("goodsName"));

            Integer transType = (Integer) detailMap.get("transType");
            if(transType == 1){
                orderDetails.setTransType("入账");
                orderDetails.setOrderSum(Fen2Yuan(detailMap.get("orderSum").toString()));
                orderDetails.setOrderAmt(Fen2Yuan(detailMap.get("orderAmt").toString()));
                orderDetails.setAcctPay(Fen2Yuan(detailMap.get("acctPay").toString()));
                orderDetails.setFundPaySumamt(Fen2Yuan(detailMap.get("fundPaySumamt").toString()));
            }else{
                //出账金额需要添加-号
                orderDetails.setTransType("出账");
                if("0.00".equals(Fen2Yuan(detailMap.get("orderSum").toString()))){
                    orderDetails.setOrderSum(Fen2Yuan(detailMap.get("orderSum").toString()));
                }else{
                    orderDetails.setOrderSum("-"+Fen2Yuan(detailMap.get("orderSum").toString()));
                }
                if("0.00".equals(Fen2Yuan(detailMap.get("orderAmt").toString()))){
                    orderDetails.setOrderAmt(Fen2Yuan(detailMap.get("orderAmt").toString()));
                }else{
                    orderDetails.setOrderAmt("-"+Fen2Yuan(detailMap.get("orderAmt").toString()));
                }
                if("0.00".equals(Fen2Yuan(detailMap.get("acctPay").toString()))){
                    orderDetails.setAcctPay(Fen2Yuan(detailMap.get("acctPay").toString()));
                }else{
                    orderDetails.setAcctPay("-"+Fen2Yuan(detailMap.get("acctPay").toString()));
                }
                if("0.00".equals(Fen2Yuan(detailMap.get("fundPaySumamt").toString()))){
                    orderDetails.setFundPaySumamt(Fen2Yuan(detailMap.get("fundPaySumamt").toString()));
                }else{
                    orderDetails.setFundPaySumamt("-"+Fen2Yuan(detailMap.get("fundPaySumamt").toString()));
                }
            }

            String status = (String)detailMap.get("status");
            if("0".equals(status)){
                orderDetails.setStatus("待确认");
            }else if("1".equals(status)){
                orderDetails.setStatus("已确认");
            }else{
                orderDetails.setStatus("订单关闭");
            }

            list.add(orderDetails);
        }
        return list;
    }

    // 创建订单数据
    private List<TransDetails> createTransData(Map paramMap) throws ExceptionCenter {
        List<Map> detailList = (List<Map>) sqlService.selectList("payOrder", "selectAll", paramMap);
        List<TransDetails> list = new ArrayList<>();
        for (Map detailMap : detailList) {
            TransDetails transDetails = new TransDetails();
            transDetails.setMerchantName((String) detailMap.get("merchantName"));
            transDetails.setOrderTrace((String) detailMap.get("orderTrace"));
            transDetails.setChannelName((String) detailMap.get("channelName"));
            transDetails.setGoodsName((String) detailMap.get("goodsName"));
            transDetails.setBuyerName((String) detailMap.get("buyerName"));
            transDetails.setPayStatus(fomatPayStatus((String) detailMap.get("payStatus")));
            transDetails.setRefundStatus(fomatRefundStatus((String) detailMap.get("refundStatus")));
            transDetails.setCreateTimeFormat((String) detailMap.get("createTimeFormat"));
            transDetails.setOrderAmt(Fen2Yuan(detailMap.get("orderAmt").toString()));
            transDetails.setRefundAmt(Fen2Yuan(detailMap.get("refundAmt").toString()));
            list.add(transDetails);
        }
        return list;
    }

    // 创建his账单数据
    private List<HisDetails> createHisData(Map paramMap) throws ExceptionCenter {
        List<Map> detailList = (List<Map>) sqlService.selectList("chkBizTrans", "selectAll", paramMap);
        List<HisDetails> list = new ArrayList<>();
        for (Map detailMap : detailList) {
            HisDetails hisDetails = new HisDetails();
            Integer transType = (Integer) detailMap.get("transType");
            String checkOriginId = (String) detailMap.get("checkOriginId");
            String checkId = (String) detailMap.get("checkId");

            hisDetails.setMerchantName((String) detailMap.get("merchantName"));
            hisDetails.setTransType((transType.equals(1)) ? "入账" : "出账");
            hisDetails.setThirdName((String) detailMap.get("thirdName"));
            hisDetails.setPayTypeName((String) detailMap.get("payTypeName"));
            hisDetails.setTransTypeName((String) detailMap.get("transTypeName"));
            hisDetails.setFlowNo((transType.equals(1)) ? checkId : checkOriginId);
            hisDetails.setTransAmt(Fen2Yuan(detailMap.get("transAmt").toString()));
            hisDetails.setOperId((String) detailMap.get("operId"));
            hisDetails.setTransTime((String) detailMap.get("transTime"));
            hisDetails.setRefundFlowNo((transType.equals(1)) ? checkOriginId : checkId);
            hisDetails.setSerialNo((String) detailMap.get("serialNo"));
            hisDetails.setRefundSerialNo((String) detailMap.get("refundSerialNo"));
            list.add(hisDetails);
        }
        return list;
    }

    // 创建三方账单数据
    private List<ThirdDetails> createThirdData(Map paramMap) throws ExceptionCenter {
        List<Map> detailList = (List<Map>) sqlService.selectList("chkThirdTrans", "selectAll", paramMap);
        List<ThirdDetails> list = new ArrayList<>();
        for (Map detailMap : detailList) {

            ThirdDetails thirdDetails = new ThirdDetails();
            Integer transType = (Integer) detailMap.get("transType");
            String checkOriginId = (String) detailMap.get("checkOriginId");
            String checkId = (String) detailMap.get("checkId");

            thirdDetails.setMerchantName((String) detailMap.get("merchantName"));
            thirdDetails.setTransType((transType.equals(1)) ? "入账" : "出账");
            thirdDetails.setThirdName((String) detailMap.get("thirdName"));
            thirdDetails.setFlowNo((transType.equals(1)) ? checkId : checkOriginId);
            thirdDetails.setTransAmt(Fen2Yuan(detailMap.get("transAmt").toString()));
            thirdDetails.setPayerAccount((String) detailMap.get("payerAccount"));
            thirdDetails.setTransTime((String) detailMap.get("transTime"));
            thirdDetails.setRefundFlowNo((transType.equals(1)) ? checkOriginId : checkId);
            thirdDetails.setSerialNo((String) detailMap.get("serialNo"));
            thirdDetails.setRefundSerialNo((String) detailMap.get("refundSerialNo"));
            list.add(thirdDetails);
        }
        return list;
    }

    // 格式化支付状态
    public String fomatPayStatus(String status) {
        String payStatus = "";
        switch (status) {
            case "0":
                payStatus = "初始状态";
                break;
            case "1":
                payStatus = "待支付";
                break;
            case "2":
                payStatus = "已支付";
                break;
            case "9":
                payStatus = "已关闭";
                break;
        }
        return payStatus;
    }

    // 格式化退款状态
    public String fomatRefundStatus(String status) {
        String refundStatus = "";
        switch (status) {
            case "0":
                refundStatus = "未退款";
                break;
            case "1":
                refundStatus = "全额退款";
                break;
            case "2":
                refundStatus = "部分退款";
                break;
            case "3":
                refundStatus = "冲正退款";
                break;
        }
        return refundStatus;
    }

    /* 将交易金额(可能含有2位小数)由分到元的转换 */
    public String Fen2Yuan(String Money) {
        java.math.BigDecimal aa = new java.math.BigDecimal(Money);
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        return df.format(aa.doubleValue() / 100);
    }

}
