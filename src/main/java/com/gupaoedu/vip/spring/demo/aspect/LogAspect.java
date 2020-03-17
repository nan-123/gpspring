package com.gupaoedu.vip.spring.demo.aspect;

public class LogAspect {

    public void before(){
        System.out.println("Incoker Before Method !!!");
        // 自定义
    }

    public void after(){
        System.out.println("Incoker after Method !!!");
        // 自定义
    }
}
