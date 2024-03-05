CREATE DATABASE IF NOT EXISTS `midend_identity` CHARACTER SET utf8;
USE `midend_identity`;

DROP TABLE IF EXISTS `idn_address`;
CREATE TABLE `idn_address` (
  `idn_id` int(11) NOT NULL COMMENT 'patient表对应id',
  `address_type` varchar(2) NOT NULL COMMENT '地址类别',
  `province` varchar(10) DEFAULT NULL COMMENT '省',
  `city` varchar(20) DEFAULT NULL COMMENT '市',
  `area` varchar(50) DEFAULT NULL COMMENT '区',
  `street` varchar(50) DEFAULT NULL COMMENT '街道',
  `full_addr` varchar(100) DEFAULT NULL COMMENT '门牌',
  `zip_code` varchar(50) DEFAULT NULL COMMENT '邮编',
  `pad1` varchar(50) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`idn_id`,`address_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='地址';

DROP TABLE IF EXISTS `idn_guardian`;
CREATE TABLE `idn_guardian` (
  `idn_id` int(11) NOT NULL COMMENT 'patient表对应id',
  `relation` varchar(10) NOT NULL COMMENT '关系',
  `name` varchar(10) NOT NULL COMMENT '姓名',
  `phone` varchar(15) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(50) DEFAULT NULL COMMENT '地址',
  `pad1` varchar(50) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`idn_id`,`relation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='监护人';

DROP TABLE IF EXISTS `idn_patient`;
CREATE TABLE `idn_patient` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_name` varchar(10) NOT NULL COMMENT '患者姓名',
  `patient_sex` varchar(1) NOT NULL COMMENT '患者性别',
  `real_flag` varchar(1) DEFAULT '1' COMMENT '实名标志 0=实名  1=非实名',
  `status` varchar(1) DEFAULT '0' COMMENT '状态 0=正常  1=停用',
  `idno` varchar(18) NOT NULL COMMENT '身份证号',
  `health_card_no` varchar(50) DEFAULT NULL COMMENT '居民健康卡号',
  `social_card_no` varchar(50) DEFAULT NULL COMMENT '医保卡号',
  `card_no` varchar(50) DEFAULT NULL COMMENT '就诊卡号',
  `birth` date DEFAULT NULL COMMENT '出生日期',
  `marriage` varchar(2) DEFAULT NULL COMMENT '婚姻状况',
  `country` varchar(15) DEFAULT NULL COMMENT '国籍',
  `nation` varchar(10) DEFAULT NULL COMMENT '民族',
  `occ` varchar(20) DEFAULT NULL COMMENT '职业',
  `work_company` varchar(50) DEFAULT NULL COMMENT '工作单位',
  `work_phone` varchar(15) DEFAULT NULL COMMENT '工作电话',
  `phone` varchar(15) DEFAULT NULL COMMENT '电话',
  `wechat_id` varchar(50) DEFAULT NULL COMMENT '微信id',
  `blood` varchar(5) DEFAULT NULL COMMENT '血型',
  `medical` varchar(200) DEFAULT NULL COMMENT '疾病史',
  `infectious` varchar(200) DEFAULT NULL COMMENT '传染病史',
  `vaccination` varchar(200) DEFAULT NULL COMMENT '预防接种史',
  `operation` varchar(200) DEFAULT NULL COMMENT '手术史',
  `transfusion` varchar(200) DEFAULT NULL COMMENT '输血史',
  `marriage_childbirth` varchar(200) DEFAULT NULL COMMENT '婚育史',
  `menstruation` varchar(200) DEFAULT NULL COMMENT '月经史',
  `family` varchar(200) DEFAULT NULL COMMENT '家族史',
  `allergy` char(1) DEFAULT NULL COMMENT '是否过敏 0=过敏  1=不过敏',
  `allergys` varchar(200) DEFAULT NULL COMMENT '过敏情况',
  `create_time` datetime DEFAULT NULL COMMENT '建档日期',
  `up_time` datetime DEFAULT NULL COMMENT '最近更新日期',
  `create_user` varchar(10) DEFAULT NULL COMMENT '创建人员',
  `up_user` varchar(10) DEFAULT NULL COMMENT '最近更新人员',
  `create_channel` varchar(10) DEFAULT NULL COMMENT '创建渠道',
  `up_channel` varchar(10) DEFAULT NULL COMMENT '更新渠道',
  `real_cert` varchar(10) DEFAULT NULL COMMENT '实名证件类型',
  `real_no` varchar(50) DEFAULT NULL COMMENT '实名证件号',
  `pad1` varchar(50) DEFAULT NULL COMMENT '备注1',
  `pad2` varchar(100) DEFAULT NULL COMMENT '备注2',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idno` (`idno`),
  UNIQUE KEY `health_card_no` (`health_card_no`),
  UNIQUE KEY `social_card_no` (`social_card_no`),
  UNIQUE KEY `card_no` (`card_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='患者基本信息';

DROP TABLE IF EXISTS `idn_patientids`;
CREATE TABLE `idn_patientids` (
  `idn_id` int(11) NOT NULL COMMENT 'patient表对应id',
  `hospital` varchar(10) NOT NULL COMMENT '医院代码',
  `patient_id` varchar(15) NOT NULL COMMENT '院内个人id',
  `id_type` varchar(2) NOT NULL COMMENT 'id类型  0=身份证，1=医保 ，2=居民健康卡  ，3=就诊卡',
  `pad1` varchar(50) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`idn_id`,`hospital`,`id_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='患者个人id';

