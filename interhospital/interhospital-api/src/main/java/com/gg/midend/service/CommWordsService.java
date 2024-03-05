package com.gg.midend.service;

import com.gg.midend.domain.DTO.CreatWordsReq;
import com.gg.midend.domain.DTO.QryWordsReq;
import com.gg.midend.domain.VO.CommWordsInfo;

/**
 * 医生常用语相关服务
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 */
public interface CommWordsService {

    /**
     * 查询医生常用语
     *
     * @param qryWordsReq 查询常用语入参
     * @return CommWordsInfo 医生常用语信息
     */
    CommWordsInfo selCommWordsList(QryWordsReq qryWordsReq);

    /**
     * 更新或插入医生常用语
     *
     * @param creatWordsReq 创建医生常用语
     * @return num 更新记录条数
     */
    int updOrIntCommWords(CreatWordsReq creatWordsReq);
}
