package com.spring.formework.aop.aspect;


import com.spring.formework.aop.intercept.MAMethodInterceptor;
import com.spring.formework.aop.intercept.MAMethodInvocation;

import java.lang.reflect.Method;

public class MAAfterThrowingAdviceInterceptor extends MAAbstractAspectAdvice implements MAAdvice, MAMethodInterceptor {

    private String throwingName;

    public MAAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MAMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();//先回调proceed
        }catch (Throwable e){
            invokeAdviceMethod(mi,null,e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName){
        this.throwingName = throwName;
    }
}
