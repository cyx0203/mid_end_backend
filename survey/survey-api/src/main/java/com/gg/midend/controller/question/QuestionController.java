package com.gg.midend.controller.question;

import com.gg.midend.controller.base.GGBaseController;
import com.gg.midend.service.question.QuestionServeice;
import com.mysql.cj.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author txy
 * @create 2022-12-29-16:25
 */

@RestController
@RequestMapping("/survey-api")
public class QuestionController extends GGBaseController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QuestionServeice questionServeice;

    /**
     * 获取问卷内容
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "getQuestionItems")
    public Object getQuestionItems(@RequestBody Map paramMap){

        HashMap<String, Object> reqMap = new HashMap<>();      //定义请求参数
        Map respBodyMap = new HashMap();			           //定义返回正文

        log.info("getQuestionItems入参：" + paramMap);
        Map bodyMap = (Map) paramMap.get("body");

        if(StringUtils.isNullOrEmpty((String) bodyMap.get("questionId"))){
            return retBack("1", "questionId不能为空");
        }
        reqMap.put("questionId", bodyMap.get("questionId"));	//问卷ID
        log.info("reqMap入参:" + reqMap);

        List resp = questionServeice.getQuestionItems(reqMap);
        log.info("出参:" + resp);

        if(resp.size() <= 0) {	//判断记录是否为空
            return retBack("1", "未查询到问卷信息，请确认参数是否正确！");
        }
        respBodyMap.put("List", resp);
        return  retBack("0", "交易成功", respBodyMap);
    }


    /**
     * 保存问卷结果
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "saveQuestionResult")
    public Object saveQuestionResult(@RequestBody Map paramMap){

        HashMap<String, Object> reqMap = new HashMap<>();      //定义请求参数
        Map respBodyMap = new HashMap();			           //定义返回正文

        log.info("saveQuestionResult入参：" + paramMap);
        Map bodyMap = (Map) paramMap.get("body");

        if(StringUtils.isNullOrEmpty((String) bodyMap.get("questionId"))){
            return retBack("1", "questionId不能为空");
        }
        if(StringUtils.isNullOrEmpty((String) bodyMap.get("userId"))){
            return retBack("1", "userId不能为空");
        }
        if(StringUtils.isNullOrEmpty((String) bodyMap.get("channelId"))){
            return retBack("1", "channelId不能为空");
        }
        if(StringUtils.isNullOrEmpty((String) bodyMap.get("userName"))){
            return retBack("1", "userName不能为空");
        }
        if(StringUtils.isNullOrEmpty((String) bodyMap.get("userPhone"))){
            return retBack("1", "userPhone不能为空");
        }

        String questionId = (String) bodyMap.get("questionId");
        String userId = (String) bodyMap.get("userId");
        String userName = (String) bodyMap.get("userName");
        String userPhone = (String) bodyMap.get("userPhone");
        String channelId = (String) bodyMap.get("channelId");
        List result = (List) bodyMap.get("result");

        if ((result == null) || (result.size() == 0)){
            return retBack("1", "result不能为空");
        }

        reqMap.put("questionId", questionId);
        reqMap.put("userId", userId);
        reqMap.put("userName", userName);
        reqMap.put("userPhone", userPhone);
        reqMap.put("channelId", channelId);
        reqMap.put("result", result);
        log.info("reqMap入参:" + reqMap);

        Object resp = questionServeice.saveQuestionResult(questionId, userId, channelId, userName, userPhone,  result);
        log.info("出参:" + resp);

        respBodyMap.put("success", resp);

        return  retBack("0", "交易成功", respBodyMap);
    }

}
