package com.tellhow.convenienttelephone.service;

import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 描述:驳回操作
 *
 * @Author zhangrui
 * @Date 2019/3/14 16:14
 */
public class Reject {
    InsertInfoUtil util = new InsertInfoUtil();
    DatabaseBo dbo = new DatabaseBo();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public void bgsbh(HttpServletRequest req, HttpServletResponse resp, LoginModel login){
        String zid = req.getParameter("id");
        System.out.println("当前主表id:"+zid);
        String nodeStatus = req.getParameter("nodestatus");//办理节点
        System.out.println("办理节点:"+nodeStatus);
        String idea1 = req.getParameter("officedirector");//办公室主任批示
        System.out.println("办公室主任批示:"+idea1);
        String idea2 = req.getParameter("director");//主任批示
        System.out.println("主任批示:"+idea2);
        String idea3 = req.getParameter("neighborhooddirector");//居委会主任批示
        System.out.println("居委会主任批示:"+idea3);
        String idea4 = req.getParameter("handleperidea");//办理人员提交
        System.out.println("办理人员提交:"+idea4);
        String idea5 = req.getParameter("neighborhooddirectoridea");//居委会审核
        System.out.println("居委会审核:"+idea5);
        String idea6 = req.getParameter("communityleaderidea");//社区里长审核
        System.out.println("社区里长审核:"+idea6);
        String idea7 = req.getParameter("sectionchief");//科长批示
        System.out.println("科长批示:"+idea7);
        String idea8 = req.getParameter("khandleperidea");//科员提交
        System.out.println("科员提交:"+idea8);
        String idea9 = req.getParameter("sectionchiefidea");//科长审核
        System.out.println("科长审核:"+idea9);
        String idea10 = req.getParameter("deputydirectoridea");//主管副职审核
        System.out.println("主管副职审核:"+idea10);
        String idea12 = req.getParameter("officedirectoropt");//办公室主任审核
        System.out.println("办公室主任审核:"+idea12);
        String idea13 = req.getParameter("directoropt");//主任审核
        System.out.println("办公室主任审核:"+idea13);
        String idea14 = req.getParameter("secretaryidea");//书记审核
        System.out.println("书记审核:"+idea14);
        String idea15 = req.getParameter("agdirectoropt");//主任再次审核
        System.out.println("主任再次审核:"+idea15);
        String bl = req.getParameter("bl");//办理社区/科室code
        System.out.println("办理社区/科室:"+bl);

        //定义修改审批状态
        String updateSql = "";
        try {
            String blcode = bl;
            //根据办理社区/科室code查询对应的name
            String nameSql = "select name from EAP_DEPARTMENT where code = '"+bl+"'";
            List<HashMap<String,String>> deptList = dbo.prepareQuery(nameSql,null);
            if(deptList.size()>0){
                bl = deptList.get(0).get("NAME");
            }
            InsertInfoUtil util = new InsertInfoUtil();
            //办公室主管副职签批阶段节点状态 1
            if ("1".equals(nodeStatus)) {//办公室主管副职驳回（签批阶段）
                //修改上一环节和本环节审批状态为被驳回
               /* for (int i = 0; i < 2; i++) {
                    updateSql = "update TELEPHONE_HANDLE set STATUS = '被驳回' where hid = '" + zid + "' and NODESTATUS = '" + i + "' and ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                }
                //修改上一环节的递交人为当前登录人
                String updateSql2 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '0' and ISDELETE = '0' ";
                dbo.prepareUpdate(updateSql2, null);
                //修改本环节审批意见和审批时间
                String updateSql1 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea1+"',CHECKTIME = '' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and ISDELETE = '0'";
                dbo.prepareUpdate(updateSql1, null);*/
                //修改上一环节和本环节审批状态为被驳回,修改上一环节的递交人为当前登录人,修改本环节审批意见和审批时间
                util.reject(nodeStatus,zid,idea1,login,bl);
                String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '0'";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                String name = list.get(0).get("CHECKNAME");
                String code = list.get(0).get("CHECKCODE");
                //添加日志信息
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","0","");
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,1',CONTENT = '"+idea1+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '"+nodeStatus+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
            }else if("2".equals(nodeStatus)){//主任驳回（签批阶段）
            	//将上一节点状态和节点状态审批人的状态修改为被驳回
            	/*for (int i = (Integer.parseInt(nodeStatus)-1); i <= (Integer.parseInt(nodeStatus)); i++) {
                    updateSql = "update TELEPHONE_HANDLE set STATUS = '被驳回' where hid = '" + zid + "' and NODESTATUS = '" + i + "' and ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                }
                //修改上一环节的递交人为当前登录人
            	String updateSql2 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '1' and ISDELETE = '0'";
                dbo.prepareUpdate(updateSql2, null);
                //修改本环节审批意见和审批时间
            	 String updateSql1 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea2+"',CHECKTIME = '' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql1, null);*/
                //修改上一环节和本环节审批状态为被驳回,修改上一环节的递交人为当前登录人,修改本环节审批意见和审批时间
                util.reject(nodeStatus,zid,idea2,login,bl);
                String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '1'";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                String name = list.get(0).get("CHECKNAME");
                String code = list.get(0).get("CHECKCODE");
                //添加日志信息
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","1","");
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,2',CONTENT = '"+idea2+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '"+nodeStatus+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
            }else if("3".equals(nodeStatus)){//居委会主任驳回（办理阶段）
            	//将节点状态为7,8,9,10,3的子表信息删除（科室办理阶段的所有节点）
            	for (int i = 3; i <= 10; i++) {
                    String deleteSql = "update TELEPHONE_HANDLE set ISDELETE = '1' where hid = '"+zid+"' and NODESTATUS = '"+i+"' and ISDELETE = '0'";
                    dbo.prepareUpdate(deleteSql, null);
                }
            	//将本环节数据删除
            	//String deleteSql = "update TELEPHONE_HANDLE set ISDELETE = '1' where hid = '"+zid+"' and NODESTATUS = '3' and ISDELETE = '0'";
                //dbo.prepareUpdate(deleteSql, null);
                //String updateSql2 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea3+"' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and BL = '"+bl+"' and ISDELETE = '0'";
                //dbo.prepareUpdate(updateSql2, null);
                //修改上一环节审批状态为未审核，审批意见和时间为空,递交人为当前登录人
                String updateSql3 = "update TELEPHONE_HANDLE set STATUS = '未审核',CHECKTIME = '',CHECKIDEA = '',ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '2' and ISDELETE = '0'";
                dbo.prepareUpdate(updateSql3, null);
                //String updateSql4 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '2' and ISDELETE = '0'";
                //dbo.prepareUpdate(updateSql4, null);
                String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '2'";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                String name = list.get(0).get("CHECKNAME");
                String code = list.get(0).get("CHECKCODE");
                //添加日志信息
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","2","");
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,3',CONTENT = '"+idea3+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '"+nodeStatus+"' and UNDERTAKECODE = '"+blcode+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
              //将主表的任务阶段改为1
            	String updateSql1 = "update TELEPHONE_BASIC set TASKSTAGE = '1' where id = '"+zid+"'";
            	dbo.prepareUpdate(updateSql1, null);
            }else if("4".equals(nodeStatus)){//社区办理人员驳回
            	//将上一节点状态和本节点状态审批人的状态修改为被驳回
            	/*for (int i = (Integer.parseInt(nodeStatus)-1); i <= (Integer.parseInt(nodeStatus)); i++) {
                    updateSql = "update TELEPHONE_HANDLE set STATUS = '被驳回' where hid = '" + zid + "' and NODESTATUS = '" + i + "'  and BL = '"+bl+"' and ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                }
                //修改本环节审批意见和审批时间
            	 String updateSql1 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea4+"',CHECKTIME = '' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql1, null);
                //修改上一环节的递交人为当前登录人
                 String updateSql2 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '3' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql2, null);*/
                //修改上一环节和本环节审批状态为被驳回,修改上一环节的递交人为当前登录人,修改本环节审批意见和审批时间
                util.reject(nodeStatus,zid,idea4,login,bl);
                String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '3' and BLCODE = '"+blcode+"'";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                String name = list.get(0).get("CHECKNAME");
                String code = list.get(0).get("CHECKCODE");
                 //添加日志信息
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","3",blcode);
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,4',CONTENT = '"+idea4+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '"+nodeStatus+"' and UNDERTAKECODE = '"+blcode+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
            }else if("5".equals(nodeStatus)){//居委会主任驳回办理人员
            	//将上一节点状态和节点状态审批人的状态修改为被驳回
            	/*for (int i = (Integer.parseInt(nodeStatus)-1); i <= (Integer.parseInt(nodeStatus)); i++) {
                    updateSql = "update TELEPHONE_HANDLE set STATUS = '被驳回' where hid = '" + zid + "' and NODESTATUS = '" + i + "' and BL = '"+bl+"' and ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                }
                //修改本环节审批意见和审批时间
            	 String updateSql1 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea5+"',CHECKTIME = '' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql1, null);
                //修改上一环节的递交人为当前登录人
                 String updateSql2 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '4' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql2, null);*/
                //修改上一环节和本环节审批状态为被驳回,修改上一环节的递交人为当前登录人,修改本环节审批意见和审批时间
                util.reject(nodeStatus,zid,idea5,login,bl);
                String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '4' and BLCODE = '"+blcode+"'";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                String name = list.get(0).get("CHECKNAME");
                String code = list.get(0).get("CHECKCODE");
                //添加日志信息
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","4",blcode);
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,5',CONTENT = '"+idea5+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '"+nodeStatus+"' and UNDERTAKECODE = '"+blcode+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
            }else if("6".equals(nodeStatus)){//社区理长驳回居委会主任
            	//将上一节点状态和节点状态审批人的状态修改为被驳回
            	/*for (int i = (Integer.parseInt(nodeStatus)-1); i <= (Integer.parseInt(nodeStatus)); i++) {
                    updateSql = "update TELEPHONE_HANDLE set STATUS = '被驳回' where hid = '" + zid + "' and NODESTATUS = '" + i + "' and BL = '"+bl+"' and ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                }
            	////修改本环节审批意见和审批时间
            	 String updateSql1 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea6+"',CHECKTIME = '' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql1, null);
                //修改上一环节的递交人为当前登录人
                 String updateSql2 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '5' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql2, null);*/
                //修改上一环节和本环节审批状态为被驳回,修改上一环节的递交人为当前登录人,修改本环节审批意见和审批时间
                util.reject(nodeStatus,zid,idea6,login,bl);
                 //添加日志信息
                //util.addLogoInfo(zid, login, idea6, "", "", "驳回,6");
            }else if("7".equals(nodeStatus)){//科长批示驳回
            	//将节点状态为3,4,5,6,7的子表信息删除（社区办理阶段）
            	for (int i = 3; i <= 10; i++) {
                    String deleteSql = "update TELEPHONE_HANDLE set ISDELETE = '1' where hid = '"+zid+"' and NODESTATUS = '"+i+"' and ISDELETE = '0'";
                    dbo.prepareUpdate(deleteSql, null);
                }
            	//修改本环节的审批意见和审批时间
            	 String updateSql2 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea7+"',CHECKTIME = '' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql2, null);
                //修改上一环节审批状态为未审核，审批意见和时间为空,递交人为当前登录人
                 String updateSql3 = "update TELEPHONE_HANDLE set STATUS = '未审核',CHECKTIME = '',CHECKIDEA = '',ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '2' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql3, null);
                 //String updateSql4 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '2' and ISDELETE = '0' ";
                 //dbo.prepareUpdate(updateSql4, null);
                String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '2'";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                String name = list.get(0).get("CHECKNAME");
                String code = list.get(0).get("CHECKCODE");
                //添加日志信息
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","2","");
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,7',CONTENT = '"+idea7+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '"+nodeStatus+"' and UNDERTAKECODE = '"+blcode+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
              //将主表的任务阶段改为1
              String updateSql1 = "update TELEPHONE_BASIC set TASKSTAGE = '1' where id = '"+zid+"'";
              dbo.prepareUpdate(updateSql1, null);
            }else if("8".equals(nodeStatus)){//科室办理人员驳回科长
            	//将上一节点状态和节点状态审批人的状态修改为被驳回
            	/*for (int i = (Integer.parseInt(nodeStatus)-1); i <= (Integer.parseInt(nodeStatus)); i++) {
                    updateSql = "update TELEPHONE_HANDLE set STATUS = '被驳回' where hid = '" + zid + "' and NODESTATUS = '" + i + "' and BL = '"+bl+"' and ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                }
                ////修改本环节审批意见和审批时间
            	 String updateSql1 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea8+"',CHECKTIME = '' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql1, null);
                //修改上一环节的递交人为当前登录人
                 String updateSql2 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '7' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql2, null);*/
                //修改上一环节和本环节审批状态为被驳回,修改上一环节的递交人为当前登录人,修改本环节审批意见和审批时间
                util.reject(nodeStatus,zid,idea8,login,bl);
                String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '7' and BLCODE = '"+blcode+"'";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                String name = list.get(0).get("CHECKNAME");
                String code = list.get(0).get("CHECKCODE");
                 //添加日志信息
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","7",blcode);
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,8',CONTENT = '"+idea8+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '"+nodeStatus+"' and UNDERTAKECODE = '"+blcode+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
            }else if("9".equals(nodeStatus)){//科长审批驳回办理人员
            	//将上一节点状态和节点状态审批人的状态修改为被驳回
            	/*for (int i = (Integer.parseInt(nodeStatus)-1); i <= (Integer.parseInt(nodeStatus)); i++) {
                    updateSql = "update TELEPHONE_HANDLE set STATUS = '被驳回' where hid = '" + zid + "' and NODESTATUS = '" + i + "' and BL = '"+bl+"' and ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                }
                ////修改本环节审批意见和审批时间
            	 String updateSql1 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea9+"',CHECKTIME = '' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql1, null);
                //修改上一环节的递交人为当前登录人
                 String updateSql2 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '8' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql2, null);*/
                //修改上一环节和本环节审批状态为被驳回,修改上一环节的递交人为当前登录人,修改本环节审批意见和审批时间
                util.reject(nodeStatus,zid,idea9,login,bl);
                String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '8' and BLCODE = '"+blcode+"'";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                String name = list.get(0).get("CHECKNAME");
                String code = list.get(0).get("CHECKCODE");
                //添加日志信息
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","8",blcode);
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,9',CONTENT = '"+idea9+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '"+nodeStatus+"' and UNDERTAKECODE = '"+blcode+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
            }else if("10".equals(nodeStatus)){//主管副职驳回科长
            	//将上一节点状态和节点状态审批人的状态修改为被驳回
            	/*for (int i = (Integer.parseInt(nodeStatus)-1); i <= (Integer.parseInt(nodeStatus)); i++) {
                    updateSql = "update TELEPHONE_HANDLE set STATUS = '被驳回' where hid = '" + zid + "' and NODESTATUS = '" + i + "' and BL = '"+bl+"' and ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                }
                ////修改本环节审批意见和审批时间
            	 String updateSql1 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea10+"',CHECKTIME = '' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql1, null);
                //修改上一环节的递交人为当前登录人
                 String updateSql2 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '9' and BL = '"+bl+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql2, null);*/
                //修改上一环节和本环节审批状态为被驳回,修改上一环节的递交人为当前登录人,修改本环节审批意见和审批时间
                util.reject(nodeStatus,zid,idea10,login,bl);
                //添加日志信息
                //util.addLogoInfo(zid, login, idea10, "", "", "驳回,10");
            }else if("12".equals(nodeStatus)){//办公室主管副职驳回给办公室检查阶段
            	//将上一节点状态和节点状态审批人的状态修改为被驳回
            	/*for (int i = (Integer.parseInt(nodeStatus)-1); i <= (Integer.parseInt(nodeStatus)); i++) {
                    updateSql = "update TELEPHONE_HANDLE set STATUS = '被驳回' where hid = '" + zid + "' and NODESTATUS = '" + i + "' and ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                }
                ////修改本环节审批意见和审批时间
            	 String updateSql2 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea12+"',CHECKTIME = '' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql2, null);
                //修改上一环节的递交人为当前登录人
                 String updateSql3 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '11' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql3, null);*/
                //修改上一环节和本环节审批状态为被驳回,修改上一环节的递交人为当前登录人,修改本环节审批意见和审批时间
                util.reject(nodeStatus,zid,idea12,login,bl);
                String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '11'";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                String name = list.get(0).get("CHECKNAME");
                String code = list.get(0).get("CHECKCODE");
                //添加日志信息
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","11","");
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,12',CONTENT = '"+idea12+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '"+nodeStatus+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
              //将主表的任务阶段改为3
            	String updateSql1 = "update TELEPHONE_BASIC set TASKSTAGE = '3' where id = '"+zid+"'";
            	dbo.prepareUpdate(updateSql1, null);
            }else if("14".equals(nodeStatus)){//书记审核驳回给主任审核
            	//将上一节点状态和节点状态审批人的状态修改为被驳回
            	/*for (int i = (Integer.parseInt(nodeStatus)-1); i <= (Integer.parseInt(nodeStatus)); i++) {
                    updateSql = "update TELEPHONE_HANDLE set STATUS = '被驳回' where hid = '" + zid + "' and NODESTATUS = '" + i + "' and ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                }
                ////修改本环节审批意见和审批时间
            	 String updateSql1 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea14+"',CHECKTIME = '' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql1, null);
                //修改上一环节的递交人为当前登录人
                 String updateSql2 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '13' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql2, null);*/
                //修改上一环节和本环节审批状态为被驳回,修改上一环节的递交人为当前登录人,修改本环节审批意见和审批时间
                util.reject(nodeStatus,zid,idea14,login,bl);
                String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '13'";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                String name = list.get(0).get("CHECKNAME");
                String code = list.get(0).get("CHECKCODE");
                //添加日志信息
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","13","");
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,14',CONTENT = '"+idea14+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '"+nodeStatus+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
            }/*else if("15".equals(nodeStatus)){//
            	//将上一节点状态和节点状态审批人的状态修改为被驳回
            	for (int i = (Integer.parseInt(nodeStatus)-1); i <= (Integer.parseInt(nodeStatus)); i++) {
                    updateSql = "update TELEPHONE_HANDLE set STATUS = '被驳回' where hid = '" + zid + "' and NODESTATUS = '" + i + "' and ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                }
            	 String updateSql1 = "update TELEPHONE_HANDLE set CHECKIDEA = '"+idea15+"',CHECKTIME = '' where hid = '"+zid+"' and NODESTATUS = '"+nodeStatus+"' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql1, null);
                 String updateSql2 = "update TELEPHONE_HANDLE set ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' where hid = '"+zid+"' and NODESTATUS = '14' and ISDELETE = '0'";
                 dbo.prepareUpdate(updateSql2, null);
                 
                util.addLogoInfo(zid, login, idea15, "", "", "驳回,15");
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
     * 审核阶段主任驳回办公室重新检查
     */
    public void bhbgs(HttpServletRequest req, HttpServletResponse resp, LoginModel login){
    	String zid = req.getParameter("id");
        System.out.println("当前主表id:"+zid);
        String nodeStatus = req.getParameter("nodestatus");//办理节点
        System.out.println("办理节点:"+nodeStatus);
        String directoropt = req.getParameter("directoropt");//主任审批意见
        System.out.println("主任审批意见:"+directoropt);
        String agdirectoropt = req.getParameter("agdirectoropt");//主任再次审批意见
        System.out.println("主任再次审批意见:"+agdirectoropt);
        try {
            String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '11'";
            List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
            String name = list.get(0).get("CHECKNAME");
            String code = list.get(0).get("CHECKCODE");
        	if(nodeStatus.equals("13")){
            	//删除节点状态为12的子表信息
            	String deleteSql = "UPDATE TELEPHONE_HANDLE SET ISDELETE = '1' WHERE HID = '"+zid+"' AND NODESTATUS IN ('12','13') AND ISDELETE = '0'";
            	dbo.prepareUpdate(deleteSql, null);
            	//将节点状态为11的审核状态改为被驳回
            	String updateSql = "UPDATE TELEPHONE_HANDLE SET status = '被驳回' WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
            	dbo.prepareUpdate(updateSql, null);
            	//将主表的任务阶段改为3
            	String updateSql1 = "UPDATE TELEPHONE_BASIC SET TASKSTAGE = '3' WHERE ID = '"+zid+"'";
            	dbo.prepareUpdate(updateSql1, null);
            	//将驳回的办公室检查阶段的递交人修改为当前登录人
            	String updateSql2 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
                dbo.prepareUpdate(updateSql2, null);
            	//向日志表添加信息
            	 util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","11","");
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,13',CONTENT = '"+directoropt+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '13' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
            }
        	if(nodeStatus.equals("15")){
            	//删除节点状态为12的子表信息
            	String deleteSql = "UPDATE TELEPHONE_HANDLE SET ISDELETE = '1' WHERE HID = '"+zid+"' AND NODESTATUS in ('12','13','14','15') AND ISDELETE = '0'";
            	dbo.prepareUpdate(deleteSql, null);
            	//将节点状态为11的审核状态改为被驳回
            	String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '被驳回' WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
            	dbo.prepareUpdate(updateSql, null);
            	//将主表的任务阶段改为3
            	String updateSql1 = "UPDATE TELEPHONE_BASIC SET TASKSTAGE = '3' WHERE ID = '"+zid+"'";
            	dbo.prepareUpdate(updateSql1, null);
                //将驳回的办公室检查阶段的递交人修改为当前登录人
            	String updateSql2 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
                dbo.prepareUpdate(updateSql2, null);
            	//向日志表添加信息
            	util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","11","");
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,15',CONTENT = '"+agdirectoropt+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '15' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /*
     * 驳回到居委会主任或科长重派发  
     */
      public void bhpaifa(HttpServletRequest req, HttpServletResponse resp, LoginModel login){
    	  String zid = req.getParameter("id");
          System.out.println("当前主表id:"+zid);
          String nodeStatus = req.getParameter("nodestatus");//办理节点
          System.out.println("办理节点:"+nodeStatus);
          String directoropt = req.getParameter("directoropt");//主任审批意见
          System.out.println("主任审批意见:"+directoropt);
          String agdirectoropt = req.getParameter("agdirectoropt");//主任再次审批意见
          System.out.println("主任再次审批意见:"+agdirectoropt);
          String bl = req.getParameter("bl");//办理社区/科室
          System.out.println("办理社区/科室:"+bl);
          try {

          	if(nodeStatus.equals("13")){
              	//删除节点状态为12的子表信息
              	String deleteSql = "UPDATE TELEPHONE_HANDLE SET ISDELETE = '1'  WHERE HID = '"+zid+"' AND NODESTATUS IN ('4','5','6','8','9','10','11','12','13') AND ISDELETE = '0'";
              	dbo.prepareUpdate(deleteSql, null);
              	//将节点状态为11的审核状态改为被驳回
              	String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '被驳回' WHERE HID = '"+zid+"' AND NODESTATUS IN ('3','7')  AND ISDELETE = '0'";
              	dbo.prepareUpdate(updateSql, null);
              	//将主表的任务阶段改为3
              	String updateSql1 = "UPDATE TELEPHONE_BASIC SET TASKSTAGE = '2' WHERE ID = '"+zid+"'";
              	dbo.prepareUpdate(updateSql1, null);
                //将驳回到居委主任的递交人修改为当前登录人
              	String updateSql2 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS = '3' AND ISDELETE = '0'";
                dbo.prepareUpdate(updateSql2, null);
                //将驳回到科长的递交人修改为当前登录人
                String updateSql3 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS = '7' AND ISDELETE = '0'";
                dbo.prepareUpdate(updateSql3, null);
                String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus in ('3','7')";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                for(int i=0;i<list.size();i++){
                    String name = list.get(i).get("CHECKNAME");
                    String code = list.get(i).get("CHECKCODE");
                    String blcode = list.get(i).get("BLCODE");
                    String nodestatus = list.get(i).get("NODESTATUS");
                    //向日志表添加信息
                    util.addLogoInfo(zid, login, "正在处理中...", name, code, "处理中",nodestatus,blcode);
                }


                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,13',CONTENT = '"+directoropt+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '13' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
              }
          	if(nodeStatus.equals("15")){
              	//删除节点状态为12的子表信息
              	String deleteSql = "UPDATE TELEPHONE_HANDLE SET ISDELETE = '1' WHERE HID = '"+zid+"' AND NODESTATUS IN ('4','5','6','8','9','10','11','12','13','14','15') AND ISDELETE = '0'";
              	dbo.prepareUpdate(deleteSql, null);
              	//将节点状态为11的审核状态改为被驳回
              	String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '被驳回' WHERE HID = '"+zid+"' AND NODESTATUS IN ('3','7') AND ISDELETE = '0'";
              	dbo.prepareUpdate(updateSql, null);
              	//将主表的任务阶段改为3
              	String updateSql1 = "UPDATE TELEPHONE_BASIC SET TASKSTAGE = '2' WHERE ID = '"+zid+"'";
              	dbo.prepareUpdate(updateSql1, null);
                //将驳回到居委主任的递交人修改为当前登录人
              	String updateSql2 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS = '3' AND ISDELETE = '0'";
                dbo.prepareUpdate(updateSql2, null);
                //将驳回到科长的递交人修改为当前登录人
                String updateSql3 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS = '7' AND ISDELETE = '0'";
                dbo.prepareUpdate(updateSql3, null);
              	//向日志表添加信息
              	//util.addLogoInfo(zid, login, agdirectoropt, "", "", "驳回,15");
                String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus in ('7','3')";
                List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
                for(int i=0;i<list.size();i++){
                    String name = list.get(i).get("CHECKNAME");
                    String code = list.get(i).get("CHECKCODE");
                    String blcode = list.get(i).get("BLCODE");
                    String nodestatus = list.get(i).get("NODESTATUS");
                    //向日志表添加信息
                    util.addLogoInfo(zid, login, "正在处理中...", name, code, "处理中",nodestatus,blcode);
                }


                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '驳回,15',CONTENT = '"+agdirectoropt+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '15' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
              }
  		} catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
      }
}
