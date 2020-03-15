package com.gupaoedu.vip.spring.demo.action;

import com.gupaoedu.vip.spring.annotation.GPController;
import com.gupaoedu.vip.spring.demo.service.IDemoService;
import com.gupaoedu.vip.spring.annotation.GPAutowried;
import com.gupaoedu.vip.spring.annotation.GPRequestMapping;

@GPController
public class MyAction {

    @GPAutowried
    private IDemoService demoService;

    @GPRequestMapping("/index.html")
    public void query(){}
}
