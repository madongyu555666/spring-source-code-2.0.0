package com.spring.formework.aop.aspect;

import java.lang.reflect.Method;

public abstract class MAAbstractAspectAdvice implements MAAdvice{

    private Method aspectMethod;
    private Object aspectTarget;
    public MAAbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    //调用切入方法
    public Object invokeAdviceMethod(MAJoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable{
        Class<?> [] paramTypes = this.aspectMethod.getParameterTypes();
        if(null == paramTypes || paramTypes.length == 0){
            return this.aspectMethod.invoke(aspectTarget);
        }else {
            Object [] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i ++) {
                //********************
                if(paramTypes[i] == MAJoinPoint.class){
                    args[i] = joinPoint;
                }else if(paramTypes[i] == Throwable.class){
                    args[i] = tx;
                }else if(paramTypes[i] == Object.class){
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget,args);//调用方法
        }

    }
}
