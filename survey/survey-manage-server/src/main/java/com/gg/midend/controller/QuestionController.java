package com.gg.midend.controller;

import com.gg.core.exception.ExceptionCenter;
import com.gg.midend.service.QuestionServeice;
import com.gg.midend.utils.MyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author txy
 * @create 2022-12-29-16:25
 */

@RestController
@RequestMapping("/manage")
public class QuestionController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QuestionServeice questionServeice;

    /**
     * 获取问卷内容
     *
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "getQuestionItems")
    public Object getQuestionItems(@RequestBody Map paramMap) {

        HashMap<String, Object> reqMap = new HashMap<>();      //定义请求参数
        Map respBodyMap = new HashMap();                       //定义返回正文

        log.info("getQuestionItems入参：" + paramMap);

        List resp = questionServeice.getQuestionItems(paramMap);
        log.info("出参:" + resp);

        respBodyMap.put("list", resp);
        return respBodyMap;
    }

    /**
     * 保存问卷选项
     *
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "saveQuestionItem")
    @Transactional(rollbackFor = {Exception.class})
    public Object saveQuestionItem(@RequestBody Map paramMap) throws ExceptionCenter {

        HashMap<String, Object> reqMap = new HashMap<>();      //定义请求参数
        Map respBodyMap = new HashMap();                       //定义返回正文

        log.info("saveQuestionItem入参：" + paramMap);

        // 有选项的情况下，定义map，存储选项值
        ArrayList<String> reqList = new ArrayList<>();
        HashMap<String, List> optionMap = new HashMap<>();
        Set keySet = paramMap.keySet();
        Iterator iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (key.indexOf("option") != -1) {
                reqList.add((String) paramMap.get(key));
            }
        }
        optionMap.put("optionResult", reqList);

        // 其他必传参数
        String itemId = (String) paramMap.get("itemId");
        reqMap.put("questionId", (String) paramMap.get("questionId"));
        reqMap.put("itemId", itemId);
        reqMap.put("item", (String) paramMap.get("item"));
        reqMap.put("type", (String) paramMap.get("type"));
        reqMap.put("order", (Integer) paramMap.get("order"));
        reqMap.put("createUser", (String) paramMap.get("createUser"));

        Object resp = questionServeice.saveQuestionItem(itemId, reqMap, optionMap);
        log.info("出参:" + resp);

        respBodyMap.put("list", resp);
        return respBodyMap;
    }

    /**
     * 获取问卷统计结果【chats统计专用】
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "getQuestionResultForCharts")
    public Object getQuestionResultForCharts(@RequestBody Map paramMap) {

        Map respBodyMap = new HashMap();                            //定义返回正文

        log.info("getQuestionItems入参：" + paramMap);

        List items = questionServeice.getQuestionItems(paramMap);  // 获取所有的问卷题目信息
        List resp = questionServeice.getQuestionResultForCharts(items);     // 获取问卷答案统计结果

        log.info("出参:" + resp);

        respBodyMap.put("list", resp);
        return respBodyMap;
    }

    /**
     * 获取统计结果【table统计专用】
     * 【暂时弃用】
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "getQuestionResultForTable")
    public Object getQuestionResultForTable(@RequestBody Map paramMap) {

        Map respBodyMap = new HashMap();                            //定义返回正文
        Set questionIdSet = new HashSet<>();

        log.info("getQuestionItems入参：" + paramMap);

        List items = questionServeice.getQuestionItems(paramMap);  // 获取所有的问卷题目信息
        Iterator itemsIterator = items.iterator();
        while (itemsIterator.hasNext()) {
            Map nextItem = (Map) itemsIterator.next();
            questionIdSet.add((String) nextItem.get("questionId"));
        }

        List resp = questionServeice.getQuestionResultForTable(items);     // 获取问卷答案统计结果

        Iterator setIterator = questionIdSet.iterator();
        while (setIterator.hasNext()) {
            Map newMap = new HashMap<>();
            String questionId = (String) setIterator.next();
            Iterator respIterator = resp.iterator();
            List option1 = new ArrayList<Integer>();
            List option2 = new ArrayList<Integer>();
            List option3 = new ArrayList<Integer>();
            List count = new ArrayList<Integer>();
            while (respIterator.hasNext()) {
                Map next = (Map) respIterator.next();
                if (next.get("questionId").equals(questionId)) {
                    if (!next.get("option1").equals(null)) {
                        option1.add(next.get("option1"));
                    } else {
                        option1.add(0);
                    }
                    if (!next.get("option2").equals(null)) {
                        option2.add(next.get("option2"));
                    } else {
                        option2.add(0);
                    }
                    if (!next.get("option3").equals(null)) {
                        option3.add(next.get("option3"));
                    } else {
                        option3.add(0);
                    }
                    if (!next.get("count").equals(null)) {
                        count.add(next.get("count"));
                    } else {
                        count.add(0);
                    }
                } else {
                    continue;
                }
            }
            int arg1 = MyUtils.intArrage(option1);
            int arg2 = MyUtils.intArrage(option2);
            int arg3 = MyUtils.intArrage(option3);
            int arg4 = MyUtils.intArrage(count);

            newMap.put("questionId", questionId);
            newMap.put("question", "总体满意度");
            newMap.put("option1", arg1);
            newMap.put("option2", arg2);
            newMap.put("option3", arg3);
            newMap.put("count", arg4);

            resp.add(newMap);
        }

        log.info("出参:" + resp);
        respBodyMap.put("list", resp);
        return respBodyMap;
    }


    /**
     * 获取满意度统计结果
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "getQuestionResult")
    public Object getQuestionResult(@RequestBody Map paramMap) {

        Map respBodyMap = new HashMap();                            //定义返回正文
        Set questionIdSet = new HashSet<>();

        log.info("getQuestionItems入参：" + paramMap);

        List items = questionServeice.getQuestionItems(paramMap);  // 获取所有的问卷题目信息

        Iterator itemsIterator = items.iterator();
        while (itemsIterator.hasNext()) {
            Map nextItem = (Map) itemsIterator.next();
            questionIdSet.add((String) nextItem.get("questionId"));
        }

        List resp = questionServeice.getQuestionResult(items);     // 获取问卷答案统计结果

        log.info("出参:" + resp);
        respBodyMap.put("list", resp);
        return respBodyMap;
    }

    /**
     * 获取科室满意度统计结果
     * @param paramMap
     * @return
     */
    @RequestMapping(value = "getQuestionResultForDeptStat")
    public Object getQuestionResultForDeptStat(@RequestBody Map paramMap) {

        Map respBodyMap = new HashMap();                            //定义返回正文

        log.info("getQuestionItems入参：" + paramMap);

        List resp = questionServeice.getQuestionResultForDeptStat(paramMap);     // 获取问卷答案统计结果

        log.info("出参:" + resp);

        respBodyMap.put("list", resp);
        return respBodyMap;
    }


}
