package com.gg.midend.mapper;

import com.gg.midend.domain.DTO.CreatWordsReq;
import com.gg.midend.domain.DTO.QryWordsReq;
import com.gg.midend.domain.VO.CommWordsInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper
 * 医生常用语相关
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 **/
@Mapper
public interface CommWordMapper {

    CommWordsInfo selCommWordsList(QryWordsReq qryWordsReq);

    int intCommWords(CreatWordsReq creatWordsReq);

    int updCommWords(CreatWordsReq creatWordsReq);
}
