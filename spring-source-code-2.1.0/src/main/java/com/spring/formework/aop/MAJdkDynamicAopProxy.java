package com.spring.formework.aop;

import com.spring.formework.aop.intercept.MAMethodInvocation;
import com.spring.formework.aop.support.MAAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;


/**
 * jdk代理
 */
public class MAJdkDynamicAopProxy implements MAAopProxy, InvocationHandler {


    private MAAdvisedSupport advised;
    public MAJdkDynamicAopProxy(MAAdvisedSupport config){
        this.advised = config;
    }




    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
       return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass());
        MAMethodInvocation  invocation=new MAMethodInvocation(proxy,this.advised.getTarget(),method,args,this.advised.getTargetClass(),interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
