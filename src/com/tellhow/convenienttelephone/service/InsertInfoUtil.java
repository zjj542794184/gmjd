package com.tellhow.convenienttelephone.service;

import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import org.htmlparser.filters.HasSiblingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:向子表和日志表添加数据并且修改本条数据和上一条数据的审核状态
 *
 * @Author zhangrui
 * @Date 2019/3/12 19:54
 */
public class InsertInfoUtil {
    DatabaseBo dbo = new DatabaseBo();

    /**
     * 向子表添加数据
     *
     * @param zid         主表id
     * @param name        下一环节办理人name
     * @param code        下一环节办理人code
     * @param nodeStatus  节点状态
     * @param problemtype 任务阶段
     */
    public void addTable(String zid, String name, String code, String nodeStatus, String problemtype, LoginModel login, String bl, String blCode) {
        try {
            HashMap<String, String> map1 = new HashMap<String, String>();
            map1.put("HID", zid);
            map1.put("CHECKNAME", name);//下一环节审批人
            map1.put("CHECKCODE", code);
            map1.put("PROBLEMTYPE", problemtype);
            map1.put("NODESTATUS", nodeStatus);//主任审批节点状态
            map1.put("STATUS", "未审核");
            map1.put("ASSIGNERNAME", login.getUserName());
            map1.put("ASSIGNERCODE", login.getUserCode());
            map1.put("BL", bl);
            map1.put("BLCODE", blCode);
            map1.put("ISDELETE", "0");
            dbo.insert(map1, "TELEPHONE_HANDLE");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改本环节办理人的批示和审批时间
     *
     * @param checkidea  批示
     * @param zid        主表id
     * @param nodestatus 节点状态
     */
    public void updateInfo(String checkidea, String zid, String nodestatus, String bl) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(new Date());
            if (checkidea == null || "".equals(checkidea)) {
                checkidea = "同意";
            }
            String updateSql1 = "";
            if (bl != null && !"".equals(bl)) {
                updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKIDEA = '" + checkidea + "',CHECKTIME = '" + time + "' WHERE HID = '" + zid + "' AND NODESTATUS = '" + nodestatus + "' AND BL = '" + bl + "' AND ISDELETE = '0'";
            } else {
                updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKIDEA = '" + checkidea + "',CHECKTIME = '" + time + "' WHERE HID = '" + zid + "' AND NODESTATUS = '" + nodestatus + "' AND ISDELETE = '0'";
            }

            dbo.prepareUpdate(updateSql1, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改上一环节办理人的状态为已审核
     *
     * @param zid        主表id
     * @param nodestatus 节点状态
     */
    public void updatePreInfo(String zid, String nodestatus, String deptName) {
        try {
            String updateSql = "";
            if (deptName != null && !"".equals(deptName)) {
                updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '已审核' WHERE HID = '" + zid + "' AND NODESTATUS = '" + nodestatus + "' AND BL = '" + deptName + "' AND ISDELETE = '0'";
            } else {
                updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '已审核' WHERE HID = '" + zid + "' AND NODESTATUS = '" + nodestatus + "' AND ISDELETE = '0'";
            }

            dbo.prepareUpdate(updateSql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 向日志表添加信息
     *
     * @param zid     主表id
     * @param login   LoginModel对象
     * @param content 审批意见
     * @param name    下一环节办理人name
     * @param code    下一环节办理人code
     */
    public void addLogoInfo(String zid, LoginModel login, String content, String name, String code, String operation,String nodestatus,String blcode) {
        try {
            if (content == null || "".equals(content)) {
                if (operation.length() > 2) {
                    if (Integer.parseInt((operation.split(",")[1])) > 2) {
                        content = "不同意";
                    } else {
                        content = " ";
                    }

                } else {
                    content = "同意";
                }

            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(new Date());
            HashMap<String, String> map2 = new HashMap<String, String>();
            map2.put("BASICID", zid);
            map2.put("SENDERNAME", name);
            map2.put("SENDERCODE", code);
            map2.put("OPERATION", operation);
            map2.put("CONTENT", content);
            map2.put("CHANGETIME", time);
            map2.put("UNDERTAKENAME", nodestatus);
            map2.put("UNDERTAKECODE", blcode);
            dbo.insert(map2, "TELEPHONE_LOG");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 驳回操作，修改审批状态
     */
    public void reject(String nodeStatus,String zid,String idea,LoginModel login,String bl){
        String updateSql = "";
        try {
            String value = "";
            if(bl != null && !"".equals(bl)){//判断是否为办理阶段
                value = " AND BL = '"+bl+"'";
            }else{
                value = "";
            }
            //将上一节点状态和本节点状态审批人的状态修改为被驳回
            for (int i = (Integer.parseInt(nodeStatus)-1); i <= (Integer.parseInt(nodeStatus)); i++) {
                updateSql = "UPDATE TELEPHONE_HANDLE SET STATUS = '被驳回' WHERE HID = '" + zid + "' AND NODESTATUS = '" + i + "' AND ISDELETE = '0' "+value;
                dbo.prepareUpdate(updateSql, null);
            }
            ////修改本环节审批意见和审批时间
            String updateSql1 = "UPDATE TELEPHONE_HANDLE SET CHECKIDEA = '"+idea+"',CHECKTIME = '' WHERE HID = '"+zid+"' AND NODESTATUS = '"+nodeStatus+"' AND ISDELETE = '0' "+value;
            dbo.prepareUpdate(updateSql1, null);
            //修改上一环节的递交人为当前登录人
            String updateSql2 = "UPDATE TELEPHONE_HANDLE SET ASSIGNERNAME = '"+login.getUserName()+"',ASSIGNERCODE = '"+login.getUserCode()+"' WHERE HID = '"+zid+"' AND NODESTATUS = '"+(Integer.parseInt(nodeStatus)-1)+"' AND ISDELETE = '0' "+value;
            dbo.prepareUpdate(updateSql2, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 验证任务编号的唯一性
     *
     * @param taskId
     * @return
     */
    public Boolean getTaskId(String taskId) {

        //查询主表的任务编号
        String queryTaskid = "SELECT TASKID FROM TELEPHONE_BASIC";
        ArrayList<HashMap<String, String>> lists = null;//所有的任务表号
        try {
            lists = dbo.prepareQuery(queryTaskid, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Boolean flag = true;
        for (Map map : lists) {
            if (taskId.equals(map.get("TASKID"))) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 完成期限定时器
     */
    public void timer() {
        //查询所有未办结登记任务
        String sql = "SELECT ID,FINISHTIME FROM TELEPHONE_BASIC WHERE TASKSTAGE != '6'";
        try {
            List<HashMap<String, String>> list = dbo.prepareQuery(sql, null);
            for (int i = 0; i < list.size(); i++) {
                //获取完成期限
                String termtime = list.get(i).get("FINISHTIME");
                //System.out.println("获取完成期限为：" + termtime);
                //获取剩余天数
                String dayNum = termtime.substring(1, 2);
                //System.out.println("获取剩余天数为：" + dayNum);
                //获取完成期限为剩还是超
                String term = termtime.substring(0, 1);
                //System.out.println("获取完成期限的剩超值为：" + term);
                if (term.equals("剩")) {
                    //将天数减一
                    if ((Integer.parseInt(dayNum) - 1) >= 0) {
                        dayNum = "剩" + (Integer.parseInt(dayNum) - 1) + "天";
                    } else {
                        dayNum = "超" + Math.abs(Integer.parseInt(dayNum) - 1) + "天";
                    }
                    // System.out.println("最后往数据库存储的数据为：" + dayNum);
                    String updateSql = "UPDATE TELEPHONE_BASIC SET FINISHTIME = '" + dayNum + "' WHERE ID = '" + list.get(i).get("ID") + "'";
                    dbo.prepareUpdate(updateSql, null);
                } else if (term.equals("超")) {
                    //将天数加一
                    dayNum = "超" + (Integer.parseInt(dayNum) + 1) + "天";
                    //System.out.println("最后往数据库存储的数据为：" + dayNum);
                    String updateSql = "UPDATE TELEPHONE_BASIC SET FINISHTIME = '" + dayNum + "' WHERE ID = '" + list.get(i).get("ID") + "'";
                    dbo.prepareUpdate(updateSql, null);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * 时间提醒定时器
     */
    public void RemindTimer() {
        DatabaseBo dbo = new DatabaseBo();

        //查询所有未解决和有时间提醒的数据
        String sql = "SELECT * FROM TELEPHONE_BASIC WHERE RESOLVE = '未解决' AND TIMEREMIND IS NOT NULL";
        try {
            List<HashMap<String, String>> list = dbo.prepareQuery(sql, null);
            for (int i = 0; i < list.size(); i++) {
                //获取时间提醒的时间
                String remindTime = list.get(i).get("TIMEREMIND");
                //System.out.println("获取时间提醒的时间:" + remindTime);
                //获取流程办结时间
                String overtime = list.get(i).get("OVERTIME");
                //System.out.println("获取时间提醒的时间:" + remindTime);
                //获取当前时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String nowTime = df.format(new Date());
                //System.out.println("获取当前时间:" + nowTime);
                //截取时间提醒的日期
                String remind = remindTime.substring(0, 10);
                //System.out.println("截取时间提醒的日期:" + remind);
                if (nowTime.equals(remind)) {
                    //时间提醒到了，则发公告
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = sdf.format(new Date());
                    //获取问题标题
                    String title = list.get(i).get("TITLE");
                    //获取工单编号
                    String taskid = list.get(i).get("TASKID");
                    //获取附件
                    String attach = list.get(i).get("ATTACH");
                    //获取登记人
                    String handleSql = "SELECT CHECKCODE FROM TELEPHONE_HANDLE WHERE HID = '" + list.get(i).get("ID") + "' AND NODESTATUS = '0' AND ISDELETE = '0'";

                    //给OA_TEMPORARYANNOUNCEMENT表赋值
                    HashMap<String, String> gmap = new HashMap<String, String>();
                    gmap.put("TITLE", "便民电话通知公告");
                    gmap.put("PUBTIME", time);
                    gmap.put("STATE", "1");
                    gmap.put("DETAILS", "您有一条" + overtime + "的便民电话，单号为A-" + taskid + "，请及时处理。");
                    gmap.put("ATTACHMENT", attach);
                    gmap.put("CREATECODE", dbo.prepareQuery(handleSql, null).get(0).get("CHECKCODE"));
                    gmap.put("CREATETIME", time);
                    gmap.put("TYPE", "4");
                    gmap.put("PUBDEPT", "办公室");
                    String tempid = dbo.insert(gmap, "OA_TEMPORARYANNOUNCEMENT");
                    //System.out.println("公告表id为：" + tempid);
                    //查询社区办理人code
                    String sCodeSql = "SELECT CHECKCODE,CHECKNAME,OA_TEMPORARYLOGS FROM TELEPHONE_HANDLE WHERE HID = '" + list.get(i).get("ID") + "' AND NODESTATUS = '4' AND ISDELETE = '0'";
                    if (dbo.prepareQuery(sCodeSql, null).size() > 0) {
                        //给OA_TEMPORARYLOGS表赋值
                        HashMap<String, String> gmap1 = new HashMap<String, String>();
                        gmap1.put("TEMPORARYID", tempid);
                        gmap1.put("TEMPORARYPEOPLE", dbo.prepareQuery(sCodeSql, null).get(0).get("CHECKNAME"));
                        gmap1.put("TEMPORARYCODE", dbo.prepareQuery(sCodeSql, null).get(0).get("CHECKCODE"));
                        dbo.insert(gmap1, "OA_TEMPORARYLOGS");
                        System.out.println("公告日志表id为：" + dbo.insert(gmap1, "OA_TEMPORARYLOGS"));
                    }
                    //查询社区办理人code
                    String kCodeSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '" + list.get(i).get("ID") + "' AND NODESTATUS = '8' AND ISDELETE = '0'";
                    if (dbo.prepareQuery(kCodeSql, null).size() > 0) {
                        //给OA_TEMPORARYLOGS表赋值
                        HashMap<String, String> gmap1 = new HashMap<String, String>();
                        gmap1.put("TEMPORARYID", tempid);
                        gmap1.put("TEMPORARYPEOPLE", dbo.prepareQuery(kCodeSql, null).get(0).get("CHECKNAME"));
                        gmap1.put("TEMPORARYCODE", dbo.prepareQuery(kCodeSql, null).get(0).get("CHECKCODE"));
                        dbo.insert(gmap1, "OA_TEMPORARYLOGS");
                        System.out.println("公告日志表id为：" + dbo.insert(gmap1, "OA_TEMPORARYLOGS"));
                    }
                }

            }
            System.out.println("没有查询到数据、、、、");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /*
    查询所有办理社区
     */
    public List<HashMap<String, String>> selectSheQu(HttpServletRequest request, HttpServletResponse response) {
        DatabaseBo dbo = new DatabaseBo();
        //获取主表id
        String zid = request.getParameter("basicId");
        //获取ac值
        String ac = request.getParameter("ac");
        List slist = new ArrayList();
        List klist = new ArrayList();
        System.out.println("获取主表id为：" + zid);
        System.out.println("获取ac值为：" + ac);
        String sql = "SELECT HANDLEDEPTCODE FROM TELEPHONE_BASIC WHERE ID = '" + zid + "'";
        try {
            List<HashMap<String, String>> deptList = dbo.prepareQuery(sql, null);
            String deptCode = deptList.get(0).get("HANDLEDEPTCODE");
            System.out.println("查询deptList为：" + deptList.get(0).get("HANDLEDEPTCODE"));
            String[] str = new String[deptList.get(0).get("HANDLEDEPTCODE").length()];
            str = deptCode.split(",");

            for (int i = 0; i < str.length; i++) {
                if (str[i].contains("s")) {
                    HashMap<String,String> smap = new HashMap<String, String>();
                    String deptNameSql = "SELECT NAME FROM EAP_DEPARTMENT WHERE CODE = '" + str[i] + "'";
                    String deptName = dbo.prepareQuery(deptNameSql, null).get(0).get("NAME");
                    System.out.println("社区deptname为：" + deptName);
                    smap.put("deptcode",str[i]);
                    smap.put("deptname",deptName);
                    slist.add(smap);
                } else if (str[i].contains("b")) {
                    HashMap<String,String> kmap = new HashMap<String, String>();
                    String deptNameSql = "SELECT NAME FROM EAP_DEPARTMENT WHERE CODE = '" + str[i] + "'";
                    String deptName = dbo.prepareQuery(deptNameSql, null).get(0).get("NAME");
                    System.out.println("科室deptname为：" + deptName);
                    kmap.put("deptcode",str[i]);
                    kmap.put("deptname",deptName);
                    klist.add(kmap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("社区为：" + slist.toString());
        System.out.println("科室为：" + klist.toString());
        if (ac.equals("shequ")) {
            return slist;
        } else {
            return klist;
        }
    }

    /*
   查询所有办理社区
    */
    public List<HashMap<String, String>> selectKeShi(HttpServletRequest request, HttpServletResponse response) {
        DatabaseBo dbo = new DatabaseBo();
        //获取主表id
        String zid = request.getParameter("basicId");
        //获取ac值
        String ac = request.getParameter("ac");
        List slist = new ArrayList();
        List klist = new ArrayList();
        System.out.println("获取主表id为：" + zid);
        System.out.println("获取ac值为：" + ac);
        String sql = "SELECT HANDLEDEPTCODE FROM TELEPHONE_BASIC WHERE ID = '" + zid + "'";
        try {
            List<HashMap<String, String>> deptList = dbo.prepareQuery(sql, null);
            String deptCode = deptList.get(0).get("HANDLEDEPTCODE");
            System.out.println("查询deptList为：" + deptList.get(0).get("HANDLEDEPTCODE"));
            String[] str = new String[deptList.get(0).get("HANDLEDEPTCODE").length()];
            str = deptCode.split(",");

            for (int i = 0; i < str.length; i++) {
                if (str[i].contains("s")) {
                    HashMap<String,String> map = new HashMap<String, String>();
                    String deptNameSql = "SELECT NAME FROM EAP_DEPARTMENT WHERE CODE = '" + str[i] + "'";
                    String deptName = dbo.prepareQuery(deptNameSql, null).get(0).get("NAME");
                    System.out.println("社区deptname为：" + deptName);
                    map.put("deptcode",str[i]);
                    map.put("deptname",deptName);
                    slist.add(map);
                } else if (str[i].contains("b")) {
                    HashMap<String,String> map = new HashMap<String, String>();
                    String deptNameSql = "SELECT NAME FROM EAP_DEPARTMENT WHERE CODE = '" + str[i] + "'";
                    String deptName = dbo.prepareQuery(deptNameSql, null).get(0).get("NAME");
                    System.out.println("科室deptname为：" + deptName);
                    map.put("deptcode",str[i]);
                    map.put("deptname",deptName);
                    klist.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("社区为：" + slist.toString());
        System.out.println("科室为：" + klist.toString());
        if (ac.equals("keshi")) {
            return klist;
        } else {
            return slist;
        }
    }

    /*
    查询社区对应的子表信息
     */
    public HashMap<String, String> selectContent(HttpServletRequest request, HttpServletResponse response) {
        DatabaseBo dbo = new DatabaseBo();
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        //获取主表id
        String zid = request.getParameter("basicId");
        //获取办理社区name
        String name = request.getParameter("name");
        //获取ac值
        String ac = request.getParameter("ac");
        System.out.println("获取主表id为：" + zid);
        System.out.println("获取ac值为：" + ac);
        System.out.println("获取办理社区name值为：" + name);
        String nodesatus = "";
        int j = 0;
        if (ac.equals("sqContent")) {
            nodesatus = "3,4,5,6";
            j = 7;
        } else if (ac.equals("ksContent")) {
            nodesatus = "7,8,9,10";
            j = 11;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        String sql = "SELECT * FROM TELEPHONE_BASIC WHERE ID = '" + zid + "'";

        try {
            //list = dbo.prepareQuery(sql,null);
            String[] str = new String[nodesatus.length()];
            str = nodesatus.split(",");
            for (int i = Integer.parseInt(str[0]); i < j; i++) {

                String selectSql = "SELECT * FROM TELEPHONE_HANDLE WHERE HID = '" + zid + "' AND NODESTATUS = '" + i + "' AND BLCODE = '" + name + "' AND ISDELETE = '0'";
                List<HashMap<String, String>> list1 = dbo.prepareQuery(selectSql, null);
                if (list1.size() == 0) {
                    break;
                } else {
                    map.put("checkidea" + i, list1.get(0).get("CHECKIDEA"));
                    map.put("checkname" + i, list1.get(0).get("CHECKNAME"));
                    map.put("checktime" + i, list1.get(0).get("CHECKTIME"));
                    map.put("attach" + i, list1.get(0).get("ATTACH"));
                    map.put("bl" + i, list1.get(0).get("BL"));
                    map.put("reply" + i, list1.get(0).get("REPLY"));
                    map.put("satisfaction" + i, list1.get(0).get("SATISFACTION"));
                    map.put("resolve" + i, list1.get(0).get("RESOLVE"));
                }

            }
            list.add(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("获取办理阶段信息为：" + list.toString());
        return list.get(0);
    }
}
