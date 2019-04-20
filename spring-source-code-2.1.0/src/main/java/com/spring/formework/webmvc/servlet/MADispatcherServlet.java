package com.spring.formework.webmvc.servlet;

import com.spring.formework.annotation.MAController;
import com.spring.formework.annotation.MARequestMapping;
import com.spring.formework.context.MAApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 只是作为一个MVC 的启动入口
 */
@Slf4j
public class MADispatcherServlet extends HttpServlet {
    private MAApplicationContext context;
    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";
   private List<MAHandlerMapping> handlerMappings = new ArrayList<MAHandlerMapping>();
    private Map<MAHandlerMapping,MAHandlerAdapter> handlerAdapters = new HashMap<MAHandlerMapping,
                MAHandlerAdapter>();

    private List<MAViewResolver> viewResolvers = new ArrayList<MAViewResolver>();


    /**
     * 初始化
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、初始化ApplicationContext,///相当于把IOC 容器初始化了
        context =  new MAApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2、初始化Spring MVC 九大组件
        initStrategies(context);
    }

    /**
     * 初始化策略（九大组件）
     * @param context
     */
    private void initStrategies(MAApplicationContext context) {
        //有九种策略
// 针对于每个用户请求，都会经过一些处理的策略之后，最终才能有结果输出
// 每种策略可以自定义干预，但是最终的结果都是一致




        //多文件上传的组件
        initMultipartResolver(context);//context);//文件上传解析，如果请求类型是multipart 将通过MultipartResolver 进行文件上传解析


        //初始化本地语言环境
        initLocaleResolver(context);///本地化解析
        //初始化模板处理器
        initThemeResolver(context);////主题解析


        //handlerMapping，必须实现
        initHandlerMappings(context);//MAHandlerMapping 用来保存Controller 中配置的RequestMapping 和Method 的一个对应关系


        //初始化参数适配器，必须实现
        initHandlerAdapters(context);//HandlerAdapters 用来动态匹配Method 参数，包括类转换，动态赋值
        //通过HandlerAdapter 进行多类型的参数动态匹配



        //初始化异常拦截器
        initHandlerExceptionResolvers(context);//如果执行过程中遇到异常，将交给HandlerExceptionResolver 来解析


        //初始化视图预处理器
        initRequestToViewNameTranslator(context);//直接解析请求到视图名

//通过ViewResolvers 实现动态模板的解析
//自己解析一套模板语言
        //初始化视图转换器，必须实现
        initViewResolvers(context);//通过viewResolver 解析逻辑视图到具体视图实现
        //参数缓存器
        initFlashMapManager(context);//flash 映射管理器
    }





    //初始化参数适配器，必须实现
    private void initHandlerAdapters(MAApplicationContext context) {
        //把一个requet请求变成一个handler，参数都是字符串的，自动配到handler中的形参

        //可想而知，他要拿到HandlerMapping才能干活
        //就意味着，有几个HandlerMapping就有几个HandlerAdapter
        //在初始化阶段，我们能做的就是，将这些参数的名字或者类型按一定的顺序保存下来
        //因为后面用反射调用的时候，传的形参是一个数组
        //可以通过记录这些参数的位置index,挨个从数组中填值，这样的话，就和参数的顺序无关了
        for (MAHandlerMapping handlerMapping : this.handlerMappings) {
            //每一个方法有一个参数列表，那么这里保存的是形参列表
            this.handlerAdapters.put(handlerMapping,new MAHandlerAdapter());
        }
    }





    private void initViewResolvers(MAApplicationContext context) {
        //在页面敲一个http://localhost/first.html
        //解决页面名字和模板文件关联的问题
        //拿到模板的存放目录
        String templateRoot=context.getConfig().getProperty("templateRoot");
        //拿到模板的地址
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir=new File(templateRootPath);
        String[] templates=templateRootDir.list();
        for (int i = 0; i < templates.length; i ++) {
            //这里主要是为了兼容多模板，所有模仿Spring用List保存
            //在我写的代码中简化了，其实只有需要一个模板就可以搞定
            //只是为了仿真，所有还是搞了个List
            this.viewResolvers.add(new MAViewResolver(templateRoot));
        }
    }



    //handlerMapping，必须实现,//将Controller 中配置的RequestMapping 和Method 进行一一对应
    private void initHandlerMappings(MAApplicationContext context) {
        //按照我们通常的理解应该是一个Map
        //Map<String,Method> map;
        //map.put(url,Method)
        //首先从容器中取到所有的实例

        //获取类名数组
        String[]  beanNames=context.getBeanDefinitionNames();
       try {
           for (String beanName : beanNames) {
               //到了MVC 层，对外提供的方法只有一个getBean 方法
            //返回的对象不是BeanWrapper，怎么办？
               Object controller=context.getBean(beanName);
               //Object controller = GPAopUtils.getTargetObject(proxy);
               Class<?> clazz =controller.getClass();
               //判断MAContrler返回
               if(!clazz.isAnnotationPresent(MAController.class)){
                   continue;
               }
               //拼接controller上的url
               String  baseUrl="";
               //获取Controller的url配置
               if(clazz.isAnnotationPresent(MARequestMapping.class)){
                   MARequestMapping requestMapping = clazz.getAnnotation(MARequestMapping.class);
                   baseUrl=requestMapping.value();
               }
               //扫描所有的public 方法
               //获取Method的url配置
               Method[] methods = clazz.getMethods();
               for (Method method : methods) {
                   //没有加RequestMapping注解的直接忽略
                   if(!method.isAnnotationPresent(MARequestMapping.class)){ continue; }
                   //解析方法上的url,映射URL
                   MARequestMapping requestMapping = method.getAnnotation(MARequestMapping.class);

                   String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+", "/");
                   Pattern pattern=Pattern.compile(regex);
                   this.handlerMappings.add(new MAHandlerMapping(pattern,controller,method));//保存url和contrller关系
                   log.info("Mapped " + regex + "," + method);
               }
           }

       }catch (Exception e){

       }




    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       this.doPost(req,resp);
    }




    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e){
            //错误输出的信息
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
//            new GPModelAndView("500");
        }
    }




    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //根据用户请求的URL 来获得一个Handler
        MAHandlerMapping handler = getHandler(req);

        if(handler==null){
            //等于空就报个404的模板页面
            processDispatchResult(req,resp,new MAModelAndView("404"));
        }

        //2.准备调用前的参数
        MAHandlerAdapter ha = getHandlerAdapter(handler);

        //3、真正的调用方法,返回ModelAndView存储了要穿页面上值，和页面模板的名称
        MAModelAndView mv = ha.handle(req,resp,handler);

        //输出到页面
        //这一步才是真正的输出
        processDispatchResult(req, resp, mv);

    }

    private MAHandlerAdapter getHandlerAdapter(MAHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){return null;}
        MAHandlerAdapter ha = this.handlerAdapters.get(handler);
        if(ha.supports(handler)){
            return  ha;
        }
        return null;
    }

    /**
     * 返回固定视图模板(往页面)
     * @param req
     * @param resp
     * @param mv
     */
    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, MAModelAndView mv) throws Exception {
        //把给我的ModleAndView变成一个HTML、OuputStream、json、freemark、veolcity
        //ContextType
        if(null == mv){return;}

        //如果ModelAndView不为null，怎么办？
        if(this.viewResolvers.isEmpty()){return;}

        for (MAViewResolver viewResolver : this.viewResolvers) {
            MAView view = viewResolver.resolveViewName(mv.getViewName(),null);
            view.render(mv.getModel(),req,resp);
            return;
        }

    }


    private MAHandlerMapping getHandler(HttpServletRequest req) throws Exception{
        if(this.handlerMappings.isEmpty()){
            return null;
        }
        String url=req.getContextPath();
        String contextPath=req.getContextPath();
        //截取请求的url
        url = url.replace(contextPath,"").replaceAll("/+","/");
        //遍历handlerMappings
        for (MAHandlerMapping handler : this.handlerMappings) {
            Matcher matcher=handler.getPattern().matcher(url);
            //如果没有匹配上继续下一个匹配
            if(!matcher.matches()){continue;}
            return  handler;
        }
        return  null;
    }


    private void initThemeResolver(MAApplicationContext context) {
    }

    private void initLocaleResolver(MAApplicationContext context) {
    }

    private void initMultipartResolver(MAApplicationContext context) {
    }

    private void initRequestToViewNameTranslator(MAApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(MAApplicationContext context) {
    }
    private void initFlashMapManager(MAApplicationContext context) {
    }
}
