package com.gg.midend.service;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.midend.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.gg.midend.utils.MyUtils.getOptionMapForMultilple;

/**
 * @author txy
 * @create 2023-01-09-9:05
 */

@Service
public class QuestionServeice {

    @Autowired
    private SqlService sqlService;

    public List getQuestionItems(Map<Object, Object> reqMap) {
        List respList = new ArrayList<Map>();                       //定义返回List
        List<Map> itemList = sqlService.selectList("questionSurveyItem", "selectAll", reqMap);     //问卷列表
        Iterator<Map> iterator = itemList.iterator();

        while (iterator.hasNext()) {
            HashMap<String, Object> respMap = new HashMap<>(); // 处理好的一条Map记录

            Map nextItem = iterator.next();
            String typeStr = (String) nextItem.get("type");
//            String itemStr = (String) nextItem.get("item");

            if (typeStr.equals("1") || typeStr.equals("2")) {
                ArrayList<String> option = new ArrayList<>();

                String itemId = (String) nextItem.get("itemId");
                HashMap<String, Object> req = new HashMap<>();  // 组请求map包
                req.put("itemId", itemId);
                List optionList = sqlService.selectList("questionSurveyOption", "selectByItemId", req);
                Iterator<Map> optionIterator = optionList.iterator();
                while (optionIterator.hasNext()) {
                    Map nextOption = (Map) optionIterator.next();
                    option.add((String) nextOption.get("option"));
                }
                respMap.put("option", option);
            }
            respMap.putAll(nextItem);
            respList.add(respMap);
        }

        return respList;
    }

    public Object saveQuestionItem(String itemId, Map reqMap, Map optionMap) throws ExceptionCenter {
        List optionResult = (List) optionMap.get("optionResult");
        ArrayList<HashMap> resultList = new ArrayList<>();
        HashMap<String, ArrayList> reqFinalMap = new HashMap<>();  // 组请求map包

        if (!(optionResult.equals(null)) && optionResult.size() > 0) {
            Iterator optionIterator = optionResult.iterator();
            while (optionIterator.hasNext()) {
                HashMap<String, String> map = new HashMap<>();  // 组请求map包
                String nextOption = (String) optionIterator.next();
                map.put("itemId", itemId);
                map.put("option", nextOption);
                resultList.add(map);
            }
            reqFinalMap.put("resultList", resultList);
            int optionIndex = sqlService.update("questionSurveyOption", "insert", reqFinalMap);
            if (optionIndex < 1) {
                throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
            }
        }
        int index = sqlService.update("questionSurveyItem", "insert", reqMap);
        if (index < 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        return 1;
    }

    /**
     * 获取问卷答案统计结果
     * @param respList 所有问卷列表
     * @return 问卷统计结果
     */
    public List getQuestionResultForCharts(List respList) {

        List responseList = new ArrayList<>();                   // 存放问卷统计结果
        Iterator iterator = respList.iterator();
        while (iterator.hasNext()) {
            Map responseMap = new HashMap<>();                   // 处理好的一条Map记录
            Map item = (Map) iterator.next();
            List resultList = null;
            Integer countPers = 0;                               // 参评人次，初始值设为0

            String itemId = (String) item.get("itemId");         // 题目ID，用于检索问卷答案
            String itemName = (String) item.get("item");         // 题目名称，返给前端
            String typeName = (String) item.get("typeName");     // 题目类型，返给前端
            String questionId = (String) item.get("questionId"); // 问卷id，返给前端

            responseMap.put("item", itemName);
            responseMap.put("type", typeName);
            responseMap.put("questionId", questionId);


            Map queryMap = new HashMap();                       // 构造查询条件
            queryMap.put("itemId", itemId);
//            if (typeName.equals("单项选择") || typeName.equals("多项选择") || typeName.equals("星级评分") ) {

            resultList = (List) sqlService.selectList("questionSurveyResult", "selectAll", queryMap);

            countPers = resultList.size();                 // list长度即为对应题目的参评人数
            responseMap.put("count", countPers);

            Iterator resultIterator = resultList.iterator();
            List newList = new ArrayList<>();              // 存放某道多选题的所有结果

            // 多项选择的结果为“A-B”格式，有连接符，故需单独处理
            if (typeName.equals("多项选择")) {
                while (resultIterator.hasNext()) {
                    Map nextMap = (Map) resultIterator.next();  // 多选题结果集的一条答案
                    String itemResult = (String) nextMap.get("itemResult");
                    if (itemResult.indexOf("-") != -1) {
                        List<String> option = Arrays.asList(itemResult.split("-"));
                        newList.addAll(option);
                    } else {
                        newList.add(itemResult);
                    }
                }
            } else if (typeName.equals("单项选择")) {
                while (resultIterator.hasNext()) {
                    Map nextMap = (Map) resultIterator.next();
                    String itemResult = (String) nextMap.get("itemResult");
                    newList.add(itemResult);
                }
            } else {
                while (resultIterator.hasNext()) {
                    Map nextMap = (Map) resultIterator.next();
                    String itemResult = (String) nextMap.get("itemResult");
                    newList.add(itemResult);
                }
            }

            Map optionMapForMultilple = getOptionMapForMultilple(newList);            // 统计list中，相同元素的个数
            Iterator optionMapIterator = optionMapForMultilple.keySet().iterator();           // 准备遍历统计好的list
            List list = new ArrayList<>();                                                    // 存放真正前端需要格式的统计结果集

            while (optionMapIterator.hasNext()) {
                String optionMapKey = (String) optionMapIterator.next();
                if (typeName.equals("单项选择") || typeName.equals("多项选择")) {
                    Integer countNum = (Integer) optionMapForMultilple.get(optionMapKey);
                    Map respMap = MyUtils.getOptionMapForCharts(countNum, optionMapKey, countPers);
                    list.add(respMap);
                } else if (typeName.equals("星级评分")) {
                    Map optionMap = new HashMap<>();
                    Integer countNum = (Integer) optionMapForMultilple.get(optionMapKey);
                    Double num = (countNum + 0.0) / countPers * 100;
                    optionMap.put("type", optionMapKey + "星");
                    optionMap.put("value", num.intValue());
                    list.add(optionMap);
                } else if (typeName.equals("填空")) {
                    Map optionMap = new HashMap<>();
                    Integer countNum = (Integer) optionMapForMultilple.get(optionMapKey);
                    Double num = (countNum + 0.0) / countPers * 100;
                    optionMap.put("name", optionMapKey);
                    optionMap.put("value", num.intValue());
                    list.add(optionMap);
                }
            }
            responseMap.put("data", list);
            responseList.add(responseMap);
//            }
        }
        return responseList;
    }

    /**
     * 获取满意度统计结果【table统计专用】
     * 【暂时弃用】
     * @param respList 所有问卷列表
     * @return 问卷统计结果
     */
    public List getQuestionResultForTable(List respList) {
        // 定义一个字母集合，用于选择题型结果集和实际选项的映射
        List<String> englishList = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
        Integer flag = 0;

        Iterator iterator = respList.iterator();
        List responseList = new ArrayList<>();                   // 存放问卷统计结果
        while (iterator.hasNext()) {
            if (flag >= respList.size() - 2) break;
            Map responseMap = new HashMap<>();                   // 处理好的一条Map记录
            Map item = (Map) iterator.next();
            List<String> optionList = null;                      // 存放题目选项，只有单选和多选不为null
            List resultList = null;
            Integer countPers = 0;                               // 参评人次，初始值设为0

            String itemId = (String) item.get("itemId");         // 题目ID，用于检索问卷答案
            String itemName = (String) item.get("item");         // 题目名称，返给前端
            String typeName = (String) item.get("typeName");     // 题目类型，返给前端
            String questionId = (String) item.get("questionId"); // 问卷id，返给前端

            responseMap.put("question", itemName);
            responseMap.put("type", typeName);
            responseMap.put("questionId", questionId);

            Map queryMap = new HashMap();                       // 构造查询条件
            queryMap.put("itemId", itemId);
            if (typeName.equals("单项选择")) {

                resultList = (List) sqlService.selectList("questionSurveyResult", "selectAll", queryMap);

                countPers = resultList.size();                 // list长度即为对应题目的参评人数
                responseMap.put("count", countPers);           // 统计频次，返给前端

                Iterator resultIterator = resultList.iterator();
                List newList = new ArrayList<>();              // 存放某道多选题的所有结果

                // 多项选择的结果为“A-B”格式，有连接符，故需单独处理
                if (typeName.equals("多项选择")) {
                    optionList = (List) item.get("option");         // 只有选择题有option字段，需要根据optionList去匹配结果集中的A、B、C.....
                    while (resultIterator.hasNext()) {
                        Map nextMap = (Map) resultIterator.next();  // 多选题结果集的一条答案
                        String itemResult = (String) nextMap.get("itemResult");
                        if (itemResult.indexOf("-") != -1) {
                            List<String> option = Arrays.asList(itemResult.split("-"));
                            newList.addAll(option);
                        } else {
                            newList.add(itemResult);
                        }
                    }
                } else if (typeName.equals("单项选择")) {
                    optionList = (List) item.get("option");         // 只有选择题有option字段，需要根据optionList去匹配结果集中的A、B、C.....
                    while (resultIterator.hasNext()) {
                        Map nextMap = (Map) resultIterator.next();
                        String itemResult = (String) nextMap.get("itemResult");
                        newList.add(itemResult);
                    }
                } else {
                    while (resultIterator.hasNext()) {
                        Map nextMap = (Map) resultIterator.next();
                        String itemResult = (String) nextMap.get("itemResult");
                        newList.add(itemResult);
                    }
                }
                Map optionMapForMultilple = getOptionMapForMultilple(newList);            // 统计list中，相同元素的个数
                Iterator optionMapIterator = optionMapForMultilple.keySet().iterator();           // 准备遍历统计好的list
//            List list = new ArrayList<>();                                                    // 存放真正前端需要格式的统计结果集

                while (optionMapIterator.hasNext()) {
                    String optionMapKey = (String) optionMapIterator.next();
                    if (typeName.equals("单项选择") || typeName.equals("多项选择")) {
                        int length = optionList.size();
                        List<String> realList = englishList.subList(0, length); // [A、B、C]
                        for (int i = 0; i < realList.size(); i++) {
                            if (realList.get(i).equals(optionMapKey)) {
                                Integer countNum = (Integer) optionMapForMultilple.get(optionMapKey);
                                Map respMap = MyUtils.getOptionMapForTable(countNum, optionList, countPers, i);
                                responseMap.putAll(respMap);
                            }
                        }
                    } else if (typeName.equals("星级评分")) {
                        Map optionMap = new HashMap<>();
                        Integer countNum = (Integer) optionMapForMultilple.get(optionMapKey);
                        Double num = (countNum + 0.0) / countPers * 100;
                        optionMap.put("type", optionMapKey + "星");
                        optionMap.put("value", num.intValue());
//                    list.add(optionMap);
                    } else if (typeName.equals("填空")) {
                        Map optionMap = new HashMap<>();
                        Integer countNum = (Integer) optionMapForMultilple.get(optionMapKey);
                        Double num = (countNum + 0.0) / countPers * 100;
                        optionMap.put("name", optionMapKey);
                        optionMap.put("value", num.intValue());
//                    list.add(optionMap);
                    }
                }
//            responseMap.put("data", list);
                responseList.add(responseMap);
            }
            flag++;
        }
        return responseList;
    }


    /**
     * 获取满意度统计结果【只统计单选、多选和评分】
     * @param respList 所有问卷列表
     * @return 满意度统计结果
     */
    public List getQuestionResult(List respList) {
        List responseList = new ArrayList<>();                   // 存放问卷统计结果

        Iterator iterator = respList.iterator();
        while (iterator.hasNext()) {
            Map responseMap = new HashMap<>();                   // 处理好的一条Map记录
            Map item = (Map) iterator.next();
            List resultList = null;
            Integer countPers = 0;                               // 参评人次，初始值设为0

            String itemId = (String) item.get("itemId");         // 题目ID，用于检索问卷答案
            String itemName = (String) item.get("item");         // 题目名称，返给前端
            String typeName = (String) item.get("typeName");     // 题目类型，返给前端
            String questionId = (String) item.get("questionId"); // 问卷id，返给前端

            responseMap.put("question", itemName);
            responseMap.put("type", typeName);
            responseMap.put("questionId", questionId);

            Map queryMap = new HashMap();                       // 构造查询条件
            queryMap.put("itemId", itemId);
            resultList = (List) sqlService.selectList("questionSurveyResult", "selectAll", queryMap);

            if (resultList.size() == 0) continue;
            countPers = resultList.size();                 // list长度即为对应题目的参评人数
            responseMap.put("count", countPers);           // 统计频次，返给前端

            Iterator resultIterator = resultList.iterator();
            List newList = new ArrayList<>();              // 存放某道选择题的所有结果

            while (resultIterator.hasNext()) {
                Map nextMap = (Map) resultIterator.next();              // 结果集的一条答案
                String itemResult = (String) nextMap.get("itemResult");
                if (itemResult.indexOf("-") != -1) {
                    List<String> option = Arrays.asList(itemResult.split("-"));
                    newList.addAll(option);
                } else {
                    newList.add(itemResult);
                }
            }

            Map optionMapForMultilple = getOptionMapForMultilple(newList);            // 统计list中，相同元素的个数
            Iterator optionMapIterator = optionMapForMultilple.keySet().iterator();           // 准备遍历统计好的list
            while (optionMapIterator.hasNext()) {
                String optionMapKey = (String) optionMapIterator.next();


                Integer countNum = (Integer) optionMapForMultilple.get(optionMapKey);
                Map respMap = MyUtils.getOptionMap(countNum, optionMapKey);
                responseMap.putAll(respMap);
            }
            responseList.add(responseMap);
        }
        return responseList;
    }

    /**
     * 获取科室满意度统计结果
     * @param reqMap 请求参数
     * @return 科室满意度统计结果
     */
    public List getQuestionResultForDeptStat(Map reqMap) {

        List responseList = new ArrayList<>();                   // 存放问卷统计结果

        // 1、查询科室满意度统计情况
        List deptResultList = (List) sqlService.selectList("questionSurveyResult", "selectDeptStat", reqMap);  // 前端上送的科室结果集
        Iterator deptResultIterator = deptResultList.iterator();
        List kvList = new ArrayList<String>();
        while (deptResultIterator.hasNext()) {
            Map nextitemResult = (Map) deptResultIterator.next();
            String deptKV = (String) nextitemResult.get("itemResult");
            kvList.add(deptKV);
        }
        Map optionMap = getOptionMapForMultilple(kvList);

        // 2、整理数据
        Set keySet = optionMap.keySet();
        Iterator iterator = keySet.iterator();
        ArrayList<Object> outList = new ArrayList<>();
        Set uniDept = new HashSet();

        while (iterator.hasNext()) {
            HashMap<Object, Object> map1 = new HashMap<>();
            String nextKV = (String) iterator.next();     // 产科-满意
            int index = nextKV.indexOf("-");
            String name = nextKV.substring(0, index);    // 产科
            uniDept.add(name);

            String value = nextKV.substring(name.length() + 1, nextKV.length()); // 满意
            Integer countKV = (Integer) optionMap.get(nextKV);  // 1

            String keyName = "";
            switch (value) {
                case "满意":
                    keyName = "option1";
                    break;
                case "基本满意":
                    keyName = "option2";
                    break;
                case "不满意":
                    keyName = "option3";
                    break;
            }
            map1.put("deptName", name);
            map1.put(keyName, countKV);
            outList.add(map1);
        }
//        System.out.println(uniDept);
//        System.out.println(list2);
        Iterator uniDeptIterator = uniDept.iterator();
        while (uniDeptIterator.hasNext()) {
            HashMap<Object, Object> respMap = new HashMap<>();
            String deptName = (String) uniDeptIterator.next();
            respMap.put("deptName", deptName);
            for (int j = 0; j < outList.size(); j++) {
                Map o = (Map) outList.get(j);
                if (deptName.equals(o.get("deptName"))) {
                    o.remove("deptName");
                    respMap.putAll(o);
                    if (!respMap.containsKey("option1")){
                        respMap.put("option1", 0);
                    }
                    if (!respMap.containsKey("option2")){
                        respMap.put("option2", 0);
                    }
                    if (!respMap.containsKey("option3")){
                        respMap.put("option3", 0);
                    }
                }
            }
            responseList.add(respMap);
        }
        return responseList;
    }
}
