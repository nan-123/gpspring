package com.gupaoedu.vip.spring.formework.context;

public  abstract class GPAbstractApplicationContext {

    // 给子类重新
    protected void onRefresh(){}

    protected abstract void refreshBeanFactory();

}
