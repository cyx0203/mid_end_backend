package com.gg.midend.domain.VO;

import lombok.Data;

/**
 * 实体类-出参数据对象
 * 问诊统计
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-15
 **/
@Data
public class ThemeStatics {

    /**
     * 正在问诊数
     */
    private int inqNum;

    /**
     * 问诊完成数
     */
    private int finishNum;

    /**
     * 本周接诊数
     */
    private int oneWeekNum;
}
