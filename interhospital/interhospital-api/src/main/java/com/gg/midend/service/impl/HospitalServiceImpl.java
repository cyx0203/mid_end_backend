package com.gg.midend.service.impl;

import com.gg.midend.domain.VO.HospInfo;
import com.gg.midend.domain.VO.HospListInfo;
import com.gg.midend.mapper.HospitalMapper;
import com.gg.midend.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 实现类
 * 医院相关服务
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-16
 **/
@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {

    private final HospitalMapper hospitalMapper;

    @Override
    public HospInfo qryHosInfo(String hospitalId) {

        return hospitalMapper.selHospInfo(hospitalId);
    }

    @Override
    public HospListInfo getLoginHospital(String active) {

        return hospitalMapper.selHospList(active);
    }
}
