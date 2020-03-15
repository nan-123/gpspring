package com.gupaoedu.vip.spring.formework.beans;

import com.gupaoedu.vip.spring.formework.core.GPFactoryBean;

public class GPBeanWrapper extends GPFactoryBean {

    // 还会用到观察者模式 1事件监听
    private GPBeanPostProcessor beanPostProcessor;


    private Object wrapperInstance;
    // 原对象
    private Object originaInstance;
    public GPBeanWrapper(Object instance){
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

    public GPBeanPostProcessor getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public void setBeanPostProcessor(GPBeanPostProcessor beanPostProcessor) {
        this.beanPostProcessor = beanPostProcessor;
    }
}
