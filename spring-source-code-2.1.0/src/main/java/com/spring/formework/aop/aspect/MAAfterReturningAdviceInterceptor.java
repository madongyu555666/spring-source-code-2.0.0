package com.spring.formework.aop.aspect;

import com.spring.formework.aop.intercept.MAMethodInterceptor;
import com.spring.formework.aop.intercept.MAMethodInvocation;

import java.lang.reflect.Method;

public class MAAfterReturningAdviceInterceptor extends MAAbstractAspectAdvice implements MAAdvice, MAMethodInterceptor {

    private MAJoinPoint joinPoint;

    public MAAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MAMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();//先回调proceed
        this.joinPoint = mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
