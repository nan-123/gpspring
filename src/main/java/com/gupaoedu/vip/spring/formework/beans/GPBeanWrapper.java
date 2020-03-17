package com.gupaoedu.vip.spring.formework.beans;

import com.gupaoedu.vip.spring.formework.aop.GPAopConfig;
import com.gupaoedu.vip.spring.formework.aop.GPAopProxy;
import com.gupaoedu.vip.spring.formework.core.GPFactoryBean;

public class GPBeanWrapper extends GPFactoryBean {

    private GPAopProxy aopProxy = new GPAopProxy();

    // 还会用到观察者模式 1事件监听
    private GPBeanPostProcessor beanPostProcessor;


    private Object wrapperInstance;
    // 原对象
    private Object originaInstance;

    public GPBeanWrapper(Object instance){

        // 从这里开始，把动态代理的代码添加进来
        this.wrapperInstance = aopProxy.getProxy(instance);
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

    public void setAopConfig(GPAopConfig config){
        aopProxy.setConfig(config);
    }

    public Object getOriginaInstance() {
        return originaInstance;
    }

    public void setOriginaInstance(Object originaInstance) {
        this.originaInstance = originaInstance;
    }
}
