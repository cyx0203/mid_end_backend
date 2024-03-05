package com.gg.midend.controller;

import cn.hutool.core.convert.Convert;
import com.gg.core.exception.ApiException;
import com.gg.core.service.SqlService;
import com.gg.midend.config.GlobalConfig;
import com.gg.midend.utils.ExceptionHandler;
import com.gg.midend.utils.GeneralRecordUtil;
import com.gg.midend.utils.MapUtil;
import com.gg.midend.utils.SendMsgUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据同步接口
 */
@RestController
public class DataSyncController extends GGBaseController {

    @Autowired
    private SqlService sqlService;

    @Autowired
    private GeneralRecordUtil RecordUtil;

    /**
     * 3.1.1 同步号源信息
     * 描述：同步号源信息，由HIS系统调用。HIS系统每天16：00定时调取预约平台接口，获取第二天的排班信息和剩余号数量。
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/syncSource")
    public Object syncSource(@RequestBody Map<String, Object> paramMap) {
        try {
            GlobalConfig.log_api.info("syncSource =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "date"};
            MapUtil.checkParams(needParams, reqMap);
            /*
             * 1.同步号源信息
             * 操作：select
             * 思路：src_date, src_schedule, src_source三表关联，
             *      限定valid=1已生成号源, valid=1已生成号源, active=1启用
             * 注意：并不需要 status 空闲
             */
            List<?> srcList = sqlService.selectList("srcSource", "selectSource", reqMap);
            GlobalConfig.log_api.info("查询到的号源信息: ");

            // 设置返回报文正文
            respBodyMap.put("list", srcList);

            /*
             * 2.1.添加同步记录（成功）
             * 操作：insert
             */
            reqMap.put("result", "0"); // 默认 0 初始状态，等待通知
            reqMap.put("remark", "同步了 " + srcList.size() + " 条记录"); // 成功时加入同步的记录数
            RecordUtil.recordDetail("srcSyncRecord", "insertSyncRecord", reqMap,
                    "号源同步成功，记录同步信息失败", "号源同步成功，记录同步信息成功");
            // 设置返回报文的 syncId 字段
            respBodyMap.put("syncId", reqMap.get("syncId"));
            setLogFlag(false);

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            return ExceptionHandler.handleExpNeedReturn(e);
        }
    }

    /**
     * 3.1.2 同步号源结果通知
     * 描述：同步号源后通知同步操作结果
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/syncNotice")
    public Object syncNotice(@RequestBody Map<String, Object> paramMap) {
        try {
            GlobalConfig.log_api.info("syncNotice =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"syncId", "result"};
            MapUtil.checkParams(needParams, reqMap);

            /*
             * 1.更新同步记录（1.成功/2.失败）
             * 操作：update
             */
            int ret = sqlService.update("srcSyncRecord", "updateSyncRecord", reqMap);
            if (ret < 1) {
                GlobalConfig.log_api.info("同步信息更新失败, update 未匹配到记录");
                throw new Exception("同步信息更新失败");
            } else {
                GlobalConfig.log_api.info("同步信息更新成功");
            }

            /*
             * 2.发送模板短信，通知信息科
             */
            SendMsgUtil.notify(Convert.toStr(reqMap.get("sourceDate")), Convert.toStr(reqMap.get("result")));

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            return ExceptionHandler.handleExpNeedReturn(e);
        }
    }

    /**
     * 3.2. 锁号（HIS现场挂号）
     * 描述：支付前进行锁号
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/lockSourceForHis")
    @Transactional(rollbackFor = {Exception.class})
    public Object lockSourceForHis(@RequestBody Map<String, Object> paramMap) throws ApiException {
        try {
            GlobalConfig.log_api.info("lockSourceForHis =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "scheduleId", "patientId", "patientName", "phoneNo", "termId"};
            MapUtil.checkParams(needParams, reqMap);
            // 校验 phoneNo
            MapUtil.checkPhoneNo((String) reqMap.get("phoneNo"));
            // 加渠道编号
            reqMap.put("custId", Convert.toMap(String.class, Object.class, paramMap.get("header")).get("custId"));

            /*
             * 1.检查该用户是否有锁号订单，返回计数
             *   检查该用户是否重复挂同一个排班医生的号（deprecated）
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

//            int repeated = Convert.toMap(String.class, Integer.class,
//                    sqlService.selectOne("srcOrder", "countRepeatedOrder", reqMap)).get("total");
//            if (repeated > 0) {
//                GlobalConfig.log_api.info("号源锁定失败，不能重复挂同一个医生的号");
//                throw new Exception("号源锁定失败，不能重复挂同一个医生的号");
//            } else {
//                GlobalConfig.log_api.info("用户没有重复挂号，尝试锁号");
//            }

            /*
             * 2.锁号（支付前先锁号）
             * 操作：update，并且返回 src_id, date 字段（插入新 order 时需要）
             * 思路：更新 src_source 的 status 字段，从0到1（空闲到锁定），
             *      根据 scheduleId 以及 order by, limit 来确定 source，然后更新 status
             * 注意1：selectKey 返回 src_id, date 时必须要 src_date 表
             * 注意2：src_id 需要返回
             * 注意3：急诊号、非急诊号分开操作
             * 非急诊号：对时间段做限制，之前的号不能挂，防止插队；考虑加号情况，所以每次都要查到加的号（空闲号源=时间段内的+加号的）
             * 急诊号：对时间段做限制，之前的号不能挂；不考虑加号（空闲号源=时间段内的）
             * 注意4：急诊号、非急诊号合并处理，注意3废弃
             */
            int ret1 = sqlService.update("srcSource", "updateSourceFromSpareToLockForHIS", reqMap);
            if (ret1 < 1) {
                GlobalConfig.log_api.info("号源锁定失败, update 未匹配到记录");
                throw new Exception("号源锁定失败");
            } else {
                // 添加要返回的 sourceId
                respBodyMap.put("sourceId", reqMap.get("srcId"));
                GlobalConfig.log_api.info("号源已锁定, scheduleId: " + reqMap.get("scheduleId")
                        + ", patientName: " + reqMap.get("patientName")
                        + ", patientId: " + reqMap.get("patientId")
                        + ", sourceId: " + reqMap.get("srcId"));
            }
            /*
             * 3.1.生成订单
             * 操作：insert
             * 思路：在 src_order 中生成新的一行数据，最后注意 status 设为 1，lock_time
             * 注意：merchant_id 不为 null，默认 ''
             * 注意：create_source 是 2
             */
            int ret2 = sqlService.update("srcOrder", "insertNewSrcOrderForHIS", reqMap);
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
     * 3.3. 解锁（HIS现场挂号）
     * 描述：解锁号源
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/unLockSourceForHis")
    public Object unLockSourceForHis( @RequestBody Map<String, Object> paramMap) {
        try {
            GlobalConfig.log_api.info("unLockSourceForHis =================================================>");
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
            int ret = sqlService.update("srcSource", "updateSourceFromLockToSpareForHIS", reqMap);
            if (ret < 1) {
                GlobalConfig.log_api.info("解锁失败, update 未匹配到记录");
                throw new Exception("解锁失败");
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
     * 3.4. 同步挂号信息
     * 描述：第二天窗口现场挂号，操作员还是在HIS系统完成挂号流程，挂号成功时，调取接口通知预约平台。
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/syncRegister")
    @Transactional(rollbackFor = {Exception.class})
    public Object syncRegister(@RequestBody Map<String, Object> paramMap) throws ApiException {
        try {
            GlobalConfig.log_api.info("syncRegister =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "orderNo", "transOrderNo", "payType", "payFee", "merchantId", "payAccount"};
            MapUtil.checkParams(needParams, reqMap);
            // 加渠道编号
            reqMap.put("custId", Convert.toMap(String.class, Object.class, paramMap.get("header")).get("custId"));

            /*
             * 1.同步挂号信息——更新 src_order, src_source
             * 操作：update
             * 思路：更新 src_order 表，主要是 status 1-2, 以及 source 表的 status 1-2，
             * 注意：和 2.7 有区别，不需要返回字段了，没有返回报文，也不用请求 HIS
             */
            int ret = sqlService.update("srcOrder", "updateOrderForHIS", reqMap);
            if (ret < 1) {
                GlobalConfig.log_api.info("同步挂号失败, update 未匹配到记录");
                throw new Exception("同步挂号失败");
            } else {
                GlobalConfig.log_api.info("同步挂号成功");
            }

            /*
             * 2.生成订单流水记录
             * 操作：insert
             * 思路：在 src_order_detail 中生成新的一行数据
             */
            reqMap.put("result", "1"); // 成功
            reqMap.put("remark", ""); // 成功默认为空
            RecordUtil.recordDetail("srcOrderDetail", "insertSrcOrderDetail", reqMap,
                    "同步挂号成功，订单状态流水记录失败", "同步挂号成功，订单状态流水记录成功");

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }

    /**
     * 3.5. 同步退号信息
     * 描述：窗口退号，调取接口通知预约平台。
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/syncCancel")
    @Transactional(rollbackFor = {Exception.class})
    public Object syncCancel(@RequestBody Map<String, Object> paramMap) throws ApiException {
        try {
            GlobalConfig.log_api.info("syncCancel =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "orderNo", "termId"};
            MapUtil.checkParams(needParams, reqMap);
            // 加入渠道编号
            reqMap.put("custId", Convert.toMap(String.class, Object.class, paramMap.get("header")).get("custId"));

            /*
             * 1.同步退号信息——更新 src_order, src_source
             * 操作：update
             * 思路：更新 src_order 表，主要是 status 2-3, oper_id, 以及 source 表的 status 2-0，
             * 注意：source 历史表问题
             */
            int ret = sqlService.update("srcSource", "updateSourceFromAppointedToSpareForHIS", reqMap);
            if (ret < 1) {
                GlobalConfig.log_api.info("同步退号失败, update 未匹配到记录");
                throw new Exception("同步退号失败");
            } else {
                GlobalConfig.log_api.info("同步退号成功");
                respBodyMap.put("refundReptNum", "退号成功");
            }

            /*
             * 2.生成订单流水记录
             * 操作：insert
             * 思路：在 src_order_detail 中生成新的一行数据
             */
            reqMap.put("result", "1"); // 成功
            reqMap.put("remark", ""); // 成功默认为空
            RecordUtil.recordDetail("srcOrderDetail", "insertSrcOrderDetail", reqMap,
                    "同步退号成功，订单状态流水记录失败", "同步退号成功，订单状态流水记录成功");

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }

    /**
     * 3.6. 同步就诊信息
     * 描述：患者就诊时，医生站调取接口通知预约平台。
     * @param paramMap 入参
     * @return Object 返回值
     */
    @PostMapping(value = "/syncVisit")
    @Transactional(rollbackFor = {Exception.class})
    public Object syncVisit(@RequestBody Map<String, Object> paramMap) throws ApiException {
        try {
            GlobalConfig.log_api.info("syncVisit =================================================>");
            GlobalConfig.log_api.info("入参: " + paramMap);

            // 返回报文
            Map<String, Object> respBodyMap = new HashMap<>();
            // 请求报文正文，作为 mybatis 查询的数据源
            Map<String, Object> reqMap = Convert.toMap(String.class, Object.class, paramMap.get("body"));

            // 校验必传字段
            String[] needParams = {"hospitalId", "orderNo", "termId"};
            MapUtil.checkParams(needParams, reqMap);
            // 加入渠道编号
            reqMap.put("custId", Convert.toMap(String.class, Object.class, paramMap.get("header")).get("custId"));

            /*
             * 1.同步就诊信息——更新 src_order
             * 操作：update
             * 思路：更新 src_order 表，主要是 status 2-9, oper_id
             */
            int ret = sqlService.update("srcSource", "updateSourceFromAppointedToDiagnosedForHIS", reqMap);
            if (ret < 1) {
                GlobalConfig.log_api.info("同步就诊失败, update 未匹配到记录");
                throw new Exception("同步就诊失败");
            } else {
                GlobalConfig.log_api.info("同步就诊成功");
            }

            /*
             * 2.生成订单流水记录
             * 操作：insert/update
             * 思路：如果已经有 9 的记录，那就做 update，如果没有，做 insert，在 src_order_detail 中生成新的一行数据
             */
            reqMap.put("result", "1"); // 成功
            reqMap.put("remark", ""); // 成功默认为空
            int total = Convert.toMap(String.class, Integer.class,
                    sqlService.selectOne("srcOrderDetail", "selectSrcOrderDetailExist", reqMap)).get("total");
            if (total == 0) {
                RecordUtil.recordDetail("srcOrderDetail", "insertSrcOrderDetail", reqMap,
                        "同步就诊成功，订单状态流水记录失败", "同步就诊成功，订单状态流水记录成功");
            } else {
                RecordUtil.recordDetail("srcOrderDetail", "updateSrcOrderDetailExist", reqMap,
                        "同步就诊成功，订单状态流水更新失败", "同步就诊成功，订单状态流水更新成功");
            }

            return retBack("0", "交易成功", respBodyMap);
        } catch (Exception e) {
            throw ExceptionHandler.handleExpNeedRollback(e);
        }
    }

}
