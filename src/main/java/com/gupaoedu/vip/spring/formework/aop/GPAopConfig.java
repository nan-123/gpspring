package com.gupaoedu.vip.spring.formework.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class GPAopConfig {
    // 方法-（需要代理的对象，需要走切面的方法封装）
    private Map<Method,GPAspect> points = new HashMap<Method, GPAspect>();

    public void put(Method target, Object aspect, Method[] points){

        this.points.put(target, new GPAspect(aspect, points));
    }

    public GPAspect get(Method method){
        return this.points.get(method);
    }


    public boolean contains(Method method){
        return this.points.containsKey(method);
    }

    // 需要代理的对象，需要走切面的方法
    public class GPAspect{

        private Object aspect;
        private Method[] points;

        public GPAspect(Object aspect, Method[] points) {
            this.aspect = aspect;
            this.points = points;
        }

        public Object getAspect() {
            return aspect;
        }

        public void setAspect(Object aspect) {
            this.aspect = aspect;
        }

        public Method[] getPoints() {
            return points;
        }

        public void setPoints(Method[] points) {
            this.points = points;
        }
    }
}
