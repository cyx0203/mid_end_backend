package com.gg.midend.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import com.gg.midend.domain.DTO.ModifyThemeMsgReq;
import com.gg.midend.domain.DTO.QryThemeAppReq;
import com.gg.midend.domain.DTO.QryThemePatReq;
import com.gg.midend.domain.DTO.ThemeReq;
import com.gg.midend.domain.VO.ThemeAppInfo;
import com.gg.midend.domain.VO.ThemeInfo;
import com.gg.midend.domain.VO.ThemePatInfo;
import com.gg.midend.domain.VO.ThemeStatics;
import com.gg.midend.mapper.ThemeMapper;
import com.gg.midend.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.gg.midend.exception.enums.ServiceErrorCodeConstants.COUNT_NOT_NULL;
import static com.gg.midend.exception.enums.ServiceErrorCodeConstants.COUNT_SHORTAGE;
import static com.gg.midend.exception.util.ServiceExceptionUtil.exception;

/**
 * 问诊主题服务 实现类
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-17
 */
@Service
@RequiredArgsConstructor
public class ThemeServiceImpl implements ThemeService {

    private final ThemeMapper themeMapper;

    @Override
    public ThemeInfo selThemeInfo(Long themeId) {

        return themeMapper.selThemeInfo(themeId);
    }

    @Override
    public int updThemeMsg(ModifyThemeMsgReq modifyThemeMsgReq) {

        //区分医生回复和患者回复，患者回复需要记次
        if (modifyThemeMsgReq.getMsgBy().equals("A")){

            ModifyThemeMsgReq doctUpdMsg = new ModifyThemeMsgReq();
            doctUpdMsg.setThemeId(modifyThemeMsgReq.getThemeId());
            doctUpdMsg.setNewMsgId(modifyThemeMsgReq.getNewMsgId());
            doctUpdMsg.setThemeStatus(0);
            doctUpdMsg.setNewReply(2);
            return themeMapper.updThemeMsg(doctUpdMsg);
        }else if(modifyThemeMsgReq.getMsgBy().equals("B")){

            if (ObjUtil.isNull(modifyThemeMsgReq.getCountdown())){

                throw exception(COUNT_NOT_NULL);
            }

            if (NumberUtil.equals(modifyThemeMsgReq.getCountdown(),0)){

                throw exception(COUNT_SHORTAGE);
            }

            ModifyThemeMsgReq patUpdMsg = new ModifyThemeMsgReq();
            patUpdMsg.setThemeId(modifyThemeMsgReq.getThemeId());
            patUpdMsg.setNewMsgId(modifyThemeMsgReq.getNewMsgId());
            patUpdMsg.setCountdown(modifyThemeMsgReq.getCountdown()-1);
            patUpdMsg.setThemeStatus(0);
            patUpdMsg.setNewReply(1);
            return themeMapper.updThemeMsg(patUpdMsg);
        }else{

            return 0;
        }
    }

    @Override
    public Long intThemeInfo(ThemeReq themeReq) {

        themeMapper.intThemeInfo(themeReq);
        return themeReq.getThemeId();
    }

    @Override
    public int stopTheme(Long themeId) {

        return themeMapper.stopTheme(themeId);
    }

    @Override
    public List<ThemePatInfo> queryThemePatInfo(QryThemePatReq reqVO) {

        return themeMapper.queryThemePatInfo(reqVO);
    }

    @Override
    public ThemeStatics staticsStatus(String doctId) {

        return themeMapper.staticsStatus(doctId);
    }

    @Override
    public List<ThemeAppInfo> qryThemeAppInfo(QryThemeAppReq reqVO) {

        return themeMapper.selThemeAppInfo(reqVO);
    }
}
