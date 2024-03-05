package com.gg.midend.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.core.util.ParamUtil;
import com.gg.midend.beans.CaterExcel;
import com.gg.midend.beans.DeliveryExcel;
import com.gg.midend.beans.TakeExcel;
import com.gg.midend.utils.DateUtil;
import com.gg.upload.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class FileController {
    @Autowired
    private UploadService uploadService;

    @Autowired
    private SqlService sqlService;

    //生成报表访问地址
    @Value("${file.fileUrl}")
    private String fileUrl;

    //图片文件访问地址
    @Value("${file.url}")
    private String url;

    //报表模板存放本地路径
    @Value("${file.templateDir}")
    private String templateDir;

    //生成报表存放本地路径
    @Value("${file.fileDir}")
    private String fileDir;

    //上传菜品图片
    @PostMapping("/uploadDishPic")
    public Object uploadDishPic(@RequestParam("file") MultipartFile file) throws ExceptionCenter {
        return uploadService.saveFile(file);
    }

    //获取菜品图例访问地址
    @PostMapping("/getFileUrl")
    public Object getFileUrl() {
        return ParamUtil.arrayToMap(new HashMap(), "fileUrl", url);
    }

    //生成配菜单
    @RequestMapping({"/downloadCater"})
    public Object validationoppwd(@RequestBody Map paramMap) throws ExceptionCenter {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        String date_start = (String) paramMap.get("startDate");
        String date_end = (String) paramMap.get("endDate");
        String startdate = LocalDate.parse(DateUtil.foramtLocaldate(date_start)).format(fmt);
        String enddate = LocalDate.parse(DateUtil.foramtLocaldate(date_end)).format(fmt);
        String fileName = "配餐单" + "-" + date_start + "-" + date_end + ".xlsx";
        if (Objects.equals(date_start, date_end)) {
            fileName = "配餐单" + "-" + date_start + ".xlsx";
        }

        paramMap.put("startdate", date_start);
        paramMap.put("enddate", date_end);
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        ExcelWriter excelWriter = EasyExcel.write(fileDir + fileName).withTemplate(templateDir + "cater.xlsx").build();
        excelWriter.fill(data(paramMap), writeSheet);
        // 写入list之前的数据
        Map map = new HashMap();
        map.put("canteen", (String) paramMap.get("canteenName"));
        map.put("branch", (String) paramMap.get("branchName"));
        map.put("orderDate", (String) paramMap.get("orderDate"));
        map.put("orderType", (String) paramMap.get("orderType"));
        map.put("printTime", DateUtil.getNowDate("yyyy-MM-dd hh:mm:ss"));
        excelWriter.fill(map, writeSheet);
        excelWriter.finish();
        Map retMap = new HashMap();
        retMap.put("url", fileUrl + fileName);
        return retMap;
    }

    // 创建数据
    private List<CaterExcel> data(Map paramMap) throws ExceptionCenter {
        List<Map> detailList = (List<Map>) sqlService.selectList("mealorderdish", "dish-total", paramMap);
        List<CaterExcel> list = new ArrayList<>();
        for (Map detailMap : detailList) {
            CaterExcel caterExcel = new CaterExcel();
            caterExcel.setName((String) detailMap.get("name"));
            caterExcel.setTotalNum(detailMap.get("totalNum") + "");
            list.add(caterExcel);
        }
        return list;
    }

    //生成取餐单
    @RequestMapping({"/downloadTake"})
    public Object downloadTake(@RequestBody Map paramMap) throws ExceptionCenter {
        System.out.println(paramMap.get("a"));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        String date_start = (String) paramMap.get("startDate");
        String date_end = (String) paramMap.get("endDate");
        String startdate = LocalDate.parse(DateUtil.foramtLocaldate(date_start)).format(fmt);
        String enddate = LocalDate.parse(DateUtil.foramtLocaldate(date_end)).format(fmt);
        String fileName = "领餐单" + "-" + date_start + "-" + date_end + ".xlsx";
        if (Objects.equals(date_start, date_end)) {
            fileName = "领餐单" + "-" + date_start + ".xlsx";
        }

        paramMap.put("startdate", date_start);
        paramMap.put("enddate", date_end);
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        ExcelWriter excelWriter = EasyExcel.write(fileDir + fileName).withTemplate(templateDir + "take.xlsx").build();
        excelWriter.fill(dataTake(paramMap), writeSheet);
        // 写入list之前的数据
        Map map = new HashMap();
        map.put("canteen", (String) paramMap.get("canteenName"));
        map.put("branch", (String) paramMap.get("branchName"));
        map.put("orderDate", (String) paramMap.get("orderDate"));
        map.put("orderType", (String) paramMap.get("orderType"));
        map.put("printTime", DateUtil.getNowDate("yyyy-MM-dd hh:mm:ss"));
        excelWriter.fill(map, writeSheet);
        excelWriter.finish();
        Map retMap = new HashMap();
        retMap.put("url", fileUrl + fileName);
        return retMap;
    }

    // 创建数据
    private List<TakeExcel> dataTake(Map paramMap) throws ExceptionCenter {
        List<Map> detailList = (List<Map>) sqlService.selectList("mealorderdish", "take-total", paramMap);
        List<TakeExcel> list = new ArrayList<>();
        for (Map detailMap : detailList) {
            TakeExcel takeExcel = new TakeExcel();
            takeExcel.setName((String) detailMap.get("name"));
            takeExcel.setTotalNum(detailMap.get("totalNum") + "");
            list.add(takeExcel);
        }
        return list;
    }

    //生成送餐单
    @RequestMapping({"/downloadDelivery"})
    public Object downloadDelivery(@RequestBody Map paramMap) throws ExceptionCenter {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        String date_start = (String) paramMap.get("startDate");
        String date_end = (String) paramMap.get("endDate");
        String startdate = LocalDate.parse(DateUtil.foramtLocaldate(date_start)).format(fmt);
        String enddate = LocalDate.parse(DateUtil.foramtLocaldate(date_end)).format(fmt);
        String fileName = "送餐单" + "-" + date_start + "-" + date_end + ".xlsx";
        if (Objects.equals(date_start, date_end)) {
            fileName = "送餐单" + "-" + date_start + ".xlsx";
        }

        paramMap.put("startdate", date_start);
        paramMap.put("enddate", date_end);
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        ExcelWriter excelWriter = EasyExcel.write(fileDir + fileName).withTemplate(templateDir + "delivery.xlsx").build();
        List list = dataDelivery(paramMap);
        excelWriter.fill(list, writeSheet);
        // 写入list之前的数据
        Map map = new HashMap();
        map.put("num", list.size() + "");
        map.put("orderDate", (String) paramMap.get("orderDate"));
        map.put("orderType", (String) paramMap.get("orderType"));
        map.put("canteen", (String) paramMap.get("canteenName"));
        excelWriter.fill(map, writeSheet);
        excelWriter.finish();
        Map retMap = new HashMap();
        retMap.put("url", fileUrl + fileName);
        return retMap;
    }

    // 创建数据
    private List<DeliveryExcel> dataDelivery(Map paramMap) throws ExceptionCenter {
        List<Map> detailList = (List<Map>) sqlService.selectList("mealorderdish", "order-total", paramMap);
        List<DeliveryExcel> list = new ArrayList<>();
        for (Map detailMap : detailList) {
            DeliveryExcel dekiveryExcel = new DeliveryExcel();
            dekiveryExcel.setName((String) detailMap.get("name"));
            dekiveryExcel.setPhone((String) detailMap.get("phone"));
            dekiveryExcel.setDetail((String) detailMap.get("detail"));
            String[] address = ((String) detailMap.get("address")).split(" ", -1);
            if (address.length == 3) {
                dekiveryExcel.setBranch(address[0] + " " + address[1]);
                dekiveryExcel.setBed(address[2]);
            } else {
                dekiveryExcel.setBranch("");
                dekiveryExcel.setBed("");
            }
            list.add(dekiveryExcel);
        }
        return list;
    }
}
