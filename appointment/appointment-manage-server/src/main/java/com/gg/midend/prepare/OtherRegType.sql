SET @week = "1";
SET @noon = '1';

INSERT INTO sch_template_detail
(temp_id, week, noon, dept_id, register_type, create_time ) VALUES
-- 甲状腺结节专病门诊号	甲乳外科门诊
(1, @week, @noon, '0401060', '22', NOW()),
-- 结直肠癌筛查号	消化内科门诊
(1, @week, @noon, '0302020', '24', NOW()),
-- 腹痛多学科门诊号	肝胆外科门诊、胃肠外科门诊
(1, @week, @noon, '0401020', '25', NOW()),
(1, @week, @noon, '0401040', '25', NOW()),
-- 核酸检测号	急诊科
(1, @week, @noon, '9900220', '26', NOW()),
-- 皮肤科美容号	皮肤科
(1, @week, @noon, '1300020', '27', NOW()),
-- 幽门螺旋杆菌专科门诊号	消化内科门诊
(1, @week, @noon, '0302020', '28', NOW()),
-- 产科康复门诊号	产科门诊
(1, @week, @noon, '0502030', '32', NOW());
-- 肺科专家门诊、感染科(结核门诊)、肠道门诊的普通号，复诊号，义诊号
INSERT INTO sch_template_detail
        (temp_id, week, noon, dept_id, register_type, create_time )
        -- 复诊
        (SELECT 1, @week, @noon, id, '02', NOW()
        FROM midend.com_dept
        WHERE `level` = '2' AND active = '1' AND id in ('1600040', '1600060', '1600080', '1600050')
        )
        UNION
        -- 义诊
        (SELECT 1, @week, @noon, id, '07', NOW()
        FROM midend.com_dept
        WHERE `level` = '2' AND active = '1' AND id in ('1600040', '1600060', '1600080', '1600050')
        )
        UNION
        -- 普通号
        (SELECT 1, @week, @noon, id, '01', NOW()
        FROM midend.com_dept
        WHERE `level` = '2' AND active = '1' AND id in ('1600040', '1600060', '1600080', '1600050')
        )
