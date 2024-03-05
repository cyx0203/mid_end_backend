package com.gg.midend.utils;

import cn.hutool.core.convert.Convert;
import com.gg.midend.config.GlobalConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ImgPathUtil {

    private static String doctorUrl;

    @Value("${resource.doctorUrl}")
    public void setDoctorUrl(String doctorUrl) {
        ImgPathUtil.doctorUrl = doctorUrl;
    }

    /**
     * 描述：统一处理包含 docImgPath 的 List
     * @param proDocList 待处理 List
     * @param proDocListModified 已处理 List
     */
    public static void addUrlHead(List<?> proDocList, List<Map<String, Object>> proDocListModified) {
        // 给非空的 docImgPath 添加 url 头部
        for (Object element : proDocList) {
            Map<String, Object> temp = Convert.toMap(String.class, Object.class, element);
            String docImgPath = (String) temp.get("docImgPath");
            if ((docImgPath != null) && !docImgPath.equals("")) {
                String totalUrl = doctorUrl + docImgPath;
                temp.put("docImgPath", totalUrl);
            }
            proDocListModified.add(temp);
        }
    }
}
