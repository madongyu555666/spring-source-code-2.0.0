package com.spring.formework.webmvc.servlet;

import com.spring.formework.annotation.MARequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MAHandlerAdapter {
    public boolean supports(Object handler){ return (handler instanceof MAHandlerMapping);}


    /**
     * 把返回ModelAndView存储了要穿页面上值，和页面模板的名称，******
     * @param request
     * @param response
     * @param handler
     * @return
     */
    public MAModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws InvocationTargetException, IllegalAccessException {
        MAHandlerMapping handlerMapping = (MAHandlerMapping)handler;


        //把方法的形参列表和request的参数列表所在顺序进行一一对应
        Map<String,Integer> paramIndexMapping = new HashMap<String, Integer>();

        //提取方法中加了注解的参数
        //把方法上的注解拿到，得到的是一个二维数组
        //因为一个参数可以有多个注解，而一个方法又有多个参数
        Annotation[] [] pa = handlerMapping.getMethod().getParameterAnnotations();//方法上的参数，//这里只是出来了命名参数
        for (int i = 0; i < pa.length ; i ++) {
            for(Annotation a : pa[i]){
                if(a instanceof MARequestParam){
                    String paramName = ((MARequestParam) a).value();//获取注解上的值
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName, i);//把名和参数保存到paramIndexMapping中
                    }
                }
            }
        }

        //根据用户请求的参数信息，跟method 中的参数信息进行动态匹配
        //resp 传进来的目的只有一个：只是为了将其赋值给方法参数，仅此而已
        //只有当用户传过来的ModelAndView 为空的时候，才会new 一个默认的
        //1、要准备好这个方法的形参列表
        //方法重载：形参的决定因素：参数的个数、参数的类型、参数顺序、方法的名字
        //只处理Request 和Response
        Class<?> [] paramsTypes =handlerMapping.getMethod().getParameterTypes();//得到参数类型的数组
        for (int i = 0; i < paramsTypes.length ; i ++) {
            Class<?> type = paramsTypes[i];//得到参数类型
            if(type == HttpServletRequest.class ||
                    type == HttpServletResponse.class){//因为是固定的HttpServletRequest，HttpServletResponse类型
                paramIndexMapping.put(type.getName(),i);
            }
        }

        //这是paramIndexMapping里已经有方法上的参数的名和位置的关系了(自定义和固定的)

        //2、拿到自定义命名参数所在的位置
        //用户通过URL 传过来的参数列表
        Map<String,String[]> params = request.getParameterMap();

        //3、构造实参列表
        Object [] paramValues = new Object[paramsTypes.length];//根据类型的数量创建数组

        //定义参数
        for (Map.Entry<String, String[]> parm : params.entrySet()) {
            //获取请求的值
            String value = Arrays.toString(parm.getValue()).replaceAll("\\[|\\]","")
                    .replaceAll("\\s",",");

            if(!paramIndexMapping.containsKey(parm.getKey())){continue;}

            int index = paramIndexMapping.get(parm.getKey());//可以通过名字获取在方法上的位置

            //现在已经找到了对应的方法
            //因为页面上传过来的值都是String 类型的，而在方法中定义的类型是千变万化的
            //要针对我们传过来的参数进行类型转换
            paramValues[index] = caseStringValue(value,paramsTypes[index]);//在把值填上去
        }


        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }


        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }

        //4、从handler 中取出controller、method，然后利用反射机制进行调用
        //根据实例和参数列表去执行方法，得到返回值
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);

        if(result == null || result instanceof Void){ return null; }

        //不为空时,先判断返回值是否是MAModelAndView，这个对象里封装了页面的名字和参数的map
        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == MAModelAndView.class;
        if(isModelAndView){
            return (MAModelAndView) result;
        }
        return null;
    }


    /**
     * 转换常用类型（比较传进来的参数类型和方法上比配到的参数类型是否相等，不相等就转换）
     * @param value
     * @param paramsType
     * @return
     */
    private Object caseStringValue(String value, Class<?> paramsType) {
        if(String.class == paramsType){
            return value;
        }
        //如果是int
        if(Integer.class == paramsType){
            return Integer.valueOf(value);
        }
        else if(Double.class == paramsType){
            return Double.valueOf(value);
        }else {
            if(value != null){
                return value;
            }
            return null;
        }
        //如果还有double或者其他类型，继续加if
        //这时候，我们应该想到策略模式了
        //在这里暂时不实现，希望小伙伴自己来实现

    }
}
