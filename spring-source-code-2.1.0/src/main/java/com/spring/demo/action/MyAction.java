package com.spring.demo.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.spring.demo.service.IModifyService;
import com.spring.demo.service.IQueryService;
import com.spring.formework.annotation.MAAutowired;
import com.spring.formework.annotation.MAController;
import com.spring.formework.annotation.MARequestMapping;
import com.spring.formework.annotation.MARequestParam;
import com.spring.formework.webmvc.servlet.MAModelAndView;

/**
 * 公布接口url
 * @author Tom
 *
 */
@MAController
@MARequestMapping("/web")
public class MyAction {

	@MAAutowired
	IQueryService queryService;
	@MAAutowired
	IModifyService modifyService;

	@MARequestMapping("/query.json")
	public MAModelAndView query(HttpServletRequest request, HttpServletResponse response,
								@MARequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}
	
	@MARequestMapping("/add*.json")
	public MAModelAndView add(HttpServletRequest request,HttpServletResponse response,
			   @MARequestParam("name") String name,@MARequestParam("addr") String addr){
		String result = null;
		try {
			result = modifyService.add(name,addr);
			return out(response,result);
		} catch (Exception e) {
//			e.printStackTrace();
			Map<String,Object> model = new HashMap<String,Object>();
			model.put("detail",e.getMessage());
//			System.out.println(Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			return new MAModelAndView("500",model);
		}

	}
	
	@MARequestMapping("/remove.json")
	public MAModelAndView remove(HttpServletRequest request,HttpServletResponse response,
		   @MARequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}
	
	@MARequestMapping("/edit.json")
	public MAModelAndView edit(HttpServletRequest request,HttpServletResponse response,
			@MARequestParam("id") Integer id,
			@MARequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}
	
	
	
	private MAModelAndView out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
