CREATE DATABASE IF NOT EXISTS `midend_data` CHARACTER SET utf8;
USE `midend_data`;

DROP TABLE IF EXISTS `trd_record`;
CREATE TABLE `trd_record` (
  `hospital_id` varchar(20) NOT NULL DEFAULT '' COMMENT '医院编码',
  `merchant_id` varchar(30) NOT NULL COMMENT '商户id',
  `pay_client_no` varchar(50) NOT NULL COMMENT '客户端流水号（一般可认为C端操作流水）',
  `pay_order_no` varchar(40) DEFAULT '' COMMENT '交易订单流水号（对账流水号）',
  `pay_seq_no` varchar(50) DEFAULT NULL COMMENT '支付方流水号（三方流水）',
  `trd_date` char(8) DEFAULT NULL COMMENT '交易日期，格式：yyyyMMdd',
  `trd_time` char(6) DEFAULT NULL COMMENT '交易时间，格式：HHmmss',
  `dev_id` varchar(8) DEFAULT NULL COMMENT '设备编号',
  `clear_date` char(8) DEFAULT NULL COMMENT '支付平台（e百福）清算日期，格式：yyyyMMdd',
  `channel` char(2) NOT NULL DEFAULT '' COMMENT '终端渠道（WX-微信公众号，ZZ-自助机，CK-窗口扫码）',
  `trd_type` char(3) DEFAULT NULL COMMENT '交易类型：ACC 建档；REG 当日挂号；ODR 预约挂号；GET 预约取号；CAC 挂号取消; PAY 门诊缴费；REC 门诊充值；DEP 住院充值；OLA 在线问诊; COA 取消问诊;',
  `pay_type` varchar(4) DEFAULT NULL COMMENT '支付方式 （详见plat_code定义）',
  `his_tradeid` varchar(8) DEFAULT NULL COMMENT 'HIS操作员号',
  `card_no` varchar(50) DEFAULT NULL COMMENT '卡号(或者住院号)',
  `soc_no` varchar(50) DEFAULT NULL COMMENT '医保卡号',
  `pat_id` varchar(20) DEFAULT '' COMMENT '患者id',
  `pat_name` varchar(20) DEFAULT '' COMMENT '患者姓名',
  `pat_idno` varchar(40) DEFAULT '' COMMENT '身份证号码',
  `pat_type` char(1) DEFAULT '' COMMENT '患者身份类别， 1：自费；2：医保',
  `bankcard_no` varchar(20) DEFAULT '' COMMENT '银行卡号',
  `refund_seq_no` varchar(20) DEFAULT '' COMMENT '支付冲正流水号',
  `amt` varchar(20) DEFAULT '0' COMMENT '自费金额，单位元',
  `soc_amt` varchar(20) DEFAULT '0' COMMENT '医保个人支付金额，单位元',
  `pool_amt` varchar(20) DEFAULT '0' COMMENT '医保统筹金额,单位元',
  `status` varchar(20) DEFAULT NULL COMMENT '交易状态（SUCCESS-HIS交易完成，FAIL-HIS交易失败）',
  `his_msg` text COMMENT 'HIS交易返回信息',
  `sender` varchar(100) DEFAULT NULL COMMENT '微信openid',
  `pad1` varchar(100) DEFAULT NULL COMMENT '备注1',
  `pad2` varchar(100) DEFAULT NULL COMMENT '备注2',
  `pad3` varchar(100) DEFAULT NULL COMMENT '备注3',
  PRIMARY KEY (`merchant_id`, `pay_client_no`),
  KEY `k_projid_hosid_date_orderno` (`merchant_id`,`trd_date`,`pay_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='交易记录表';

DROP TABLE IF EXISTS `trd_stat`;
CREATE TABLE `trd_stat` (
  `hospital_id` varchar(20) NOT NULL DEFAULT '' COMMENT '医院编码',
  `merchant_id` varchar(30) NOT NULL COMMENT '商户id',
  `stat_date` char(8) NOT NULL COMMENT '统计日期',
  `channel` char(2) NOT NULL COMMENT '终端渠道（WX-微信公众号，ZZ-自助机，CK-窗口扫码）',
  `stat_tag` varchar(5) NOT NULL COMMENT '统计维度：POS,WX,ZFB..REG,...YB,ZF',
  `sumfee` varchar(12) DEFAULT NULL COMMENT '交易总额',
  `count` varchar(12) DEFAULT NULL COMMENT '交易笔数',
  `type` varchar(1) DEFAULT NULL COMMENT '支付统计1, 交易统计2, 其他3',
  `pad1` varchar(128) DEFAULT NULL COMMENT '备注1',
  `pad2` varchar(128) DEFAULT NULL COMMENT '备注2',
  `pad3` varchar(128) DEFAULT NULL COMMENT '备注3'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='交易统计表';

DROP TABLE IF EXISTS `trd_pay_status`;
CREATE TABLE `trd_pay_status` (
  `proj_id` varchar(20) NOT NULL COMMENT '项目编号',
  `hospital_id` varchar(20) NOT NULL DEFAULT '' COMMENT '医院编码',
  `merchant_id` varchar(30) NOT NULL COMMENT '商户id',
  `trd_date` char(8) DEFAULT NULL COMMENT '交易日期，格式：yyyyMMdd',
  `pay_order_no` varchar(40) NOT NULL DEFAULT '' COMMENT '交易订单流水号（对账流水号）',
  `trd_time` char(6) DEFAULT NULL COMMENT '交易时间，格式：HHmmss',
  `clear_date` char(8) DEFAULT NULL COMMENT '支付平台（e百福）清算日期，格式：yyyyMMdd',
  `channel` char(2) NOT NULL COMMENT '终端渠道（WX-微信公众号，ZZ-自助机，CK-窗口扫码）',
  `trd_type` char(3) DEFAULT NULL COMMENT '交易类型：ACC 建档；REG 当日挂号；ODR 预约挂号；GET 预约取号；PAY 门诊缴费；REC 门诊充值；DEP 住院充值',
  `pay_type` varchar(4) DEFAULT NULL COMMENT '支付方式：CASH 现金；ACC 预交金；POS 银行卡；WX 微信支付；ZFB 支付宝；YSF-银联云闪付',
  `open_id` varchar(50) DEFAULT '' COMMENT '微信openid',
  `card_no` varchar(20) DEFAULT NULL COMMENT '卡号(或者住院号)',
  `pat_id` varchar(20) DEFAULT '' COMMENT '患者id',
  `pat_name` varchar(20) DEFAULT '' COMMENT '患者姓名',
  `pay_seq_no` varchar(50) DEFAULT NULL COMMENT '支付平台（e百福）返回的服务端流水号',
  `refund_order_no` varchar(30) DEFAULT NULL COMMENT '商户退款订单号',
  `refund_seq_no` varchar(50) DEFAULT NULL COMMENT '支付平台（e百福）返回的退款流水号',
  `amt` varchar(20) DEFAULT '' COMMENT '交易金额（分），也即实扣金额',
  `order_amt` varchar(20) DEFAULT '' COMMENT '订单金额（分），原始金额',
  `trd_status` char(2) DEFAULT NULL COMMENT '支付平台（e百福）返回的交易结果常见枚举值（00-支付成功，01-支付失败， 02-支付已经关闭，99-交易超时，AA-退款处理中）',
  `status` varchar(10) DEFAULT '' COMMENT '交易状态（CREATE-交易创建，SUCCESS-完成，PAY-微信已支付，FAIL-失败，TIMEOUT-第三方交易超时，REFUND-已退款）',
  `notify_url` varchar(100) DEFAULT NULL COMMENT '异步通知地址URL',
  `remark` text COMMENT '交易内容，通常存储与HIS的交易报文，json结构',
  `pad1` varchar(100) DEFAULT NULL COMMENT '备注1',
  `pad2` varchar(100) DEFAULT NULL COMMENT '备注2',
  `pad3` varchar(100) DEFAULT NULL COMMENT '备注3',
  PRIMARY KEY (`proj_id`, `hospital_id`, `merchant_id`,`pay_order_no`),
  KEY `trd_pay_status_i1` (`trd_date`,`hospital_id`,`pay_order_no`,`proj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='支付交易状态表';

DROP TABLE IF EXISTS `trd_pay_status_h`;
CREATE TABLE `trd_pay_status_h` (
  `proj_id` varchar(20) NOT NULL COMMENT '项目编号',
  `hospital_id` varchar(20) NOT NULL DEFAULT '' COMMENT '医院编码',
  `merchant_id` varchar(30) NOT NULL COMMENT '商户id',
  `trd_date` char(8) DEFAULT NULL COMMENT '交易日期，格式：yyyyMMdd',
  `pay_order_no` varchar(40) NOT NULL DEFAULT '' COMMENT '交易订单流水号（对账流水号）',
  `trd_time` char(6) DEFAULT NULL COMMENT '交易时间，格式：HHmmss',
  `clear_date` char(8) DEFAULT NULL COMMENT '支付平台（e百福）清算日期，格式：yyyyMMdd',
  `channel` char(2) NOT NULL COMMENT '终端渠道（WX-微信公众号，ZZ-自助机，CK-窗口扫码）',
  `trd_type` char(3) DEFAULT NULL COMMENT '交易类型：ACC 建档；REG 当日挂号；ODR 预约挂号；GET 预约取号；PAY 门诊缴费；REC 门诊充值；DEP 住院充值',
  `pay_type` varchar(4) DEFAULT NULL COMMENT '支付方式：CASH 现金；ACC 预交金；POS 银行卡；WX 微信支付；ZFB 支付宝；YSF-银联云闪付',
  `open_id` varchar(50) DEFAULT '' COMMENT '微信openid',
  `card_no` varchar(20) DEFAULT NULL COMMENT '卡号(或者住院号)',
  `pat_id` varchar(20) DEFAULT '' COMMENT '患者id',
  `pat_name` varchar(20) DEFAULT '' COMMENT '患者姓名',
  `pay_seq_no` varchar(50) DEFAULT NULL COMMENT '支付平台（e百福）返回的服务端流水号',
  `refund_order_no` varchar(30) DEFAULT NULL COMMENT '商户退款订单号',
  `refund_seq_no` varchar(50) DEFAULT NULL COMMENT '支付平台（e百福）返回的退款流水号',
  `amt` varchar(20) DEFAULT '' COMMENT '交易金额（分），也即实扣金额',
  `order_amt` varchar(20) DEFAULT '' COMMENT '订单金额（分），原始金额',
  `trd_status` char(2) DEFAULT NULL COMMENT '支付平台（e百福）返回的交易结果常见枚举值（00-支付成功，01-支付失败， 02-支付已经关闭，99-交易超时，AA-退款处理中）',
  `status` varchar(10) DEFAULT '' COMMENT '交易状态（CREATE-交易创建，PAY-微信已支付，SUCCESS-HIS交易完成，FAIL-HIS交易失败，TIMEOUT-HIS交易超时，REFUND-已退款/冲正）',
  `notify_url` varchar(100) DEFAULT NULL COMMENT '异步通知地址URL',
  `remark` text COMMENT '交易内容，通常存储与HIS的交易报文，json结构',
  `pad1` varchar(100) DEFAULT NULL COMMENT '备注1',
  `pad2` varchar(100) DEFAULT NULL COMMENT '备注2',
  `pad3` varchar(100) DEFAULT NULL COMMENT '备注3',
  PRIMARY KEY (`proj_id`, `hospital_id`, `merchant_id`, `pay_order_no`),
  KEY `trd_pay_status_i1` (`trd_date`,`hospital_id`,`pay_order_no`,`proj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='支付交易状态表-历史表';

-- 微信公众号异步通知的记录
-- 此表主要用于分布式平台异步通知无状态化处理，异步通知防重机制
-- 解决重复异步通知影响业务的问题
-- 只要数据库里面有该流水号则证明已经收到通知了，后面不再处理
DROP TABLE IF EXISTS `trd_pay_notify`;
CREATE TABLE `trd_pay_notify` (
  `out_trade_no` varchar(80) NOT NULL COMMENT '异步通知交易订单号',
  `date` char(8) DEFAULT NULL COMMENT '异步通知时间，yyyyMMdd',
  `time` char(6) DEFAULT NULL COMMENT '异步通知时间，HHmmss',
  PRIMARY KEY (`out_trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='微信公众号异步通知的记录';

-- 收银台业务订单号和记录信息
-- 这个表主要是为了存储流水号和与HIS交互的json字符串
DROP TABLE IF EXISTS `trd_pay_temp`;
CREATE TABLE `trd_pay_temp` (
  `project_id` varchar(30) DEFAULT NULL COMMENT '项目ID',
  `hospital_id` varchar(30) DEFAULT NULL COMMENT '医院ID',
  `out_trade_no` varchar(80) NOT NULL COMMENT '流水号,格式：由大志对账平台生成流水',
  `order_id` varchar(40) DEFAULT NULL COMMENT '支付平台订单号',
  `date` char(8) DEFAULT NULL COMMENT '日期yyyyMMdd',
  `notify_url` varchar(500) DEFAULT NULL COMMENT '后端异步通知地址',
  `redirect_url` varchar(500) DEFAULT NULL COMMENT '前端完成跳转地址',
  `remark` text COMMENT '(国光银医通内部用)与HIS交互的资源',
  `data_cache` text COMMENT '(国光银医通内部用)存入的DataCache资源信息',
  `step_content` text COMMENT '(国光银医通内部用)存入的StepContent信息',
  PRIMARY KEY (`out_trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收银台业务订单号和记录信息';

DROP TABLE IF EXISTS `trd_print_data`;
CREATE TABLE `trd_print_data` (
  `hospital_id` varchar(20) NOT NULL COMMENT '医院编码',
  `date` char(8) NOT NULL COMMENT '交易日期，格式：yyyyMMdd',
  `time` char(6) NOT NULL COMMENT '交易时间，格式：HHmmss',
  `type` char(3) DEFAULT NULL COMMENT '交易类型：ACC 建档；REG 当日挂号；ODR 预约挂号；GET 预约取号；PAY 门诊缴费；REC 门诊充值；DEP 住院充值',
  `channel` char(50) DEFAULT NULL COMMENT '终端渠道（WX-微信公众号，ZZ-自助机，CK-窗口扫码）',
  `card_no` varchar(20) DEFAULT NULL COMMENT '诊疗卡号',
  `pat_id` varchar(20) DEFAULT NULL COMMENT '患者id',
  `data` text COMMENT '打印信息'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='凭条打印记录缓存表';

DROP TABLE IF EXISTS `des_state`;
CREATE TABLE `des_state` (
  `cat_id` int(4) NOT NULL DEFAULT '0' COMMENT '部件编号',
  `value` char(1) NOT NULL DEFAULT '0' COMMENT '状态值',
  `name` varchar(20) DEFAULT NULL COMMENT '状态名称',
  `todo` char(1) DEFAULT NULL COMMENT '动作：0，忽略；1，告警；2，故障',
  `pad1` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`cat_id`,`value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备部件状态值表';

INSERT INTO `des_state` (`cat_id`, `value`, `name`, `todo`, `pad1`) VALUES
	(2001, 0, '正常', '0', '#8484FF'),
	(2001, 1, '预警', '0', '#00FF00'),
	(2001, 2, '例行停机            ', '0', '#666666   '),
	(2001, 3, '报停                ', '0', '#CCCCCC   '),
	(2001, 4, '维护', '0', '#808000'),
	(2001, 5, '暂停服务', '0', '#FF0000'),
	(2001, 6, '通讯故障', '1', '#8B008B'),
	(2001, 7, '故障可用', '0', '#FFB888'),
	(2011, 0, '正常', '0', '#8484FF'),
	(2011, 3, '故障', '2', '#FF0000'),
	(2011, 9, '未安装', '0', '#CCCCCC'),
	(2012, 0, '正常', '0', '#8484FF'),
	(2012, 1, '卡箱少卡', '1', '#00FF00'),
	(2012, 2, '卡箱无卡', '1', '#00FF00'),
	(2012, 3, '故障', '2', '#FF0000'),
	(2012, 4, '废卡箱满', '1', '#00FF00'),
	(2012, 9, '未安装', '0', '#CCCCCC'),
	(2013, 0, '正常', '0', '#8484FF'),
	(2013, 3, '故障', '2', '#FF0000'),
	(2013, 9, '未安装', '0', '#CCCCCC'),
	(2014, 0, '正常', '0', '#8484FF'),
	(2014, 1, '纸少', '1', '#00FF00'),
	(2014, 2, '纸尽', '1', '#00FF00'),
	(2014, 3, '故障', '2', '#FF0000'),
	(2014, 9, '未安装', '0', '#CCCCCC'),
	(2015, 0, '正常', '0', '#8484FF'),
	(2015, 3, '故障', '2', '#FF0000'),
	(2015, 9, '未安装', '0', '#CCCCCC'),
	(2016, 0, '正常', '0', '#8484FF'),
	(2016, 3, '故障', '2', '#FF0000'),
	(2016, 9, '未安装', '0', '#CCCCCC'),
	(2017, 0, '正常                ', '0', '#8484FF   '),
	(2017, 1, '钞将满              ', '1', '#00FF00   '),
	(2017, 2, '钞满                ', '1', '#00FF00   '),
	(2017, 3, '故障                ', '2', '#FF0000   '),
	(2017, 9, '未安装              ', '0', '#CCCCCC   '),
	(2018, 0, '正常', '0', '#8484FF'),
	(2018, 1, '纸少', '1', '#00FF00'),
	(2018, 2, '纸尽', '1', '#00FF00'),
	(2018, 3, '故障', '2', '#FF0000'),
	(2018, 9, '未安装', '0', '#CCCCCC'),
	(2019, 0, '正常', '0', '#8484FF'),
	(2019, 3, '故障', '2', '#FF0000'),
	(2019, 9, '未安装', '0', '#CCCCCC'),
	(2020, 0, '正常', '0', '#8484FF'),
	(2020, 1, '纸少', '1', '#00FF00'),
	(2020, 2, '纸尽', '1', '#00FF00'),
	(2020, 3, '故障', '2', '#FF0000'),
	(2020, 9, '未安装', '0', '#CCCCCC');

DROP TABLE IF EXISTS `des_event`;
CREATE TABLE `des_event` (
  `event_id` int NOT NULL AUTO_INCREMENT,
  `hospital_id` varchar(20) DEFAULT NULL COMMENT '医院编号',
  `tstart` char(8) NOT NULL DEFAULT '' COMMENT '事件开始日期',
  `tstart_t` char(6) NOT NULL DEFAULT '' COMMENT '事件开始时间',
  `dev_id` varchar(8) NOT NULL DEFAULT '' COMMENT '设备编号',
  `cat_id` int NOT NULL DEFAULT '0' COMMENT '部件编号，见被监控部件表(t_statecat)',
  `value` int DEFAULT NULL COMMENT '故障值',
  `to` varchar(12) DEFAULT NULL COMMENT '需要通知的操作员或厂商代码',
  `totype` char(1) DEFAULT NULL COMMENT '与TO配合：‘0’=操作员，‘1’=维护商',
  `title` varchar(160) DEFAULT NULL COMMENT '故障描述',
  `phase` char(1) DEFAULT NULL COMMENT '事件已经处理到的阶段：‘0’=尚未开始跟踪，‘1’=叫修阶段，‘2’=到达现场阶段，‘3’=排除阶段，‘4’=事件已经结束',
  `jx_start` varchar(14) DEFAULT NULL,
  `jx_up` int DEFAULT NULL,
  `jx_real` varchar(14) DEFAULT NULL,
  `jx_long` int DEFAULT NULL,
  `dd_step` int DEFAULT NULL,
  `dd_ack` varchar(14) DEFAULT NULL,
  `dd_up` int DEFAULT NULL,
  `dd_real` varchar(14) DEFAULT NULL,
  `dd_long` int DEFAULT NULL,
  `ev_step` int DEFAULT NULL,
  `ev_ack` varchar(14) DEFAULT NULL,
  `ev_up` int DEFAULT NULL,
  `ev_real` datetime DEFAULT NULL COMMENT '实际解决的时间',
  `ev_long` int DEFAULT NULL,
  `tl_total` int DEFAULT NULL COMMENT '此事件从发生到解决，共耗时（分）',
  `prced` char(1) NOT NULL DEFAULT '' COMMENT '此事件已有人点击“处理”标志：‘0’=未，‘1’=已点击',
  `active` char(1) NOT NULL DEFAULT '' COMMENT '此事件尚未结束标志：‘0’=已结束，‘1’=未结束',
  `pad1` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='设备故障事件表';

DROP TABLE IF EXISTS `des_dev_state`;
CREATE TABLE `des_dev_state` (
  `hospital_id` varchar(20) NOT NULL COMMENT '医院编号',
  `dev_id` varchar(8) NOT NULL COMMENT '设备编号',
  `cat_id` char(4) NOT NULL COMMENT '部件编号，见被监控部件表',
  `value` char(1) NOT NULL DEFAULT '0' COMMENT '当前状态值，必须在部件的状态值明细表(t_states)中有对应记录',
  `wtype` char(1) DEFAULT NULL COMMENT '当前状态：0=正常，1=告警，2=故障',
  `err` varchar(15) DEFAULT NULL COMMENT '最后一次得到的故障代码',
  `dtime` datetime DEFAULT NULL COMMENT '当前状态开始的时间',
  `pad1` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`hospital_id`,`dev_id`,`cat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备各部件当前状态表';