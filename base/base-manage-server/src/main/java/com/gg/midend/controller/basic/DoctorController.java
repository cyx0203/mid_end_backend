package com.gg.midend.controller.basic;

import com.gg.core.exception.ExceptionCenter;
import com.gg.core.service.SqlService;
import com.gg.midend.utils.CopyFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DoctorController {

    @Autowired
    private SqlService sqlService;

    @Value("${file.uploadDir}")
    private String uploadDir;

    @Autowired
    private CopyFile copyFile;

    @RequestMapping(value = "/manage/comDoctor.insert")
    @Transactional(rollbackFor = {Exception.class})
    public Object insert(@RequestBody Map paramMap) throws ExceptionCenter {
        int ret;
        //插入图片 start
        String fileSeqNo = (String) paramMap.get("fileSeqNo");
        String imgSrc = uploadDir + fileSeqNo;
        String toSrc = uploadDir + "doctor";
        copyFile.copy(imgSrc, toSrc, fileSeqNo);
        //插入图片 end
        //插入 com_doctor start
        paramMap.put("imagePath", "doctor/" + fileSeqNo);
        ret = sqlService.update("comDoctor", "insert", paramMap);
        if (ret < 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        //插入 com_doctor end
        return -1;
    }

    @RequestMapping(value = "/manage/comDoctor.updatePhoto")
    @Transactional(rollbackFor = {Exception.class})
    public Object updatePhoto(@RequestBody Map paramMap) throws ExceptionCenter {
        int ret;
        //插入图片 start
        String fileSeqNo = (String) paramMap.get("fileSeqNo");
        String imgSrc = uploadDir + fileSeqNo;
        String toSrc = uploadDir + "doctor";
        copyFile.copy(imgSrc, toSrc, fileSeqNo);
        //插入图片 end
        //插入 com_doctor start
        paramMap.put("imagePath", "doctor/" + fileSeqNo);
        ret = sqlService.update("comDoctor", "update", paramMap);
        if (ret < 1) {
            throw new ExceptionCenter(HttpStatus.INTERNAL_SERVER_ERROR, "");
        }
        //插入 com_doctor end
        return -1;
    }
}
