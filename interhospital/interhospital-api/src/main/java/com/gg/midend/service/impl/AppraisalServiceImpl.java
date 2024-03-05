package com.gg.midend.service.impl;

import com.gg.midend.domain.DTO.AppraisalReq;
import com.gg.midend.mapper.AppraisalMapper;
import com.gg.midend.service.AppraisalService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static com.gg.midend.exception.enums.GlobalErrorCodeConstants.DATABASE_RECORD_REPEAT;
import static com.gg.midend.exception.util.ServiceExceptionUtil.exception;

/**
 * 实现类
 * 医生护工评价服务
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-03
 **/
@Service
@RequiredArgsConstructor
public class AppraisalServiceImpl implements AppraisalService {

    private final AppraisalMapper appraisalMapper;

    /**
     * 生成 flow_no
     * yyyyMMddHHmm+随机6位
     *
     * @return flowNo
     */
    private Long createFlowNo() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        String random = String.format("%06d", new Random().nextInt(999999));
        String flowNo = timestamp + random;
        return Long.valueOf(flowNo);
    }

    @Override
    public Long intAppraisalInfo(AppraisalReq appraisalReq) {

        try {
            appraisalReq.setFlowNo(createFlowNo());
            appraisalMapper.intAppraisalInfo(appraisalReq);
        } catch (DuplicateKeyException e) {
            throw exception(DATABASE_RECORD_REPEAT);
        }

        return appraisalReq.getFlowNo();
    }

    @Override
    public int delAppraisalInfo(Long flowNo) {
        return appraisalMapper.delByFlowNo(flowNo);
    }
}
