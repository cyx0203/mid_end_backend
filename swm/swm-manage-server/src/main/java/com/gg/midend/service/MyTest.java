package com.gg.midend.service;

import com.gg.core.service.SqlService;
import com.gg.midend.bean.TestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyTest {

    @Autowired
    private SqlService sqlService;

//    @Autowired
//    private TestBean testBean;
    public TestBean my_getId(){
        TestBean testBean = new TestBean();
        System.out.println("hosId:"+testBean.getHospitalId());
        return testBean;
    };
}
