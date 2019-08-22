package com.tellhow.convenienttelephone.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import com.siqiansoft.framework.model.PagingModel;
import com.siqiansoft.framework.model.db.ConditionModel;
import com.siqiansoft.framework.util.PageControl;

public class SelectTaskList {
	public List<HashMap<String,String>> getTaskList(LoginModel model, ConditionModel[] cs, PagingModel page){
		DatabaseBo dbo = new DatabaseBo();
		ArrayList pagelist = new ArrayList();
		List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		List<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>();
		//获取当前登录人code
		String userCode = model.getUserCode();
		System.out.println("获取当前登录人code为："+userCode);
		/*
		 * 查询当前登录人是否为办公室人员
		 */
		//查询当前登录人角色
		String sql11 = "select ROLECODE from EAP_USERROLE where USERCODE='"+userCode+"'";
		try {
			List<HashMap<String,String>> roleList = dbo.prepareQuery(sql11, null);
			//定义办公室人员
			int bPer = 0;
			//定义书记
			int bSj = 0;
			for(int i=0;i<roleList.size();i++){
				String role = roleList.get(i).get("ROLECODE");
				if(role.equals("convenienttelephone_office") || role.equals("convenienttelephone_bgs") || role.equals("convenienttelephone_leader")){
					bPer++;
				}else if(role.equals("convenienttelephone_shuji")){
					bSj++;
				}
			}
			System.out.println("查询bPer为："+bPer);
			//查询主管副职和社区理长所管的社区code和科室code
			/*String deptCodeSql = "select DEPTCODE from EAP_HIERARCHY where LEADER in (select ROLECODE from EAP_USERROLE where USERCODE='"+userCode+"')";
			List<HashMap<String,String>> deptCodeList = dbo.prepareQuery(deptCodeSql,null);
			//定义所管社区或科室code
			String deptCode = "";
			if(deptCodeList.size()>0){//当前登录人为主管副职或社区理长
				for(int m=0;m<deptCodeList.size();m++){
					deptCode = deptCode +"'"+ deptCodeList.get(m).get("DEPTCODE")+"',";
				}
			}*/
			String sql = "";
			if(bPer>0 || bSj>0){//书记或办公室登记人员查看所有
				/*sql = "SELECT * FROM (SELECT a.id id,a.taskid taskid,a.title title, a.finishtime finishtime,case when a.HANDLEKESHI is null  then a.deptname"
						+ " when a.deptname is null then a.HANDLEKESHI "
						+ "when a.deptname is not null and a.HANDLEKESHI is not null then a.HANDLEKESHI||','|| a.deptname "
						+ "end deptname,a.termtime termtime,a.taskstage taskstage,b.nodestatus nodestatus, b.status status,b.checkcode checkcode,b.checktime checktime,a.createtime createtime FROM telephone_basic a,telephone_handle b WHERE a.id       = b.hid  AND nodestatus   = '0' AND b.checkcode != '"+userCode+"' UNION ALL SELECT a.id id,a.taskid taskid,a.title title, a.finishtime finishtime, case when a.HANDLEKESHI is null  then a.deptname"
						+ " when a.deptname is null then a.HANDLEKESHI "
						+ "when a.deptname is not null and a.HANDLEKESHI is not null then a.HANDLEKESHI||','|| a.deptname "
						+ "end deptname,a.termtime termtime,a.taskstage taskstage,b.nodestatus nodestatus, b.status status,b.checkcode checkcode,b.checktime checktime,a.createtime createtime FROM telephone_basic a,telephone_handle b WHERE a.id      = b.hid AND b.checkcode = '"+userCode+"' AND b.id  IN (SELECT MAX(b.id)bid FROM telephone_basic a,  telephone_handle b WHERE a.id  = b.hid AND b.checkcode = '"+userCode+"' GROUP BY hid )) t where 1=1 ";

*/
				sql = "SELECT ID ,BL,TASKID,TITLE,FINISHTIME, DEPTNAME,TERMTIME,TASKSTAGE,NODESTATUS,STATUS,CHECKCODE,CHECKTIME,CREATETIME FROM (SELECT A.ID ID," +
						" b.BLCODE BL,A.TASKID TASKID, A.TITLE TITLE, A.FINISHTIME FINISHTIME, CASE" +
						"    WHEN handledeptname IS  NULL" +
						"    THEN DEPTNAME" +
						"    ELSE handledeptname" +
						"  END DEPTNAME, A.TERMTIME TERMTIME, A.TASKSTAGE TASKSTAGE, B.NODESTATUS NODESTATUS," +
						" B.STATUS STATUS, B.CHECKCODE CHECKCODE,B.CHECKTIME CHECKTIME,  A.CREATETIME CREATETIME FROM TELEPHONE_BASIC A,TELEPHONE_HANDLE B" +
						"  WHERE A.ID       = B.HID and b.ISDELETE = '0' AND NODESTATUS   = '0' AND B.CHECKCODE != '"+userCode+"'" +
						"  UNION ALL SELECT A.ID ID,b.BL,  A.TASKID TASKID,  A.TITLE TITLE, A.FINISHTIME FINISHTIME,  CASE" +
						"      WHEN handledeptname IS NULL" +
						"      THEN DEPTNAME" +
						"      ELSE handledeptname" +
						"    END DEPTNAME, A.TERMTIME TERMTIME,A.TASKSTAGE TASKSTAGE, B.NODESTATUS NODESTATUS, B.STATUS STATUS, B.CHECKCODE CHECKCODE," +
						"   B.CHECKTIME CHECKTIME,  A.CREATETIME CREATETIME FROM TELEPHONE_BASIC A, TELEPHONE_HANDLE B WHERE A.ID = B.HID AND B.CHECKCODE = '"+userCode+"'" +
						"  AND B.ID       IN (SELECT MAX(B.ID)BID FROM TELEPHONE_BASIC A,  TELEPHONE_HANDLE B WHERE A.ID      = B.HID and b.ISDELETE = '0'  AND B.CHECKCODE = '"+userCode+"'" +
						" GROUP BY HID  )) T WHERE 1=1";
			}else{//审核人员
				String sql1 = "select ID ,TASKID,TITLE,FINISHTIME,CASE WHEN DEPTNAME1 IS NOT NULL THEN DEPTNAME1 ELSE DEPTNAME" +
						" END DEPTNAME,TERMTIME,TASKSTAGE,NODESTATUS,STATUS,CHECKCODE,CHECKTIME,CREATETIME from (SELECT a.id id,a.taskid taskid,a.title title," +
						" a.finishtime finishtime,case when a.HANDLEKESHI is null  then a.deptname when a.deptname is null then a.HANDLEKESHI when a.deptname is not null" +
						" and a.HANDLEKESHI is not null then a.HANDLEKESHI||','|| a.deptname end deptname,CASE WHEN A.HANDLEDEPTNAME IS NULL THEN A.HANDLEDEPARTNAME" +
						" WHEN A.HANDLEDEPARTNAME IS NULL THEN A.HANDLEDEPTNAME WHEN A.HANDLEDEPARTNAME   IS NOT NULL AND A.HANDLEDEPTNAME IS NOT NULL" +
						" THEN A.HANDLEDEPTNAME ||',' || A.HANDLEDEPARTNAME  END DEPTNAME1,a.termtime termtime,a.taskstage taskstage,b.nodestatus nodestatus, " +
						" b.status status,b.checkcode checkcode,b.checktime checktime,a.createtime createtime from TELEPHONE_BASIC a,TELEPHONE_HANDLE b where " +
						" A.ID = B.HID and b.ISDELETE = '0' and b.CHECKCODE = '"+userCode+"') t where 1=1";
				List<HashMap<String,String>> list11 = dbo.prepareQuery(sql1,null);
                String nodestatus = "";
                if(list11.size()>0){
                    nodestatus = list11.get(0).get("NODESTATUS");
                }
				System.out.println("获取节点状态："+nodestatus);
				//同意审核人的节点状态是否一致
				String sql2 = "select ID ,TASKID,TITLE,FINISHTIME,CASE WHEN DEPTNAME1 IS NOT NULL THEN DEPTNAME1 ELSE DEPTNAME" +
						" END DEPTNAME,TERMTIME,TASKSTAGE,NODESTATUS,STATUS,CHECKCODE,CHECKTIME,CREATETIME from (SELECT a.id id,a.taskid taskid,a.title title," +
						" a.finishtime finishtime,case when a.HANDLEKESHI is null  then a.deptname when a.deptname is null then a.HANDLEKESHI when a.deptname is not null" +
						" and a.HANDLEKESHI is not null then a.HANDLEKESHI||','|| a.deptname end deptname,CASE WHEN A.HANDLEDEPTNAME IS NULL THEN A.HANDLEDEPARTNAME" +
						" WHEN A.HANDLEDEPARTNAME IS NULL THEN A.HANDLEDEPTNAME WHEN A.HANDLEDEPARTNAME   IS NOT NULL AND A.HANDLEDEPTNAME IS NOT NULL" +
						" THEN A.HANDLEDEPTNAME ||',' || A.HANDLEDEPARTNAME  END DEPTNAME1,a.termtime termtime,a.taskstage taskstage,b.nodestatus nodestatus, " +
						" b.status status,b.checkcode checkcode,b.checktime checktime,a.createtime createtime from TELEPHONE_BASIC a,TELEPHONE_HANDLE b where " +
						" A.ID = B.HID and b.ISDELETE = '0' and b.CHECKCODE = '"+userCode+"') t where 1=1 and NODESTATUS <> '"+nodestatus+"'";
				List<HashMap<String,String>> list22 = dbo.prepareQuery(sql2,null);
				String value = "";
				if(list22.size()>0){
					if(!nodestatus.equals("17") && !nodestatus.equals("18")){
						//节点状态不一样
						value = "and b.id in (select max(b.id)bid from TELEPHONE_BASIC a,TELEPHONE_HANDLE b " +
								" where A.ID = B.HID and b.CHECKCODE = '"+userCode+"' and b.ISDELETE = '0' group by hid)";
					}else{
						String sql3 = "select ID ,TASKID,TITLE,FINISHTIME,CASE WHEN DEPTNAME1 IS NOT NULL THEN DEPTNAME1 ELSE DEPTNAME" +
								" END DEPTNAME,TERMTIME,TASKSTAGE,NODESTATUS,STATUS,CHECKCODE,CHECKTIME,CREATETIME from (SELECT a.id id,a.taskid taskid,a.title title," +
								" a.finishtime finishtime,case when a.HANDLEKESHI is null  then a.deptname when a.deptname is null then a.HANDLEKESHI when a.deptname is not null" +
								" and a.HANDLEKESHI is not null then a.HANDLEKESHI||','|| a.deptname end deptname,CASE WHEN A.HANDLEDEPTNAME IS NULL THEN A.HANDLEDEPARTNAME" +
								" WHEN A.HANDLEDEPARTNAME IS NULL THEN A.HANDLEDEPTNAME WHEN A.HANDLEDEPARTNAME   IS NOT NULL AND A.HANDLEDEPTNAME IS NOT NULL" +
								" THEN A.HANDLEDEPTNAME ||',' || A.HANDLEDEPARTNAME  END DEPTNAME1,a.termtime termtime,a.taskstage taskstage,b.nodestatus nodestatus, " +
								" b.status status,b.checkcode checkcode,b.checktime checktime,a.createtime createtime from TELEPHONE_BASIC a,TELEPHONE_HANDLE b where " +
								" A.ID = B.HID and b.ISDELETE = '0' and b.CHECKCODE = '"+userCode+"') t where 1=1 and NODESTATUS not in ('17','18')";
						List<HashMap<String,String>> list33 = dbo.prepareQuery(sql3,null);
						if(list33.size()>0){
							value = "and b.id in (select max(b.id)bid from TELEPHONE_BASIC a,TELEPHONE_HANDLE b " +
									" where A.ID = B.HID and b.CHECKCODE = '"+userCode+"' and b.ISDELETE = '0' group by hid)";
						}else{
							value = "and 1=1";
						}
					}
				}
				/*String ifValue = "";
				if(deptCode.length()>0){
					ifValue = " or a.HANDLEDEPTCODE in ("+deptCode.substring(0,deptCode.length()-1)+")";
				}*/
				sql = "select ID ,BL,TASKID,TITLE,FINISHTIME, DEPTNAME,TERMTIME,TASKSTAGE,NODESTATUS,STATUS,CHECKCODE,CHECKTIME,CREATETIME from (SELECT a.id id,b.BLCODE bl,a.taskid taskid,a.title title," +
						" a.finishtime finishtime,CASE" +
						"    WHEN handledeptname IS  NULL" +
						"    THEN DEPTNAME" +
						"    ELSE handledeptname" +
						"  END DEPTNAME,a.termtime termtime,a.taskstage taskstage,b.nodestatus nodestatus, " +
						" b.status status,b.checkcode checkcode,b.checktime checktime,a.createtime createtime from TELEPHONE_BASIC a,TELEPHONE_HANDLE b where " +
						" A.ID = B.HID and b.ISDELETE = '0' and b.CHECKCODE = '"+userCode+"' "+value+" ) t where 1=1";

			}
			int from = page.getFrom();
			int to = page.getTo();
			int pageRows = page.getPageRows();
			int curPage = page.getCurPage();
			int i;
			if(cs != null && !"".equals(cs)){
				for(i=0;i<cs.length;++i){
					if (!cs[i].getValue().equals((Object) null) && "" != cs[i].getValue()) {
						if(cs[i].getId().equals("dept")){
							sql = sql + " and DEPTNAME like '%"+cs[i].getValue()+"%'";
						}
						if(cs[i].getId().equals("renwujieduan")){
							sql = sql + " and TASKSTAGE = '"+cs[i].getValue()+"'";
						}
						if(cs[i].getId().equals("title")){
							sql = sql + " and TITLE like '%"+cs[i].getValue()+"%'";
						}
						if(cs[i].getId().equals("dayu")){
							sql = sql + " and termtime >= '"+cs[i].getValue()+"'";
						}
						if(cs[i].getId().equals("xiaoyu")){
							sql = sql + " and termtime <= '"+cs[i].getValue()+"'";
						}
						if(cs[i].getId().equals("repairid")){
							sql = sql + " and taskid like '%"+cs[i].getValue()+"%'";
						}
					}
				}
			}
			sql = sql + " ORDER by t.createtime desc";
			list = dbo.prepareQuery(sql, null);
			List<String> taskList = new ArrayList<String>();
			if(bSj>0 || bPer>0){
				for(int j=0;j<list.size();j++){
					/*if(bSj>0){
						//查询书记审批的节点状态
						String nodestatus = list.get(j).get("NODESTATUS");
						if(nodestatus.equals("14") || nodestatus.equals("1")){
							//查询书记审批的任务单号
							String taskId = list.get(j).get("TASKID");
							taskList.add(taskId);
						}
					}*/
					String taskId = list.get(j).get("TASKID");
					for(int k=j+1;k<list.size();k++){
						String taskIdk = list.get(k).get("TASKID");
						if(taskId.equals(taskIdk)){
							String nodestatus = list.get(k).get("NODESTATUS");
							if(nodestatus.equals("0")){
								list.remove(list.get(k));
							}else{
								list.remove(list.get(j));
							}
						}
					}
				}
			}

			/*for(int k=0;k<list.size();k++){
				String taskId = list.get(k).get("TASKID");
				if(taskList.contains(taskId)){
					//查询节点状态
					String nodestatus = list.get(k).get("NODESTATUS");
					if(nodestatus.equals("0")){
						list.remove(list.get(k));
					}
				}
			}*/
			for(int k=0;k<list.size();k++){
				//获取节点状态
				String nodestatus = list.get(k).get("NODESTATUS");

				if(nodestatus.equals("19")){
					list.get(k).put("yincang", "隐藏");
				}else{
					//获取审批时间
					String checkTime = list.get(k).get("CHECKTIME");

					//获取审批状态
					String status = list.get(k).get("STATUS");
					//获取审批人
					String checkCode= list.get(k).get("CHECKCODE");
					if(( !"".equals(checkTime) && status.equals("未审核") && checkTime != null) ||
							(("".equals(checkTime) || checkTime == null) && status.equals("被驳回") ) ||
							status.equals("已审核") || !userCode .equals(checkCode)){
						list.get(k).put("yincang", "隐藏");
					}else{
						list.get(k).put("yincang", "不隐藏");
					}
				}

			}
			if (list.size() == 0) {
				PageControl.calcPage(page, 0, 0);
				return null;
			}
			if ((from == 1) && (to == 0)) {
				to = list.size();
			}
			int rowsCount = list.size();
			int pageCount = list.size() / pageRows + (list.size() % pageRows == 0 ? 0 : 1);
			page.setPageCount(pageCount);
			page.setRowsCount(rowsCount);
			if (to > list.size()) {
				to = list.size();
			}
			page.setTo(to);
			System.out.println("长度："+list.size());
			return list.subList(from - 1, to);
		}catch(Exception var14){
			var14.printStackTrace();

			return list;
		}

	}
}
