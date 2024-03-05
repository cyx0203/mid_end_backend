package com.gg.midend.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gg.core.exception.ApiException;
import com.gg.core.service.SqlService;
import com.gg.midend.config.GlobalConfig;
import com.gg.midend.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 渠道调用接口
 */
@RestController
public class ChannelController extends GGBaseController {

    @Autowired
    private SqlService sqlService;

    @Autowired
    private GeneralRecordUtil RecordUtil;

    /**
     * 2.1. 获取科室
     * 描述：查询一级、二级排班科室
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/queryDepartment")
    public Object queryDepartment(@RequestBody Map<String, Object> paramMap) {
        try {
            GlobalConfig.log_api.info("queryDepartment =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId"};
            MapUtil.checkParams(needParams, reqMap);

            /*
             * 获取科室（查询一级、二级排班科室）
             * 操作：select
             * 思路：将 com_dept 按不同级别科室分为两张子表，然后关联查询，使用 <collection> 一次性映射
             */
            List<?> deptList = sqlService.selectList("comDept", "selectDepartment", reqMap);
            GlobalConfig.log_api.info("查询到的一级、二级排班科室信息");

            // 设置返回报文正文
            respBodyMap.put("list", deptList);

            setLogFlag(false);

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            return ExceptionHandler.handleExpNeedReturn(e);
        }
    }

    /**
     * 2.2.1 按日期获取排班
     * 描述：查询所有可预约的医生排班信息
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/querySchedule")
    public Object querySchedule(@RequestBody Map<String, Object> paramMap) {
        try {
            GlobalConfig.log_api.info("querySchedule =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "queryType"};
            MapUtil.checkParams(needParams, reqMap);
            // 加渠道编号
            reqMap.put("custId", Convert.toMap(String.class, Object.class, paramMap.get("header")).get("custId"));

            if ("date".equals(reqMap.get("queryType"))) {
                String[] needParamsDate = {"deptCode", "queryDate"};
                MapUtil.checkParams(needParamsDate, reqMap);
                /*
                 * 1.根据科室、渠道，从 rule_register_type 表中获取可开放号别
                 *   然后添加至 reqMap 入参
                 * 操作：select
                 */
                List<String> regTypeList = Convert.toList(String.class, sqlService.selectList("ruleChannelSchedule", "selectOpenedRegType", reqMap));
                if (regTypeList.size() == 0) {
                    throw new Exception("号类渠道规则为空，请查看预约平台后管系统");
                } else if (regTypeList.size() == 1) {
                    reqMap.put("notAllowRegTypeList", null);
                    reqMap.put("allowRegTypeList", regTypeList.get(0));
                    GlobalConfig.log_api.info("当前渠道 " + reqMap.get("custId") + " 开放的号别: " + reqMap.get("allowRegTypeList"));
                } else {
                    reqMap.put("notAllowRegTypeList", regTypeList.get(0));
                    reqMap.put("allowRegTypeList", regTypeList.get(1));
                    GlobalConfig.log_api.info("当前渠道 " + reqMap.get("custId") + " 不开放的号别: " + reqMap.get("notAllowRegTypeList"));
                    GlobalConfig.log_api.info("当前渠道 " + reqMap.get("custId") + " 开放的号别: " + reqMap.get("allowRegTypeList"));
                }
                /*
                 * 2.获取排班（查询所有可预约的医生排班信息）
                 * 操作：select
                 * 思路：src_schedule, src_date, com_doctor, com_doctor_title, com_register_type 五表关联，
                 *      限定valid=1已生成号源, valid=1已生成号源, active=1启用，
                 *      用子查询单独统计每个 scheduleId 对应的 source 数据，status=0预约状态空闲，如果是当天，不统计之前时间段排班
                 * 注意1：费用信息，只返回总费用，clinicFee 和 regFee 默认为0
                 */
                List<?> scheduleList = sqlService.selectList("srcSchedule", "selectSchedule", reqMap);
                GlobalConfig.log_api.info("查询到的所有可预约的医生排班信息");

                // 处理图像地址
                List<Map<String, Object>> scheduleListModified = new ArrayList<>();
                ImgPathUtil.addUrlHead(scheduleList, scheduleListModified);

                // 设置返回报文正文
                respBodyMap.put("list", scheduleListModified);
            } else {
                String[] needParamsDoc = {"docName"};
                MapUtil.checkParams(needParamsDoc, reqMap);
                /*
                 * 按姓名获取专家（按照姓名查询排班内的专家）
                 * 操作：select
                 * 思路：src_schedule, src_date, com_doctor, com_doctor_title, com_register_type, com_dept 六表关联，
                 *      限定valid=1已生成号源, valid=1已生成号源, active=1启用
                 * 注意1：不用限定号别，有名字的一定是专家
                 * 注意2：今天之前的排班肯定不显示，要让 date >= now()
                 */
                List<?> scheduleList = sqlService.selectList("srcSchedule", "selectScheduleByDoctorName", reqMap);
                GlobalConfig.log_api.info("查询到专家的所有排班（按姓名）");

                // 处理图像地址
                List<Map<String, Object>> scheduleListModified = new ArrayList<>();
                ImgPathUtil.addUrlHead(scheduleList, scheduleListModified);

                // 设置返回报文正文
                respBodyMap.put("list", scheduleListModified);
            }
            setLogFlag(false);

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            return ExceptionHandler.handleExpNeedReturn(e);
        }
    }

    /**
     * 2.2.2 获取专家
     * 描述：查询排班内的所有专家
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/queryDoctor")
    public Object queryDoctor(@RequestBody Map<String, Object> paramMap) {
        try {
            GlobalConfig.log_api.info("queryDoctor =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "deptCode"};
            MapUtil.checkParams(needParams, reqMap);

            /*
             * 获取专家（查询排班内的所有专家）
             * 操作：select
             * 思路：src_schedule, src_date, com_doctor, com_doctor_title, com_register_type 五表关联，
             *      限定valid=1已生成号源, valid=1已生成号源, active=1启用
             * 注意1：限定号别 register_type in ('10', '11', '12', '13', '14', '15')
             */
            List<?> proDocList = sqlService.selectList("comDoctor", "selectDoctor", reqMap);
            GlobalConfig.log_api.info("查询到排班内的所有专家");
            // 处理图像地址
            List<Map<String, Object>> proDocListModified = new ArrayList<>();
            ImgPathUtil.addUrlHead(proDocList, proDocListModified);

            // 设置返回报文正文
            respBodyMap.put("list", proDocListModified);
            setLogFlag(false);

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            return ExceptionHandler.handleExpNeedReturn(e);
        }
    }

    /**
     * 2.2.3 按专家获取排班
     * 描述：查询某个专家的排班信息
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/queryScheduleByDoctor")
    public Object queryScheduleByDoctor(@RequestBody Map<String, Object> paramMap) {
        try {
            GlobalConfig.log_api.info("queryScheduleByDoctor =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "deptCode", "docCode"};
            MapUtil.checkParams(needParams, reqMap);

            /*
             * 按专家获取排班（查询所有排班内的专家）
             * 操作：select
             * 思路：src_schedule, src_date, com_register_type 三表关联，
             *      限定valid=1已生成号源, valid=1已生成号源, active=1启用
             * 注意1：不用限定号别，因为查专家的时候已经限定过了
             * 注意2：今天之前的排班肯定不显示，要让 date >= now()
             */
            List<?> scheduleList = sqlService.selectList("srcSchedule", "selectScheduleByDoctor", reqMap);
            GlobalConfig.log_api.info("查询到专家的所有排班");

            // 处理图像地址
            List<Map<String, Object>> scheduleListModified = new ArrayList<>();
            ImgPathUtil.addUrlHead(scheduleList, scheduleListModified);

            // 设置返回报文正文
            respBodyMap.put("list", scheduleListModified);
            setLogFlag(false);

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            return ExceptionHandler.handleExpNeedReturn(e);
        }
    }

    /**
     * 2.3. 查询分时段
     * 描述：查询医生排班的分时段信息
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/queryTime")
    public Object queryTime(@RequestBody Map<String, Object> paramMap) {
        try {
            GlobalConfig.log_api.info("queryTime =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "scheduleId"};
            MapUtil.checkParams(needParams, reqMap);

            /*
             * 查询分时段（查询医生排班的分时段信息）
             * 操作：select
             * 思路：src_source, src_schedule, src_date 三表关联，
             *      限定 valid=1 已生成号源, valid=1 已生成号源, active=1 启用, status=0 预约状态空闲
             * 注意：用 stime 分组，统计同一时段的号源数量
             */
            List<?> scheduleList = sqlService.selectList("srcSource", "selectTime", reqMap);
            GlobalConfig.log_api.info("查询到的医生排班的分时段信息");

            // 设置返回报文正文
            respBodyMap.put("list", scheduleList);
            setLogFlag(false);

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            return ExceptionHandler.handleExpNeedReturn(e);
        }
    }

    /**
     * 2.4. 查询划价费用
     * 描述：根据患者信息查询划价费用
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/getPrice")
    public Object getPrice(@RequestBody Map<String, Object> paramMap) {
        try {
            GlobalConfig.log_api.info("getPrice =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "patientId", "deptCode", "regType"};
            MapUtil.checkParams(needParams, reqMap);

            /*
             * 请求 HIS 获取划价信息
             */
            // 构造入参, xml 格式
            JSONObject reqJson = JSONUtil.createObj().set("patientId", reqMap.get("patientId"))
                    .set("deptCode", reqMap.get("deptCode")).set("regType", reqMap.get("regType"));
            // 发送请求
            String response = HISRequestUtil.requestHIS("YQ_getPrice", reqJson);
            // 返回 xml 格式报文，转为 json 字符串
            JSONObject respJson = JSONUtil.xmlToJson(response);
            // 判断请求结果，失败则抛出异常，成功取 payFee
            JSONObject respJsonObj = respJson.getJSONObject("response");
            if (!"0".equals(respJsonObj.getStr("returnCode"))) {
                throw new Exception(respJsonObj.getStr("returnMsg"));
            }
            respBodyMap.put("payFee", respJsonObj.getStr("payFee"));
            GlobalConfig.log_api.info("查询到的划价费用: " + respBodyMap);

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            // 非 sql 错误处理
            return retBack("99", e.getMessage());
        }
    }

    /**
     * 2.5. 锁号
     * 描述：支付前进行锁号
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/lockSource")
    @Transactional(rollbackFor = {Exception.class})
    public Object lockSource(@RequestBody Map<String, Object> paramMap) throws ApiException {
        try {
            GlobalConfig.log_api.info("lockSource =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "scheduleId", "stime", "etime", "patientId", "patientName", "phoneNo", "termId"};
            MapUtil.checkParams(needParams, reqMap);
            // 校验 phoneNo
            MapUtil.checkPhoneNo((String) reqMap.get("phoneNo"));
            // 加渠道编号
            reqMap.put("custId", Convert.toMap(String.class, Object.class, paramMap.get("header")).get("custId"));

            /*
             * 1.检查该用户是否有锁号订单，返回计数
             *   检查该用户是否重复挂同一个排班医生的号
             * 操作：select
             * 思路：根据 patientId 查询 order 表中 status 为 1 (锁号) 的订单数量，如果超过 0 就代表存在未完成订单
             *      根据 patientId, scheduleId 查询 order 表中 status 为 2 (预约) 的订单数量，如果超过 0 就代表重复了
             */
//            int locked = Convert.toMap(String.class, Integer.class,
//                    sqlService.selectOne("srcOrder", "countLockedOrder", reqMap)).get("total");
//            if (locked > 0) {
//                GlobalConfig.log_api.info("号源锁定失败，存在未完成订单");
//                throw new Exception("号源锁定失败，存在未完成订单");
//            } else {
//                GlobalConfig.log_api.info("用户不存在未完成订单，尝试锁号");
//            }
//
//            int repeated = Convert.toMap(String.class, Integer.class,
//                    sqlService.selectOne("srcOrder", "countRepeatedOrder", reqMap)).get("total");
//            if (repeated > 0) {
//                GlobalConfig.log_api.info("号源锁定失败，不能重复挂同一个医生的号");
//                throw new Exception("号源锁定失败，不能重复挂同一个医生的号");
//            } else {
//                GlobalConfig.log_api.info("用户没有重复挂号，尝试锁号");
//            }

            /*
             * 2.获取待锁号号源, 并锁号（支付前先锁号）
             * 操作：update
             * 思路：更新 src_source 的 status 字段，从0到1（空闲到锁定）
             * 注意：非急诊号、急诊号，自助机上的都做时间限制，返回时间段内的空闲号源
             */
            int ret1 = sqlService.update("srcSource", "updateSourceFromSpareToLock", reqMap);
            if (ret1 < 1) {
                GlobalConfig.log_api.info("号源锁定失败, update 未匹配到记录");
                throw new Exception("号源锁定失败");
            } else {
                GlobalConfig.log_api.info("号源已锁定, scheduleId: " + reqMap.get("scheduleId")
                        + ", patientName: " + reqMap.get("patientName")
                        + ", patientId: " + reqMap.get("patientId")
                        + ", sourceId: " + reqMap.get("srcId"));
            }

            /*
             * 3.1.生成订单
             * 操作：insert，selectKey 返回 orderNo
             * 思路：在 src_order 中生成新的一行数据，最后注意 status 设为 1
             * 注意：merchant_id 不为 null，默认 ''
             */
            int ret2 = sqlService.update("srcOrder", "insertNewSrcOrder", reqMap);
            if (ret2 < 1) {
                GlobalConfig.log_api.info("订单生成失败, update 未匹配到记录");
                throw new Exception("订单生成失败");
            } else {
                // 添加要返回的 orderNo
                respBodyMap.put("orderNo", reqMap.get("orderNo"));
                GlobalConfig.log_api.info("订单已生成");
            }
            /*
             * 3.2.生成订单流水记录
             * 操作：insert
             * 思路：在 src_order_detail 中生成新的一行数据
             */
            reqMap.put("result", "1"); // 成功
            reqMap.put("remark", ""); // 成功默认为空
            RecordUtil.recordDetail("srcOrderDetail", "insertSrcOrderDetail", reqMap,
                    "锁号成功，订单状态流水记录失败", "锁号成功，订单状态流水记录成功");

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollbackForLockSource(e);
        }
    }

    /**
     * 2.6. 解锁
     * 描述：解锁号源
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/unLockSource")
    public Object unLockSource(@RequestBody Map<String, Object> paramMap) {
        try {
            GlobalConfig.log_api.info("unLockSource =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "orderNo"};
            MapUtil.checkParams(needParams, reqMap);
            // 加渠道编号
            reqMap.put("custId", Convert.toMap(String.class, Object.class, paramMap.get("header")).get("custId"));

            /*
             * 1.解锁（超时需要解锁）—— src_order, src_source 都需要解锁
             * 操作：update
             * 思路：因为 src_order 已经有数据了，需要更改 status 1-0（锁定到空闲），unlock_time设置
             *      另外需要更新 src_source 中的 status 字段，1-0
             */
            int ret = sqlService.update("srcSource", "updateSourceFromLockToSpare", reqMap);
            if (ret < 1) {
                GlobalConfig.log_api.info("解锁失败, update 未匹配到记录");
                throw new Exception();
            } else {
                GlobalConfig.log_api.info("解锁成功");
            }

            /*
             * 2.生成订单流水记录
             * 操作：insert
             * 思路：在 src_order_detail 中生成新的一行数据
             * 注意：解锁 termId 传空，不需要追踪
             */
            reqMap.put("result", "1"); // 成功
            reqMap.put("remark", ""); // 成功默认为空
            RecordUtil.recordDetail("srcOrderDetail", "insertSrcOrderDetail", reqMap,
                    "解锁成功，订单状态流水记录失败", "解锁成功，订单状态流水记录成功");

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            return ExceptionHandler.handleExpNeedReturn(e);
        }
    }

    /**
     * 2.7. 预约挂号确认
     * 描述：预约挂号确认
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/getRegister")
    @Transactional(rollbackFor = {Exception.class})
    public Object getRegister(@RequestBody Map<String, Object> paramMap) throws ApiException {
        try {
            GlobalConfig.log_api.info("getRegister =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "orderNo", "transOrderNo", "busOrderNo", "paySerialNo", "payType", "payFee", "merchantId", "payAccount"};
            MapUtil.checkParams(needParams, reqMap);
            // 加渠道编号
            reqMap.put("custId", Convert.toMap(String.class, Object.class, paramMap.get("header")).get("custId"));

            /*
             * 1.预约挂号确认——更新 src_order, src_source
             *   查询返回所需字段
             * 操作：update, selectKey
             * 思路：更新 src_order 表，主要是 apt_time, status 1-2, 以及 source 表的 status 1-2，
             *      先更新，source、schedule、doctor、doctor_title、register_type五表
             *      selectKey 返回 scheduleId, sourceId, sequence, regDate, regTime,
             *                    idCard, patientId, patientName, phoneNo, termId,
             *                    regType, regTypeName, deptCode, deptName, docCode, docName, docTitle, noon
             *      selectKey Order: AFTER
             * 注意1：scheduleId, sourceId, sequence, idCard, patientId, patientName, phoneNo, termId, docCode, deptCode, regDate, regType, noon 会在下面请求时用到
             * 注意2：regTime 拆分出 startTime，用于 HIS 请求
             * 注意3：sequence, regDate, regTime, regType, regTypeName, deptName, docName, docTitle 最后需要返回给源启的 8/9 个字段
             */
            int ret = sqlService.update("srcOrder", "updateOrder", reqMap);
            if (ret < 1) {
                GlobalConfig.log_api.info("预约挂号失败, update 未匹配到记录");
                throw new Exception("预约挂号失败");
            } else {
                GlobalConfig.log_api.info("预约挂号成功");
            }

            /*
             * 2.请求 HIS 的 getRegister 接口，得到 position 字段
             * 注意：会用到传入的 6 个字段 && 第 1 步查出的 14/20 个字段，共 20 个
             */
            // 构造入参, xml 格式
            JSONObject reqJson = JSONUtil.createObj().set("scheduleId", reqMap.get("scheduleId"))
                    .set("sourceId", reqMap.get("sourceId")).set("sequence", reqMap.get("sequence"))
                    .set("idCard", reqMap.get("idCard")).set("patientId", reqMap.get("patientId"))
                    .set("patientName", reqMap.get("patientName")).set("phoneNo", reqMap.get("phoneNo"))
                    .set("termId", reqMap.get("termId"))
                    .set("transOrderNo", reqMap.get("transOrderNo")).set("payType", reqMap.get("payType"))
                    .set("payFee", reqMap.get("payFee")).set("merchantId", reqMap.get("merchantId"))
                    .set("paySerialNo", reqMap.get("paySerialNo")).set("payAccount", reqMap.get("payAccount"))
                    .set("custId", reqMap.get("custId"))
                    .set("deptCode", reqMap.get("deptCode")).set("docCode", reqMap.get("docCode"))
                    .set("regDate", reqMap.get("regDate")).set("regType", reqMap.get("regType"))
                    .set("noon", reqMap.get("noon"))
                    .set("startTime", ((String) reqMap.get("regTime")).substring(0, 5).concat(":00"))
                    .set("endTime", ((String) reqMap.get("regTime")).substring(6, 11).concat(":00"))
                    .set("orderNo", reqMap.get("orderNo"));
            // 发送请求
            String response = HISRequestUtil.requestHIS("YQ_getRegister", reqJson);
            // 返回 xml 格式报文，转为 json 字符串
            JSONObject respJson = JSONUtil.xmlToJson(response);
            // 判断请求结果，失败则抛出异常，成功取 payFee
            JSONObject respJsonObj = respJson.getJSONObject("response");
            if (!"0".equals(respJsonObj.getStr("returnCode"))) {
                throw new Exception(respJsonObj.getStr("returnMsg"));
            }
            // 查到的字段，设置到返回报文中 position, times, ledgerSn, receiptSn
            respBodyMap.put("position", respJsonObj.getStr("position"));
            respBodyMap.put("times", respJsonObj.getStr("times"));
            respBodyMap.put("ledgerSn", respJsonObj.getStr("ledgerSn"));
            respBodyMap.put("receiptSn", respJsonObj.getStr("receiptSn"));

            /*
             * 3.设置返回报文正文
             * 返回字段：deptName, regType, regTypeName, docName, docTitle, position, regDate, regTime, sequence
             */
            respBodyMap.put("deptName", reqMap.get("deptName"));
            respBodyMap.put("regType", reqMap.get("regType"));
            respBodyMap.put("regTypeName", reqMap.get("regTypeName"));
            respBodyMap.put("docName", reqMap.get("docName"));
            respBodyMap.put("docTitle", reqMap.get("docTitle"));
            respBodyMap.put("regDate", reqMap.get("regDate"));
            respBodyMap.put("regTime", reqMap.get("regTime"));
            respBodyMap.put("sequence", reqMap.get("sequence"));

            /*
             * 4.生成订单流水记录
             * 操作：insert
             * 思路：在 src_order_detail 中生成新的一行数据
             */
            reqMap.put("result", "1"); // 成功
            reqMap.put("remark", ""); // 成功默认为空
            RecordUtil.recordDetail("srcOrderDetail", "insertSrcOrderDetail", reqMap,
                    "预约挂号确认成功，订单状态流水记录失败", "预约挂号确认成功，订单状态流水记录成功");

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }

    /**
     * 2.8. 预约订单查询
     * 描述：预约订单查询
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/queryOrder")
    public Object queryOrder(@RequestBody Map<String, Object> paramMap) {
        try {
            GlobalConfig.log_api.info("queryOrder =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "patientId", "startDate", "endDate"};
            MapUtil.checkParams(needParams, reqMap);

            /*
             * 查询预约订单
             * 操作：select
             * 思路：src_date, src_schedule, src_order, com_doctor, src_source, com_dept, com_doctor_title, com_register_type
             *      八表内关联，查询指定时间段内的患者订单，并从新到旧排列
             * 注意：对时间要 format
             */
            List<?> orderList = sqlService.selectList("srcOrder", "selectOrder", reqMap);
            GlobalConfig.log_api.info("查询到的预约订单信息");

            // 设置返回报文正文
            respBodyMap.put("list", orderList);
            setLogFlag(false);

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            return ExceptionHandler.handleExpNeedReturn(e);
        }
    }

    /**
     * 2.9. 预约挂号退号
     * 描述：预约挂号退号
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/cancelRegister")
    @Transactional(rollbackFor = {Exception.class})
    public Object cancelRegister(@RequestBody Map<String, Object> paramMap) throws ApiException {
        try {
            GlobalConfig.log_api.info("cancelRegister =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "orderNo", "patientId", "termId", "receiptSn"};
            MapUtil.checkParams(needParams, reqMap);
            // 加渠道编号
            reqMap.put("custId", Convert.toMap(String.class, Object.class, paramMap.get("header")).get("custId"));

            /*
             * 1.预约挂号退号
             * 操作：update, selectKey
             * 思路：根据 orderNo 更新 order, status 2-3 oper_id, 更新 source, status 2-0
             *      根据 orderNo 得到 sourceId, 请求 HIS 需要用到
             * 注意：source 历史表问题
             */
            int ret = sqlService.update("srcSource", "updateSourceFromAppointedToSpare", reqMap);
            if (ret < 1) {
                GlobalConfig.log_api.info("预约挂号退号失败, update 未匹配到记录");
                throw new Exception("预约挂号退号失败");
            } else {
                GlobalConfig.log_api.info("预约挂号退号成功");
            }

            /*
             * 2.请求 HIS 的 cancelRegister 接口，得到 refundReptNum 字段
             */
            // 构造入参, xml 格式
            JSONObject reqJson = JSONUtil.createObj().set("sourceId", reqMap.get("sourceId"))
                    .set("patientId", reqMap.get("patientId")).set("termId", reqMap.get("termId"))
                    .set("receiptSn", reqMap.get("receiptSn"));
            // 发送请求
            String response = HISRequestUtil.requestHIS("YQ_cancelRegister", reqJson);
            // 返回 xml 格式报文，转为 json 字符串
            JSONObject respJson = JSONUtil.xmlToJson(response);
            // 判断请求结果，失败则抛出异常，成功取 refundReptNum
            JSONObject respJsonObj = respJson.getJSONObject("response");
            if (!"0".equals(respJsonObj.getStr("returnCode"))) {
                throw new Exception(respJsonObj.getStr("returnMsg"));
            }
            // 返回的字段，设置到返回报文中
            respBodyMap.put("refundReptNum", respJsonObj.getStr("refundReptNum"));
            respBodyMap.put("times", respJsonObj.getStr("times"));
            respBodyMap.put("ledgerSn", respJsonObj.getStr("ledgerSn"));
            respBodyMap.put("flag", respJsonObj.getStr("flag"));
            respBodyMap.put("receiptSn", respJsonObj.getStr("receiptSn"));
            respBodyMap.put("zyMzFlag", respJsonObj.getStr("zyMzFlag"));
            respBodyMap.put("eBillBatchCode", respJsonObj.getStr("eBillBatchCode"));
            respBodyMap.put("eBillNo", respJsonObj.getStr("eBillNo"));
            respBodyMap.put("reason", respJsonObj.getStr("reason"));
            respBodyMap.put("operator", respJsonObj.getStr("operatorId"));
            respBodyMap.put("busDateTime", respJsonObj.getStr("busDateTime"));
            respBodyMap.put("placeCode", respJsonObj.getStr("placeCode"));

            /*
             * 3.生成订单流水记录
             * 操作：insert
             * 思路：在 src_order_detail 中生成新的一行数据
             */
            reqMap.put("result", "1"); // 成功
            reqMap.put("remark", ""); // 成功默认为空
            RecordUtil.recordDetail("srcOrderDetail", "insertSrcOrderDetail", reqMap,
                    "预约挂号退号成功，订单状态流水记录失败", "预约挂号退号成功，订单状态流水记录成功");

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }

    /**
     * x.x. 测试用
     * 描述：测试 update, insert, delete 是否能返回主键并从 reqMap 里取到
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/testReturnValue")
    @Transactional(rollbackFor = {Exception.class})
    public Object testReturnValue(@RequestBody Map<String, Object> paramMap) throws Throwable {
        try {
            GlobalConfig.log_api.info("testReturnValue =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "sourceId"};
//            String[] needParams = {"hospitalId", "sourceId", "docCode"};
            MapUtil.checkParams(needParams, reqMap);

//            int ret2 = sqlService.update("srcSource", "testReturnValue", reqMap);
//            System.out.println(ret2);

            /*
             * 测试是否能取到返回的主键
             */
//            int ret = sqlService.update("srcSource", "testReturnValue", reqMap);
//            int ret = sqlService.update("srcSource", "testInsertReturnValue", reqMap);
            int ret = sqlService.update("srcSource", "testReturnList", reqMap);
            System.out.println(ret);
            if (ret < 1) {
                GlobalConfig.log_api.info("测试结束, update 匹配到 0 行记录");
                throw new Exception("测试结束222");
            } else {
//                GlobalConfig.log_api.info("测试结束");
                System.out.println("reqMap: " + reqMap);
                System.out.println("sourceIdListStr: " + StrUtil.split((CharSequence) reqMap.get("sourceIdListStr"), ","));
            }

            // 构造 sourceIdListStr
            List<String> sourceIdList = StrUtil.split((CharSequence) reqMap.get("sourceIdListStr"), ",");
            reqMap.put("sourceIdList", sourceIdList);
            System.out.println("sourceIdList: " + sourceIdList);
            System.out.println("sourceIdList size: " + sourceIdList.size());

//            int ret2 = sqlService.update("srcSource", "testInsertList", reqMap);
//            System.out.println(ret2);
//            if (ret2 < 1) {
//                GlobalConfig.log_api.info("测试结束, update 匹配到 0 行记录");
//                throw new Exception("测试结束222");
//            } else {
//                GlobalConfig.log_api.info("测试结束");
//                GlobalConfig.log_api.info("插入成功");
//            }

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
//            throw new ApiException(retBack("99", e.getCause().getMessage(), new HashMap<>()));
//            throw (SQLException) e.getCause();
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }


}
