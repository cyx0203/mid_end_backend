package com.gg.midend.controller.interhospital;

import com.gg.core.annotation.MultiRequestBody;
import com.gg.core.exception.api.ApiResult;
import com.gg.midend.domain.DTO.CreatMsgReq;
import com.gg.midend.domain.DTO.QryMsgReq;
import com.gg.midend.domain.DTO.RecallMsgReq;
import com.gg.midend.domain.VO.ThemeMsgInfo;
import com.gg.midend.service.MsgService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 *  消息对话管理
 *  Controller
 *
 * @author fun-mean
 * @version 1.0
 * @since 2023-02-13
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/interhospital-api/ChatMsg")
public class MsgController {

    private final MsgService msgService;

    /**
     * 根据主题ID查询聊天记录
     *
     * @param qryMsgReq 查询聊天记录入参
     * @return ThemeMsgInfo 聊天记录信息列表
     */
    @RequestMapping(value = "/selectMsg")
    public ApiResult<List<ThemeMsgInfo>> selectMsg(@Valid @MultiRequestBody("body") QryMsgReq qryMsgReq) {

        return ApiResult.success("listInfo", msgService.selMsgInfo(qryMsgReq));
    }

    /**
     * 回复消息
     *
     * @param creatMsgReq 回复消息入参
     * @return Long msgId
     */
    @PostMapping(value = "/insertitem")
    @Transactional(rollbackFor = {java.lang.Exception.class, java.lang.RuntimeException.class})
    public ApiResult<Long> creatMsg(@Valid @MultiRequestBody("body") CreatMsgReq creatMsgReq) {

        return ApiResult.success("msgId",msgService.creatMsg(creatMsgReq));
    }

    /**
     * 撤回消息
     *
     * @param recallMsgReq 撤回消息入参
     * @return num 更新记录条数
     */
    @RequestMapping(value = "/recallMsg")
    @Transactional(rollbackFor = {java.lang.Exception.class, java.lang.RuntimeException.class})
    public ApiResult<Integer> recallMsg(@Valid @MultiRequestBody("body") RecallMsgReq recallMsgReq) {

        return ApiResult.success("num",msgService.updMsgState(recallMsgReq));
    }

    /**
     * 消息回复引用
     *
     * @return 结果
     */
    @RequestMapping(value = "/quoteMsg")
    @Transactional(rollbackFor = {java.lang.Exception.class, java.lang.RuntimeException.class})
    public ApiResult<Long> quoteMsg(@Valid @MultiRequestBody("body") CreatMsgReq creatMsgReq) {

        return ApiResult.success("msgId",msgService.creatMsg(creatMsgReq));
    }

}
