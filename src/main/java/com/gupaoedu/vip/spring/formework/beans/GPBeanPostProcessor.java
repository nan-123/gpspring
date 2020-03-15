package com.gupaoedu.vip.spring.formework.beans;

// 用于做事件监听
public class GPBeanPostProcessor {

    //为在Bean的初始化前提供回调入口
    public Object postProcessBeforeInitialization(Object bean, String beanName){
        return bean;
    }

    //为在Bean的初始化之后提供回调入口
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

}
