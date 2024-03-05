package com.gg.midend.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gg.core.service.SqlService;
import com.gg.midend.config.GlobalConfig;
import com.gg.midend.service.http.SelfRequest;
import com.gg.midend.utils.DateUtils;
import com.gg.midend.utils.JsonUtils;

import cn.hutool.core.convert.Convert;
import net.javacrumbs.shedlock.core.SchedulerLock;

/**
 * 支付宝未确认状态支付、退费订单,自动确认,只 处理纯自费
 * 
 * @author 87392
 *
 */
@Component
public class AliPayConfirmTask {

	@Autowired
	private SqlService sqlService;
	@Autowired
	private SelfRequest selfRequest;

	/** 支付平台URL */
	@Value("${SysConstants.PayAppApi_URL}:18010/payPlatform")
	private String PayAppApi_URL;
	/** 是否开启检测 */
	@Value("${SysConstants.AliPayAutoCheck:0}")
	private String AliPayAutoCheck;

	/**
	 * 5分钟确认一次支付宝未确认状态的支付、退费订单
	 * 
	 * @throws InterruptedException
	 */
	@Scheduled(cron = "0 0/5 * * * ?") // 每n分钟执行一次
	// @Scheduled(cron = "0/5 * * * * ? ") // 每n秒执行一次
	@SchedulerLock(name = "aliTask", lockAtMostFor = 300000, lockAtLeastFor = 300000)
	public void aliPayOrderStatusConfirmTask() throws InterruptedException {
		if ("0".equals(AliPayAutoCheck))
			return;
		GlobalConfig.log_api.info("aliPayOrderStatusConfirmTask 执行时间：{}", DateUtils.getDateTime("yyyy-MM-dd HH:mm:ss"));
		Map<String, Object> tradeParam = new HashMap<String, Object>();
		tradeParam.put("createDate", DateUtils.getDateTime("yyyyMMdd"));
		tradeParam.put("thirdId", "01");
		List<Map<String, Object>> recordList = sqlService.selectList("pay_order_pay", "selectAllUnConfirmOrder",
				tradeParam);// 查出pay_order_pay中当日所有status为0的支付宝记录,待确认记录
		if (recordList.size() <= 0) {// 没有需要确认的记录
			return;
		}
		List<Map<String, Object>> recordYbList = sqlService.selectList("pay_order_pay", "selectTodayAllYBOrder",
				tradeParam);// 查出pay_order_pay中当日所有医保记录
		List<Map<String, Object>> ownList = this.filterOwnOrder(recordList, recordYbList);// 过滤掉拼付记录
		if (ownList.size() <= 0) {// 没有需要确认的记录
			return;
		}
		this.payStatusConfirm(ownList);// 纯自费支付订单状态确认
		this.refundStatusConfirm(ownList);// 纯自费退费订单状态确认
	}

	/**
	 * 从所有未确认支付状态的支付宝交易记录中过滤掉医保拼付的记录 剔除与医保记录相同orderTrace的记录即可
	 * 此方法只能完全过滤支付订单，因为支付订单创建都是当日 而退费订单有隔日情况，只能过滤掉当日退费的拼付记录
	 * 
	 * @param recordList
	 *            查出pay_order_pay中当日所有status为0的支付宝记录
	 * @param recordYbList
	 *            当日医保记录
	 * @return
	 */
	private List<Map<String, Object>> filterOwnOrder(List<Map<String, Object>> recordList,
			List<Map<String, Object>> recordYbList) {
		List ownList = new ArrayList();// 纯自费支付记录
		for (Object o : recordList) {
			Map<Object, Object> oo = (Map) o;
			String orderTrace = Convert.convert(String.class, oo.get("order_trace"));
			String flag = "0";// 假设是纯自费交易
			for (Object m : recordYbList) {
				Map<Object, Object> mm = (Map) m;
				String filterOrderTrace = Convert.convert(String.class, mm.get("order_trace"));
				if (orderTrace.equals(filterOrderTrace))
					flag = "1";// 和某一笔医保记录相同orderTrace,表明原交易不是纯自费支付
			}
			if ("0".equals(flag))
				ownList.add(o);
		}
		return ownList;
	}

	/**
	 * 纯自费支付订单状态确认
	 * 
	 * @param recordList
	 */
	private void payStatusConfirm(List<Map<String, Object>> recordList) {
		for (Object o : recordList) {
			Map<Object, Object> oo = (Map) o;
			Integer transType = Convert.convert(Integer.class, oo.get("trans_type"));
			if (1 == transType) {
				String orderId = Convert.convert(String.class, oo.get("order_id"));
				Map<String, Object> reqMap = new HashMap<String, Object>();
				Map<String, Object> tradeParamMap = new HashMap<String, Object>();
				tradeParamMap.put("orderId", orderId);
				reqMap.put("tradeType", "0003");
				reqMap.put("tradeParam", tradeParamMap);
				selfRequest.dopost(PayAppApi_URL + "/queryOrder", JsonUtils.MapToJson(reqMap));
			}
		}
		return;
	}

	/**
	 * 纯自费退费订单状态确认 还需要过滤一下非当日退费的医保拼付记录
	 * 
	 * @param recordList
	 */
	private void refundStatusConfirm(List<Map<String, Object>> recordList) {
		for (Object o : recordList) {
			Map<Object, Object> oo = (Map) o;
			Integer transType = Convert.convert(Integer.class, oo.get("trans_type"));
			String refundReason = Convert.convert(String.class, oo.get("refund_reason"));
			if (refundReason == null)// 可能是支付订单或者退费订单没有上送退费原因,不做处理继续下一笔记录判断
				continue;
			if (-1 == transType) {// 是退费订单类型
				String refundorderId = Convert.convert(String.class, oo.get("order_id"));// 退费订单号
				String orderTrace = Convert.convert(String.class, oo.get("order_trace"));
				Map<String, Object> tradeParam = new HashMap<String, Object>();
				tradeParam.put("orderTrace", orderTrace);
				tradeParam.put("thirdId", "01");
				List<Map<String, Object>> recordYbList = sqlService.selectList("pay_order_pay",
						"selectYBOrderByOrderTrace", tradeParam);// 查出pay_order_pay中对应orderTrace的医保记录
				if (recordYbList.size() <= 0) {// 没有医保相关记录，属于纯自费支付订单的退费
					List<Map<String, Object>> origalRecordList = sqlService.selectList("pay_order_pay",
							"selectOrigalOrderId", tradeParam);// 查出退费订单对应的原支付订单order_id,并且支付状态是确认的
					if (origalRecordList.size() == 1) {// 能找到一条已经原支付订单信息，并且是已经确认支付状态的
						Map<String, Object> reqMap = new HashMap<String, Object>();
						Map<String, Object> tradeParamMap = new HashMap<String, Object>();
						Map<String, Object> origalOrderInfo = origalRecordList.get(0);
						String orderId = Convert.convert(String.class, origalOrderInfo.get("order_id"));
						tradeParamMap.put("orderId", orderId);
						tradeParamMap.put("refundorderId", refundorderId);
						tradeParamMap.put("refundFlag", "0");//默认正常退费
						if("系统冲正".equals(refundReason))
							tradeParamMap.put("refundFlag", "1");//冲正
						reqMap.put("tradeType", "0007");
						reqMap.put("tradeParam", tradeParamMap);
						selfRequest.dopost(PayAppApi_URL + "/refundQuery", JsonUtils.MapToJson(reqMap));
					}
				}
			}
		}
		return;
	}

}
