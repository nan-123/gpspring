package com.gupaoedu.vip.demo.mvc.service.impl;

import com.gupaoedu.vip.demo.mvc.service.IDemoService;
import com.gupaoedu.vip.spring.annotation.Service;

@Service
public class DemoService implements IDemoService {

    public String get(String name) {
        System.out.println("名字是：" + name);
        return  name;
    }
}
