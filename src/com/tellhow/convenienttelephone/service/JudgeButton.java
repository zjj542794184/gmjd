package com.tellhow.convenienttelephone.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
/*
 * 页面初始化查询当前节点状态
 */
public class JudgeButton {
	public String JudgeButton(String userCode,String hid){
		DatabaseBo dbo = new DatabaseBo();
		List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		//判断当前登录人角色
//		String roleSql = "select ROLECODE from EAP_USERROLE where USERCODE='"+userCode+"'";
//		List<HashMap<String,String>> roleList = new ArrayList<HashMap<String,String>>();
//		int register =0;//办公室登记人员
//		int officeDirector = 0;//办公主任批示
//		int director = 0;//主任批示
//		int neighborhoodDirector = 0;//居委会主任
//		int sectionChief =0;//科长
//		int clerk =0;//科员
//		int communityLeader =0;//社区理长
//		int DeparDeputyDirector =0;//科室主管副职
//		int oficeDeputyDirector =0;//办公室主管副职
//		int secretary =0;//书记
		
		/*
		 * 查询当前办理节点
		 */
		String status = "";
		String sql = "select NODESTATUS from TELEPHONE_HANDLE where CHECKCODE = '"+userCode+"' and HID = '"+hid+"' and STATUS in ('未审核','被驳回') and ISDELETE = '0'";
		try {
			if(!hid.equals("0")){
				list = dbo.prepareQuery(sql, null);
				//获取节点状态
				status = list.get(0).get("NODESTATUS");
			}else{
				status = "0";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
	/*
	查询当前办理人角色
	 */
	public String getRole(String userCode) {
		DatabaseBo dbo = new DatabaseBo();
		//查询当前登录人角色
		String sql11 = "select ROLECODE from EAP_USERROLE where USERCODE='" + userCode + "'";
		List<HashMap<String, String>> roleList = new ArrayList<HashMap<String, String>>();
		String b = "";
		try {
			roleList = dbo.prepareQuery(sql11, null);

			//定义办公室人员
			int bSj = 0;
			for (int i = 0; i < roleList.size(); i++) {
				String role = roleList.get(i).get("ROLECODE");
				if (role.equals("convenienttelephone_shuji")) {
					bSj++;
				}
			}
			if(bSj>0){
				b = "true";
			}else{
				b = "false";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
}