package com.gg.midend.service.impl;

import com.gg.midend.domain.DTO.CreatMsgReq;
import com.gg.midend.domain.DTO.ModifyThemeMsgReq;
import com.gg.midend.domain.DTO.QryMsgReq;
import com.gg.midend.domain.DTO.RecallMsgReq;
import com.gg.midend.domain.VO.ThemeMsgInfo;
import com.gg.midend.mapper.MsgMapper;
import com.gg.midend.service.MsgService;
import com.gg.midend.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.gg.midend.exception.enums.GlobalErrorCodeConstants.DATABASE_UPDATE_ERROR;
import static com.gg.midend.exception.util.ServiceExceptionUtil.exception;

/**
 * 实现类
 * 消息相关服务
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-20
 **/
@Service
@RequiredArgsConstructor
public class MsgServiceImpl implements MsgService {

    private final MsgMapper msgMapper;

    private final ThemeService themeService;

    @Override
    public List<ThemeMsgInfo> selMsgInfo(QryMsgReq qryMsgReq) {

        return msgMapper.selMsgInfo(qryMsgReq);
    }

    @Override
    public Long creatMsg(CreatMsgReq creatMsgReq) {

        int num = msgMapper.intMsgInfo(creatMsgReq);

        if (num == 1) {

            ModifyThemeMsgReq modifyThemeMsgReq = new ModifyThemeMsgReq();
            modifyThemeMsgReq.setThemeId(creatMsgReq.getThemeId());
            modifyThemeMsgReq.setMsgBy(creatMsgReq.getMsgBy());
            modifyThemeMsgReq.setCountdown(creatMsgReq.getCountdown());
            modifyThemeMsgReq.setNewMsgId(creatMsgReq.getMsgId());

            int updNum = themeService.updThemeMsg(modifyThemeMsgReq);
            if (updNum == 0) {

                throw exception(DATABASE_UPDATE_ERROR);
            }
        }
        return creatMsgReq.getMsgId();
    }

    @Override
    public int updMsgState(RecallMsgReq recallMsgReq) {
        return msgMapper.updMsgState(recallMsgReq);
    }
}
