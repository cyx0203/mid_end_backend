package com.gg.midend.service;

import com.gg.midend.domain.DTO.CreatMsgReq;
import com.gg.midend.domain.DTO.QryMsgReq;
import com.gg.midend.domain.DTO.RecallMsgReq;
import com.gg.midend.domain.VO.ThemeMsgInfo;

import java.util.List;

/**
 * 主题消息服务
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-03-20
 */
public interface MsgService{

    /**
     * 根据主题查询聊天记录
     *
     * @param qryMsgReq 查询信息入参
     * @return ThemeMsgInfo 消息记录
     */
    List<ThemeMsgInfo> selMsgInfo(QryMsgReq qryMsgReq);

    /**
     * 插入消息记录并更新主题表
     *
     * @param creatMsgReq 插入消息入参
     * @return msgId 消息id
     */
    Long creatMsg(CreatMsgReq creatMsgReq);

    /**
     * 更新消息状态
     *
     * @param recallMsgReq 插入消息入参
     * @return num 更新记录条数
     */
    int updMsgState(RecallMsgReq recallMsgReq);

}
