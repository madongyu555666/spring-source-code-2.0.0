package com.spring.formework.aop.support;

import com.spring.formework.aop.aspect.MAAfterReturningAdviceInterceptor;
import com.spring.formework.aop.aspect.MAAfterThrowingAdviceInterceptor;
import com.spring.formework.aop.aspect.MAMethodBeforeAdviceInterceptor;
import com.spring.formework.aop.config.MAAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MAAdvisedSupport {

    private Class<?> targetClass;

    private Object target;

    private MAAopConfig config;//配置文件对象

    private Pattern pointCutClassPattern;//切入点的正则比配

    private transient Map<Method, List<Object>> methodCache;////方法对应的连接器链

    public MAAdvisedSupport(MAAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public Object getTarget(){
        return this.target;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        //把符合规则的类，每个类封装为调用链，封装到map中
        parse();//******
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    private void parse() {
        //获取aop的切面
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        //玩正则，解析为需要代理的包名
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));

        try{
            //创建拦截器链的map
            methodCache = new HashMap<Method, List<Object>>();
            Pattern pattern = Pattern.compile(pointCut);

            Class aspectClass =  Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String,Method>();

            for (Method m : aspectClass.getMethods()) {//获取写好的织入类
                aspectMethods.put(m.getName(),m);//保存在map中
            }

            for (Method m : this.targetClass.getMethods()) {
                String methodString = m.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                //可以判断现在得类是否需要代理
                Matcher matcher = pattern.matcher(methodString);

                //包含的话，需要构建代理执行器链
                if(matcher.matches()){
                    //执行器链
                    List<Object> advices = new LinkedList<Object>();
                    //before
                    if(!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        //创建一个Advivce
                        advices.add(new MAMethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()),aspectClass.newInstance()));
                    }

                    //after
                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        //创建一个Advivce
                        advices.add(new MAAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
                    }
                    //afterThrowing

                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        //创建一个Advivce
                        MAAfterThrowingAdviceInterceptor throwingAdvice =new MAAfterThrowingAdviceInterceptor(
                                aspectMethods.get(config.getAspectAfterThrow()),
                                aspectClass.newInstance());
                        throwingAdvice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }
                    methodCache.put(m,advices);
                }

            }

        }catch (Exception e){

        }

    }

    /**
     * 拦截器和动力接收装置
     * @param method
     * @param targetClass
     * @return
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws NoSuchMethodException {
        List<Object> cached = methodCache.get(method);
        if(cached == null){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());

            cached = methodCache.get(m);

            //底层逻辑，对代理方法进行一个兼容处理
            this.methodCache.put(m,cached);
        }

        return cached;
    }


    /**
     * 判断类是否符合pointCut=public .* com.spring.demo.service..*Service..*(.*)
     * @return
     */
    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
