package com.gg.midend.mapper;

import com.gg.midend.domain.DTO.CreatMsgReq;
import com.gg.midend.domain.DTO.QryMsgReq;
import com.gg.midend.domain.DTO.RecallMsgReq;
import com.gg.midend.domain.VO.ThemeMsgInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Mapper
 * 消息记录相关
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-20
 **/
@Mapper
public interface MsgMapper {

    List<ThemeMsgInfo> selMsgInfo(QryMsgReq qryMsgReq);

    int intMsgInfo(CreatMsgReq creatMsgReq);

    int updMsgState(RecallMsgReq recallMsgReq);
}
