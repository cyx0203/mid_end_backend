package com.gg.midend.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.gg.midend.domain.DTO.CreatWordsReq;
import com.gg.midend.domain.DTO.QryWordsReq;
import com.gg.midend.domain.VO.CommWordsInfo;
import com.gg.midend.mapper.CommWordMapper;
import com.gg.midend.service.CommWordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 实现类
 * 医生常用语
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 **/
@Service
@RequiredArgsConstructor
public class CommWordsServiceImpl implements CommWordsService {

    private final CommWordMapper commWordMapper;

    @Override
    public CommWordsInfo selCommWordsList(QryWordsReq qryWordsReq) {

        return commWordMapper.selCommWordsList(qryWordsReq);
    }

    @Override
    public int updOrIntCommWords(CreatWordsReq creatWordsReq) {

        QryWordsReq qryWordsReq = new QryWordsReq();
        qryWordsReq.setHospitalId(creatWordsReq.getHospitalId());
        qryWordsReq.setDoctorCode(creatWordsReq.getDoctorCode());
        qryWordsReq.setType(creatWordsReq.getType());

        CommWordsInfo commWordsInfo = commWordMapper.selCommWordsList(qryWordsReq);

        if (ObjUtil.isNull(commWordsInfo)) {

            return commWordMapper.intCommWords(creatWordsReq);
        } else {

            return commWordMapper.updCommWords(creatWordsReq);
        }
    }
}
