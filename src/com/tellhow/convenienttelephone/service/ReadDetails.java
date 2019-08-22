package com.tellhow.convenienttelephone.service;

import com.alibaba.fastjson.JSONObject;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 描述:不同账号点击操作处理
 *
 * @Author zhangrui
 * @Date 2019/3/11 16:08
 */
public class ReadDetails {
    public HashMap<String,String> caozuo(LoginModel login,String zid,String nodestatus2,String xq,String taskstage,String bl) throws ServletException, IOException {
        System.out.println("id++++++++++++++:" + zid);
        System.out.println("获取nodestatus++++++++++++++:" + nodestatus2);
        System.out.println("获取nodestatus++++++++++++++:" + xq);
        System.out.println("taskstage++++++++++++++:" + taskstage);
        System.out.println("获取khandleper++++++++++++++:" + bl);

        ArrayList<HashMap<String, String>> list = null;
        List<HashMap<String,String>> Lists = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> map = new HashMap<String,String>();
        try {
            DatabaseBo dbo = new DatabaseBo();
            //定义节点状态
            int nodeStatus =0;
            // if(xq.equals("xq")){
            if(nodestatus2 != null && !"".equals(nodestatus2)){
                nodeStatus = Integer.parseInt(nodestatus2);
            }
            //查询当前登录人角色
            String sql11 = "select ROLECODE from EAP_USERROLE where USERCODE='"+login.getUserCode()+"'";
            List<HashMap<String,String>> roleList =dbo.prepareQuery(sql11, null);
            //定义书记
            int bSj = 0;
            for(int i=0;i<roleList.size();i++){
                String role = roleList.get(i).get("ROLECODE");
                if(role.equals("convenienttelephone_shuji")){
                    bSj++;
                }
            }
            if(bSj>0){//当前登陆人员为书记
                if(xq.equals("xq")){//点击操作进入详情
                    if(taskstage.equals("1")){//签批阶段
                        nodeStatus = 2;
                    }else if(taskstage.equals("2")){//办理阶段
                        if(bl.contains("s")){
                            nodeStatus = 6;
                        }else{
                            nodeStatus = 10;
                        }
                    }else if(taskstage.equals("3")){//检查阶段
                        nodeStatus = 11;
                    }else if(taskstage.equals("4") || taskstage.equals("5")){//审核阶段和确认阶段
                        nodeStatus = 15;
                    }else if(taskstage.equals("6")){//流程结束
                        nodeStatus = 16;
                    }
                }
            }
           /* }else{
            	String nodeStatusSql = "select NODESTATUS from TELEPHONE_HANDLE where CHECKCODE = '"+login.getUserCode()+"' and HID = '"+zid+"' and STATUS in ('未审核','被驳回')";
            	int nodeStatus1 = 0;
            	if(dbo.prepareQuery(nodeStatusSql,null).size()>0){
            		nodeStatus1 = Integer.parseInt(dbo.prepareQuery(nodeStatusSql,null).get(0).get("NODESTATUS"));//获得上一环节审批人节点状态
                    System.out.println("当前登录人节点状态:"+nodeStatus1);
                }
            	nodeStatus = nodeStatus1;
            }*/
            System.out.println("最后运用的节点状态为："+nodeStatus);
            List<HashMap<String,String>> MList = new ArrayList<HashMap<String,String>>();
            //获取是否为二次办理
            String again = "0";
            //定义办理人员
            String transactor = "";
            //查询主表信息
            String sql = "select * from TELEPHONE_BASIC where id = "+zid;
            if(zid != null && !"".equals(zid)){
                MList = dbo.prepareQuery(sql, null);
                map = MList.get(0);
                again = MList.get(0).get("AGAINBANLI");
                transactor = MList.get(0).get("TRANSACTOR");
            }
            //判断当前登录人是否为社区理长或者主管副职  convenienttelephone_baoju
            String userCode = login.getUserCode();
            //查询当前登录人角色
    		/*String sql11 = "select ROLECODE from EAP_USERROLE where USERCODE='"+userCode+"'";
    		List<HashMap<String,String>> roleList = new ArrayList<HashMap<String,String>>();
    		roleList = dbo.prepareQuery(sql11, null);
    		int ldCount =0;
    		for(int i=0;i<roleList.size();i++){
    			String role = roleList.get(i).get("ROLECODE");
    			if(role.equals("convenienttelephone_baoju")){
    				ldCount++;
    			}
    		}
    		if(ldCount>0){
    			//当前登录人为社区理长或者主管副职
    			if(nodeStatus == 7 || nodeStatus == 8 || nodeStatus == 9 || nodeStatus == 10){
					String selectSql11 = "select * from telephone_handle where hid = '"+zid+"' and checkcode = '"+userCode+"' and nodestatus = '10'";
					userCode = dbo.prepareQuery(selectSql11,null).get(0).get("ASSIGNERCODE");
    			}else if(nodeStatus == 3 || nodeStatus == 4 || nodeStatus == 5 || nodeStatus == 6){
					String selectSql11 = "select * from telephone_handle where hid = '"+zid+"' and checkcode = '"+userCode+"' and nodestatus = '6'";
					userCode = dbo.prepareQuery(selectSql11,null).get(0).get("ASSIGNERCODE");
					System.out.println("好气哟："+userCode);
    			}
    		}
			System.out.println("获取当前办理递交人code为:"+userCode);*/
            //办理社区/科室name
            String deptName = "";
            //定义办理人code
            String blcode = "";
            if(nodeStatus != 0){
                //根据当前登录人查询社区name和code
				/*String selectSql = "select name,code from EAP_DEPARTMENT where code = (select deptcode from EAP_ACCOUNT where code = '"+userCode+"')";
				deptName = dbo.prepareQuery(selectSql, null).get(0).get("NAME");
				String deptCode = dbo.prepareQuery(selectSql, null).get(0).get("CODE");*/
				/*if(bl != null && !"".equals(bl)){
					Boolean b ;
					char c = (bl.substring(0,1)).toCharArray()[0];
					System.out.println("截取第一个字未："+bl.substring(0,1));
					System.out.println("zhuanhuan截取第一个字未："+c);
					Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

						if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

								|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

								|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A

								|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION

								|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION

								|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

							b = true;

						}else{
							b = false;
						}
					System.out.println("查询是否未汉字："+b);
						if(b == true){
							deptName = bl;
							String codeSql = "select code from EAP_DEPARTMENT where name = '"+bl+"'";
							String deptCode = dbo.prepareQuery(codeSql,null).get(0).get("CODE");
							if(bSj>0){
								if(deptCode.contains("s")){
									nodeStatus = 6;
								}else{
									nodeStatus = 10;
								}
							}
						}else{
							if(bSj>0){
								if(bl.contains("s")){
									nodeStatus = 6;
								}else{
									nodeStatus = 10;
								}
							}
							String nameSql = "select name from EAP_DEPARTMENT where code = '"+bl+"'";
							deptName = dbo.prepareQuery(nameSql,null).get(0).get("NAME");
						}


				}*/
                //查询办理社区/科室name
                String nameSql = "select name from EAP_DEPARTMENT where code = '"+bl+"'";
                deptName = dbo.prepareQuery(nameSql,null).get(0).get("NAME");
                System.out.println("办理社区name为:"+deptName);
                //System.out.println("办理社区code为:"+deptCode);
                String[] str = transactor.split(",");
                //查询办理人员coed
                for(int i=0;i<str.length;i++){
                    String[] str1 = str[i].split("<");
                    String deptname = str1[0];
                    if(deptName.equals(deptname)){
                        blcode = (str[i].split(":")[0]).split("<")[1];
                        System.out.println("获取当前社区办理人code为:"+blcode);
                    }
                }
                //查询所有办理节点的详情信息
                for(int i=1;i<=nodeStatus;i++){
                    //如果节点状态为7，8，9，10（可是办理阶段）时，则不查询社区办理阶段信息
                    if(nodeStatus == 7 || nodeStatus == 8 || nodeStatus == 9 || nodeStatus == 10){//科室办理阶段
                        if(i != 3 && i != 4 && i != 5 && i != 6){//不查询社区办理阶段信息
                            String msql = "";
                            if(i>=3 && i<11 && bl != null && !"".equals(bl)){//查询办理阶段信息
                                msql = "select b.*,h.*,h.ATTACH hattach,h.REPLY hreply,h.SATISFACTION hsatisfaction,h.RESOLVE hresolve from TELEPHONE_BASIC b,TELEPHONE_HANDLE h  where b.id = " + zid + " and h.hid =  b.id and nodestatus = '" + i + "' and BL = '"+deptName+"'";
                            }else{//不是办理阶段
                                msql = "select b.*,h.*,h.ATTACH hattach,h.REPLY hreply,h.SATISFACTION hsatisfaction,h.RESOLVE hresolve from TELEPHONE_BASIC b,TELEPHONE_HANDLE h  where b.id = " + zid + " and h.hid =  b.id and nodestatus = '" + i + "'";
                            }
                            list = dbo.prepareQuery(msql, null);
                            if(list.size()>0){
                                map.put("checkidea"+i, list.get(0).get("CHECKIDEA"));
                                map.put("checkname"+i, list.get(0).get("CHECKNAME"));
                                map.put("checktime"+i, list.get(0).get("CHECKTIME"));
                                map.put("attach"+i, list.get(0).get("HATTACH"));
                                map.put("bl"+i, list.get(0).get("BL"));
                                map.put("reply"+i, list.get(0).get("HREPLY"));
                                map.put("satisfaction"+i, list.get(0).get("HSATISFACTION"));
                                map.put("resolve"+i, list.get(0).get("HRESOLVE"));
                                Lists.add(map);
                            }
                        }
                    }else{//除了科室办理阶段的信息查询
                        String msql = "";
                        if(nodeStatus>=11){//查询办公室检查阶段之前的所有详情信息
                            msql = "select b.*,h.*,h.ATTACH hattach,h.REPLY hreply,h.SATISFACTION hsatisfaction,h.RESOLVE hresolve from TELEPHONE_BASIC b,TELEPHONE_HANDLE h  where b.id = " + zid + " and h.hid =  b.id and nodestatus = '" + i + "'";
                        }else{
                            if((i == 3 || i == 4 || i == 5 || i == 6)  &&  (bl != null && !"".equals(bl))){
                                msql = "select b.*,h.*,h.ATTACH hattach ,h.REPLY hreply,h.SATISFACTION hsatisfaction,h.RESOLVE hresolve from TELEPHONE_BASIC b,TELEPHONE_HANDLE h  where b.id = " + zid + " and h.hid =  b.id and nodestatus = '" + i + "' and BL = '"+deptName+"'";
                            }else{
                                msql = "select b.*,h.*,h.ATTACH hattach,h.REPLY hreply,h.SATISFACTION hsatisfaction,h.RESOLVE hresolve from TELEPHONE_BASIC b,TELEPHONE_HANDLE h  where b.id = " + zid + " and h.hid =  b.id and nodestatus = '" + i + "'";
                            }
                        }
                        list = dbo.prepareQuery(msql, null);
                        if(list.size() > 0){
                            map.put("checkidea"+i, list.get(0).get("CHECKIDEA"));
                            map.put("checkname"+i, list.get(0).get("CHECKNAME"));
                            map.put("checktime"+i, list.get(0).get("CHECKTIME"));
                            map.put("attach"+i, list.get(0).get("HATTACH"));
                            map.put("bl"+i, list.get(0).get("BL"));
                            map.put("reply"+i, list.get(0).get("HREPLY"));
                            map.put("satisfaction"+i, list.get(0).get("HSATISFACTION"));
                            map.put("resolve"+i, list.get(0).get("HRESOLVE"));
                            Lists.add(map);
                        }
                    }
                }
            }
            if(again.equals("1")){//二次办理详情查询
                for(int i=17;i<=18;i++){//查询主管副职和社区理长的详情信息
                    String msql = "select b.*,h.*,h.ATTACH hattach,h.REPLY hreply,h.SATISFACTION hsatisfaction,h.RESOLVE hresolve from TELEPHONE_BASIC b,TELEPHONE_HANDLE h  where b.id = " + zid + " and h.hid =  b.id and nodestatus = '"+i+"'";
                    list = dbo.prepareQuery(msql, null);
                    if(list.size()>0){
                        map.put("checkidea"+i, list.get(0).get("CHECKIDEA"));
                        map.put("checkname"+i, list.get(0).get("CHECKNAME"));
                        map.put("checktime"+i, list.get(0).get("CHECKTIME"));
                        map.put("attach"+i, list.get(0).get("HATTACH"));
                        map.put("bl"+i, list.get(0).get("BL"));
                        map.put("reply"+i, list.get(0).get("HREPLY"));
                        map.put("satisfaction"+i, list.get(0).get("HSATISFACTION"));
                        map.put("resolve"+i, list.get(0).get("HRESOLVE"));
                        Lists.add(map);
                    }
                }
            }
            if(nodeStatus == 4 || nodeStatus == 8){//社区/科室办理人员节点
                map.put("handleper",login.getUserName());
                Lists.add(map);
            }
            map.put("nodestatus",nodestatus2);
            map.put("blcode",blcode);
            Lists.add(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("获取详情为："+Lists.toString());
        return Lists.get(0);
    }
}
