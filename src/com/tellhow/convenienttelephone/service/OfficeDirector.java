package com.tellhow.convenienttelephone.service;

import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 描述:办公室主任审批
 *
 * @Author zhangrui
 * @Date 2019/3/11 21:27
 */
public class OfficeDirector {
    DatabaseBo dbo = new DatabaseBo();
    InsertInfoUtil util = new InsertInfoUtil();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 办公室主任签批
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void qianpi(HttpServletRequest req, HttpServletResponse resp, LoginModel login) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        String zid = req.getParameter("id");//主表的id
        String nodestatus = req.getParameter("nodestatus");//主表的id
        String officedirector = req.getParameter("officedirector");//办公室主任批示
        String handledeptname = req.getParameter("handledeptname");//获取办理社区
        System.out.println("获取办理社区为："+handledeptname);
        try {
            //查询下一环节处理人的code和name并向子表添加数据
            String roleSql = "SELECT CODE,NAME FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = 'convenienttelephone_zhuren')";
            String name = dbo.prepareQuery(roleSql, null).get(0).get("NAME");//下一环节办理人名字
            String code = dbo.prepareQuery(roleSql, null).get(0).get("CODE");//下一环节办理人code
            //查询子表是否存在下一环节办理人
            String hSql = "SELECT ID FROM TELEPHONE_HANDLE WHERE HID = '" + zid + "' AND NODESTATUS = '2' AND ISDELETE = '0'";
            //如果查询到数据,证明是驳回,则只走更新操作
            if (dbo.prepareQuery(hSql, null).size() == 0) {
                util.addTable(zid, name, code, "2", "签批阶段", login,"","");
            } else {
                String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核' WHERE HID = '" + zid + "' AND NODESTATUS IN ('1','2') AND ISDELETE = '0'";
                dbo.prepareUpdate(updateSql, null);
                String updateSql1 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='2' AND ISDELETE = '0'";
                dbo.prepareUpdate(updateSql1, null);
            }
            //添加办理社区和科室
            String[] str = handledeptname.split(",");//将办理科室或者社区挨个放到数组中
            String bl1 = "";//办理科室或者社区
            String blCode = "";//办理科室或者社区code
            //查询办理科室或者社区的code
            for(int i=0;i<str.length;i++){
                String codeSql = "SELECT CODE FROM EAP_DEPARTMENT WHERE NAME = '"+str[i]+"'";
                blCode += dbo.prepareQuery(codeSql, null).get(0).get("CODE")+",";
                bl1 += str[i]+",";
            }
            //System.out.println("查询所有办理单位为:"+bl1);
            // System.out.println("查询所有办理单位code为:"+blCode);
            //更改主表的默认办理科室或者社区的name和code
            String basicSql = "UPDATE TELEPHONE_BASIC SET "
                    + "HANDLEDEPTNAME = '"+bl1.substring(0, bl1.length()-1)+"',HANDLEDEPTCODE = '"+blCode.substring(0, blCode.length()-1)+"' WHERE ID = '"+zid+"'";
            dbo.prepareUpdate(basicSql, null);
            //如果表单获取的处理意见没有填,则赋默认值请尽快处理
            if (officedirector == null || "".equals(officedirector)) {
                officedirector = "请尽快处理";
            }
            //修改本环节办理人的批示和审批时间
            util.updateInfo(officedirector, zid, "1","");
            //修改上一环节办理人的状态为已审核
            util.updatePreInfo(zid, "0","");
            //向日志表添加信息
            if(officedirector == null || "".equals(officedirector)){
                officedirector = "同意";
            }
            util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","2","");
            String updateSql0  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+officedirector+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '1' and OPERATION = '处理中'";
            dbo.prepareUpdate(updateSql0,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理人员提交任务
     *
     * @param req
     * @param resp
     * @param login
     * @throws ServletException
     * @throws IOException
     */
    public void handleSub(HttpServletRequest req, HttpServletResponse resp, LoginModel login) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        String zid = req.getParameter("id");//主表的id
        String handleperidea = req.getParameter("handleperidea");//社区办理人员反馈结果
        String handleattach = req.getParameter("handleattach");//社区办理人员上传的附件
        //System.out.println("社区办理人员反馈结果:" + handleperidea);
        String khandleperidea = req.getParameter("khandleperidea");//科室办理人员反馈结果
        String khandleattach = req.getParameter("khandleattach");//科室办理人员上传的附件
        //System.out.println("社区办理人员反馈结果:" + khandleperidea);
        String nodeStatus = req.getParameter("nodestatus");
        //System.out.println("当前节点:" + nodeStatus);
        String reply = req.getParameter("reply");//社区回复情况
        String satisfaction = req.getParameter("satisfaction");//社区满意程度
        String resolve = req.getParameter("resolve");//社区解决情况
        String kreply = req.getParameter("kreply");//科室回复情况
        String ksatisfaction = req.getParameter("ksatisfaction");//科室满意程度
        String kresolve = req.getParameter("kresolve");//科室解决情况
        //String deptName = req.getParameter("bl");//科室解决情况
        //当前办理人为社区人员
        try {
            //根据当前登录人查询社区name和code
			/*String selectSql = "select name,code from EAP_DEPARTMENT where code = (select deptcode from EAP_ACCOUNT where code = '"+login.getUserCode()+"')";
			String deptName = dbo.prepareQuery(selectSql, null).get(0).get("NAME");
			String deptCode = dbo.prepareQuery(selectSql, null).get(0).get("CODE");*/
            String deptCode = req.getParameter("bl");//办理社区或者科室
            //查询办理科室或者科室的code
            String sql1 = "SELECT NAME FROM EAP_DEPARTMENT WHERE CODE = '"+deptCode+"'";
            String deptName = dbo.prepareQuery(sql1,null).get(0).get("NAME");//办理社区或者科室NAME
            //////////////////////////////////////////////////////////////////////

            //System.out.println("办理社区name为:"+deptName);
            //System.out.println("办理社区code为:"+deptCode);
            if ("4".equals(nodeStatus)) {
                //查询下一环节处理人的code和name并向子表添加数据(其实就是查询上一环节办理人的code和name)
                String preHandleSql = "SELECT CHECKNAME NAME ,CHECKCODE CODE FROM TELEPHONE_HANDLE WHERE HID = '" + zid + "' AND NODESTATUS = '3' AND BL = '"+deptName+"' AND ISDELETE = '0'";
                String name = dbo.prepareQuery(preHandleSql, null).get(0).get("NAME");
                String code = dbo.prepareQuery(preHandleSql, null).get(0).get("CODE");
                String hSql = "SELECT ID FROM TELEPHONE_HANDLE WHERE HID = '" + zid + "' AND NODESTATUS = '5' AND BL = '"+deptName+"' AND ISDELETE = '0'";
                //如果查询到数据,证明是驳回,则只走更新操作
                if (dbo.prepareQuery(hSql, null).size() == 0) {
                    util.addTable(zid, name, code, "5", "办理阶段", login,deptName,deptCode);
                } else {
                    String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核' WHERE HID = '" + zid + "' AND NODESTATUS IN ('4','5') AND BL = '"+deptName+"' AND ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                    String updateSql1 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='5' AND BL = '"+deptName+"' AND ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql1, null);
                }
                //将回复情况，满意程度，解决情况添加主表中
                /*String updateSql = "update TELEPHONE_BASIC set " +
                        "reply = '" + reply + "',satisfaction = '" + satisfaction + "'," +
                        "resolve = '" + resolve + "' where id = " + zid;
                dbo.prepareUpdate(updateSql, null);*/

                //修改本环节办理人的批示和审批时间
                util.updateInfo(handleperidea, zid, "4",deptName);
                //查询是否已存在附件
                String attachSql = "select ATTACH from TELEPHONE_HANDLE WHERE HID = '" + zid + "' AND NODESTATUS = '4' AND BL = '"+deptName+"' AND ISDELETE = '0'";
                List<HashMap<String,String>> attachList = dbo.prepareQuery(attachSql,null);
                String xzAttach = attachList.get(0).get("ATTACH");
                String attach = "";
                if(xzAttach != null && !"".equals(xzAttach)){
                    attach = xzAttach+","+handleattach;
                }else{
                    attach = handleattach;
                }
                //子表添加附件信息
                String sql = "UPDATE TELEPHONE_HANDLE SET ATTACH = '" + attach + "',REPLY = '"+reply+"',SATISFACTION = '"+satisfaction+"',RESOLVE = '"+resolve+"' WHERE HID = '" + zid + "' AND NODESTATUS = '4' AND BL = '"+deptName+"' AND ISDELETE = '0'";
                dbo.prepareUpdate(sql, null);
                //修改上一环节办理人的状态为已审核
                util.updatePreInfo(zid, "3",deptName);
                //向日志表添加信息
                if(handleperidea == null || "".equals(handleperidea)){
                    handleperidea = "同意";
                }
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","5",deptCode);
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+handleperidea+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '4' and UNDERTAKECODE = '"+deptCode+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
            }
            //当前办理人员为科员
            if ("8".equals(nodeStatus)) {
                //查询下一环节处理人的code和name并向子表添加数据(其实就是查询上一环节办理人的code和name)
                String preHandleSql = "SELECT  CHECKNAME NAME ,CHECKCODE CODE FROM TELEPHONE_HANDLE WHERE HID = '" + zid + "' AND NODESTATUS = '7' AND BL = '"+deptName+"' AND ISDELETE = '0'";
                String name = dbo.prepareQuery(preHandleSql, null).get(0).get("NAME");
                String code = dbo.prepareQuery(preHandleSql, null).get(0).get("CODE");
                String hSql = "SELECT ID FROM TELEPHONE_HANDLE WHERE HID = '" + zid + "' AND NODESTATUS = '9' AND BL = '"+deptName+"' AND ISDELETE = '0'";
                //如果查询到数据,证明是驳回,则只走更新操作
                if (dbo.prepareQuery(hSql, null).size() == 0) {
                    util.addTable(zid, name, code, "9", "办理阶段", login,deptName,deptCode);
                } else {
                    String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核' WHERE HID = '" + zid + "' AND NODESTATUS IN ('8','9') AND BL = '"+deptName+"' AND ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql, null);
                    String updateSql1 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='9' AND BL = '"+deptName+"' AND ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql1, null);
                }
                //将回复情况，满意程度，解决情况添加主表中
                /*String updateSql = "update TELEPHONE_BASIC set " +
                        "kreply = '" + kreply + "',ksatisfaction = '" + ksatisfaction + "'," +
                        "kresolve = '" + kresolve + "' where id = " + zid;
                dbo.prepareUpdate(updateSql, null);*/

                //修改本环节办理人的批示和审批时间
                util.updateInfo(khandleperidea, zid, "8",deptName);
                //查询是否已存在附件
                String attachSql = "select ATTACH from TELEPHONE_HANDLE WHERE HID = '" + zid + "' AND NODESTATUS = '8' AND BL = '"+deptName+"' AND ISDELETE = '0'";
                List<HashMap<String,String>> attachList = dbo.prepareQuery(attachSql,null);
                String xzAttach = attachList.get(0).get("ATTACH");
                String attach = "";
                if(xzAttach != null && !"".equals(xzAttach)){
                    attach = xzAttach+","+khandleattach;
                }else{
                    attach = khandleattach;
                }
                //子表添加附件信息
                String sql = "UPDATE TELEPHONE_HANDLE SET ATTACH = '" + attach + "',REPLY = '"+kreply+"',SATISFACTION = '"+ksatisfaction+"',RESOLVE = '"+kresolve+"' WHERE HID = '" + zid + "' AND NODESTATUS = '8' AND BL = '"+deptName+"' AND ISDELETE = '0'";
                dbo.prepareUpdate(sql, null);
                //修改上一环节办理人的状态为已审核
                util.updatePreInfo(zid, "7",deptName);
                //向日志表添加信息
                if(khandleperidea == null || "".equals(khandleperidea)){
                    khandleperidea = "同意";
                }
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","9",deptCode);
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+khandleperidea+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '8' and UNDERTAKECODE = '"+deptCode+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 完成居委会主任审核
     *
     * @param req
     * @param resp
     * @param login
     * @throws ServletException
     * @throws IOException
     */
    public void jwhzrsh(HttpServletRequest req, HttpServletResponse resp, LoginModel login) throws ServletException, IOException {
        String neighborhooddirectoridea = req.getParameter("neighborhooddirectoridea");//居委会主任审核意见
        String zid = req.getParameter("id");//主表的id
        String nodeStatus = req.getParameter("nodestatus");//当前节点状态
        String handledeptcode = req.getParameter("handledeptname");//办理单位
        try {
            System.out.println("当前登录人的code:"+login.getUserCode());
            //根据当前登录人查询社区name和code
			/*String selectSql = "select name,code from EAP_DEPARTMENT where code = (select deptcode from EAP_ACCOUNT where code = '"+login.getUserCode()+"')";
			String deptName = dbo.prepareQuery(selectSql, null).get(0).get("NAME");
			String deptCode = dbo.prepareQuery(selectSql, null).get(0).get("CODE");*/
            String deptCode = req.getParameter("bl");//办理社区code
            String sql1 = "SELECT NAME FROM EAP_DEPARTMENT WHERE CODE = '"+deptCode+"'";
            ////////////////////////////////////////////////////////////////////////////
            String deptName = dbo.prepareQuery(sql1,null).get(0).get("NAME");

            System.out.println("办理社区name为:"+deptName);
            System.out.println("办理社区code为:"+deptCode);
            //查询下一环节处理人的code和name并向子表添加数据
//            String preHandleSql = "SELECT B.LEADERCODE CODE,A.NAME FROM EAP_BAOJU B,EAP_ACCOUNT A WHERE  A.CODE = B.LEADERCODE AND B.DEPTCODE LIKE '%" + handledeptcode + "%'";
            /*String preHandleSql = "SELECT B.LEADERCODE CODE,A.NAME FROM EAP_BAOJU B,EAP_ACCOUNT A WHERE  A.CODE = B.LEADERCODE AND B.DEPTCODE LIKE '%" + deptCode + "%'";
           List<HashMap<String,String>> preList = dbo.prepareQuery(preHandleSql, null);
            String name = preList.get(0).get("NAME");
            String code = preList.get(0).get("CODE");
            String hSql = "SELECT ID FROM TELEPHONE_HANDLE WHERE HID = '" + zid + "' AND NODESTATUS = '6' AND BL = '"+deptName+"' AND BL = '"+deptName+"' AND ISDELETE = '0'";
            //如果查询到数据,证明是驳回,则只走更新操作
            if (dbo.prepareQuery(hSql, null).size() == 0) {
                util.addTable(zid, name, code, "6", "办理阶段", login,deptName,deptCode);
            } else {
                String updateSql = "update TELEPHONE_HANDLE set STATUS = '未审核' where hid = '" + zid + "' and NODESTATUS in ('5','6') and BL = '"+deptName+"' and ISDELETE = '0'";
                dbo.prepareUpdate(updateSql, null);
                String updateSql1 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='6' AND BL = '"+deptName+"' AND ISDELETE = '0'";
                dbo.prepareUpdate(updateSql1, null);
            }*/
            //修改本环节办理人的批示和审批时间
            util.updateInfo(neighborhooddirectoridea, zid, "5",deptName);
            //修改上一环节办理人的状态为已审核
            util.updatePreInfo(zid, "4",deptName);
            //查询下一环办理人code和name
            String roleSql = "SELECT CHECKNAME,CHECKCODE FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '0' AND ISDELETE = '0'";
            List<HashMap<String,String>> roleList = dbo.prepareQuery(roleSql, null);
            String name = roleList.get(0).get("CHECKNAME");
            String code = roleList.get(0).get("CHECKCODE");
            //查询子表是否有办公室检查阶段信息
            String nodeSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
            List<HashMap<String,String>> nodeList = dbo.prepareQuery(nodeSql, null);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(new Date());
            //查询是否派发给科室
            String selectSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS IN ('7','18') AND ISDELETE = '0'";
            List<HashMap<String,String>> selectList = dbo.prepareQuery(selectSql, null);
            //查询办理阶段是否全部办理完
            String sql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS IN ('3','4','5','7','8','9','17','18') AND CHECKTIME IS NULL AND ISDELETE = '0'";
            List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
            System.out.println("进入办公室检查阶段。。。。。。。。。:"+list.size());
            if(nodeList.size()==0){//没有办公室检查阶段信息
                //给子表添加下环节信息
                util.addTable(zid, name, code, "11", "办理阶段",login,"","");
                if(selectList.size()>0){//派发给科室
                    //修改办公室检查阶段审批时间
                    String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKTIME = '"+time+"' WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql1, null);
                }
                if(list.size()<=0){
                    //向日志表添加信息
                    util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","11","");
                }
            }else{//有办公室检查阶段信息

                if(list.size()<=0){//全部办理完
                    System.out.println("进入办公室检查阶段。。。。。。。。。");
                    //修改办公室检查阶段审批时间
                    String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKTIME = '',ATTACH = '', PROBLEMTYPE = '检查阶段' WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
                    dbo.prepareUpdate(updateSql1, null);
                    //将主表阶段类型修改为检查阶段（3）
                    String updateSql = "UPDATE TELEPHONE_BASIC SET TASKSTAGE = '3' WHERE ID = "+zid;
                    dbo.prepareUpdate(updateSql, null);
                    //向日志表添加信息
                    util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","11","");
                }
            }


            if(neighborhooddirectoridea == null || "".equals(neighborhooddirectoridea)){
                neighborhooddirectoridea = "同意";
            }
            String updateSql0  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+neighborhooddirectoridea+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '5' and UNDERTAKECODE = '"+deptCode+"' and OPERATION = '处理中'";
            dbo.prepareUpdate(updateSql0,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 办公室任务办理检查阶段
     *
     * @param req
     * @param resp
     * @param login
     * @throws ServletException
     * @throws IOException
     */
    public void check(HttpServletRequest req, HttpServletResponse resp, LoginModel login) throws ServletException, IOException {
        String zid = req.getParameter("id");//主表的id
        String officeidea = req.getParameter("officeidea");//办公室整理意见
        String officeattach = req.getParameter("officeattach");//办公室上传的附件
        String handledeptcode = req.getParameter("handledeptname");//办理社区
        String handledepartcode = req.getParameter("handledepartname");//办理科室
        String nodestatus = req.getParameter("nodestatus");
        String checked = req.getParameter("checked");//核实情况
        System.out.println("核实情况为：" + checked);
        String reconsider = req.getParameter("reconsider");//是否复议
        System.out.println("是否复议为：" + reconsider);
        String duplicatecell = req.getParameter("duplicatecell");//是否重复来电
        System.out.println("是否重复来电为：" + duplicatecell);
        String remarks = req.getParameter("remarks");//备注
        System.out.println("备注为：" + remarks);
        String breply = req.getParameter("breply");//办公室回复情况
        System.out.println("办公室回复情况为：" + breply);
        String bresolve = req.getParameter("bresolve");//办公室解决情况
        System.out.println("办公室解决情况为：" + bresolve);
        String bsatisfaction = req.getParameter("bsatisfaction");//办公室满意程度
        System.out.println("办公室满意程度为：" + bsatisfaction);

        try {
            //查询下一环节处理人的code和name并向子表添加数据
            //查询办公室主管副职
            String roleSql = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = 'convenienttelephone_leader')";
            List<HashMap<String,String>> roleList = dbo.prepareQuery(roleSql, null);
            String name = roleList.get(0).get("NAME");
            String code = roleList.get(0).get("CODE");
            System.out.println("办公室主管副职name为："+name);
            System.out.println("办公室主管副职code为："+code);
            //查询主任
            String roleSql1 = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = 'convenienttelephone_zhuren')";
            List<HashMap<String,String>> roleList1 = dbo.prepareQuery(roleSql1, null);
            String name1 = roleList1.get(0).get("NAME");
            String code1 = roleList1.get(0).get("CODE");
            System.out.println("查询主任name1为："+name1);
            System.out.println("查询主任code1为："+code1);
            String hSql = "select * from TELEPHONE_HANDLE where hid = '" + zid + "' and NODESTATUS = '12' and ISDELETE = '0'";
            List<HashMap<String,String>> hList = dbo.prepareQuery(hSql, null);
            String hSqlz = "select * from TELEPHONE_HANDLE where hid = '" + zid + "' and NODESTATUS = '13' and ISDELETE = '0'";
            List<HashMap<String,String>> hListz = dbo.prepareQuery(hSqlz, null);
            //如果查询到数据,证明是驳回,则只走更新操作
            if (hList.size() == 0 && hListz.size() == 0) {
                if(bsatisfaction.equals("满意")){//满意则走办公室主管副职，不满意则走主任
                    util.addTable(zid, name, code, "12", "审核阶段", login,"","");
                }else{
                    util.addTable(zid, name1, code1, "13", "审核阶段", login,"","");
                }
            } else {
                if(bsatisfaction.equals("满意")){//满意则走办公室主管副职，不满意则走主任
                    if(hList.size()>0){
                        String updateSql = "update TELEPHONE_HANDLE set STATUS = '未审核' where hid = '" + zid + "' and NODESTATUS in ('11','12') and ISDELETE = '0'";
                        dbo.prepareUpdate(updateSql, null);
                        String updateSql1 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='12' AND ISDELETE = '0'";
                        dbo.prepareUpdate(updateSql1, null);
                    }else{
                        util.addTable(zid, name, code, "12", "审核阶段", login,"","");
                    }

                }else{
                    if(hListz.size()>0){
                        String updateSql = "update TELEPHONE_HANDLE set STATUS = '未审核' where hid = '" + zid + "' and NODESTATUS in ('11','13') and ISDELETE = '0'";
                        dbo.prepareUpdate(updateSql, null);
                        String updateSql1 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='13' AND ISDELETE = '0'";
                        dbo.prepareUpdate(updateSql1, null);
                    }else{
                        util.addTable(zid, name1, code1, "13", "审核阶段", login,"","");
                    }
                }
            }
            //修改本环节办理人的批示和审批时间
            util.updateInfo(officeidea, zid, "11","");
            //子表添加附件信息
            String sql = "update TELEPHONE_HANDLE set ATTACH = '" + officeattach + "',STATUS = '未审核' where hid = '" + zid + "' and NODESTATUS = '11' and ISDELETE = '0'";
            dbo.prepareUpdate(sql, null);
            //修改上一环节办理人的状态为已审核,如果办理社区和办理科室都存在值,则需要修改两条子表的数据
           /* if (!"".equals(handledeptcode) && null != handledeptcode) {
                util.updatePreInfo(zid, "6","");
            }
            if (!"".equals(handledepartcode) && null != handledepartcode) {
                util.updatePreInfo(zid, "10","");
            }*/
            //修改上一环节办理人的状态为已审核
            String updateSql1 = "update TELEPHONE_HANDLE set STATUS = '已审核' where hid = '" + zid + "' and NODESTATUS in ('6','10') and ISDELETE = '0'";
            dbo.prepareUpdate(updateSql1, null);
            //修改主表数据状态为审核阶段
            String updateSql = "update TELEPHONE_BASIC set TASKSTAGE = '4'," +
                    "duplicatecell = '" + duplicatecell + "',REMARKS = '" + remarks + "'," +
                    "breply = '" + breply + "',bresolve = '" + bresolve + "',bsatisfaction = '" + bsatisfaction + "' where id = " + zid;
            dbo.prepareUpdate(updateSql, null);
            if(bsatisfaction.equals("满意")){//满意则走办公室主管副职，不满意则走主任
                //向日志表添加信息
                util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","12","");

            }else{
                //向日志表添加信息
                util.addLogoInfo(zid, login, "正在办理中...", name1, code1, "处理中","13","");
            }
            if(officeidea == null || "".equals(officeidea)){
                officeidea = "同意";
            }
            String updateSql0  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+officeidea+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '11' and OPERATION = '处理中'";
            dbo.prepareUpdate(updateSql0,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 完成主任审核
     *
     * @param req
     * @param resp
     * @param login
     * @throws ServletException
     * @throws IOException
     */
    public void wczrsh(HttpServletRequest req, HttpServletResponse resp, LoginModel login) throws ServletException, IOException {
        String directoropt = req.getParameter("directoropt");//主任审核意见
        String agdirectoropt = req.getParameter("agdirectoropt");//主任再次审核意见

        String zid = req.getParameter("id");//主表的id
        int nodeStatus = Integer.parseInt(req.getParameter("nodestatus"));//当前节点状态
        //获取问题标题
        String title = req.getParameter("title");
        System.out.println("获取问题标题为："+title);
        String attach = req.getParameter("attach");//附件
        System.out.println("获取附件为："+attach);
        String supervisor = req.getParameter("supervisor");//主管领导督办
        System.out.println("获取主管领导督办code 为："+supervisor);
        try {
            String supervisorcode = "";
            if(nodeStatus == 13){
                //根据主管领导code查询对应的name
                String zSql = "select code from EAP_ACCOUNT where name = '"+supervisor+"' ";
                List<HashMap<String,String>> zList  = dbo.prepareQuery(zSql,null);
                supervisorcode = zList.get(0).get("CODE");
                System.out.println("获取主管领导督办name 为："+supervisorcode);
                SubTask st = new SubTask();
                st.insertG(supervisor,"",supervisorcode,"","d",login,title,attach,zid,"","");
                //给日志表中添加主管领导督办日志
                util.addLogoInfo(zid,login,"",supervisor,supervisorcode,"督办","13","");
            }


            //查询下一环节处理人的code和name并向子表添加数据
            String preHandleSql = "SELECT CHECKNAME NAME,CHECKCODE CODE FROM TELEPHONE_HANDLE WHERE HID = '" + zid + "' AND NODESTATUS = '0' and ISDELETE = '0'";
            List<HashMap<String,String>> preList = dbo.prepareQuery(preHandleSql, null);
            String name = preList.get(0).get("NAME");
            String code = preList.get(0).get("CODE");
            String hSql = "select * from TELEPHONE_HANDLE where hid = '" + zid + "' and NODESTATUS = '16' and ISDELETE = '0'";
            //如果查询到数据,证明是驳回,则只走更新操作
            if (dbo.prepareQuery(hSql, null).size() == 0) {
                util.addTable(zid, name, code, "16", "确认阶段", login,"","");
            } else {
                String updateSql = "update TELEPHONE_HANDLE set STATUS = '未审核' where hid = '" + zid + "' and NODESTATUS in ('13','14') and ISDELETE = '0'";
                dbo.prepareUpdate(updateSql, null);
                String updateSql1 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='14' AND ISDELETE = '0'";
                dbo.prepareUpdate(updateSql1, null);
            }

            //修改本环节办理人的批示和审批时间
            String idea = "";
            if (nodeStatus == 13) {
                idea = directoropt;
            } else {
                if (agdirectoropt == null || "".equals(agdirectoropt)) {
                    idea = "请参照书记意见执行";
                } else {
                    idea = agdirectoropt;
                }
            }
            util.updateInfo(idea, zid, nodeStatus + "","");
            //修改上一环节办理人的状态为已审核
            nodeStatus = nodeStatus - 1;
            util.updatePreInfo(zid, nodeStatus + "","");
            //向日志表添加信息
            if(idea == null || "".equals(idea)){
                idea = "同意";
            }
            util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","16","");
            String updateSql0  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+idea+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '13' and OPERATION = '处理中'";
            dbo.prepareUpdate(updateSql0,null);
            //修改主表数据状态为审核阶段
            String updateSql = "UPDATE TELEPHONE_BASIC SET TASKSTAGE = '5',SUPERVISOR = '"+supervisor+"',SUPERVISORCODE = '"+supervisorcode+"' WHERE ID = " + zid;
            dbo.prepareUpdate(updateSql, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 办公室任务办结
     *
     * @param req
     * @param resp
     * @param login
     * @throws ServletException
     * @throws IOException
     */
    public void rwbj(HttpServletRequest req, HttpServletResponse resp, LoginModel login) throws ServletException, IOException {
        String zid = req.getParameter("id");//主表的id
        String officeconfirm = req.getParameter("officeconfirm");//办公室确认
        String existdifficulty = req.getParameter("existdifficulty"); //存在困难
        String timeremind = req.getParameter("timeremind"); //时间提醒
        try {
            //修改本环节办理人的批示和审批时间
            util.updateInfo(officeconfirm, zid, "16","");
            //修改上一环节办理人的状态为已审核
            //查询数据是否审核
            String sql = "SELECT NODESTATUS  FROM TELEPHONE_HANDLE WHERE HID = '" + zid + "'  AND NODESTATUS = '15' and ISDELETE = '0'";
            ArrayList<HashMap<String, String>> list = dbo.prepareQuery(sql, null);
            if (list.size() > 0) {
                util.updatePreInfo(zid, "15","");
            } else {
                util.updatePreInfo(zid, "13","");
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(new Date());
            //修改主表数据状态为确认阶段,并添加确认阶段的值到数据库
            String updateSql = "update TELEPHONE_BASIC set TASKSTAGE = '6'," +
                    "remark = '" + existdifficulty + "',OVERTIME = '" + time + "',TIMEREMIND = '" + timeremind + "' where id = " + zid;
            dbo.prepareUpdate(updateSql, null);
            //向日志表添加信息
            if(officeconfirm == null || "".equals(officeconfirm)){
                officeconfirm = "同意";
            }
            //util.addLogoInfo(zid, login, officeconfirm, null, null, "已办结");
            String updateSql0  = "update TELEPHONE_LOG set OPERATION = '已办结',CONTENT = '"+officeconfirm+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '16' and OPERATION = '处理中'";
            dbo.prepareUpdate(updateSql0,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 二次办理
     *
     * @param req
     * @param resp
     * @param login
     * @throws ServletException
     * @throws IOException
     */
    public void ecbl(HttpServletRequest req, HttpServletResponse resp, LoginModel login) throws ServletException, IOException {
        String zid = req.getParameter("id");//主表的id
        String agdirectoropt = req.getParameter("agdirectoropt");//获得主任再次审核的意见
        System.out.println("意见:"+agdirectoropt);
        String title = req.getParameter("title");//获得问题标题
        System.out.println("标题:"+title);
        String attach = req.getParameter("attach");//获得附件
        System.out.println("附件:"+attach);
        String taskid = req.getParameter("taskid");//获得任务编号
        System.out.println("标题:"+title);
        String nodestatus = req.getParameter("nodestatus");//获得任务编号
        System.out.println("标题:"+nodestatus);
        SubTask st = new SubTask();
        try {
            //删除子表除节点状态为0,1,2的数据
            String delSql = "update TELEPHONE_HANDLE set ISDELETE = '1' WHERE HID = '" + zid + "' AND NODESTATUS NOT IN ('0','1','2') and ISDELETE = '0'";
            dbo.prepareUpdate(delSql, null);
            //修改节点状态为2的状态为未审核
            String statusSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核'  WHERE NODESTATUS = '2' AND HID = '"+zid+"' and ISDELETE = '0'";
            dbo.prepareUpdate(statusSql,null);
            String shequ = req.getParameter("shequ");//获得二次办理的社区名
            System.out.println("获取办理社区科室为："+shequ);
            //定义办理社区/科室code
            String blCode = "";
            if(shequ!=null) {
                String[] str = new String[shequ.length()];
                str = shequ.split(",");
                //定义办理社区/科室name
                String bl1 = "";
                for(int i=0;i<str.length;i++){
                    String codeSql = "select * from EAP_DEPARTMENT where name = '"+str[i]+"'";
                    blCode += dbo.prepareQuery(codeSql, null).get(0).get("CODE")+",";
                    bl1 += str[i]+",";
                }
                System.out.println("查询所有办理单位为:"+bl1);
                System.out.println("查询所有办理单位code为:"+blCode);
                String[] dept = blCode.split(",");
                for(int i=0;i<dept.length;i++){
                    String selectSql = "select name from EAP_DEPARTMENT where code = '"+dept[i]+"'";
                    String deptName = dbo.prepareQuery(selectSql,null).get(0).get("NAME");
                    //定义判断是社区还是科室
                    String bldw = "";
                    if(dept[i].contains("s")){
                        bldw = "shequ";
                    }else{
                        bldw = "keshi";
                    }
                    System.out.println("查询办理单位为："+bldw);
                    if(bldw.equals("shequ")){
                        //查询社区理长
                        String preHandleSql = "SELECT B.LEADERCODE CODE,A.NAME FROM EAP_BAOJU B,EAP_ACCOUNT A WHERE  A.CODE = B.LEADERCODE AND B.DEPTCODE LIKE '%" + dept[i] + "%'";
                        String name1 = dbo.prepareQuery(preHandleSql, null).get(0).get("NAME");
                        String code1 = dbo.prepareQuery(preHandleSql, null).get(0).get("CODE");
                        //添加下一环节办理人数据
                        util.addTable(zid,name1,code1,"17","办理阶段",login,deptName,dept[i]);
                        //添加日志信息
                        util.addLogoInfo(zid,login,"正在处理中...",name1,code1,"处理中","17",dept[i]);
                        //给社区理长发公告
                        st.insertG(name1,"",code1,"","d",login,title,attach,zid,"","");
                    }else if(bldw.equals("keshi")){
                        //查询主管副职
                        String preHandleSql = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = (SELECT h.LEADER FROM eap_hierarchy h WHERE h.deptcode ='"+dept[i]+"'))";
                        String name1 = dbo.prepareQuery(preHandleSql, null).get(0).get("NAME");
                        String code1 = dbo.prepareQuery(preHandleSql, null).get(0).get("CODE");
                        //添加下一环节办理人数据
                        util.addTable(zid,name1,code1,"18","办理阶段",login,deptName,dept[i]);
                        //添加日志信息
                        util.addLogoInfo(zid,login,"正在处理中...",name1,code1,"处理中","18",dept[i]);
                        //查询主管副职
                        st.insertG(name1,"",code1,"","d",login,title,attach,zid,"","");
                    }
                }
                //添加日志信息
                //util.addLogoInfo(zid,login,"正在处理中...","","","处理中");
                if(agdirectoropt == null || "".equals(agdirectoropt)){
                    agdirectoropt = "同意";
                }
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '二次办理,15',CONTENT = '"+agdirectoropt+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '"+nodestatus+"' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
                //获得二次办理的社区code
                /*String shequCodeSql = "SELECT CODE FROM EAP_DEPARTMENT WHERE NAME = '" + shequ + "'";
                shequCode = dbo.prepareQuery(shequCodeSql, null).get(0).get("CODE");*/

            }else{
                shequ="";
            }
            String keshi = req.getParameter("keshi");//获得二次办理的科室名
            String keshiCode ="";
            /*if(keshi!=null) {
                //获得二次办理的科室code
                String keshiCodeSql = "SELECT CODE FROM EAP_DEPARTMENT WHERE NAME = '" + keshi + "'";
                keshiCode = dbo.prepareQuery(keshiCodeSql, null).get(0).get("CODE");
                //查询主管副职
                String preHandleSql = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = (SELECT h.LEADER FROM eap_hierarchy h WHERE h.deptcode ='"+keshiCode+"'))";
                String name1 = dbo.prepareQuery(preHandleSql, null).get(0).get("NAME");
                String code1 = dbo.prepareQuery(preHandleSql, null).get(0).get("CODE");
                //添加下一环节办理人数据
                util.addTable(zid,name1,code1,"18","办理阶段",login,"","");
                //添加日志信息
                util.addLogoInfo(zid,login,agdirectoropt,name1,code1,"驳回,15");
                //查询主管副职
                st.insertG(name1,"",code1,"","d",login,title,attach);
            }else{
                keshi = "";
            }*/

            List<HashMap<String,String>> bmap = st.selectB(shequ);
            String bl = "";
            //给社区和科室对应人员发公告
            for(int i=0;i<bmap.size();i++){
                if(bmap.get(i).get("sname") != null &&
                        !"".equals((bmap.get(i).get("sname")))){
                    bl += (bmap.get(i).get("deptname"))+"<"+(bmap.get(i).get("sname"))+","+(bmap.get(i).get("scode"))+">,";
                }
                if(bmap.get(i).get("kname") != null &&
                        !"".equals((bmap.get(i).get("kname")))){
                    bl += (bmap.get(i).get("deptname"))+"<"+(bmap.get(i).get("kname"))+","+(bmap.get(i).get("kcode"))+">,";
                }
            }

            //修改主表数据
            String updateSql = "UPDATE TELEPHONE_BASIC SET AGAINBANLI = '1',HANDLEDEPTNAME = '"+shequ+"' ,HANDLEDEPTCODE = '"+blCode.substring(0,blCode.length()-1)+"',TRANSACTOR = '"+bl.substring(0, bl.length()-1)+"',HANDLEDEPARTNAME = '"+keshi+"',HANDLEDEPARTCODE = '"+keshiCode+"'," +
                    "TASKSTAGE = '2',REPLY = '',SATISFACTION = '',RESOLVE = ''," +
                    "DUPLICATECELL = '',KSATISFACTION = '',KREPLY = '',KRESOLVE = '',BREPLY = '',BRESOLVE ='',BSATISFACTION = '',REMARKS = '' WHERE ID = '"+zid+"'";
            dbo.prepareUpdate(updateSql,null);
            //给办公室登记人员发公告
            String faQiSql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and NODESTATUS = '0' and ISDELETE = '0'";
            List<HashMap<String,String>> faQiList = dbo.prepareQuery(faQiSql, null);
            String registrantCode = faQiList.get(0).get("CHECKCODE");
            String registrantName = faQiList.get(0).get("CHECKNAME");
            st.insertG(registrantName,"",registrantCode,"","d",login,"单号为"+taskid+"的便民电话任务被二次办理，请悉知",attach,zid,"","");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /*
     * 二次办理社区理长审核
     */
    public void sqlzsh(HttpServletRequest req, HttpServletResponse resp, LoginModel login){
        //获取基础信息id///zid
        String zid = req.getParameter("id");
        System.out.println("获取基础信息id为："+zid);
        //获取基础信息id///zid
        String nodestatus = req.getParameter("nodestatus");
        System.out.println("获取节点状态为："+nodestatus);
        //获取办理社区
        String deptCode = req.getParameter("bl");
        System.out.println("获取办理社区为："+deptCode);
        //获取社区理长审核意见
        String communityleaderopt = req.getParameter("communityleaderopt");
        System.out.println("获取社区理长审核意见为："+communityleaderopt);
        String sql = "select name from EAP_DEPARTMENT where code = '"+deptCode+"'";

        try {
            String bl = dbo.prepareQuery(sql,null).get(0).get("NAME");
            //查询居委会主任
            String roleSql = "select code,name from EAP_ACCOUNT where DEPTCODE = '"+deptCode+"' and RANK = '0'";
            List<HashMap<String, String>> roleList= dbo.prepareQuery(roleSql, null);
            String name = roleList.get(0).get("NAME");
            String code = roleList.get(0).get("CODE");
            util.addTable(zid, name, code, "3", "办理阶段",login,bl,deptCode);
            //修改上一环节办理人的状态为已审核
            util.updatePreInfo(zid, "2","");
            util.updateInfo(communityleaderopt, zid, nodestatus,bl);
            /*
             * 添加日志信息
             */
            if(communityleaderopt == null || "".equals(communityleaderopt)){
                communityleaderopt = "同意";
            }
            util.addLogoInfo(zid, login, "正在办理中...", name, code,"处理中","3",deptCode);
            String updateSql0  = "update TELEPHONE_LOG set OPERATION = '二次办理,15',CONTENT = '"+communityleaderopt+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '17' and UNDERTAKECODE = '"+deptCode+"' and OPERATION = '处理中'";
            dbo.prepareUpdate(updateSql0,null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    /*
     * 二次办理主管副职审核
     */
    public void zgfzsh(HttpServletRequest req, HttpServletResponse resp, LoginModel login){
        //获取基础信息id///zid
        String zid = req.getParameter("id");
        System.out.println("获取基础信息id为："+zid);
        //获取办理科室
        String deptCode = req.getParameter("bl");
        System.out.println("获取办理科室为："+deptCode);
        String nodestatus = req.getParameter("nodestatus");
        System.out.println("获取节点状态为："+nodestatus);
        //获取主管副职审核意见
        String deputydirectoropt = req.getParameter("deputydirectoropt");
        System.out.println("获取主管副职审核意见为："+deputydirectoropt);

        try {
            String sql = "select name from EAP_DEPARTMENT where code = '"+deptCode+"'";
            String bl = dbo.prepareQuery(sql,null).get(0).get("NAME");

            //查询科长
            String roleSql = "select code,name from EAP_ACCOUNT where DEPTCODE = '"+deptCode+"' and RANK = '0'";
            List<HashMap<String, String>> roleList= dbo.prepareQuery(roleSql, null);
            String name = roleList.get(0).get("NAME");
            String code = roleList.get(0).get("CODE");
            util.addTable(zid, name, code, "7", "办理阶段",login,bl,deptCode);
            //修改上一环节办理人的状态为已审核
            util.updatePreInfo(zid, "2","");
            util.updateInfo(deputydirectoropt, zid, nodestatus,bl);
            /*
             * 添加日志信息
             */
            if(deputydirectoropt == null || "".equals(deputydirectoropt)){
                deputydirectoropt = "同意";
            }
            util.addLogoInfo(zid, login, "正在办理中...", name, code,"处理中","7",deptCode);
            String updateSql0  = "update TELEPHONE_LOG set OPERATION = '二次办理,15',CONTENT = '"+deputydirectoropt+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '18' and UNDERTAKECODE = '"+deptCode+"' and OPERATION = '处理中'";
            dbo.prepareUpdate(updateSql0,null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
