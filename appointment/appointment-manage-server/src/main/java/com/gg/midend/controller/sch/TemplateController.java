package com.gg.midend.controller.sch;

import com.gg.core.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateController {

    @Autowired
    private SqlService sqlService;
    
}
