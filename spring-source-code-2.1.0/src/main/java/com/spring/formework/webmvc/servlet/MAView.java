package com.spring.formework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析为视图
 */
public class MAView {
    public final String DEFULAT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    public MAView(File viewFile) {
        this.viewFile = viewFile;
    }


    /**
     * ****************不太明白，打断点好好看看
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    public void render(Map<String, ?> model,
                       HttpServletRequest request, HttpServletResponse response) throws Exception{

        StringBuffer sb=new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(this.viewFile,"r");
        String line  = null;
        while (null != (line = ra.readLine())){
            //转换字节编码
            line = new String(line.getBytes("ISO-8859-1"),"utf-8");
            //创建正则对象Pattern
            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            //如果找到的话，说明有自定义的数据显示，比如页面中#{}，我们自定义为￥{}
            while (matcher.find()){

                //查找页面的参数的值(key)，并用通过map（model）找到value,赋值之后，输出
                String paramName = matcher.group();
                paramName = paramName.replaceAll("￥\\{|\\}","");
                Object paramValue = model.get(paramName);
                if(null == paramValue){ continue;}
                line=matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                matcher=pattern.matcher(line);
            }
            sb.append(line);
        }

        response.setCharacterEncoding("utf-8");

        response.getWriter().write(sb.toString());
    }



    //处理特殊字符(替换)
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }

}
