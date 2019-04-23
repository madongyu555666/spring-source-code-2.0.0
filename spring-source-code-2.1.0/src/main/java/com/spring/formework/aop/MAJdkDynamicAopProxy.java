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
        List<Object> interceptorsAndDynamicMethodMatchers = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass());//得到匹配方法的链接器链Linked
        MAMethodInvocation  invocation=new MAMethodInvocation(proxy,this.advised.getTarget(),method,args,this.advised.getTargetClass(),interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();//充分运用了链式调用的精髓
        /**
         * （1）先调用before，之后再调用after时，
         * （2）在回调proceed，这时就是Throwing类了，
         * （3）在该类中在回调proceed,这时拦截器链完结，
         * （4）判断一下，调用正常的方法，
         * （5）return 返回到Throwing类中，在return 返回到after类中，执行after中的增强逻辑，
         * （6）在return到before中，在层层返回，在渲染视图，显示到页面上
         */
    }
}
