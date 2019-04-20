package com.spring.formework.aop.intercept;

import com.spring.formework.aop.aspect.MAJoinPoint;

import java.lang.reflect.Method;
import java.util.List;

public class MAMethodInvocation implements MAJoinPoint {


    private Object proxy;
    private Method method;
    private Object target;
    private Object [] arguments;
    private List<Object> interceptorsAndDynamicMethodMatchers;
    private Class<?> targetClass;

    public MAMethodInvocation(
            Object proxy, Object target, Method method, Object[] arguments,
            Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {

        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Object proceed() {
        return null;
    }
}
