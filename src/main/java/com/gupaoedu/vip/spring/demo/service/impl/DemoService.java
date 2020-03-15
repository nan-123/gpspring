package com.gupaoedu.vip.spring.demo.service.impl;

import com.gupaoedu.vip.spring.annotation.GPService;
import com.gupaoedu.vip.spring.demo.service.IDemoService;

@GPService
public class DemoService implements IDemoService {

    public String get(String name) {
        System.out.println("名字是：" + name);
        return  name;
    }
}
