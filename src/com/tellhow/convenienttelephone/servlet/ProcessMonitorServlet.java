package com.tellhow.convenienttelephone.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import com.tellhow.convenienttelephone.service.JudgeButton;


public class ProcessMonitorServlet extends HttpServlet {
   /*
    * 页面初始化查询流程监控信息和节点状态值   (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        LoginModel login = (LoginModel)request.getSession(false).getAttribute("LOGINMODEL");
        String userCode = login.getUserCode();
        String basicId = request.getParameter("basicId");//获取便民电话处理表id
        System.out.println("获取便民电话基础信息id为："+basicId);
        if(basicId == null || "".equals(basicId)){
        	basicId = "0";
        }
        String ac = request.getParameter("ac");
        System.out.println("获取参数为///////////////////////："+ac);
        JSONObject json = new JSONObject();
        JudgeButton j = new JudgeButton();
        if(ac.equals("xq")){
        	json.put("role", j.getRole(login.getUserCode()));
        	json.put("list", getNote(basicId));
        }else{
        	json.put("button", j.JudgeButton(login.getUserCode(),basicId));
        	json.put("list", getNote(basicId));
        }
        
        response.getWriter().println(json);
	}
	/*
	 * 便民电话任务处理流程监控
	 */
	public List<HashMap<String,String>> getNote(String basicId){
		DatabaseBo dbo = new DatabaseBo();
		String sql = "select * from TELEPHONE_LOG where BASICID = '"+basicId+"' ORDER BY id ASC";
		ArrayList<HashMap<String, String>> list = new ArrayList();
        try {
            list = dbo.prepareQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return list;
	}
}
