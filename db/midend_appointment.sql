CREATE DATABASE IF NOT EXISTS `midend_appointment` CHARACTER SET utf8;
USE `midend_appointment`;

CREATE FUNCTION uuid_format(_uuid CHAR(36))
RETURNS CHAR(16)
LANGUAGE SQL DETERMINISTIC CONTAINS SQL SQL SECURITY INVOKER
RETURN
CONCAT(
SUBSTR(_uuid, 10, 4),
SUBSTR(_uuid,  1, 8),
SUBSTR(_uuid, 20, 4));
DELIMITER ;

---------------------------------sch 周排班--------------------------------------------

DROP TABLE IF EXISTS `sch_template`;
CREATE TABLE `sch_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `name` varchar(100) NOT NULL COMMENT '模板描述',
  `hospital_id` varchar(20) NOT NULL COMMENT '院区编号',
  `active` char(1) NOT NULL DEFAULT '0' COMMENT '激活标志,1-启用；0-停用',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='周排班模板表';

INSERT INTO `sch_template` (`name`, `hospital_id`, `active`, `create_time`) VALUES
	('默认模板', 'H001', '1', NOW());

DROP TABLE IF EXISTS `sch_template_detail`;
CREATE TABLE `sch_template_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `temp_id` int(11) NOT NULL COMMENT 'sch_template表主键',
  `week` char(1) NOT NULL COMMENT '周排班星期 1星期一 2星期二 3星期三 4星期四 5星期五 6星期六 7星期日 ',
  `noon` char(1) NOT NULL COMMENT '午别：1，上午；2，下午',
  `dept_id` varchar(20) NOT NULL COMMENT '科室编号',
  `register_type` char(2) NOT NULL COMMENT '挂号号别',
  `doctor_id` varchar(20) DEFAULT NULL COMMENT '医生编号',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_all` (`temp_id`, `week`, `noon`, `dept_id`, `register_type`, `doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='周排班明细表';

DROP TABLE IF EXISTS `sch_dept`;
CREATE TABLE `sch_dept` (
  `hospital_id` varchar(20) NOT NULL COMMENT '院区编号',
  `id` varchar(20) NOT NULL COMMENT '科室编号',
  `name` varchar(40) NOT NULL COMMENT '科室名称',
  `par_id` varchar(20) NOT NULL COMMENT '上级科室编号',
  `level` char(1) NOT NULL COMMENT '等级：1-一级科室；2-二级科室',
  `order` int(3) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`, `hospital_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='门诊排班科室表';

---------------------------------src 号源--------------------------------------------

DROP TABLE IF EXISTS `src_date`;
CREATE TABLE `src_date` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `business_type` char(1) NOT NULL COMMENT '预约业务类型',
  `hospital_id` varchar(20) NOT NULL COMMENT '院区编号',
  `date` char(8) NOT NULL COMMENT '预约日期',
  `valid` char(1) NOT NULL DEFAULT '0' COMMENT '有效状态：0-未生成号源；1-已生成号源',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_hos_date` (`business_type`, `hospital_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='预约号源日期表';

DROP TABLE IF EXISTS `src_schedule`;
CREATE TABLE `src_schedule` (
  `id` char(16) NOT NULL COMMENT '排班编号-uuid',
  `src_date_id` int(11) NOT NULL COMMENT 'src_date表主键',
  `noon` char(1) NOT NULL COMMENT '午别：1，上午；2，下午',
  `dept_id` varchar(20) NOT NULL COMMENT '科室编号',
  `register_type` char(2) NOT NULL COMMENT '挂号号类，0-普通号；1-专家号',
  `doctor_id` varchar(20) DEFAULT NULL COMMENT '医生编号',
  `valid` char(1) NOT NULL DEFAULT '0' COMMENT '有效状态：0-未生成号源；1-已生成号源',
  `active` char(1) NOT NULL DEFAULT '1' COMMENT '激活标志,1-启用；0-停用',
  PRIMARY KEY (`id`),
  KEY `index_date` (`src_date_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='预约号源排班表';

DROP TABLE IF EXISTS `src_source`;
CREATE TABLE `src_source` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `src_sch_id` char(16) NOT NULL COMMENT 'src_schedule表主键',
  `order` int(4) NOT NULL COMMENT '序号',
  `stime` char(4) NOT NULL COMMENT '开始时间段',
  `etime` char(4) NOT NULL COMMENT '结束时间段',
  `status` char(1) NOT NULL COMMENT '预约状态：0，空闲；1，锁定；2，预约；9，就诊',
  `num_type` char(1) DEFAULT '0' COMMENT '0-普通号；1-保留号；2-附加号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sch_order` (`src_sch_id`,`order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='预约号源表';

DROP TABLE IF EXISTS `src_source_h`;
CREATE TABLE `src_source_h` (
  `id` int(11) NOT NULL COMMENT 'src_source表主键',
  `src_sch_id` char(16) NOT NULL COMMENT 'src_schedule表主键',
  `order` int(4) NOT NULL COMMENT '序号',
  `stime` char(4) NOT NULL COMMENT '开始时间段',
  `etime` char(4) NOT NULL COMMENT '结束时间段',
  `status` char(1) NOT NULL COMMENT '预约状态：0，空闲；1，锁定；2，预约；9，就诊',
  `num_type` char(1) DEFAULT '0' COMMENT '0-普通号；1-保留号；2-附加号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sch_order` (`src_sch_id`,`order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='预约号源历史表';

DROP TABLE IF EXISTS `src_order`;
CREATE TABLE `src_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `order_no` char(16) NOT NULL COMMENT '订单编号-uuid',
  `src_id` int(11) NOT NULL COMMENT 'src_source表主键',
  `src_sch_id` char(16) NOT NULL COMMENT 'src_schedule表主键',
  `cust_id` varchar(20) NOT NULL COMMENT '渠道编号',
  `date` char(8) NOT NULL COMMENT '预约日期',
  `idcard` char(18) DEFAULT NULL COMMENT '身份证号码',
  `user_id` varchar(50) DEFAULT NULL COMMENT '预约人识别卡号',
  `user_name` varchar(100) DEFAULT NULL COMMENT '预约人姓名',
  `user_phone` varchar(11) DEFAULT NULL COMMENT '预约人手机号',
  `pay_type` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `pay_fee` varchar(10) DEFAULT NULL COMMENT '支付金额，单位元',
  `merchant_id` varchar(30) NOT NULL COMMENT '商户号',
  `trans_order_no` varchar(64) DEFAULT NULL COMMENT '交易订单号',
  `bus_order_no` varchar(64) DEFAULT NULL COMMENT '业务订单号',
  `pay_account` varchar(64) DEFAULT NULL COMMENT '支付账号',
  `status` char(1) NOT NULL COMMENT '订单状态：0，解锁；1，锁号；2，预约；3，取消；9，就诊',
  `oper_id` varchar(20) DEFAULT NULL COMMENT '操作员编号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_source` char(1) NOT NULL DEFAULT '1' COMMENT '订单来源：1，预约平台；2，HIS同步',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orderno` (`order_no`),
  KEY `idx_srcid` (`src_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='预约订单表';

DROP TABLE IF EXISTS `src_order_detail`;
CREATE TABLE `src_order_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `order_id` int(11) NOT NULL COMMENT '订单表id，关联src_order表的id',
  `action` char(1) NOT NULL COMMENT '操作类型：0，解锁；1，锁号；2，预约；3，取消；9，就诊',
  `cust_id` varchar(20) NOT NULL COMMENT '渠道编号',
  `oper_id` varchar(20) DEFAULT NULL COMMENT '操作员编号',
  `result` char(1) NOT NULL DEFAULT '1' COMMENT '操作结果:1-成功；0-失败',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` text COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_srcid` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='预约订单流水表';

---------------------------------rule 号源生成规则--------------------------------------------
DROP TABLE IF EXISTS `rule_season`;
CREATE TABLE `rule_season` (
  `hospital_id` varchar(20) NOT NULL COMMENT '院区编号',
  `pid` char(1) NOT NULL COMMENT '主时令,1-夏，2-冬，0-全年',
  `sid` char(1) NOT NULL COMMENT '次时令，1-上午，2-下午',
  `start_date` char(4) DEFAULT NULL COMMENT '开始日期',
  `end_date` char(4) DEFAULT NULL COMMENT '结束日期',
  `start_time` char(4) NOT NULL COMMENT '开始时间',
  `end_time` char(4) NOT NULL COMMENT '结束时间',
  `active` char(1) NOT NULL COMMENT '激活标志,1-启用；0-停用',
  PRIMARY KEY (`hospital_id`, `pid`, `sid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='时令规则表';

INSERT INTO `rule_season` (`hospital_id`, `pid`, `sid`, `start_date`, `end_date`, `start_time`, `end_time`, `active`) VALUES
('H001', '0', '1', NULL, NULL, '0800', '1200', '1'),
('H001', '0', '2', NULL, NULL, '1300', '1700', '1'),
('H001', '1', '1', '0501', '1031', '0730', '1130', '0'),
('H001', '1', '2', '0501', '1031', '1230', '1630', '0'),
('H001', '2', '1', '1101', '0430', '0800', '1200', '0'),
('H001', '2', '2', '1101', '0430', '1300', '1700', '0');

DROP TABLE IF EXISTS `rule_source`;
CREATE TABLE `rule_source` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `hospital_id` varchar(20) NOT NULL COMMENT '院区编号',

  `dept_id_list` varchar(255) NOT NULL COMMENT '绑定科室编号，用“，”分割，默认##',
  `register_type` char(2) NOT NULL COMMENT '挂号号别',
  `source_num` int NOT NULL COMMENT '单位分时段内号源设置数量',
  `time_type` char(1) NOT NULL COMMENT '分时段区间：0-30分钟；1-半天',
  `retain_num` varchar(4) DEFAULT NULL COMMENT '保留号个数',
  `add_num` varchar(4) DEFAULT NULL COMMENT '附加号个数',
  `prefix` char(1) DEFAULT NULL COMMENT '号序前缀',
  `length` char(1) DEFAULT NULL COMMENT '号序长度',
  `active` char(1) DEFAULT '1' COMMENT '激活标志,1-启用；0-停用',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='号源数量规则表';

DROP TABLE IF EXISTS `rule_source_special`;
CREATE TABLE `rule_source_special` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `rule_source_id` int(11) NOT NULL COMMENT 'rule_source的id',
  `stime` char(4) NOT NULL COMMENT '开始时间段',
  `etime` char(4) NOT NULL COMMENT '结束时间段',
  `source_num` int NOT NULL COMMENT '分时段内号源设置数量',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='号源数量规则-全天分时表';

DROP TABLE IF EXISTS `rule_register_type`;
CREATE TABLE `rule_register_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `hospital_id` varchar(20) NOT NULL COMMENT '院区编号',
  `register_type` varchar(2) NOT NULL COMMENT '挂号号别',
  `channel_id_list` varchar(100) NOT NULL COMMENT '渠道ID，用“，”分割，默认##',
  `dept_id_list` char(200) NOT NULL COMMENT '科室ID，用“，”分割，默认##',
  `active` char(1) DEFAULT '1' COMMENT '激活标志：1-启用；0-停用',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8 COMMENT='号别规则表';

DROP TABLE IF EXISTS `src_stop_record`;
CREATE TABLE `src_stop_record` (
  `hospital_id` varchar(20) NOT NULL COMMENT '院区编号',
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `src_sch_id` char(16) NOT NULL COMMENT 'src_schedule表主键',
  `notice_time` varchar(64) DEFAULT NULL COMMENT '通知门办时间',
  `notice_type` varchar(64) DEFAULT NULL COMMENT '通知门办方式',
  `reason` varchar(255) DEFAULT NULL COMMENT '停诊原因',
  `reboot` varchar(255) NOT NULL DEFAULT '0' COMMENT '重启标记：0-未重启，1-已重启',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='停诊记录表';

DROP TABLE IF EXISTS `src_stop_notice`;
CREATE TABLE `src_stop_notice` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `record_id` INT NOT NULL COMMENT 'src_stop_record表主键',
  `order_no` char(16) NOT NULL COMMENT '订单编号-uuid',
  `idcard` char(18) DEFAULT NULL COMMENT '身份证号码',
  `user_id` varchar(50) DEFAULT NULL COMMENT '预约人识别卡号',
  `user_name` varchar(100) DEFAULT NULL COMMENT '预约人姓名',
  `user_phone` varchar(11) DEFAULT NULL COMMENT '预约人手机号',
  `tip` char(1) NOT NULL DEFAULT '0' COMMENT '标记状态：0-未通知，1-已通知',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='停诊后通知患者列表';

DROP TABLE IF EXISTS `src_add_record`;
CREATE TABLE `src_add_record` (
  `hospital_id` varchar(20) NOT NULL COMMENT '院区编号',
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `src_sch_id` char(16) NOT NULL COMMENT 'src_schedule表主键',
  `add_num` int(4) NOT NULL COMMENT '加号数量',
  `start_order` int(4) NOT NULL COMMENT '开始号序',
  `end_order` int(4) NOT NULL COMMENT '结束号序',
  `reason` varchar(255) DEFAULT NULL COMMENT '加号原因',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='加号记录表';

DROP TABLE IF EXISTS `src_sync_record`;
CREATE TABLE `src_sync_record` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自动编号',
  `hospital_id` varchar(20) NOT NULL COMMENT '院区编号',
  `date` char(8) NOT NULL COMMENT '同步日期',
  `result` char(1) NOT NULL COMMENT '同步结果：1，成功；0，失败',
  `remark` text COMMENT '备注信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='号源同步记录表';