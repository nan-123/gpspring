package com.gupaoedu.vip.spring.core;

public interface BeanFactory {

    // 从ioc容器中根据名字获取一个bean
    Object getBean(String beanName);
}
