package com.spring.formework.aop.aspect;

import com.spring.formework.aop.intercept.MAMethodInterceptor;
import com.spring.formework.aop.intercept.MAMethodInvocation;

import java.lang.reflect.Method;

public class MAMethodBeforeAdviceInterceptor extends MAAbstractAspectAdvice implements MAAdvice, MAMethodInterceptor {
    private MAJoinPoint joinPoint;

    public MAMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method,Object[] args,Object target) throws Throwable{
        //传送了给织入参数
        //method.invoke(target);
        super.invokeAdviceMethod(this.joinPoint,null,null);

    }

    @Override
    public Object invoke(MAMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
