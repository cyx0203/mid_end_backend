CREATE DATABASE IF NOT EXISTS `midend_pay` CHARACTER SET utf8;
USE `midend_pay`;

DROP TABLE IF EXISTS `pay_type`;
CREATE TABLE `pay_type` (
  `id` char(4) NOT NULL COMMENT '支付类型码',
  `name` varchar(50) NOT NULL COMMENT '支付类型名',
  `third_id` char(2) NOT NULL COMMENT '第三方码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='第三方支付方式表';

INSERT INTO `pay_type` (`id`, `third_id`, `name`) VALUES
	('0101', '01', '用户被扫'),
	('0102', '01', '用户主扫'),
	('0104', '01', '刷脸支付'),
	('0105', '01', '一码付'),
	('0201', '02', '用户被扫'),
	('0202', '02', '用户主扫'),
	('0203', '02', '微信公众号支付'),
	('0204', '02', '刷脸支付'),
	('0205', '02', '一码付');

DROP TABLE IF EXISTS `pay_third`;
CREATE TABLE `pay_third` (
  `id` char(2) NOT NULL COMMENT '三方支付渠道编号',
  `name` varchar(50) NOT NULL COMMENT '三方支付渠道名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='第三方支付渠道表';

INSERT INTO `pay_third` (`id`, `name`) VALUES
	('01', '支付宝'),
	('02', '微信');

DROP TABLE IF EXISTS `pay_operate_record`;
CREATE TABLE `pay_operate_record` (
  `oper_id` varchar(32) NOT NULL COMMENT '管理员工号',
  `order_trace` varchar(64) NOT NULL COMMENT '原始订单号',
  `order_id` varchar(64) NOT NULL COMMENT '订单号',
  `op_time` char(1) NOT NULL COMMENT '操作类型，1，退款；2，调账；3，手动对账'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='管理员后台操作关键记录表';

DROP TABLE IF EXISTS `chk_biz_trans`;
CREATE TABLE `chk_biz_trans` (
  `merchant_id` varchar(30) NOT NULL COMMENT '商户号',
  `account_date` char(8) NOT NULL COMMENT '账单日期',
  `fac_id` varchar(8) DEFAULT NULL COMMENT '厂商id',
  `trans_type` int NOT NULL COMMENT '交易类型：1-收费；-1-退款',
  `third_id` char(2) NOT NULL COMMENT '第三方渠道代码',
  `pay_type` char(6) DEFAULT NULL COMMENT '支付类型',
  `order_type` char(10) NOT NULL COMMENT '订单类型，如挂号，缴费',
  `check_id` varchar(64) NOT NULL COMMENT '用于对账的唯一订单号，关联pay_order_pay的order_id',
  `check_origin_id` varchar(64) DEFAULT NULL DEFAULT '' COMMENT '退款交易的原支付订单号，当trans_type为收费时，该字段为空，关联pay_order_pay的order_id',
  `serial_no` varchar(64) DEFAULT NULL COMMENT '业务收费流水号',
  `refund_serial_no` varchar(64) DEFAULT NULL COMMENT '业务退费流水号',
  `oper_id` varchar(20) DEFAULT NULL COMMENT '操作员编号',
  `oper_name` varchar(20) DEFAULT NULL COMMENT '操作员名称',
  `trans_time` char(14) NOT NULL COMMENT '交易时间，格式yyyyMMddHHmmss',
  `trans_amt` int NOT NULL COMMENT '自费交易金额(单位分)',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  PRIMARY KEY (`merchant_id`,`check_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务账单流水表';

DROP TABLE IF EXISTS `chk_biz_trans_h`;
CREATE TABLE `chk_biz_trans_h` (
  `merchant_id` varchar(30) NOT NULL COMMENT '商户号',
  `account_date` char(8) NOT NULL COMMENT '账单日期',
  `fac_id` varchar(8) DEFAULT NULL COMMENT '厂商id',
  `trans_type` int NOT NULL COMMENT '交易类型：1-收费；-1-退款',
  `third_id` char(2) NOT NULL COMMENT '第三方渠道代码',
  `pay_type` char(6) DEFAULT NULL COMMENT '支付类型',
  `order_type` char(10) NOT NULL COMMENT '订单类型，如挂号，缴费',
  `check_id` varchar(64) NOT NULL COMMENT '用于对账的唯一订单号，关联pay_order_pay的order_id',
  `check_origin_id` varchar(64) DEFAULT NULL DEFAULT '' COMMENT '退款交易的原支付订单号，当trans_type为收费时，该字段为空，关联pay_order_pay的order_id',
  `serial_no` varchar(64) DEFAULT NULL COMMENT '业务收费流水号',
  `refund_serial_no` varchar(64) DEFAULT NULL COMMENT '业务退费流水号',
  `oper_id` varchar(20) DEFAULT NULL COMMENT '操作员编号',
  `oper_name` varchar(20) DEFAULT NULL COMMENT '操作员名称',
  `trans_time` char(14) NOT NULL COMMENT '交易时间，格式yyyyMMddHHmmss',
  `trans_amt` int NOT NULL COMMENT '自费交易金额(单位分)',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  PRIMARY KEY (`merchant_id`,`check_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务账单流水表-历史表';

DROP TABLE IF EXISTS `chk_third_trans`;
CREATE TABLE `chk_third_trans` (
  `merchant_id` varchar(30) NOT NULL COMMENT '商户号',
  `account_date` char(8) NOT NULL COMMENT '账单日期',
  `fac_id` varchar(8) DEFAULT NULL COMMENT '厂商id',
  `trans_type` int NOT NULL COMMENT '交易类型：1-收费；-1-退款',
  `third_id` char(2) NOT NULL COMMENT '第三方渠道代码',
  `check_id` varchar(64) NOT NULL COMMENT '用于对账的唯一订单号，关联pay_order_pay的order_id',
  `check_origin_id` varchar(64) DEFAULT NULL DEFAULT '' COMMENT '退款交易的原支付订单号，当trans_type为收费时，该字段为空，关联pay_order_pay的order_id',
  `serial_no` varchar(64) DEFAULT NULL COMMENT '收费流水号',
  `refund_serial_no` varchar(64) DEFAULT NULL COMMENT '退费流水号',
  `payer_name` varchar(64) DEFAULT NULL COMMENT '付款人姓名',
  `payer_account` varchar(64) DEFAULT NULL COMMENT '支付账号',
  `trans_time` char(14) NOT NULL COMMENT '交易时间，格式yyyyMMddHHmmss',
  `trans_amt` int NOT NULL COMMENT '自费交易金额(单位分)',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  PRIMARY KEY (`merchant_id`,`check_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='第三方支付账单流水表';

DROP TABLE IF EXISTS `chk_third_trans_h`;
CREATE TABLE `chk_third_trans_h` (
  `merchant_id` varchar(30) NOT NULL COMMENT '商户号',
  `account_date` char(8) NOT NULL COMMENT '账单日期',
  `fac_id` varchar(8) DEFAULT NULL COMMENT '厂商id',
  `trans_type` int NOT NULL COMMENT '交易类型：1-收费；-1-退款',
  `third_id` char(2) NOT NULL COMMENT '第三方渠道代码',
  `check_id` varchar(64) NOT NULL COMMENT '用于对账的唯一订单号，关联pay_order_pay的order_id',
  `check_origin_id` varchar(64) DEFAULT NULL DEFAULT '' COMMENT '退款交易的原支付订单号，当trans_type为收费时，该字段为空，关联pay_order_pay的order_id',
  `serial_no` varchar(64) DEFAULT NULL COMMENT '支付流水号',
  `refund_serial_no` varchar(64) DEFAULT NULL COMMENT '退费流水号',
  `payer_name` varchar(64) DEFAULT NULL COMMENT '付款人姓名',
  `payer_account` varchar(64) DEFAULT NULL COMMENT '支付账号',
  `trans_time` char(14) NOT NULL COMMENT '交易时间，格式yyyyMMddHHmmss',
  `trans_amt` int NOT NULL COMMENT '自费交易金额(单位分)',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  PRIMARY KEY (`merchant_id`,`check_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='第三方账单流水表-历史表';

DROP TABLE IF EXISTS `chk_third_getstatus`;
CREATE TABLE `chk_third_getstatus` (
  `merchant_id` varchar(30) NOT NULL COMMENT '商户号',
  `account_date` char(8) NOT NULL COMMENT '账单日期',
  `status` char(1) NOT NULL COMMENT '状态，0表示明细收全(数量同条数字段)，1表示有异常',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  PRIMARY KEY (`merchant_id`,`account_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='第三方支付账单收取状态';

DROP TABLE IF EXISTS `chk_biz_getstatus`;
CREATE TABLE `chk_biz_getstatus` (
  `merchant_id` varchar(30) NOT NULL COMMENT '商户号',
  `account_date` char(8) NOT NULL COMMENT '账单日期',
  `status` char(1) NOT NULL COMMENT '状态，0表示明细收全(数量同条数字段)，1表示有异常',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  PRIMARY KEY (`merchant_id`,`account_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务账单收取状态';

DROP TABLE IF EXISTS `chk_result_status`;
CREATE TABLE `chk_result_status` (
  `merchant_id` varchar(30) NOT NULL COMMENT '商户号',
  `account_date` char(8) NOT NULL COMMENT '账单日期',
  `status` char(1) NOT NULL COMMENT '对账状态，0对平，1异常，2账不平',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  PRIMARY KEY (`merchant_id`, `account_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='对账结果表';

DROP TABLE IF EXISTS `chk_error_record`;
CREATE TABLE `chk_error_record` (
  `merchant_id` varchar(30) NOT NULL COMMENT '商户号',
  `account_date` char(8) NOT NULL COMMENT '账单日期',
  `trans_type` int NOT NULL COMMENT '交易类型：1-收费；-1-退款',
  `check_id` varchar(64) NOT NULL COMMENT '用于对账的唯一订单号，关联pay_order_pay的order_id',
  `check_origin_id` varchar(64) DEFAULT NULL DEFAULT '' COMMENT '退款交易的原支付订单号，当trans_type为收费时，该字段为空，关联pay_order_pay的order_id',
  `account_source` char(1) NOT NULL COMMENT '账单来源：1-三方账单，2-业务账单',
  `error_type` char(1) NOT NULL COMMENT '错账类型：0-已经退款或冲正（平账），1-多账，2-金额不一致',
  `trans_time` char(14) NOT NULL COMMENT '交易时间，格式yyyyMMddHHmmss',
  `trans_amt` int NOT NULL COMMENT '自费交易金额(单位分)',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  `refund_time` datetime DEFAULT NULL COMMENT '跨天退款时间',
  PRIMARY KEY (`merchant_id`, `account_source`, `check_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='对账结果多条目记录表';

DROP TABLE IF EXISTS `mer_institution`;
CREATE TABLE `mer_institution` (
  `id` varchar(20) NOT NULL COMMENT '收单机构编号',
  `name` varchar(50) NOT NULL COMMENT '收单机构名称',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收单机构表';

DROP TABLE IF EXISTS `mer_merchant`;
CREATE TABLE `mer_merchant` (
  `id` varchar(20) NOT NULL COMMENT '商户号',
  `name` varchar(50) NOT NULL COMMENT '商户名称',
  `inst_id` varchar(20) NOT NULL COMMENT '收单机构编号',
  `active` char(1) DEFAULT '1' COMMENT '激活标志:1-启动；0-停用',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户表';

DROP TABLE IF EXISTS `mer_channel`;
CREATE TABLE `mer_channel` (
  `inst_id` varchar(20) NOT NULL COMMENT '收单机构编号',
  `merchant_id` varchar(20) NOT NULL COMMENT '商户号',
  `channel_id` varchar(20) NOT NULL COMMENT '渠道代码',
  `pay_type_id` char(4) NOT NULL COMMENT '支付类型码',
  `active` char(1) DEFAULT '1' COMMENT '激活标志:1-启动；0-停用',
  PRIMARY KEY (`channel_id`, `pay_type_id`, `merchant_id`, `inst_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='支付渠道授权关系表';

DROP TABLE IF EXISTS `mer_pay_type`;
CREATE TABLE `mer_pay_type` (
  `merchant_id` varchar(20) NOT NULL COMMENT '商户号',
  `pay_type_id` char(4) NOT NULL COMMENT '支付类型码',
  `active` char(1) DEFAULT '1' COMMENT '激活标志:1-启动；0-停用',
  PRIMARY KEY (`merchant_id`, `pay_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户与支付方式关系表';

DROP TABLE IF EXISTS `mer_param_wechat`;
CREATE TABLE `mer_param_wechat` (
  `merchant_id` varchar(20) NOT NULL COMMENT '商户号',
  `service_id` varchar(20) DEFAULT NULL COMMENT '服务商或公众号主体编号',
  PRIMARY KEY (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户微信支付表';

DROP TABLE IF EXISTS `mer_param_wechat_service`;
CREATE TABLE `mer_param_wechat_service` (
  `id` varchar(20) NOT NULL COMMENT '服务商或公众号主体编号',
  `app_id` varchar(64) NOT NULL COMMENT 'appId,由不同的第三方决定',
  `key` varchar(255) DEFAULT NULL COMMENT '微信支付密钥',
  `cert` text COMMENT '微信p12证书',
  `type` char(1) DEFAULT NULL COMMENT '1-服务商模式；2-普通模式',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户微信支付服务商表';

DROP TABLE IF EXISTS `mer_param_alipay`;
CREATE TABLE `mer_param_alipay` (
  `merchant_id` varchar(20) NOT NULL COMMENT '商户号',
  `app_id` varchar(64) NOT NULL COMMENT 'appId,由不同的第三方决定',
  `third_merchant_id` varchar(64) DEFAULT NULL COMMENT '第三方商户号(支付宝、微信、网关、聚合等支付)',
  `app_public_key` text COMMENT '支付宝应用公钥',
  `app_private_key` text COMMENT '支付宝应用私钥',
  `public_key` text COMMENT '支付宝公钥',
  `sign_type` varchar(10) DEFAULT NULL COMMENT '支付宝签名算法：RSA或RSA2',
  PRIMARY KEY (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商户支付宝参数表';

DROP TABLE IF EXISTS `mer_hospital`;
CREATE TABLE `mer_hospital` (
  `merchant_id` varchar(20) NOT NULL COMMENT '商户号',
  `hospital_id` varchar(20) NOT NULL COMMENT '院区编号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收单商户与院区的绑定关系表';

DROP TABLE IF EXISTS `mer_user`;
CREATE TABLE `mer_user` (
  `merchant_id` varchar(20) NOT NULL COMMENT '商户号',
  `account` varchar(20) NOT NULL COMMENT '系统用户名',
  PRIMARY KEY (`merchant_id`, `account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='登录用户可查看商户的限制';

DROP TABLE IF EXISTS `pay_goods`;
CREATE TABLE `pay_goods` (
  `id` varchar(10) NOT NULL COMMENT '商品编号',
  `name` varchar(50) NOT NULL COMMENT '商品名称',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='支付商品表';

DROP TABLE IF EXISTS `pay_user_password`;
CREATE TABLE `pay_user_password` (
  `account` varchar(20) NOT NULL COMMENT '系统用户名',
  `password` char(32) NOT NULL DEFAULT 'c4ca4238a0b923820dcc509a6f75849b' COMMENT '超级密码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统用户超级密码表';

DROP TABLE IF EXISTS `pay_order`;
CREATE TABLE `pay_order` (
  `merchant_id` varchar(20) NOT NULL COMMENT '商户号',
  `hospital_id` varchar(20) NOT NULL COMMENT '院区编号',
  `order_trace` varchar(64) NOT NULL COMMENT '平台创建的订单号',
  `channel_id` varchar(20) NOT NULL COMMENT '渠道代码',
  `goods_id` varchar(10) NOT NULL COMMENT '商品编号',

  `order_amt` int(13) NOT NULL DEFAULT 0 COMMENT '订单总金额(单位分)',
  `refund_amt` int(13) NOT NULL DEFAULT 0 COMMENT '已退款金额(单位分)',

  `pay_status` char(1) NOT NULL DEFAULT '0' COMMENT '支付状态：0初始状态，1待支付，2支付完成，9已关闭',
  `refund_status` char(1) NOT NULL DEFAULT '0' COMMENT '退款状态：0未退款，1已全额退，2已部分退，3已冲正',
  `biz_status` char(1) NOT NULL DEFAULT '0' COMMENT '业务状态：0初始状态，1已申请，2成功，3明确失败，4异常',

  `buyer_id` varchar(20) DEFAULT NULL COMMENT '用户证件号码',
  `buyer_name` varchar(20) DEFAULT NULL COMMENT '用户姓名',
  `buyer_phone` char(11) DEFAULT NULL COMMENT '用户手机号',
  `fac_id` varchar(8) DEFAULT NULL COMMENT '厂商id',
  `create_date` char(8) NOT NULL COMMENT '创建日期',
  `create_time` char(6) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`order_trace`),
  KEY `idx_order` (`create_date`, `merchant_id`, `order_trace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='支付订单表';

DROP TABLE IF EXISTS `pay_order_pay`;
CREATE TABLE `pay_order_pay` (
  `merchant_id` varchar(20) NOT NULL COMMENT '商户号',
  `hospital_id` varchar(20) NOT NULL COMMENT '院区编号',
  `order_id` varchar(64) NOT NULL COMMENT '平台流水号。支付时，平台流水号=支付订单号，退费时，平台流水号=退费订单号',
  `order_trace` varchar(64) NOT NULL COMMENT '平台创建的原始订单号',
  `trans_type` int NOT NULL COMMENT '交易类型：1-收费；-1-退款',

  `third_id` char(2) NOT NULL COMMENT '支付目录',
  `pay_type_id` char(4) NOT NULL COMMENT '支付类型码',
  `channel_id` varchar(20) NOT NULL COMMENT '渠道代码',

  `order_amt` int(13) DEFAULT NULL COMMENT '送到第三方的实际支付或退费金额（单位分）',
  `third_seq_no` varchar(64) DEFAULT NULL COMMENT '第三方平台支付或退费流水号(第三方平台对账明细无法记录平台订单号时比较重要)',

  `create_date` char(8) NOT NULL COMMENT '创建日期',
  `create_time` char(6) NOT NULL COMMENT '创建时间',
  `third_date` char(8) DEFAULT NULL COMMENT '第三方支付或退费完成日期（第三方返回）',
  `third_time` char(6) DEFAULT NULL COMMENT '第三方支付或退费完成时间（第三方返回）',

  `status` char(1) NOT NULL COMMENT '订单支付状态：0-待确认，1-已确认，9-订单关闭',
  `refund_reason` varchar(255) DEFAULT NULL COMMENT '退款原因',
  `oper_id` varchar(20) DEFAULT NULL COMMENT '操作员编号',
  `term_id` varchar(20) DEFAULT NULL COMMENT '自助设备id',
  PRIMARY KEY (`order_id`),
  KEY `idx_ordertrace` (`order_trace`),
  KEY `idx_date` (`create_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='支付订单明细表';

DROP TABLE IF EXISTS `pay_medical_fee`;
CREATE TABLE `pay_medical_fee` (
  `order_id` varchar(64) NOT NULL COMMENT 'pay_order_pay的order_id',
  `medfee_sumamt` int DEFAULT NULL COMMENT '医疗费总额(his返回的总额)',
  `fulamt_ownpay_amt` int DEFAULT NULL COMMENT '全自费金额',
  `overlmt_selfpay` int DEFAULT NULL COMMENT '超限价自费费用',
  `preselfpay_amt` int DEFAULT NULL COMMENT '先行自付金额',
  `inscp_scp_amt` int DEFAULT NULL COMMENT '符合政策范围金额',
  `act_pay_dedc` int DEFAULT NULL COMMENT '实际支付起付线',
  `hifp_pay` int DEFAULT NULL COMMENT '基本医疗保险统筹基金支出',
  `pool_prop_selfpay` int DEFAULT NULL COMMENT '基本医疗保险统筹基金支付比例',
  `cvlserv_pay` int DEFAULT NULL COMMENT '公务员医疗补助资金支出',
  `hifes_pay` int DEFAULT NULL COMMENT '企业补充医疗保险基金支出',
  `hifmi_pay` int DEFAULT NULL COMMENT '居民大病保险资金支出',
  `hifob_pay` int DEFAULT NULL COMMENT '职工大额医疗费用补助基金支出',
  `maf_pay` int DEFAULT NULL COMMENT '医疗救助基金支出',
  `oth_pay` int DEFAULT NULL COMMENT '其他支出',
  `fund_pay_sumamt` int DEFAULT NULL COMMENT '基金支付总额(统筹金额)',
  `psn_part_amt` int DEFAULT NULL COMMENT '个人负担总金额',
  `acct_pay` int DEFAULT NULL COMMENT '个人账户支出',
  `psn_cash_pay` int DEFAULT NULL COMMENT '个人现金支出（三方支付金额）',
  `hosp_part_amt` int DEFAULT NULL COMMENT '医院负担金额',
  `balc` int DEFAULT NULL COMMENT '余额',
  `acct_mulaid_pay` int DEFAULT NULL COMMENT '个人账户共济支付金额',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='医保收费金额表';
