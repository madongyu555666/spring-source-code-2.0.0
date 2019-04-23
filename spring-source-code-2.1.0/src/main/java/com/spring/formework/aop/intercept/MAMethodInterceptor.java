package com.spring.formework.aop.intercept;

public interface MAMethodInterceptor {
    //调用
    Object invoke(MAMethodInvocation invocation) throws Throwable;
}
