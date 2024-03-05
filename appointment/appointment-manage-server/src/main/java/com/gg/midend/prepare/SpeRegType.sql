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
  UNIQUE KEY `uk_all` (`temp_id`, `week`, `noon`, `dept_id`, `register_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='周排班明细表';

 SET @week = "1";
 SET @noon = '1';

 INSERT INTO sch_template_detail
        (temp_id, week, noon, dept_id, register_type, create_time )
        -- 复诊
        (SELECT 1, @week, @noon, id, '02', NOW()
        FROM midend.com_dept
        WHERE `level` = '2' AND active = '1' AND id <> '9900220'
		    )
        UNION
        -- 义诊
        (SELECT 1, @week, @noon, id, '07', NOW()
        FROM midend.com_dept
        WHERE `level` = '2' AND active = '1' AND id <> '9900220'
        )
        UNION
        -- 妇产普通号
        (SELECT 1, @week, @noon, id, '04', NOW()
        FROM midend.com_dept
        WHERE `level` = '2' AND active = '1' AND id in ('0500020', '0502030')
        )
        UNION
        -- 口腔普通号
        (SELECT 1, @week, @noon, id, '05', NOW()
        FROM midend.com_dept
        WHERE `level` = '2' AND active = '1' AND id = '1200020'
        )
        UNION
        -- 普通号
        (SELECT 1, @week, @noon, id, '01', NOW()
        FROM midend.com_dept
        WHERE `level` = '2' AND active = '1' AND id IN (
        '2100020',
        '0402020',
        '3100000',
        '3200060',
        '0308030',
        '1400020',
        '1900020',
        '0404020',
        '9900220',
        '0301030',
        '0303030',
        '0308020',
        '0401020',
        '1600050',
        '2700020',
        '0401040',
        '0401080',
        '1100020',
        '1500000',
        '0403050',
        '3100010',
        '0307120',
        '0310020',
        '1000020',
        '1300020',
        '1600040',
        '0310010',
        '5000020',
        '1900030',
        '0304020',
        '0306030',
        '0308010',
        '0401100',
        '1600030',
        '1600070',
        '0700030',
        '9900230',
        '0302020',
        '0306020',
        '0307030',
        '0401060',
        '0405020'
        )
       )