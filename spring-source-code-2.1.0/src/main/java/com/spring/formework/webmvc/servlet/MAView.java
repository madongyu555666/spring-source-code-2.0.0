package com.spring.formework.webmvc.servlet;

import java.io.File;

/**
 * 解析为视图
 */
public class MAView {
    public final String DEFULAT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    public MAView(File viewFile) {
        this.viewFile = viewFile;
    }



}
