package com.spring.formework.aop.support;

import com.spring.formework.aop.config.MAAopConfig;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MAAdvisedSupport {

    private Class<?> targetClass;

    private Object target;

    private MAAopConfig config;//配置文件对象

    private Pattern pointCutClassPattern;//切入点的正则比配

    private transient Map<Method, List<Object>> methodCache;//方法缓存

    public MAAdvisedSupport(MAAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public Object getTarget(){
        return this.target;
    }

    /**
     * 拦截器和动力接收装置
     * @param method
     * @param targetClass
     * @return
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws NoSuchMethodException {

        List<Object> cached =methodCache.get(method);
        if(cached==null){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
        }
        return null;
    }
}
