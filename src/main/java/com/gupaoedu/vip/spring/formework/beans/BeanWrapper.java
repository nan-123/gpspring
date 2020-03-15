package com.gupaoedu.vip.spring.formework.beans;

import com.gupaoedu.vip.spring.formework.core.FactoryBean;

public class BeanWrapper extends FactoryBean {

    // 还会用到观察者模式 1事件监听
    private BeanPostProcessor beanPostProcessor;


    private Object wrapperInstance;
    // 原对象
    private Object originaInstance;
    public BeanWrapper(Object instance){
        this.wrapperInstance = instance;
        this.originaInstance = instance;
    }

   public Object getWrappedInstance(){
        return this.wrapperInstance;
   }

   //返回代理以后的class  $proxy0
   public Class<?> getWrappedClass(){
        return this.wrapperInstance.getClass();
    }

    public BeanPostProcessor getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public void setBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessor = beanPostProcessor;
    }
}
