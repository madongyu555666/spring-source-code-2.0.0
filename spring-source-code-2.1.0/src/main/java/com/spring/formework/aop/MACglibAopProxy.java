package com.spring.formework.aop;


import com.spring.formework.aop.support.MAAdvisedSupport;

public class MACglibAopProxy implements MAAopProxy {

    private MAAdvisedSupport config;
    public MACglibAopProxy(MAAdvisedSupport config){
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
