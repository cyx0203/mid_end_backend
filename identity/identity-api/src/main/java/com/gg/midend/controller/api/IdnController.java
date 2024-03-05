package com.gg.midend.controller.api;


import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class IdnController extends GGBaseController {


    @Autowired
    private SqlService sqlService;


    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @PostMapping(value = "/identity-api/registerPatient")
    public Object registerPatient(@RequestBody Map paramMap) {
        Map retMap = new HashMap();
        try{
            Map headerMap = (Map) paramMap.get("header");
            Map<String,Object> bodyMap = (Map) paramMap.get("body");
            String channel = (String) headerMap.get("custId");
            bodyMap.put("channel",channel);

            if (StringUtil.isNullOrEmpty((String) bodyMap.get("idno"))) {
                throw new Exception("身份证号不能为空");
            }

            boolean isupdate = false;
            //查询是否已存在记录
            List list = sqlService.selectList("idnpatient", "selectBySimpKey", bodyMap);
            if (list.size() > 0) {
                isupdate = true;
            }

            int idnId;
            if (isupdate) {
                if ("0".equals(bodyMap.get("realFlag"))) {
                    if (StringUtil.isNullOrEmpty((String) bodyMap.get("realCert")) || StringUtil.isNullOrEmpty((String) bodyMap.get("realNo"))) {
                        throw new Exception("实名制卡类型和卡号不能为空");
                    }
                } else {
                    bodyMap.remove("realFlag");
                    bodyMap.remove("realCert");
                    bodyMap.remove("realNo");

                }

                Map map = (Map) list.get(0);
                idnId = Integer.valueOf(map.get("id").toString());
                sqlService.update("idnpatient", "updateInfos", bodyMap);
            } else {
                if (StringUtil.isNullOrEmpty((String) bodyMap.get("patientName")) || StringUtil.isNullOrEmpty((String) bodyMap.get("patientSex"))) {
                    throw new Exception("姓名或性别不能为空");
                }

    //            if(StringUtil.isNullOrEmpty((String) paramMap.get("hospital")) || StringUtil.isNullOrEmpty((String) paramMap.get("patientId"))){
    //                throw new ExceptionCenter("500","医院编码或者patientId不能为空");
    //            }
                //插入患者主信息
                sqlService.update("idnpatient", "insertSelective", bodyMap);
                idnId = Integer.valueOf(bodyMap.get("id").toString());
            }


            List add_list = (List) bodyMap.get("address");
            bodyMap.remove("address");
            List con_list = (List) bodyMap.get("contact");
            bodyMap.remove("contact");


            if (add_list != null && add_list.size() > 0) {
                //插入地址
                sqlService.update("idnaddress", "insertBatch", "list", add_list, "idnId", idnId);
            }
            if (con_list != null && con_list.size() > 0) {
                //插入监护人
                sqlService.update("idnguardian", "insertBatch", "list", con_list, "idnId", idnId);
            }
            if (!(StringUtil.isNullOrEmpty((String) bodyMap.get("hospital")) || StringUtil.isNullOrEmpty((String) bodyMap.get("patientId")) || StringUtil.isNullOrEmpty((String) bodyMap.get("idType")))) {

                String hospital = (String) bodyMap.get("hospital");
                String patientId = (String) bodyMap.get("patientId");
                //插入patientId
                sqlService.update("idnpatientids", "insertOrUpdate", "idnId", idnId, "hospital", hospital, "patientId", patientId, "idType", bodyMap.get("idType"));

            }

            retMap = (Map) retBack("00", "成功");
        }catch (Exception e){
            e.printStackTrace();
            retMap = (Map) retBack("99", e.getMessage());
//            throw new RuntimeException();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }finally {
            return retMap;
        }

    }


    @PostMapping(value = "/identity-api/queryPatientInfo")
    public Object queryPatientInfo(@RequestBody Map paramMap){
        try{
            Map headerMap = (Map) paramMap.get("header");
            Map<String,Object> bodyMap = (Map) paramMap.get("body");
            String channel = (String) headerMap.get("custId");
            bodyMap.put("channel",channel);


            Map map = (Map) sqlService.selectOne("idnpatient", "selectBySimpKey", bodyMap, "status", "0");
            if (map == null || map.isEmpty()) {
                throw new Exception("患者信息不存在");
            }
            int idnId = Integer.valueOf(map.get("id").toString());

            List patientIds_list = sqlService.selectList("idnpatientids", "selectByPrimaryKey", bodyMap, "idnId", idnId);

            List add_list = sqlService.selectList("idnaddress", "selectByPrimaryKey", bodyMap, "idnId", idnId);

            List con_list = sqlService.selectList("idnguardian", "selectByPrimaryKey", bodyMap, "idnId", idnId);

            Map retMap = new HashMap();
            retMap.put("patientIds", patientIds_list);
            retMap.put("address", add_list);
            retMap.put("contact", con_list);
            retMap.putAll(map);


            return retBack("00", "成功", retMap);
        }catch (Exception e){
            e.printStackTrace();
            if ("患者信息不存在".equals(e.getMessage()))
                return retBack("502",e.getMessage());
            return retBack("99",e.getMessage());
        }
    }

    /**
     * 弃用
     *
     * @param paramMap
     * @return
     * @throws ExceptionCenter
     */
    @PostMapping(value = "/api/modifyPatient")
    public Object modifyPatient(@RequestBody Map paramMap) throws ExceptionCenter {

        if (StringUtil.isNullOrEmpty((String) paramMap.get("patientName")) || StringUtil.isNullOrEmpty((String) paramMap.get("idno"))) {
            throw new ExceptionCenter("500", "姓名或身份证号不能为空");
        }

        if (StringUtil.isNullOrEmpty((String) paramMap.get("hospital")) || StringUtil.isNullOrEmpty((String) paramMap.get("patientId"))) {
            throw new ExceptionCenter("500", "医院编码或者patientId不能为空");
        }
        //查询是否已存在记录
        Map map = new HashMap();
        map.put("patientName", paramMap.get("patientName"));
        map.put("idno", paramMap.get("idno"));
        List list = sqlService.selectList("idnpatient", "selectByPrimaryKey", map);
        if (list.size() > 0) {
            throw new ExceptionCenter("501", "患者信息已存在");
        }

        List add_list = (List) paramMap.get("address");
        paramMap.remove("address");
        List con_list = (List) paramMap.get("contact");
        paramMap.remove("contact");

        //插入患者主信息
        sqlService.update("idnpatient", "insertSelective", paramMap);

        BigInteger id = (BigInteger) paramMap.get("id");
        String hospital = (String) paramMap.get("hospital");
        String patientId = (String) paramMap.get("patientId");
        //插入patientId
        sqlService.update("idnpatientids", "insertSelective", "idnId", id, "hospital", hospital, "patientId", patientId, "idType", paramMap.get("idType"));
        //插入地址
        sqlService.update("idnaddress", "insertBatch", "list", add_list, "idnId", id);
        //插入监护人
        sqlService.update("idnguardian", "insertBatch", "list", con_list, "idnId", id);

        return retBack("00", "成功");
    }


}
