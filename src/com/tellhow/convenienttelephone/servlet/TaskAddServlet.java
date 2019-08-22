package com.tellhow.convenienttelephone.servlet;

import com.alibaba.fastjson.JSONObject;
import com.siqiansoft.framework.model.LoginModel;
import com.tellhow.convenienttelephone.service.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TaskAddServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        LoginModel login = (LoginModel)request.getSession(false).getAttribute("LOGINMODEL");
        String ac = request.getParameter("ac");
        System.out.println("获取参数为：" + ac);
        String taskId = request.getParameter("taskId");

        InsertInfoUtil utils = new InsertInfoUtil();
        //验证编号的唯一性
        if(ac.equals("taskid")){
            Boolean flag = utils.getTaskId(taskId);
            JSONObject json = new JSONObject();
            json.put("flag",flag);
            response.getWriter().println(json);
        }else if(ac.equals("shequ")){
        List list = utils.selectSheQu(request,response);
        JSONObject json = new JSONObject();
        json.put("list",list);
        response.getWriter().println(json);
    }else if(ac.equals("sqContent") || ac.equals("ksContent")){
            HashMap map = utils.selectContent(request,response);
            JSONObject json = new JSONObject();
            json.put("map",map);
            response.getWriter().println(json);
        }else if(ac.equals("keshi")){
            List list = utils.selectKeShi(request,response);
            JSONObject json = new JSONObject();
            json.put("list",list);
            response.getWriter().println(json);
        }

        Approval approval = new Approval();
        if (ac.equals("sy")) {
            Operat os = new Operat();
            os.caozuo(request, response);
        }else if (ac.equals("zrqp")) {
            approval.directorQianP(request, response, login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("distribute")){
            approval.distribute(request, response, login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("communityDirector")){
            approval.communityDirector(request, response, login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("officeDirectorAudit")){
            approval.officeDirectorAudit(request, response, login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("secretarySign")){
            approval.secretarySign(request, response, login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("secretaryExamine")){
            approval.secretaryExamine(request, response, login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("reProcess")){
            approval.reProcess(request, response, login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("sectionChief")){
            approval.sectionChief(request, response, login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("deputyDirector")){
            approval.deputyDirector(request, response, login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("bgsch")){//办公室提交重办
            String status = approval.reRun(request, response, login);
            if(status.equals("1")){//可重办
                JSONObject json = new JSONObject();
                json.put("button", "已重办");
                response.getWriter().println(json);
            }else{//不可重办
                JSONObject json = new JSONObject();
                json.put("button", "办公室主任已办理，则不可再进行重办...");
                response.getWriter().println(json);
            }

        }else if(ac.equals("xztj")){//协助科室提交
            approval.xzsbmit(response,request);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }

        OfficeDirector od = new OfficeDirector();
        if (ac.equals("bgszrqp")) {
            od.qianpi(request, response, login);
            // request.getRequestDispatcher("../convenienttelephone/taskmanagement.cmd?$ACTION=c01").forward(request,response);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("handleSub")){
            od.handleSub(request,response,login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("jwhzrsh")){
            od.jwhzrsh(request,response,login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("check")){
            od.check(request,response,login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("wczrsh")){
            od.wczrsh(request,response,login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("rwbj")){
            od.rwbj(request,response,login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("ecbl")){
            od.ecbl(request,response,login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("sqlzsh")){
            od.sqlzsh(request,response,login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("zgfzsh")){
            od.zgfzsh(request,response,login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }



        Reject bohui = new Reject();
        if (ac.equals("bohui")) {
            bohui.bgsbh(request,response,login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("bhbgs")){
            bohui.bhbgs(request,response,login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }else if(ac.equals("bhpaifa")){
            bohui.bhpaifa(request,response,login);
            response.sendRedirect("convenienttelephone/taskmanagement.cmd?$ACTION=c01");
        }




    }
}