package com.gg.midend.service.question;

import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author txy
 * @create 2023-01-09-9:05
 */

@Service
public class QuestionServeice {

    @Autowired
    private SqlService sqlService;

    public List getQuestionItems(HashMap<String, Object> reqMap) {
        List respList = new ArrayList<Map>();			           //定义返回List
        List<Map> itemList = sqlService.selectList("questionSurveyItem", "selectByQuestionId", reqMap);	 //问卷列表
        Iterator<Map> iterator = itemList.iterator();

        while (iterator.hasNext()){
            HashMap<String, Object> respMap = new HashMap<>(); // 处理好的一条Map记录

            Map nextItem = iterator.next();
            String typeStr = (String) nextItem.get("type");
            String itemStr = (String) nextItem.get("item");

            if (typeStr.equals("1") || typeStr.equals("2")){
                ArrayList<String> option = new ArrayList<>();

                String itemId = (String) nextItem.get("itemId");
                HashMap<String, Object> req = new HashMap<>();  // 组请求map包
                req.put("itemId", itemId);
                List optionList = sqlService.selectList("questionSurveyOption", "selectByItemId", req);
                Iterator<Map> optionIterator =  optionList.iterator();
                while (optionIterator.hasNext()){
                    Map nextOption = (Map)  optionIterator.next();
                    option.add((String) nextOption.get("option"));
                }
                respMap.put("option", option);
            }
            respMap.put("item", itemStr);
            respMap.put("type", typeStr);
            respList.add(respMap);
        }

        return respList;
    }

    public Object saveQuestionResult(String questionId, String userId, String channelId, String userName,String userPhone, List reqList){
        ArrayList<HashMap> resultList = new ArrayList<>();
        HashMap<String, Object> reqFinalMap = new HashMap<>();  // 组请求map包

        Object[] resultArray = reqList.toArray();

        HashMap<String, String> req = new HashMap<>();  // 组请求map包
        req.put("questionId", questionId);
        List<Map> itemIdList = sqlService.selectList("questionSurveyItem", "selectItemIdByQuestionId", req);	 //题目ID列表

        Iterator itemIdIterator = itemIdList.iterator();

        Integer i = 0;
        while (itemIdIterator.hasNext()){
            HashMap<String, Object> reqMap = new HashMap<>();  // 组请求map包
            Map nextItemId = (Map) itemIdIterator.next();
            String itemId = (String) nextItemId.get("itemId");
            String itemResullt = (String) resultArray[i];
            reqMap.put("questionId", questionId);
            reqMap.put("itemId", itemId);
            reqMap.put("itemResullt", itemResullt);
            reqMap.put("userId", userId);
            reqMap.put("userName", userName);
            reqMap.put("userPhone", userPhone);
            reqMap.put("channelId", channelId);
            resultList.add(reqMap);
            i++;
        }
        reqFinalMap.put("resultList", resultList);
        int index = sqlService.update("questionSurveyResult", "insert", reqFinalMap);
        return index;
    }
}
