package com.tellhow.convenienttelephone.service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.siqiansoft.framework.AppData;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;

/*
 * 办公室登记提交阶段
 */
public class SubTask {
    InsertInfoUtil util = new InsertInfoUtil();
    public void subTask(HashMap<String, String> map, LoginModel login) {
        DatabaseBo dbo = new DatabaseBo();
        // 获取表单id
        String zid = map.get("id");
        System.out.println("获取表单zid：" + zid);
        // 来件人姓名 接收时间 回复时间 问题类型 派单方式 问题标题 任务编号 反映问题 附件 完成时限 完成期限 任务阶段
        /*String receivername = map.get("receivername");// 接收人姓名
        String handlekeshi = map.get("handlekeshi");// 所属科室
        String photo = map.get("sendertel");// 来件电话
        String sendername = map.get("sendername");// 来件人姓名
        String replytime = map.get("replytime");// 回复时间
        String problemtype = map.get("problemtype");// 问题类型
        String dispatch = map.get("dispatch");// 派单方式
        String taskid = map.get("taskid");// 任务编号
        String probleminfo = map.get("probleminfo");// 反映问题
        String statisticstype = map.get("statisticstype");// 统计分类
        */
        String finishtime = map.get("finishtime");// 完成时限FINISHTIME
        //String finishtime = //完成时限 = 接收时间+7天
        // String termtime = ;//完成期限:完成时限-当前时间
        String deptname = map.get("deptname");// 主办科室
        String xdeptname = map.get("xdeptname");// 协办科室
        String receivetime = map.get("receivetime");// 接收时间
        String title = map.get("title");// 问题标题
        String attach = map.get("attach");// 附件
        String taskstage = "1";// 任务阶段
        HashMap<String, String> map1 = new HashMap<String, String>();

        try {
            //根据协办科室name查询code
            String deptSql = "select code from EAP_DEPARTMENT where name = '"+xdeptname+"'";
            List<HashMap<String,String>> deptList = dbo.prepareQuery(deptSql,null);
            String xdeptCode = deptList.get(0).get("CODE");

            /*
                计算完成时限（根据接收时间加七天）
             */
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dd = sdf.parse(receivetime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dd);
            calendar.add(Calendar.DAY_OF_MONTH, 7);// 加7天
            System.out.println("获取完成时限为：" + sdf.format(calendar.getTime()));
           /* map1.put("receivername", receivername);
            map1.put("deptname", deptname);
            map1.put("sendername", sendername);
            map1.put("receivetime", receivetime);
            map1.put("replytime", replytime);
            map1.put("problemtype", problemtype);
            map1.put("dispatch", dispatch);
            map1.put("title", title);
            map1.put("taskid", taskid);
            map1.put("probleminfo", probleminfo);
            map1.put("attach", attach);
            map1.put("FINISHTIME", "剩" + finishtime + "天");
            map1.put("SENDERTEL", photo);
            map1.put("STATISTICSTYPE", statisticstype);*/
            map.put("XDEPTCODE", xdeptCode);//给主表添加协办科室code
            map.put("FINISHTIME", "剩" + finishtime + "天");
            map.put("TERMTIME", sdf.format(calendar.getTime()));//给主表添加完成时限
            map.put("taskstage", taskstage);//给主表提交时添加任务阶段的1（签批阶段）
            map.put("STARTTIME", sdf.format(new Date()));//给主表添加流程开始时间
            map.put("FAQINAME", login.getUserName());//给主表添加发起人name

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(new Date());
            //根据所属社区/科室查询对应的办理人code和name，和社区/科室的code，name
            List<HashMap<String, String>> bmap = selectB(deptname);
            //定义所有的社区/科室的code
            String deptcode = "";
            //定义所有的社区/科室的对应的办理人员name和code
            String bl = "";
            //给社区/科室对应的所有办理人员发公告
            for (int i = 0; i < bmap.size(); i++) {
                System.out.println("bmap.get(i).get('sname')" + bmap.get(i).get("sname"));
                System.out.println("bmap.get(i).get('kname')" + bmap.get(i).get("kname"));
                if (bmap.get(i).get("sname") != null && !"".equals((bmap.get(i).get("sname")))) {//判断是否为社区人员
                    //给对应的社区人员发公告
                    insertG(bmap.get(i).get("sname"), "", bmap.get(i).get("scode"), "", "d", login, title, attach,zid,"","");
                    deptcode += (bmap.get(i).get("deptcode")) + ",";
                    bl += (bmap.get(i).get("deptname")) + "<" + (bmap.get(i).get("sname")) + ":" + (bmap.get(i).get("scode")) + ">,";
                }
                if (bmap.get(i).get("kname") != null && !"".equals((bmap.get(i).get("kname")))) {//判断是否为科室人员
                    //给对应的科室人员发公告
                    insertG(bmap.get(i).get("kname"), "", bmap.get(i).get("kcode"), "", "d", login, title, attach,zid,"","");
                    deptcode += (bmap.get(i).get("deptcode")) + ",";
                    bl += (bmap.get(i).get("deptname")) + "<" + (bmap.get(i).get("kname")) + ":" + (bmap.get(i).get("kcode")) + ">,";
                }
            }
            System.out.println("查询所属科室与社区code为:" + deptcode);
            String nodestatus = "";
            if(deptcode.contains("s")){//社区
                nodestatus = "4";
            }else{
                nodestatus = "8";
            }

            System.out.println("查询所属科室与社区所对应的办理人为:" + bl);
            map.put("DEPTCODE", deptcode.substring(0, deptcode.length() - 1));//给主表添加所属社区/科室code
            map.put("TRANSACTOR", bl.substring(0, bl.length() - 1));//给主表添加所有的社区/科室的对应的办理人员name和code
            // 给社区对应人员发公告
//			if (deptname != null && !"".equals(deptname)) {
//				String codeSql = "select code from EAP_DEPARTMENT where name = '"
//						+ deptname + "'";
//				System.out.println("所属社区code为、、、、、、、、、、、："
//						+ dbo.prepareQuery(codeSql, null).get(0).get("CODE"));
//				map1.put("DEPTCODE", dbo.prepareQuery(codeSql, null).get(0)
//						.get("CODE"));
//				insertG(bmap.get("sname"), "", bmap.get("scode"), "", "d",
//						login, title, attach);
//			}
//			// 给科室对应人员发公告
//			if (handlekeshi != null && !"".equals(handlekeshi)) {
//				String codeSql1 = "select code from EAP_DEPARTMENT where name = '"
//						+ handlekeshi + "'";
//				map1.put("HANDLECODE", dbo.prepareQuery(codeSql1, null).get(0)
//						.get("CODE"));
//				insertG(bmap.get("kname"), "", bmap.get("kcode"), "", "d",
//						login, title, attach);
//			}
//
            /*
            给所属社区和所属科室发送资料传送
             */
            System.out.println("查询所属社区与科室为:" + deptname);
            String[] str = deptname.split(",");
            for (int i = 0; i < str.length; i++) {
                //查询所属社区/科室对应的邮箱code，name
                String syxSql = "SELECT CODE,NAME FROM EAP_ACCOUNT WHERE NAME LIKE '%" + str[i] + "%'";
                List<HashMap<String,String>> syxList = dbo.prepareQuery(syxSql, null);
                String syxcode = syxList.get(0).get("CODE");//社区/科室公共邮箱code
                String syxname = syxList.get(0).get("NAME");//社区/科室公共邮箱name
                System.out.println("社区公共邮箱code:" + syxcode);
                System.out.println("社区公共邮箱name:" + syxname);
                HashMap<String, String> soutmap = new HashMap<String, String>();
                soutmap.put("SENDER", login.getUserCode());
                soutmap.put("TAKETIME", time);
                soutmap.put("THEME", title);
                soutmap.put("ATTACHMENT", attach);
                soutmap.put("BODY", "见附件");
                soutmap.put("STATUSN", "1");
                soutmap.put("RECIPIENT", syxname + "<" + syxcode + ">");
                String rid = dbo.insert(soutmap, "OA_OUTBOX");
                HashMap<String, String> sinmap = new HashMap<String, String>();
                sinmap.put("RID", rid);
                sinmap.put("RECIPIENT", syxcode);
                dbo.insert(sinmap, "OA_INBOX");
            }
//			String syxSql = "select code,name from EAP_ACCOUNT where name like '%"+deptname+"%'";
//			String syxcode = dbo.prepareQuery(syxSql, null).get(0).get("CODE");//社区公共邮箱code
//			String syxname = dbo.prepareQuery(syxSql, null).get(0).get("NAME");//社区公共邮箱name
//			System.out.println("社区公共邮箱code:"+syxcode);
//			System.out.println("社区公共邮箱name:"+syxname);
//			String kyxSql = "select code,name from EAP_ACCOUNT where name like '%"+handlekeshi+"%'";
//			String kyxcode = dbo.prepareQuery(kyxSql, null).get(0).get("CODE");//科室公共邮箱code
//			String kyxname = dbo.prepareQuery(kyxSql, null).get(0).get("NAME");//科室公共邮箱name
//			System.out.println("科室公共邮箱code:"+kyxcode);
//			System.out.println("科室公共邮箱name:"+kyxname);
//			//给社区发送
//			HashMap<String,String> soutmap = new HashMap<String,String>();
//			soutmap.put("SENDER", login.getUserCode());
//			soutmap.put("TAKETIME", time);
//			soutmap.put("THEME", title);
//			soutmap.put("ATTACHMENT", attach);
//			soutmap.put("BODY", "见附件");
//			soutmap.put("STATUSN", "1");
//			soutmap.put("RECIPIENT", syxname+"<"+syxcode+">");
//			String rid = dbo.insert(soutmap, "OA_OUTBOX");
//			HashMap<String,String> sinmap = new HashMap<String,String>();
//			sinmap.put("RID", rid);
//			sinmap.put("RECIPIENT", syxcode);
//			dbo.insert(sinmap, "OA_INBOX");
//			//给科室发送
//			HashMap<String,String> koutmap = new HashMap<String,String>();
//			koutmap.put("SENDER", login.getUserCode());
//			koutmap.put("TAKETIME", time);
//			koutmap.put("THEME", title);
//			koutmap.put("ATTACHMENT", attach);
//			koutmap.put("BODY", "见附件");
//			koutmap.put("STATUSN", "1");
//			koutmap.put("RECIPIENT", kyxname+"<"+kyxcode+">");
//			String krid = dbo.insert(koutmap, "OA_OUTBOX");
//			HashMap<String,String> kinmap = new HashMap<String,String>();
//			kinmap.put("RID", krid);
//			kinmap.put("RECIPIENT", kyxcode);
//			dbo.insert(kinmap, "OA_INBOX");
            // 查询办公室主管副职
            String roleSql = "SELECT * FROM EAP_ACCOUNT WHERE CODE IN (SELECT USERCODE FROM EAP_USERROLE WHERE ROLECODE = 'c71')";
            List<HashMap<String, String>> roleList = dbo.prepareQuery(roleSql, null);
            String id="";
            if (zid == null || "".equals(zid)) {//判断该条主数据是否存在；没有则进行添加，有则进行修改
                //map.put("AGAINBANLI", "0");//给主表添加是否为二次办理（0：未二次办理，1：二次办理）
                System.out.println("000000000000000000000000000000AGAINBANLI====:"+map.get("AGAINBANLI"));
                String hid = dbo.insert(map, "TELEPHONE_BASIC");
                id = hid;
                /*
                 * 添加登记人员子表信息
                 */
                HashMap<String, String> map2 = new HashMap<String, String>();
                map2.put("HID", hid);//主表id
                map2.put("CHECKNAME", login.getUserName());//登记人name
                map2.put("CHECKCODE", login.getUserCode());//登记人code
                map2.put("CHECKTIME", time);//登记时间
                map2.put("CHECKIDEA", "登记");
                map2.put("PROBLEMTYPE", "签批阶段");//任务阶段
                map2.put("NODESTATUS", "0");//节点状态
                map2.put("STATUS", "未审核");//审批状态
                map2.put("ISDELETE", "0");//是否被删除（0：没有删除，1：删除）
                dbo.insert(map2, "TELEPHONE_HANDLE");
                /*
                 * 添加办公室主管副职子表信息
                 */

                HashMap<String, String> map3 = new HashMap<String, String>();
                map3.put("HID", hid);
                map3.put("CHECKNAME", roleList.get(0).get("NAME"));
                map3.put("CHECKCODE", roleList.get(0).get("CODE"));
                map3.put("PROBLEMTYPE", "签批阶段");
                map3.put("NODESTATUS", "1");
                map3.put("STATUS", "未审核");
                map3.put("ASSIGNERCODE", login.getUserCode());
                map3.put("ASSIGNERNAME", login.getUserName());
                map3.put("ISDELETE", "0");
                dbo.insert(map3, "TELEPHONE_HANDLE");
                /*
                 * 添加日志表信息
                 */
                HashMap<String, String> map4 = new HashMap<String, String>();
                map4.put("BASICID", hid);
                map4.put("SENDERNAME", login.getUserName());
                map4.put("SENDERCODE", login.getUserCode());
                map4.put("OPERATION", "提交任务申请");
                map4.put("UNDERTAKENAME", roleList.get(0).get("NAME"));
                map4.put("UNDERTAKECODE", roleList.get(0).get("CODE"));
                map4.put("CONTENT", "登记申请");
                map4.put("CHANGETIME", time);
                map4.put("UNDERTAKENAME", "0");
                dbo.insert(map4, "TELEPHONE_LOG");
                HashMap<String, String> map5 = new HashMap<String, String>();
                map5.put("BASICID", hid);
                map5.put("SENDERNAME", roleList.get(0).get("NAME"));
                map5.put("SENDERCODE", roleList.get(0).get("CODE"));
                map5.put("OPERATION", "处理中");
                map5.put("CONTENT", "正在办理中...");
                map5.put("CHANGETIME", time);
                map5.put("UNDERTAKENAME", "1");
                dbo.insert(map5, "TELEPHONE_LOG");
            } else {//该条主表信息存在则进行修改
                id = zid;
                map.put("ID", zid);
                /*if (handlekeshi == null || "".equals(handlekeshi)) {
                    map.put("HANDLECODE", "");
                }*/
                if (deptname == null || "".equals(deptname)) {
                    map.put("DEPTCODE", "");
                }
                dbo.update(map, "TELEPHONE_BASIC");
                //将登记人员和下一环节审批人员子表状态修改为未审核
                String updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '未审核' WHERE HID = '"+ zid + "' AND NODESTATUS IN ('0','1') AND ISDELETE = '0'";
                dbo.prepareUpdate(updateSql, null);
                String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKTIME = '"+time+"',CHECKIDEA = '登记' WHERE HID = '"+ zid + "' AND NODESTATUS = '0' AND ISDELETE = '0'";
                dbo.prepareUpdate(updateSql1, null);
                //向日志表添加信息
                util.addLogoInfo(zid, login, "正在处理中...",roleList.get(0).get("NAME"), roleList.get(0).get("CODE"),"处理中","1","");
                String updateSql0  = "update TELEPHONE_LOG set OPERATION = '已办',CONTENT = '提交任务申请',CHANGETIME = '"+df.format(new Date())+"' where BASICID = '"+zid+"' and UNDERTAKENAME = '0' and OPERATION = '处理中'";
                dbo.prepareUpdate(updateSql0,null);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /*
    发公告
     */
    public void insertG(String name, String name1, String code, String code1,String ac, LoginModel login, String title, String attach,String zid,String xz,String nodestatus) {
        DatabaseBo dbo = new DatabaseBo();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(new Date());
        // 给OA_TEMPORARYANNOUNCEMENT表赋值
        HashMap<String, String> gmap = new HashMap<String, String>();
        if(xz != null && !"".equals(xz)){
            gmap.put("DETAILS", "<a href='../convenienttelephone/taskmanagement.cmd?$ACTION=c02&id="+zid+"&nodestatus="+nodestatus+"&bl="+xz+"&taskstage=2&type=0'>"+title+"</a>");//type0:公告
        }else{
            gmap.put("DETAILS", title);
        }
        gmap.put("TITLE", "便民电话通知公告");
        gmap.put("PUBTIME", time);
        gmap.put("STATE", "1");
        gmap.put("RID", zid);
        gmap.put("ATTACHMENT", attach);
        gmap.put("CREATECODE", login.getUserCode());
        gmap.put("CREATETIME", time);
        gmap.put("TYPE", "11");
        gmap.put("PUBDEPT", "办公室");
        String tempid;
        try {
            tempid = dbo.insert(gmap, "OA_TEMPORARYANNOUNCEMENT");
            // 给OA_TEMPORARYLOGS表赋值
            HashMap<String, String> gmap1 = new HashMap<String, String>();
            gmap1.put("TEMPORARYID", tempid);
            gmap1.put("TEMPORARYPEOPLE", name);
            gmap1.put("TEMPORARYCODE", code);
            dbo.insert(gmap1, "OA_TEMPORARYLOGS");
            /*if (ac.equals("zr")) {
                HashMap<String, String> gmap2 = new HashMap<String, String>();
                gmap2.put("TEMPORARYID", tempid);
                gmap2.put("TEMPORARYPEOPLE", name1);
                gmap2.put("TEMPORARYCODE", code1);
                dbo.insert(gmap2, "OA_TEMPORARYLOGS");
            }*/

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    // 查询对应所属社区/科室对应的办理人code，name.和所属社区/科室的code和name
    public List<HashMap<String, String>> selectB(String deptname) {
        System.out.println("主办科室为##########"+deptname);
        DatabaseBo dbo = new DatabaseBo();
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            // 查询所有所属社区/科室name，
            String[] str = new String[deptname.length()];
            str = deptname.split(",");
            if (deptname != null && !"".equals(deptname)) {
                for (int i = 0; i < str.length; i++) {
                    Properties prop = new Properties();
                    InputStream in = new BufferedInputStream(new FileInputStream(AppData.getInstance().getSystemPath()+"WEB-INF/classes/defaultPersonnel.properties"));
                    prop.load(new InputStreamReader(in, "utf-8"));
                    System.out.println("主办科室为22222222222222##########"+str[i]);
                    String value = prop.getProperty(str[i]);
                    HashMap<String, String> map = new HashMap<String, String>();
                    System.out.println("获取所有值为："+value);
                    map.put("scode", value.split(",")[1]);//scode,默认办理人员code
                    map.put("sname", value.split(",")[2]);//默认办理人员name
                    map.put("deptcode", value.split(",")[0]);//所属社区或科室code
                    map.put("deptname", str[i]);//所属社区或科室name
                    list.add(map);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("查询list集合为:" + list.toString());
        return list;
    }
}
