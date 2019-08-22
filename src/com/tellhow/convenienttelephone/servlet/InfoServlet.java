package com.tellhow.convenienttelephone.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tellhow.convenienttelephone.service.MoonSummaryService;
import com.tellhow.convenienttelephone.service.WeekSummaryService;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.IOException;


public class InfoServlet extends HttpServlet {
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        String types=req.getParameter("types");
        String operation=req.getParameter("operation");
        String year=req.getParameter("year");
        String week=req.getParameter("week");
        String month=req.getParameter("month");

        JSON array=new JSONArray();
        if(types.equals("moon")){
            MoonSummaryService moonSummaryService=new MoonSummaryService();
            array=moonSummaryService.MoonSummaryServices(operation,year,month);
        }else if(types.equals("week")){
            WeekSummaryService weekSummaryService=new WeekSummaryService();
            array=weekSummaryService.WeekSummaryServices(operation,year,week);
        }
        res.setCharacterEncoding("UTF-8");
        res.getWriter().println(array);
    }

}
