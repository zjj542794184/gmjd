package com.tellhow.convenienttelephone.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;


public class Approval {
	DatabaseBo dbo = new DatabaseBo();
	InsertInfoUtil util = new InsertInfoUtil();
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/*
	 * 主任签批阶段
	 */
	public void directorQianP(HttpServletRequest request, HttpServletResponse response,LoginModel login){
		//获取基础信息id///zid
		String zid = request.getParameter("id");
		System.out.println("获取基础信息id为："+zid);
		//获取主管副职意见
		String officedirector = request.getParameter("officedirector");
		System.out.println("获取主管副职意见为："+officedirector);
		//获取主任批示信息
		String director = request.getParameter("director");
		System.out.println("获取主任批示信息为："+director);
		//获取办理社区
		String handledeptname = request.getParameter("handledeptname");
		System.out.println("获取办理社区为："+handledeptname);
		//获取办理科室
		String handledepartname = request.getParameter("handledepartname");
		System.out.println("获取办理科室为："+handledepartname);
		//获取节点状态
		String nodestatus = request.getParameter("nodestatus");
		System.out.println("获取节点状态为："+nodestatus);
		//获取问题标题
		String title = request.getParameter("title");
		System.out.println("获取问题标题为："+title);
		String attach = request.getParameter("attach");//附件
		System.out.println("获取附件为："+attach);
		//根据办理社区查询对应name值
		//String sqDept = "select name from EAP_DEPARTMENT where code = '"+handledeptname+"'";
		//根据办理科室查询对应name值
		//String ksDept = "select name from EAP_DEPARTMENT where code = '"+handledepartname+"'";
		if(handledeptname != null || !"".equals(handledeptname) ){
			try {
				/*
				 * 修改基础信息表的任务阶段（将签批阶段改为办理阶段1-》2）；添加办理社区和办理科室
				 */
//				String sqname = "";
//				String ksname = "";
//				if(handledeptname != null && !"".equals(handledeptname)){
//					sqname = dbo.prepareQuery(sqDept, null).get(0).get("NAME");
//				}
//				if(handledepartname != null && !"".equals(handledepartname)){
//					ksname = dbo.prepareQuery(ksDept, null).get(0).get("NAME");
//				}
				String[] str = handledeptname.split(",");
				//定义办理社区/科室name
				String bl1 = "";
				//定义办理社区/科室code
				String blCode = "";
				//查询所有的办理社区/科室对应的code
				for(int i=0;i<str.length;i++){
					//根据办理社区/科室name查询对应的code
					String codeSql = "SELECT * FROM EAP_DEPARTMENT WHERE NAME = '"+str[i]+"'";
					blCode += dbo.prepareQuery(codeSql, null).get(0).get("CODE")+",";
					bl1 += str[i]+",";
				}
				System.out.println("查询所有办理单位为:"+bl1);
				System.out.println("查询所有办理单位code为:"+blCode);
				/*
				 * 将子表中办公室主任（上一节点）status状态改为已审核；修改本节点状态的审批意见和时间；给子表添加下一环节办理人信息
				 */
				//定义上一环节的节点状态
				String nodestatus1 = "";
				if(nodestatus.equals("7")){
					nodestatus1 = "2";
				}else{
					nodestatus1 = ""+((Integer.parseInt(nodestatus))-1);
				}
				//修改上一环状态为已审核
				util.updatePreInfo(zid,nodestatus1,"");
				//如果主任批示为空则填写默认值
				if(director == null || "".equals(director)){
					director = "请尽快处理";
				}
				//修改本环节办理人的批示和审批时间
				util.updateInfo(director, zid, nodestatus,"");
				/*
				给下环节子表添加数据
				 */
				//定义办理人员code，name
				String bl = "";
				SubTask st = new SubTask();
				//查询办理社区/科室对应的办理人员code和name
				List<HashMap<String,String>> bmap = st.selectB(handledeptname);
				for(int i=0;i<str.length;i++){
					System.out.println("查询办理单位为:"+str[i]);
					//String selectSql = "select code from EAP_DEPARTMENT where name = '"+str[i]+"'";
					//String deptCode = dbo.prepareQuery(selectSql,null).get(0).get("CODE");
					String deptCode = bmap.get(i).get("deptcode");
					//定义下一环节办理人节点状态
					String nodestatus2 = "";
					//定义社区理长/主管副职name
					String namez = "";
					//定义社区理长/主管副职code
					String codez = "";
					if(deptCode.contains("s")){//判断是否为社区
						nodestatus2 = "3";
						//查询社区理长name和code
						String preHandleSql = "SELECT B.LEADERCODE CODE,A.NAME FROM EAP_BAOJU B,EAP_ACCOUNT A WHERE  A.CODE = B.LEADERCODE AND B.DEPTCODE LIKE '%" + deptCode + "%'";
						List<HashMap<String,String>> ncList = dbo.prepareQuery(preHandleSql, null);
						if(ncList.size()>0){
							namez = ncList.get(0).get("NAME");
							codez = ncList.get(0).get("CODE");
						}
					}else{
						nodestatus2 = "7";
						//查询主管副职
						String preHandleSql = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = (SELECT h.LEADER FROM eap_hierarchy h WHERE h.deptcode ='"+deptCode+"'))";
						List<HashMap<String,String>> ncList = dbo.prepareQuery(preHandleSql, null);
						if(ncList.size()>0){
							namez = ncList.get(0).get("NAME");
							codez = ncList.get(0).get("CODE");
						}
					}
					//String deptnameSql = "select code from EAP_DEPARTMENT where name = '"+str[i]+"'";
					//String deptname = dbo.prepareQuery(deptnameSql, null).get(0).get("CODE");
					//System.out.println("查询社区和科室");
					//查询居委会主任/科长
					String name = "";
					String code = "";
					String roleSql = "select code,name from EAP_ACCOUNT where DEPTCODE = '"+deptCode+"' and RANK = '0'";
					List<HashMap<String,String>> roleList =  dbo.prepareQuery(roleSql, null);
					if(roleList.size()>0){
						name = roleList.get(0).get("NAME");
						code = roleList.get(0).get("CODE");
					}
					util.addTable(zid, name, code, nodestatus2, "办理阶段",login,bmap.get(i).get("deptname"),bmap.get(i).get("deptcode"));
					//添加日志信息
					util.addLogoInfo(zid, login, "正在办理中...", name, code,"处理中",nodestatus2,bmap.get(i).get("deptcode"));
					/*
					 * 给社区理长和主管副职发公告
					 */
					/*
					 * By
					 * ylt
					 * start
					 * 2019-06-24
					 *
					 * */

					String titleString = "主管副职意见："+officedirector+",主任意见为："+director+"";


					/* By
					 * ylt
					 * end*/
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = df.format(new Date());
					String sql = "select id from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '19' and CHECKCODE = '"+codez+"'" ;
					List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
					if(list.size()==0){
						System.out.println("namez:"+namez);
						System.out.println("codez:"+codez);
						HashMap<String, String> map3 = new HashMap<String, String>();
						map3.put("HID", zid);
						map3.put("CHECKNAME", namez);
						map3.put("CHECKCODE", codez);
						map3.put("CHECKTIME", time);
						map3.put("PROBLEMTYPE", "办理阶段");
						map3.put("NODESTATUS", "19");
						map3.put("STATUS", "已审核");
						map3.put("ASSIGNERCODE", login.getUserCode());
						map3.put("ASSIGNERNAME", login.getUserName());
						map3.put("BL",bmap.get(i).get("deptname"));
						map3.put("BLCODE", bmap.get(i).get("deptcode"));
						map3.put("ISDELETE", "0");
						dbo.insert(map3, "TELEPHONE_HANDLE");
						//util.addTable(zid, namez, codez, "19", "办理阶段",login,bmap.get(i).get("deptname"),bmap.get(i).get("deptcode"));
					}


					System.out.println("查询居委会主任/科长name和code为："+namez+"###################:"+codez);
					st.insertG(namez,"",codez,"","d",login,titleString,attach,zid,"","");
					if(bmap.get(i).get("sname") != null && !"".equals((bmap.get(i).get("sname")))){
						bl += (bmap.get(i).get("deptname"))+"<"+(bmap.get(i).get("sname"))+","+(bmap.get(i).get("scode"))+">,";
					}
					if(bmap.get(i).get("kname") != null && !"".equals((bmap.get(i).get("kname")))){
						bl += (bmap.get(i).get("deptname"))+"<"+(bmap.get(i).get("kname"))+","+(bmap.get(i).get("kcode"))+">,";
					}
				}
				//添加日志信息
				//util.addLogoInfo(zid, login, "正在办理中...", "", "","处理中","2");
				//修改本环节日志信息
				if(director == null || "".equals(director)){
					director = "同意";
				}
				String updateSql  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+director+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '2' and OPERATION = '处理中'";
				dbo.prepareUpdate(updateSql,null);
				//将主表中的任务阶段修改为2（办理阶段），同时将办理社区/科室name和code存入主表中,和社区对应的人姓名和code
				String basicSql = "UPDATE TELEPHONE_BASIC SET TASKSTAGE = '2',"
						+ "HANDLEDEPTNAME = '"+bl1.substring(0, bl1.length()-1)+"',HANDLEDEPTCODE = '"+blCode.substring(0, blCode.length()-1)+"',TRANSACTOR = '"+bl.substring(0, bl.length()-1)+"' WHERE ID = '"+zid+"'";
				dbo.prepareUpdate(basicSql, null);
//				//有办理社区无办理科室
//				if((handledeptname != null && !"".equals(handledeptname)) && (handledepartname == null || "".equals(handledepartname))){
//					//查询居委会主任
//					String roleSql = "select code,name from EAP_ACCOUNT where DEPTCODE = '"+handledeptname+"' and RANK = '0'";
//				    List<HashMap<String,String>> roleList =  dbo.prepareQuery(roleSql, null);
//				    String name = roleList.get(0).get("NAME");
//				    String code = roleList.get(0).get("CODE");
//				    util.addTable(zid, name, code, "3", "办理阶段",login);
//				    /*
//				     * 添加日志信息
//				     */
//				    util.addLogoInfo(zid, login, director, name, code,"已办");
//				    //给社区理长发公告
//				    String preHandleSql = "SELECT B.LEADERCODE CODE,A.NAME FROM EAP_BAOJU B,EAP_ACCOUNT A WHERE  A.CODE = B.LEADERCODE AND B.DEPTCODE LIKE '%" + handledeptname + "%'";
//		            String name1 = dbo.prepareQuery(preHandleSql, null).get(0).get("NAME");
//		            String code1 = dbo.prepareQuery(preHandleSql, null).get(0).get("CODE");
//		            SubTask st = new SubTask();
//		            HashMap<String,String> map = st.selectB(sqname, ksname);
//		            String sname = map.get("sname");
//		            String scode = map.get("scode");
//		            System.out.println("获取对应办理人为："+sname+"code为："+scode);
//		            //修改主表社区对应的人姓名和code
//		            String updateSql = "update TELEPHONE_BASIC set TRANSACTOR = '"+sname+","+scode+"' where id = '"+zid+"'";
//		            dbo.prepareUpdate(updateSql, null);
//		            st.insertG(name1,"",code1,"","d",login,title,attach);
//				}else if((handledeptname == null || "".equals(handledeptname)) && (handledepartname != null && !"".equals(handledepartname))){//无办理社区有办理科室
//					//查询科长
//					String roleSql = "select code,name from EAP_ACCOUNT where DEPTCODE = '"+handledepartname+"' and RANK = '0'";
//				    List<HashMap<String,String>> roleList =  dbo.prepareQuery(roleSql, null);
//				    String name = roleList.get(0).get("NAME");
//				    String code = roleList.get(0).get("CODE");
//				    util.addTable(zid, name, code, "7", "办理阶段",login);
//					/*
//				     * 添加日志信息
//				     */
//				    util.addLogoInfo(zid, login, director, name, code,"已办");
//				    String preHandleSql = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = (SELECT h.LEADER FROM eap_hierarchy h WHERE h.deptcode ='"+handledepartname+"'))";
//			        String name1 = dbo.prepareQuery(preHandleSql, null).get(0).get("NAME");
//			        String code1 = dbo.prepareQuery(preHandleSql, null).get(0).get("CODE");
//				    SubTask st = new SubTask();
//				    HashMap<String,String> map = st.selectB(sqname, ksname);
//		            String kname = map.get("kname");
//		            String kcode = map.get("kcode");
//		            System.out.println("获取对应办理人为："+kname+"code为："+kcode);
//		            //修改主表社区对应的人姓名和code
//		            String updateSql = "update TELEPHONE_BASIC set HANDLEDEPTCHARGE = '"+kname+","+kcode+"' where id = '"+zid+"'";
//		            dbo.prepareUpdate(updateSql, null);
//				    st.insertG(name1,"",code1,"","d",login,title,attach);
//				}else{
//					//查询居委会主任
//					String roleSql = "select code,name from EAP_ACCOUNT where DEPTCODE = '"+handledeptname+"' and RANK = '0'";
//				    List<HashMap<String,String>> roleList =  dbo.prepareQuery(roleSql, null);
//				    String name1 = roleList.get(0).get("NAME");
//				    String code1 = roleList.get(0).get("CODE");
//				    util.addTable(zid, name1, code1, "3", "办理阶段",login);
//					//查询科长
//				  //查询科长
//					String roleSql1 = "select code,name from EAP_ACCOUNT where DEPTCODE = '"+handledepartname+"' and RANK = '0'";
//				    List<HashMap<String,String>> roleList1 =  dbo.prepareQuery(roleSql1, null);
//				    String name = roleList1.get(0).get("NAME");
//				    String code = roleList1.get(0).get("CODE");
//				    util.addTable(zid, name, code, "7", "办理阶段",login);
//					/*
//				     * 添加社区日志信息
//				     */
//					util.addLogoInfo(zid, login, director, name1, code1,"已办");
//					String preHandleSql = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = (SELECT h.LEADER FROM eap_hierarchy h WHERE h.deptcode ='"+handledepartname+"'))";
//				    String name2 = dbo.prepareQuery(preHandleSql, null).get(0).get("NAME");
//				    String code2 = dbo.prepareQuery(preHandleSql, null).get(0).get("CODE");
//					SubTask st = new SubTask();
//					HashMap<String,String> map = st.selectB(sqname, ksname);
//		            String sname = map.get("sname");
//		            String scode = map.get("scode");
//		            System.out.println("获取社区对应办理人为："+sname+"code为："+scode);
//		            //修改主表社区对应的人姓名和code
//		            String updateSql = "update TELEPHONE_BASIC set TRANSACTOR = '"+sname+","+scode+"' where id = '"+zid+"'";
//		            dbo.prepareUpdate(updateSql, null);
//		            String kname = map.get("kname");
//		            String kcode = map.get("kcode");
//		            System.out.println("获取科室对应办理人为："+kname+"code为："+kcode);
//		            //修改主表社区对应的人姓名和code
//		            String updateSql1 = "update TELEPHONE_BASIC set HANDLEDEPTCHARGE = '"+kname+","+kcode+"' where id = '"+zid+"'";
//		            dbo.prepareUpdate(updateSql1, null);
//					String preHandleSql1 = "SELECT B.LEADERCODE CODE,A.NAME FROM EAP_BAOJU B,EAP_ACCOUNT A WHERE  A.CODE = B.LEADERCODE AND B.DEPTCODE LIKE '%" + handledeptname + "%'";
//		            String name3 = dbo.prepareQuery(preHandleSql1, null).get(0).get("NAME");
//		            String code3 = dbo.prepareQuery(preHandleSql1, null).get(0).get("CODE");
//		            st.insertG(name2,name3,code2,code3,"zr",login,title,attach);
//				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/*
	 * 居委会主任或科长派发任务
	 */
	public void distribute(HttpServletRequest request, HttpServletResponse response,LoginModel login){
		//获取基础信息id///zid
		String zid = request.getParameter("id");
		System.out.println("获取基础信息id为："+zid);
		//获取居委会主任批示
		String neighborhooddirector = request.getParameter("neighborhooddirector");
		System.out.println("获取居委会主任批示为："+neighborhooddirector);
		//获取科长批示
		String sectionchief = request.getParameter("sectionchief");
		System.out.println("获取科长批示为："+sectionchief);
		//获取社区办理人员name
		String handleper = request.getParameter("handleper");
		System.out.println("获取社区办理人员name为："+handleper);
		//获取科室办理人员name
		String khandleper = request.getParameter("khandleper");
		System.out.println("获取科室办理人员name为："+khandleper);
		//获取当前登录人节点状态
		String nodestatus = request.getParameter("nodestatus");
		System.out.println("获取办理人员name为："+nodestatus);
		String deptCode = request.getParameter("bl");//办理社区/科室
		String xdeptname = request.getParameter("xdeptname");//协办科室
		//获取问题标题
		String title = request.getParameter("title");
		System.out.println("获取问题标题为："+title);
		String attach = request.getParameter("attach");//附件
		System.out.println("获取附件为："+attach);
		try {
			//根据协办科室name查询code
			String deptSql1 = "select code from EAP_DEPARTMENT where name = '"+xdeptname+"'";
			List<HashMap<String,String>> deptList1 = dbo.prepareQuery(deptSql1,null);
			String xdeptCode = deptList1.get(0).get("CODE");
			/**
			 * 给协办科室科长发通知公告
			 */
			//查询协办科室科长
			String ksql = "select * from eap_account a where a.code in (select u.usercode from eap_userrole u where u.rolecode in (select h.deptleader from eap_hierarchy h where h.deptcode ='" + xdeptCode + "'))";
			List<HashMap<String,String>> klist = dbo.prepareQuery(ksql,null);
			String xzname = klist.get(0).get("NAME");
			String xzcode = klist.get(0).get("CODE");
			System.out.println("获取协助科室name为："+xzname);
			System.out.println("获取协助科室code为："+xzcode);


			/*
			查询该流程是否走了二次办理
			 */
			String sql= "SELECT * FROM TELEPHONE_BASIC WHERE ID = '"+zid+"'";
			List<HashMap<String,String>> againList = dbo.prepareQuery(sql, null);
			String again = "";
			if(againList.size()>0){
				again = dbo.prepareQuery(sql, null).get(0).get("AGAINBANLI");
			}
			//查询办理社区/科室对应的name
			String sql1 = "SELECT NAME FROM EAP_DEPARTMENT WHERE CODE = '"+deptCode+"'";
			List<HashMap<String,String>> deptList = dbo.prepareQuery(sql1,null);
			String deptName = "";
			if(deptList.size()>0){
				deptName = deptList.get(0).get("NAME");
			}
			System.out.println("办理社区name为:"+deptName);
			System.out.println("办理社区code为:"+deptCode);
			if(nodestatus.equals("3")){//判断为居委会主任派发
				//修改上一环节办理人的状态为已审核
				if(again.equals("0")){//不是二次办理
					util.updatePreInfo(zid, "2","");
				}else{
					util.updatePreInfo(zid, "17",deptName);
				}
				//修改本环节办理人的批示和审批时间
				util.updateInfo(neighborhooddirector, zid, nodestatus,deptName);
				//查询办理人员code
				String codeSql = "SELECT CODE FROM EAP_ACCOUNT WHERE NAME = '"+handleper+"'";
				List<HashMap<String,String>> codeList = dbo.prepareQuery(codeSql, null);
				String code = "";
				if(codeList.size()==0){
					code = handleper;
				}else{
					code = codeList.get(0).get("CODE");
				}
				//查询下一环节信息是否存在
				String hSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '4' AND BL = '"+deptName+"' AND ISDELETE = '0'";
				//util.addTable(zid, handleper, code, "4", "办理阶段",login,deptName,deptCode);
				/*String updateSql = "update TELEPHONE_HANDLE set STATUS = '未审核' where hid = '"+zid+"' and NODESTATUS = '3' and BL = '"+deptName+"'";
				dbo.prepareUpdate(updateSql, null);*/
				List<HashMap<String,String>> exList = dbo.prepareQuery(hSql, null);
				if(exList.size()==0){//下一环节信息不存在
					//向下一环节子表添加信息
					util.addTable(zid, handleper, code, "4", "办理阶段",login,deptName,deptCode);
					//String updateSql = "update TELEPHONE_HANDLE set STATUS = '未审核' where hid = '"+zid+"' and NODESTATUS = '3'and BL = '"+deptName+"' and ISDELETE = '0'";
					//dbo.prepareUpdate(updateSql, null);
				}else{//下一环节信息存在
					//修改下一环节子表信息为空
					String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKNAME = '"+handleper+"',CHECKCODE = '"+code+"',CHECKIDEA = '',ATTACH = '',ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='4' AND BL = '"+deptName+"' AND ISDELETE = '0'";
					//将本环节和上一环节子表审批状态修改为未审核
					String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核' WHERE HID = '"+zid+"' AND NODESTATUS IN ('3','4') AND BL = '"+deptName+"' AND ISDELETE = '0'";
					dbo.prepareUpdate(updateSql1, null);
					dbo.prepareUpdate(updateSql, null);
				}
				//向日志表添加信息
				util.addLogoInfo(zid, login, "正在处理中...", handleper, code,"处理中","4",deptCode);
				//修改本环节日志信息
				if(neighborhooddirector == null || "".equals(neighborhooddirector)){
					neighborhooddirector = "同意";
				}
				String updateSql  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+neighborhooddirector+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '3' and UNDERTAKECODE = '"+deptCode+"' and OPERATION = '处理中'";
				dbo.prepareUpdate(updateSql,null);
			}else if(nodestatus.equals("7")){//科长派发任务
				//修改上一环节办理人的状态为已审核
				if(again.equals("0")){
					util.updatePreInfo(zid, "2","");
				}else{
					util.updatePreInfo(zid, "18",deptName);
				}
				//修改本环节办理人的批示和审批时间
				util.updateInfo(sectionchief, zid, nodestatus,deptName);
				//查询办理人员code
				String codeSql = "SELECT CODE FROM EAP_ACCOUNT WHERE NAME = '"+khandleper+"'";
				List<HashMap<String,String>> codeList = dbo.prepareQuery(codeSql, null);
				String code = codeList.get(0).get("CODE");
				String hSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '8' AND BL = '"+deptName+"' AND ISDELETE = '0'";
				List<HashMap<String,String>> exList = dbo.prepareQuery(hSql, null);
				if(exList.size()==0){
					util.addTable(zid, khandleper, code, "8", "办理阶段",login,deptName,deptCode);
					//String updateSql = "update TELEPHONE_HANDLE set STATUS = '未审核' where hid = '"+zid+"' and NODESTATUS = '7' and  BL = '"+deptName+"' and ISDELETE = '0'";
					//dbo.prepareUpdate(updateSql, null);
				}else{
					String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKNAME = '"+khandleper+"',CHECKCODE = '"+code+"',CHECKIDEA = '',ATTACH = '',ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='8' AND BL = '"+deptName+"' AND ISDELETE = '0'";
					String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核' WHERE HID = '"+zid+"' AND NODESTATUS IN ('7','8') AND BL = '"+deptName+"' AND ISDELETE = '0'";
					dbo.prepareUpdate(updateSql1, null);
					dbo.prepareUpdate(updateSql, null);
				}
				//向日志表添加信息
				if(sectionchief == null || "".equals(sectionchief)){
					sectionchief = "同意";
				}
				util.addLogoInfo(zid, login, "正在处理中...", khandleper, code,"已办","8",deptCode);//修改本环节日志信息
				String updateSql  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+sectionchief+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '7' and UNDERTAKECODE = '"+deptCode+"' and OPERATION = '处理中'";
				dbo.prepareUpdate(updateSql,null);

			}
			SubTask st = new SubTask();
			//给协办科室科长发公告
			st.insertG(xzname, "", xzcode, "", "d", login, title, attach,zid,deptCode,(Integer.parseInt(nodestatus)+1)+"");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * 完成社区理长审批
	 */
	public void communityDirector(HttpServletRequest request, HttpServletResponse response,LoginModel login){
		//获取基础信息id///zid
		String zid = request.getParameter("id");
		System.out.println("获取基础信息id为："+zid);
		//获取社区理长批示
		String communityleaderidea = request.getParameter("communityleaderidea");
		System.out.println("获取社区理长批示为："+communityleaderidea);
		//获取办理人name
		String handleper = request.getParameter("handleper");
		System.out.println("查询当前办理人name为:"+handleper);
		//获取当前登录人节点状态
		String nodestatus = request.getParameter("nodestatus");
		System.out.println("获取办理人员name为："+nodestatus);
		String deptCode = request.getParameter("bl");//办理社区code

		//String selectSql33 = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND CHECKCODE = '"+handleper+"' AND NODESTATUS = '4' AND ISDELETE = '0'";


		try {
			//查询下一环办理人code和name
			String roleSql = "SELECT CHECKNAME,CHECKCODE FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '0' AND ISDELETE = '0'";
			List<HashMap<String,String>> roleList = dbo.prepareQuery(roleSql, null);
			String name = roleList.get(0).get("CHECKNAME");
			String code = roleList.get(0).get("CHECKCODE");
			//查询办理社区name
			String codeSql = "SELECT NAME FROM EAP_DEPARTMENT WHERE CODE = '"+deptCode+"'";
			String deptName = dbo.prepareQuery(codeSql,null).get(0).get("NAME");
			//将上一环节状态改为已审核
			util.updatePreInfo(zid, "5",deptName);
			//修改本环节办理人审批时间和审批意见
			util.updateInfo(communityleaderidea, zid, "6",deptName);
			//查询子表是否有办公室检查阶段信息
			String nodeSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
			List<HashMap<String,String>> nodeList = dbo.prepareQuery(nodeSql, null);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date());
			//查询是否派发给科室
			String selectSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS IN ('7','18') AND ISDELETE = '0'";
			List<HashMap<String,String>> selectList = dbo.prepareQuery(selectSql, null);

			if(nodeList.size()==0){//没有办公室检查阶段信息
				//给子表添加下环节信息
				util.addTable(zid, name, code, "11", "办理阶段",login,"","");
				if(selectList.size()>0){//派发给科室
					//修改办公室检查阶段审批时间
					String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKTIME = '"+time+"' WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
					dbo.prepareUpdate(updateSql1, null);
				}
			}else{//有办公室检查阶段信息
				//查询办理阶段是否全部办理完
				String sql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS IN ('3','4','5','7','8','9','17','18') AND CHECKTIME IS NULL AND ISDELETE = '0'";
				List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
				if(list.size()==0){//全部办理完
					//修改办公室检查阶段审批时间
					String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKTIME = '',ATTACH = '', PROBLEMTYPE = '检查阶段' WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
					dbo.prepareUpdate(updateSql1, null);
					//将主表阶段类型修改为检查阶段（3）
					String updateSql = "UPDATE TELEPHONE_BASIC SET TASKSTAGE = '3' WHERE ID = "+zid;
					dbo.prepareUpdate(updateSql, null);
				}
			}
			//给日志表添加数据
			//util.addLogoInfo(zid, login, communityleaderidea, name, code, "已办");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * 完成办公室主管副职审核（审核阶段）
	 */
	public void officeDirectorAudit(HttpServletRequest request, HttpServletResponse response,LoginModel login){
		//获取基础信息id///zid
		String zid = request.getParameter("id");
		System.out.println("获取基础信息id为："+zid);
		//获取办公室审核意见
		String officedirectoropt = request.getParameter("officedirectoropt");
		System.out.println("获取办公室审核意见为："+officedirectoropt);
		//获取当前登录人节点状态
		String nodestatus = request.getParameter("nodestatus");
		System.out.println("获取当前登录人节点状态为："+nodestatus);
		//将上一环节状态改为已审核
		util.updatePreInfo(zid, "11","");
		//修改本环节办理人审批时间和审批意见
		util.updateInfo(officedirectoropt, zid, "12","");

		try {
			//查询下一环节处理人的code和name
			String roleSql = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = 'convenienttelephone_zhuren')";
			List<HashMap<String,String>> roleList = dbo.prepareQuery(roleSql, null);
			String name = "";
			String code = "";
			if(roleList.size()>0){
				name = roleList.get(0).get("NAME");
				code = roleList.get(0).get("CODE");
			}
			//查询下一环节子表是否有数据
			String hSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '13' AND ISDELETE = '0'";
			List<HashMap<String,String>> hList =dbo.prepareQuery(hSql, null);
			if(hList.size()==0){//下一环节子表没有数据
				//向子表添加下一环节数据
				util.addTable(zid, name, code, "13", "审核阶段",login,"","");
			}else{//下一环节子表有数据
				//将本环节和下一环节审批状态修改为未审核
				String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核' WHERE HID = '"+zid+"' AND NODESTATUS IN ('12','13') AND ISDELETE = '0'";
				dbo.prepareUpdate(updateSql, null);
				String updateSql1 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='13' AND ISDELETE = '0'";
				dbo.prepareUpdate(updateSql1, null);
			}
			//给日志表添加数据
			if(officedirectoropt == null || "".equals(officedirectoropt)){
				officedirectoropt = "同意";
			}
			util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","13","");
			String updateSql  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+officedirectoropt+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '12' and OPERATION = '处理中'";
			dbo.prepareUpdate(updateSql,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/*
	 * 完成提交书记签批13
	 */
	public void secretarySign(HttpServletRequest request, HttpServletResponse response,LoginModel login){
		//获取基础信息id///zid
		String zid = request.getParameter("id");
		System.out.println("获取基础信息id为："+zid);
		//获取主任审批意见
		String directoropt = request.getParameter("directoropt");
		System.out.println("获取主任审批意见为："+directoropt);
		//获取当前登录人节点状态
		String nodestatus = request.getParameter("nodestatus");
		System.out.println("获取当前登录人节点状态为："+nodestatus);
		//将上一环节状态改为已审核
		util.updatePreInfo(zid, "12","");
		//修改本环节办理人审批时间和审批意见
		util.updateInfo(directoropt, zid, "13","");

		try {
			//查询下一环节处理人的code和name并向子表添加数据
			String roleSql = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = 'convenienttelephone_shuji')";
			List<HashMap<String,String>> roleList = dbo.prepareQuery(roleSql, null);
			String name = "";
			String code = "";
			if(roleList.size()>0){
				name = roleList.get(0).get("NAME");
				code = roleList.get(0).get("CODE");
			}
			//查询子表是否有下一环节信息
			String hSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '14' AND ISDELETE = '0'";
			List<HashMap<String,String>> hList =dbo.prepareQuery(hSql, null);
			if(hList.size()==0){//没有下一环节信息
				//向子表添加下一环节信息
				util.addTable(zid, name, code, "14", "审核阶段",login,"","");
			}else{//有下一环节信息
				//修改本环节和下一环节审批状态为未审核
				String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核' WHERE HID = '"+zid+"' AND NODESTATUS IN ('13','14') AND ISDELETE = '0'";
				dbo.prepareUpdate(updateSql, null);
				String updateSql1 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='14' AND ISDELETE = '0'";
				dbo.prepareUpdate(updateSql1, null);
			}
			//给日志表添加数据
			if(directoropt == null || "".equals(directoropt)){
				directoropt = "同意";
			}
			util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","14","");
			String updateSql  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+directoropt+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '13' and OPERATION = '处理中'";
			dbo.prepareUpdate(updateSql,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * 完成书记审核14
	 */
	public void secretaryExamine(HttpServletRequest request, HttpServletResponse response,LoginModel login){
		//获取基础信息id///zid
		String zid = request.getParameter("id");
		System.out.println("获取基础信息id为："+zid);
		//获取书记审批意见
		String secretaryidea = request.getParameter("secretaryidea");
		System.out.println("获取书记审批意见为："+secretaryidea);
		//获取当前登录人节点状态
		String nodestatus = request.getParameter("nodestatus");
		System.out.println("获取当前登录人节点状态为："+nodestatus);
		//将上一环节状态改为已审核
		util.updatePreInfo(zid, "13","");
		//修改本环节办理人审批时间和审批意见
		util.updateInfo(secretaryidea, zid, "14","");

		try {
			//查询下一环节处理人的code和name并向子表添加数据
			String roleSql = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = 'a02')";
			List<HashMap<String,String>> roleList = dbo.prepareQuery(roleSql, null);
			String name = "";
			String code = "";
			if(roleList.size()>0){
				name = roleList.get(0).get("NAME");
				code = roleList.get(0).get("CODE");
			}
			//判断子表是否有下一环节信息
			String hSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '15' AND ISDELETE = '0'";
			List<HashMap<String,String>> hList =dbo.prepareQuery(hSql, null);
			if(hList.size()==0){//没有下一环节信息
				//向子表添加下一环节信息
				util.addTable(zid, name, code, "15", "审核阶段",login,"","");
			}else{//有下一环节信息
				//修改本环节和下一环节审批状态为未审核
				String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核' WHERE HID = '"+zid+"' AND NODESTATUS IN ('14','15') AND ISDELETE = '0'";
				dbo.prepareUpdate(updateSql, null);
				String updateSql1 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='15' AND ISDELETE = '0'";
				dbo.prepareUpdate(updateSql1, null);
			}
			//给日志表添加数据
			if(secretaryidea == null || "".equals(secretaryidea)){
				secretaryidea = "同意";
			}
			util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","15","");
			String updateSql  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+secretaryidea+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '14' and OPERATION = '处理中'";
			dbo.prepareUpdate(updateSql,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * 完成办公室人员重新办理16
	 */
	public void reProcess(HttpServletRequest request, HttpServletResponse response,LoginModel login){
		//获取基础信息id///zid
		String zid = request.getParameter("id");
		System.out.println("获取基础信息id为："+zid);
		String officeconfirm = request.getParameter("officeconfirm");
		System.out.println("获取办公室意见为："+officeconfirm);
		//将主表的办理社区和办理科室设置为空
		String updateBSql = "UPDATE TELEPHONE_BASIC SET HANDLEDEPTNAME = '',TASKSTAGE = '1',AGAINBANLI = '0',REMARKS = '',breply =  '',bresolve = '',bsatisfaction = '' WHERE ID = '"+zid+"'";
		try {
			dbo.prepareUpdate(updateBSql, null);
			//将子表除了登记阶段的信息外删除
			String deleteSql = "UPDATE TELEPHONE_HANDLE SET ISDELETE = '1' WHERE HID = '"+zid+"' AND NODESTATUS NOT IN ('0','1') AND ISDELETE = '0'";
			dbo.prepareUpdate(deleteSql, null);
			//将子表办公室主管副职办理意见和时间清空并将登记人员状态改为未审核
			String updateSql = "UPDATE TELEPHONE_HANDLE SET CHECKTIME = '',CHECKIDEA = '',STATUS = '未审核' WHERE HID = '"+zid+"' AND NODESTATUS = '1' AND ISDELETE = '0'";
			String updateSql1 = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核' WHERE HID = '"+zid+"' AND NODESTATUS = '0' AND ISDELETE = '0'";
			dbo.prepareUpdate(updateSql, null);
			dbo.prepareUpdate(updateSql1, null);
			//String roleSql = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = 'convenienttelephone_zhuren')";
			//String name = dbo.prepareQuery(roleSql, null).get(0).get("NAME");
			//String code = dbo.prepareQuery(roleSql, null).get(0).get("CODE");
			String sql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus= '0'";
			List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
			String name = list.get(0).get("CHECKNAME");
			String code = list.get(0).get("CHECKCODE");
			//给日志表添加数据
			if(officeconfirm == null || "".equals(officeconfirm)){
				officeconfirm = "同意";
			}
			util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","0","");
			String updateSql0  = "update TELEPHONE_LOG set OPERATION = '重办',CONTENT = '"+officeconfirm+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '16' and OPERATION = '处理中'";
			dbo.prepareUpdate(updateSql0,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * 完成科长审批9
	 */
	public void sectionChief(HttpServletRequest req, HttpServletResponse resp, LoginModel login){
		String sectionchiefidea = req.getParameter("sectionchiefidea");//科长审核意见
		String zid = req.getParameter("id");//主表的id
		String nodeStatus = req.getParameter("nodestatus");//当前节点状态
		System.out.println("当前节点状态为："+nodeStatus);
		String handledepartname = req.getParameter("handledepartname");//办理单位
		String deptCode = req.getParameter("bl");//办理科室CODE
		try {
			//根据当前登录人查询社区name和code
			/*String selectSql = "select name,code from EAP_DEPARTMENT where code = (select deptcode from EAP_ACCOUNT where code = '"+login.getUserCode()+"')";
			String deptName = dbo.prepareQuery(selectSql, null).get(0).get("NAME");
			String deptCode = dbo.prepareQuery(selectSql, null).get(0).get("CODE");*/
			//根据办理科室code查询对应的name
			String sql1 = "SELECT NAME FROM EAP_DEPARTMENT WHERE CODE = '"+deptCode+"'";
			List<HashMap<String,String>> deptList = dbo.prepareQuery(sql1,null);
			String deptName = deptList.get(0).get("NAME");
			System.out.println("办理社区name为:"+deptName);
			System.out.println("办理社区code为:"+deptCode);
			//修改本环节办理人的批示和审批时间
			util.updateInfo(sectionchiefidea, zid, "9",deptName);
			//修改上一环节办理人的状态为已审核
			util.updatePreInfo(zid, "8",deptName);
			//查询下一环节处理人的code和name
	        /*String preHandleSql = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = (SELECT h.LEADER FROM eap_hierarchy h WHERE h.deptcode ='"+deptCode+"'))";
	       List<HashMap<String,String>> preHandleList = dbo.prepareQuery(preHandleSql, null);
	        String name = preHandleList.get(0).get("NAME");
	        String code = preHandleList.get(0).get("CODE");
	        //查询子表是否有下一环节信息
	        String hSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '10' AND BL = '"+deptName+"' AND ISDELETE = '0'";
            List<HashMap<String,String>> hList = dbo.prepareQuery(hSql, null);
            if(hList.size()==0){//没有下一环节信息
            	//向子表添加下一环节信息
            	util.addTable(zid, name, code, "10", "办理阶段",login,deptName,deptCode);
            }else{//有下一环节信息
            	//修改本环节和下一环节审批状态为未审核
            	String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核' WHERE HID = '"+zid+"' AND NODESTATUS IN ('9','10') AND BL = '"+deptName+"' AND ISDELETE = '0'";
				dbo.prepareUpdate(updateSql, null);
				String updateSql1 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS ='10' AND ISDELETE = '0' AND BL = '"+deptName+"'";
				dbo.prepareUpdate(updateSql1, null);
            }*/
			//查询下一环办理人code和name
			String roleSql = "SELECT CHECKNAME,CHECKCODE FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '0' AND ISDELETE = '0'";
			List<HashMap<String,String>> roleList = dbo.prepareQuery(roleSql, null);
			String name = roleList.get(0).get("CHECKNAME");
			String code = roleList.get(0).get("CHECKCODE");
			//查询子表是否有办公室检查阶段的信息
			String nodeSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
			List<HashMap<String,String>> nodeList = dbo.prepareQuery(nodeSql, null);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date());
			//查询是否派发给社区
			String selectSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS IN ('3','17') AND ISDELETE = '0'";
			List<HashMap<String,String>> selectList = dbo.prepareQuery(selectSql, null);

			//查询办理阶段是否已办理完成
			String sql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS IN ('3','4','5','7','8','9','17','18') AND CHECKTIME IS NULL AND ISDELETE = '0'";
			List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
			System.out.println("进入办公室检查阶段。。。。。。。。。:"+list.size());
			if(nodeList.size()==0){//没有办公室检查阶段的信息
				//给子表添加下环节信息
				util.addTable(zid, name, code, "11", "办理阶段",login,"","");
				if(selectList.size()>0){//派发给社区
					//修改检查阶段审批时间
					String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKTIME = '"+time+"' WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
					dbo.prepareUpdate(updateSql1, null);
				}
				if(list.size()<=0){
					//向日志表添加信息
					util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","11","");
				}
			}else{//有办公室检查阶段的信息

				if(list.size()<=0){//办理阶段已全部办理完
					System.out.println("进入办公室检查阶段。。。。。。。。。");
					////修改检查阶段审批时间
					String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKTIME = '',ATTACH = '',PROBLEMTYPE = '检查阶段' WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
					dbo.prepareUpdate(updateSql1, null);
					//将主表阶段类型修改为检查阶段（3）
					String updateSql = "UPDATE TELEPHONE_BASIC SET TASKSTAGE = '3' WHERE ID = "+zid;
					dbo.prepareUpdate(updateSql, null);
					//向日志表添加信息
					util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","11","");
				}
			}
			if(sectionchiefidea == null || "".equals(sectionchiefidea)){
				sectionchiefidea = "同意";
			}
			String updateSql0  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '"+sectionchiefidea+"',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '9' and UNDERTAKECODE = '"+deptCode+"' and OPERATION = '处理中'";
			dbo.prepareUpdate(updateSql0,null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/*
	 * 完成完成主管副职审批10
	 */
	public void deputyDirector(HttpServletRequest request, HttpServletResponse response, LoginModel login){
		//获取基础信息id///zid
		String zid = request.getParameter("id");
		System.out.println("获取基础信息id为："+zid);
		//获取主管副职审批
		String deputydirectoridea = request.getParameter("deputydirectoridea");
		System.out.println("获取社主管副职审批为："+deputydirectoridea);
		//获取当前登录人节点状态
		String nodestatus = request.getParameter("nodestatus");
		System.out.println("获取办理人员name为："+nodestatus);
		//获取办理人name
		String khandleper = request.getParameter("khandleper");
		System.out.println("查询当前办理人name为:"+khandleper);//获取办理人name
		String deptCode = request.getParameter("bl");
		System.out.println("查询b办理社区为:"+deptCode);

		try {
			String codeSql = "SELECT NAME FROM EAP_DEPARTMENT WHERE CODE = '"+deptCode+"'";
			List<HashMap<String,String>> deptList = dbo.prepareQuery(codeSql,null);
			String deptName = "";
			if(deptList.size()>0){
				deptName = deptList.get(0).get("NAME");
			}
			//将上一环节状态改为已审核
			util.updatePreInfo(zid, "9",deptName);
			//修改本环节办理人审批时间和审批意见
			util.updateInfo(deputydirectoridea, zid, "10",deptName);
			//查询下一环办理人code和name
			String roleSql = "SELECT CHECKNAME,CHECKCODE FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '0' AND ISDELETE = '0'";
			List<HashMap<String,String>> roleList = dbo.prepareQuery(roleSql, null);
			String name = roleList.get(0).get("CHECKNAME");
			String code = roleList.get(0).get("CHECKCODE");
			//查询子表是否有办公室检查阶段的信息
			String nodeSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
			List<HashMap<String,String>> nodeList = dbo.prepareQuery(nodeSql, null);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date());
			//查询是否派发给社区
			String selectSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS IN ('3','17') AND ISDELETE = '0'";
			List<HashMap<String,String>> selectList = dbo.prepareQuery(selectSql, null);
			if(nodeList.size()==0){//没有办公室检查阶段的信息
				//给子表添加下环节信息
				util.addTable(zid, name, code, "11", "办理阶段",login,"","");
				if(selectList.size()>0){//派发给社区
					//修改检查阶段审批时间
					String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKTIME = '"+time+"' WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
					dbo.prepareUpdate(updateSql1, null);
				}
			}else{//有办公室检查阶段的信息
				//查询办理阶段是否已办理完成
				String sql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '"+zid+"' AND NODESTATUS IN ('3','4','5','7','8','9','17','18') AND CHECKTIME IS NULL AND ISDELETE = '0'";
				List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
				if(list.size()==0){//办理阶段已全部办理完
					////修改检查阶段审批时间
					String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKTIME = '',ATTACH = '',PROBLEMTYPE = '检查阶段' WHERE HID = '"+zid+"' AND NODESTATUS = '11' AND ISDELETE = '0'";
					dbo.prepareUpdate(updateSql1, null);
					//将主表阶段类型修改为检查阶段（3）
					String updateSql = "UPDATE TELEPHONE_BASIC SET TASKSTAGE = '3' WHERE ID = "+zid;
					dbo.prepareUpdate(updateSql, null);
				}
			}
			//给日志表添加数据
			//util.addLogoInfo(zid, login, deputydirectoridea, name, code, "已办");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 办公室提交环节重办
	 */
	public String reRun(HttpServletRequest request,HttpServletResponse response,LoginModel login){
		String status = "";
		//获取主表id
		String zid = request.getParameter("id");
		//查询子表节点状态为2的数据是不是为已审核
		String sql = "select id from TELEPHONE_HANDLE where HID = '"+zid+"' and NODESTATUS = '1' and  ISDELETE = '0' and CHECKTIME is not null and CHECKIDEA is not null";
		try {
			List<HashMap<String,String>> list = dbo.prepareQuery(sql,null);
			if(list.size()==0){//办公室主任未审核可重办
				System.out.println("@@@@@@@@@@@@@@@@@@办公室主任未审核可重办");
				//将节点为1的状态修改为重办
				String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '被驳回' WHERE HID = '" + zid + "' AND NODESTATUS = '1' AND ISDELETE = '0' ";
				dbo.prepareUpdate(updateSql, null);
				//修改节点为0的状态为未审核
				String updateSql2 = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核',CHECKTIME = '',CHECKIDEA = '' WHERE HID = '" + zid + "' AND NODESTATUS = '0' AND ISDELETE = '0' ";
				dbo.prepareUpdate(updateSql2, null);
				////修改本环节审批意见和审批时间
				String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKIDEA = '已重办 ',CHECKTIME = '' WHERE HID = '"+zid+"' AND NODESTATUS = '1' AND ISDELETE = '0' ";
				dbo.prepareUpdate(updateSql1, null);
				//修改上一环节的递交人为当前登录人
				String updateSql3 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS = '0' AND ISDELETE = '0' ";
				dbo.prepareUpdate(updateSql3, null);

				String cheeckSql = "select * from TELEPHONE_HANDLE where hid = '"+zid+"' and nodestatus = '0'";
				List<HashMap<String,String>> cheeckList = dbo.prepareQuery(cheeckSql,null);
				String name = cheeckList.get(0).get("CHECKNAME");
				String code = cheeckList.get(0).get("CHECKCODE");
				//添加日志信息
				util.addLogoInfo(zid, login, "正在办理中...", name, code, "处理中","0","");
				String updateSql0  = "update TELEPHONE_LOG set OPERATION = '重办',CONTENT = '已重办',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '1' and OPERATION = '处理中'";
				dbo.prepareUpdate(updateSql0,null);
				status = "1";//重办跳列表
			}else{//办公室主任已审核则不可再重办
				status = "0";//不可重办，不跳列表，点击无反应
				System.out.println("@@@@@@@@@@@@@@@@@@办公室主任已审核则不可再重办");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	/**
	 * 协助科室提交上传附件
	 */
	public void xzsbmit(HttpServletResponse response,HttpServletRequest request){
		//获取主表di
		String zid = request.getParameter("id");
		//获取科室附件
		String khandleattach = request.getParameter("khandleattach");
		//获取社区附件
		String handleattach = request.getParameter("handleattach");
		//获取节点状态
		String nodestatus = request.getParameter("nodestatus");
		//获取主办科室
		String handledeptname = request.getParameter("handledeptname");
		String attach = "";
		if(nodestatus.equals("4")){
			attach = handleattach;
		}else{
			attach = khandleattach;
		}
		//修改子表附件
		String sql = "update TELEPHONE_HANDLE set ATTACH = '"+attach+"' where HID = '"+zid+"' and NODESTATUS = '"+nodestatus+"' and BL = '"+handledeptname+"' and ISDELETE = '0'";
		try {
			dbo.prepareUpdate(sql,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
