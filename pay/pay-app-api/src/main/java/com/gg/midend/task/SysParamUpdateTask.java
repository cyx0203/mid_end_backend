package com.gg.midend.task;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gg.core.service.SqlService;
import com.gg.midend.config.GlobalConfig;
import com.gg.midend.config.GlobalParam;
import com.gg.midend.utils.DateUtils;

/**
 * 服务需要做合法性判断 参数获取并定时更新
 * 
 * @author 87392
 *
 */
@Component
public class SysParamUpdateTask {

	@Autowired
	private SqlService sqlService;

	// @Scheduled(cron = "0 0/5 * * * ?")//每n分钟执行一次
	@Scheduled(cron = "0/59 * * * * ? ") // 每n秒执行一次
	public void sysParamUpdateTask() throws InterruptedException {
		GlobalConfig.log_api.info("sysParamUpdateTask 执行时间：{}", DateUtils.getDateTime("yyyy-MM-dd HH:mm:ss"));
		sysMerchantWithChannelGet();
		sysHospIdGet();
		sysMerchantWithInstGet();
		sysTransactionTypeGet();
		sysMerchantWithPayTpeGet();
		//sysPayTypeGet();
		sysPayThirdGet();
		sysAlipayParamGet();
		sysWechatParamGet();
	}

	/**
	 * 系统交易类型获取, List节点中存的数据包括交易类型
	 */
	private void sysTransactionTypeGet() {
		GlobalParam.transactionTypeList = sqlService.selectList("pay_goods", "selectDistinct");
	}

	/**
	 * 系统商户与收单机构关系获取 ,List节点中存的数据包括（收单机构编号、商户号） 一个收单机构号可能对应多个商户号
	 */
	private void sysMerchantWithInstGet() {
		GlobalParam.merchantWithInstList = sqlService.selectList("mer_merchant", "selectActive");
	}

	/**
	 * 商户与渠道关系获取 ,List节点中存的数据包括（商户号、渠道号） 一个商户号可能对应多个渠道号
	 */
	private void sysMerchantWithChannelGet() {
		GlobalParam.merchantWithChannelList = sqlService.selectList("mer_channel", "selectActive");
	}
	
	/**
	 * 商户与支付方式关系获取 ,List节点中存的数据包括（商户号、支付方式） 一个商户号可能对应多个支付方式
	 */
	private void sysMerchantWithPayTpeGet() {
		GlobalParam.merchantWithPayTpeyList = sqlService.selectList("mer_pay_type", "selectActive");
	}
	
	/**
	 * 系统医院ID获取 ,List节点中存的数据包括医院ID
	 */
	private void sysHospIdGet() {
		GlobalParam.hospIdList = sqlService.selectList("mer_hospital", "selectDistinct");
	}
	
	/**
	 * 系统支付方式获取,List节点中存的数据包括支付方式
	 */
	private void sysPayTypeGet() {
		GlobalParam.payTypeList = sqlService.selectList("pay_type", "select");
	}
	
	/**
	 * 系统thirdid获取,List节点中存的数据包括thirdid
	 */
	private void sysPayThirdGet() {
		GlobalParam.payThirdList = sqlService.selectList("pay_third", "select");
	}
	
	/**
	 * 支付宝参数获取
	 */
	private void sysAlipayParamGet() {
		GlobalParam.alipayParamList = sqlService.selectList("mer_param_alipay", "select");
	}
	
	/**
	 * 微信参数获取
	 */
	private void sysWechatParamGet() {
		GlobalParam.wechatParamList = sqlService.selectList("mer_param_wechat", "select");
	}

}
