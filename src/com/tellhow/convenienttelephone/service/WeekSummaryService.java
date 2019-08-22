package com.tellhow.convenienttelephone.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.siqiansoft.framework.bo.DatabaseBo;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WeekSummaryService {
    public JSON WeekSummaryServices(String operation,String year,String week){
        DatabaseBo databaseBo = new DatabaseBo();

        //上下切换返回集合
        HashMap<String, Object> operation_time = new HashMap<String, Object>();
        Calendar c = Calendar.getInstance();
        int[] operation_time_info=new int[3];
        String date="";
        int year_now = 0;
        int month_now = c.get(Calendar.MONTH)+1;
        int weekOfYear_now = 0;

        if(operation.equals("")){
            year_now = c.get(Calendar.YEAR);
            weekOfYear_now = c.get(Calendar.WEEK_OF_YEAR);
        }else if(operation.equals("prev-week")){
            year_now =Integer.parseInt(year);
            weekOfYear_now = Integer.parseInt(week);
            if(weekOfYear_now<=1){
                year_now=year_now-1;
                weekOfYear_now=52;
            }else{
                year_now= Integer.parseInt(year);
                weekOfYear_now=Integer.parseInt(week)-1;
            }
        }else if(operation.equals("next-week")){
            year_now =Integer.parseInt(year);
            weekOfYear_now = Integer.parseInt(week);
            if(weekOfYear_now>=52){
                year_now=year_now+1;
                weekOfYear_now=1;
            }
            else{
                year_now= Integer.parseInt(year);
                weekOfYear_now=Integer.parseInt(week)+1;
            }
        }
        c.set(Calendar.YEAR, year_now);
        c.set(Calendar.WEEK_OF_YEAR, weekOfYear_now);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

        SimpleDateFormat sdf_now = new SimpleDateFormat("yyyy-MM-dd");
        date=sdf_now.format( c.getTime());
        operation_time_info[0]=year_now;
        operation_time_info[1]=month_now;
        operation_time_info[2]=weekOfYear_now;
        //根据当前时间获得本月起始时间
        String[] arr = new String[2];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            c.setTime(sdf.parse(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        int d = 0;
        if(c.get(Calendar.DAY_OF_WEEK)==1){
            d = -6;
        }else{
            d = 1-c.get(Calendar.DAY_OF_WEEK);
        }
        c.add(Calendar.DAY_OF_WEEK, d);//所在周开始日期
        arr[0]=sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_WEEK, 7);//所在周结束日期
        arr[1]=sdf.format(c.getTime());
/*时间结束 此处再不更改时间阶段的话不需要改*/


        //相同社区数量求和
        String sql_nameCon = "SELECT h.bl as HANDLEDEPTNAME ,count(HANDLEDEPTNAME) FROM " +
                "TELEPHONE_BASIC B,TELEPHONE_HANDLE H WHERE b.CREATETIME " +
                "between to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  " +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss') AND   " +
                "B.ID = H.HID   AND H.NODESTATUS='4'  AND h.ISDELETE='0' group by h.bl order by h.bl desc";
        //总办结数量
        String sql_nameConOver = "select HANDLEDEPTNAME, sum(count) ,bb from (\n" +
                "select * from (\n" +
                "select h.bl as HANDLEDEPTNAME ,h.blcode as bb, TO_CHAR(count(HANDLEDEPTNAME)) as count from   " +
                "TELEPHONE_BASIC B,TELEPHONE_HANDLE H where  b.TASKSTAGE=6\n" +
                " and to_date(b.OVERTIME,'yyyy-mm-dd hh24:mi:ss') between " +
                "to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  \n" +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss') AND   B.ID = H.HID  AND h.ISDELETE='0' AND H.NODESTATUS='4'\n" +
                "group by h.bl,h.blcode order by h.bl desc)\n" +
                "union all \n" +
                "select * from (\n" +
                "select h.bl as HANDLEDEPTNAME,h.blcode as bb,'0' as count from  " +
                "TELEPHONE_BASIC B,TELEPHONE_HANDLE H where HANDLEDEPTNAME not in (\n" +
                "select h.bl as HANDLEDEPTNAME from  TELEPHONE_BASIC B,TELEPHONE_HANDLE H where b.TASKSTAGE=6\n" +
                "AND   B.ID = H.HID AND h.ISDELETE='0'  AND H.NODESTATUS='4'\n" +
                "and to_date(b.OVERTIME,'yyyy-mm-dd hh24:mi:ss') between\n" +
                "to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  \n" +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss'))  \n" +
                "group by h.bl ,h.blcode order by h.bl desc)\n" +
                ")\n" +
                "where bb like  's%'  and HANDLEDEPTNAME in (select *from(\n" +
                "SELECT H2.bl   FROM \n" +
                "TELEPHONE_BASIC B2,TELEPHONE_HANDLE H2 WHERE b2.CREATETIME \n" +
                "between to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and \n" +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss') AND   \n" +
                "B2.ID = H2.HID   AND H2.NODESTATUS='4' AND H2.ISDELETE='0' group by H2.bl order by H2.bl desc))\n" +
                "group by HANDLEDEPTNAME,bb order by HANDLEDEPTNAME desc";

        //响应数量
        String sql_nameConOver_res =  "select HANDLEDEPTNAME, sum(count) ,bb from (\n" +
                "select * from (\n" +
                "select h.bl as HANDLEDEPTNAME ,h.blcode as bb, TO_CHAR(count(HANDLEDEPTNAME)) as count from   " +
                "TELEPHONE_BASIC B,TELEPHONE_HANDLE H where  b.TASKSTAGE=6 and b.BREPLY='已回复'\n" +
                " and to_date(b.OVERTIME,'yyyy-mm-dd hh24:mi:ss') between " +
                "to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  \n" +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss') AND   B.ID = H.HID   AND H.NODESTATUS='4'\n" +
                "AND H.ISDELETE='0' group by h.bl,h.blcode order by h.bl desc)\n" +
                "union all \n" +
                "select * from (\n" +
                "select h.bl as HANDLEDEPTNAME,h.blcode as bb,'0' as count from  " +
                "TELEPHONE_BASIC B,TELEPHONE_HANDLE H where HANDLEDEPTNAME not in (\n" +
                "select h.bl as HANDLEDEPTNAME from  TELEPHONE_BASIC B,TELEPHONE_HANDLE H where b.TASKSTAGE=6 and b.BREPLY='已回复'\n" +
                "AND   B.ID = H.HID   AND H.NODESTATUS='4'\n" +
                "AND H.ISDELETE='0' and to_date(b.OVERTIME,'yyyy-mm-dd hh24:mi:ss') between\n" +
                "to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  \n" +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss'))  \n" +
                "group by h.bl ,h.blcode order by h.bl desc)\n" +
                ")\n" +
                "where bb like  's%' and HANDLEDEPTNAME in (select *from(\n" +
                "SELECT H2.bl   FROM \n" +
                "TELEPHONE_BASIC B2,TELEPHONE_HANDLE H2 WHERE b2.CREATETIME \n" +
                "between to_date('"+arr[0]+" 12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and \n" +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss') AND   \n" +
                "B2.ID = H2.HID   AND H2.NODESTATUS='4'  AND H2.ISDELETE='0' group by H2.bl order by H2.bl desc))\n" +
                "group by HANDLEDEPTNAME,bb order by HANDLEDEPTNAME desc";

        //解决数量
        String sql_nameConOver_over =  "select HANDLEDEPTNAME, sum(count) ,bb from (\n" +
                "select * from (\n" +
                "select h.bl as HANDLEDEPTNAME ,h.blcode as bb, TO_CHAR(count(HANDLEDEPTNAME)) as count from   TELEPHONE_BASIC B,TELEPHONE_HANDLE H where  b.TASKSTAGE=6 and b.BRESOLVE='已解决'\n" +
                " and to_date(b.OVERTIME,'yyyy-mm-dd hh24:mi:ss') between " +
                "to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  \n" +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss') AND   B.ID = H.HID   AND H.NODESTATUS='4'\n" +
                "AND H.ISDELETE='0' group by h.bl,h.blcode order by h.bl desc)\n" +
                "union all \n" +
                "select * from (\n" +
                "select h.bl as HANDLEDEPTNAME,h.blcode as bb,'0' as count from  TELEPHONE_BASIC B,TELEPHONE_HANDLE H where HANDLEDEPTNAME not in (\n" +
                "select h.bl as HANDLEDEPTNAME from  TELEPHONE_BASIC B,TELEPHONE_HANDLE H where b.TASKSTAGE=6 and b.BRESOLVE='已解决'\n" +
                "AND   B.ID = H.HID  AND H.ISDELETE='0'  AND H.NODESTATUS='4'\n" +
                "and to_date(b.OVERTIME,'yyyy-mm-dd hh24:mi:ss') between\n" +
                "to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  \n" +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss'))  \n" +
                "group by h.bl ,h.blcode order by h.bl desc)\n" +
                ")\n" +
                "where bb like  's%' and HANDLEDEPTNAME in (select *from(\n" +
                "SELECT H2.bl   FROM \n" +
                "TELEPHONE_BASIC B2,TELEPHONE_HANDLE H2 WHERE b2.CREATETIME \n" +
                "between to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and \n" +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss') AND   \n" +
                "B2.ID = H2.HID   AND H2.NODESTATUS='4' AND H2.ISDELETE='0' group by H2.bl order by H2.bl desc))\n" +
                "group by HANDLEDEPTNAME,bb order by HANDLEDEPTNAME desc";

        //满意率
        String sql_satisfaction = "select HANDLEDEPTNAME, sum(count) ,bb from (\n" +
                "select * from (\n" +
                "select h.bl as HANDLEDEPTNAME ,h.blcode as bb, TO_CHAR(count(HANDLEDEPTNAME)) as count from   TELEPHONE_BASIC B,TELEPHONE_HANDLE H where  b.TASKSTAGE=6 and b.BSATISFACTION='满意'\n" +
                " and to_date(b.OVERTIME,'yyyy-mm-dd hh24:mi:ss') between " +
                "to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  \n" +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss') AND   B.ID = H.HID   AND H.NODESTATUS='4'\n" +
                "AND H.ISDELETE='0' group by h.bl,h.blcode order by h.bl desc)\n" +
                "union all \n" +
                "select * from (\n" +
                "select h.bl as HANDLEDEPTNAME,h.blcode as bb,'0' as count from  TELEPHONE_BASIC B,TELEPHONE_HANDLE H where HANDLEDEPTNAME not in (\n" +
                "select h.bl as HANDLEDEPTNAME from  TELEPHONE_BASIC B,TELEPHONE_HANDLE H where b.TASKSTAGE=6 and b.BSATISFACTION='满意'\n" +
                "AND   B.ID = H.HID  AND H.ISDELETE='0' AND H.NODESTATUS='4'\n" +
                "and to_date(b.OVERTIME,'yyyy-mm-dd hh24:mi:ss') between\n" +
                "to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  \n" +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss'))  \n" +
                "group by h.bl ,h.blcode order by h.bl desc)\n" +
                ")\n" +
                "where bb like  's%' and HANDLEDEPTNAME in (select *from(\n" +
                "SELECT H2.bl   FROM \n" +
                "TELEPHONE_BASIC B2,TELEPHONE_HANDLE H2 WHERE b2.CREATETIME \n" +
                "between to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and \n" +
                "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss') AND   \n" +
                "B2.ID = H2.HID  AND H2.ISDELETE='0' AND H2.NODESTATUS='4'  group by H2.bl order by H2.bl desc))\n" +
                "group by HANDLEDEPTNAME,bb order by HANDLEDEPTNAME desc";

        //诉求类别
        String sql_type =
                "select * from (select i.*,NVL2(j.ggfwl,j.ggfwl,'公共服务类') ggfwl,NVL2(j.countg,j.countg,0) countg from (\n" +
                        "select g.*,NVL2(h.wzjzl,h.wzjzl,'违章建筑类') wzjzl,NVL2(h.countw,h.countw,0) countw from (\n" +
                        "select e.*,NVL2(f.hjjsl,f.hjjsl,'环境保护类') hjjsl,NVL2(f.counth,f.counth,0) counth from (\n" +
                        "select c.*,NVL2(d.csgll,d.csgll,'城市管理类') csgll,NVL2(d.countc,d.countc,0) countc from (\n" +
                        "select a.HANDLEDEPTNAME, NVL2(b.sqgll,b.sqgll,'社区管理类') sqgll,NVL2(b.count,b.count,0) counts from \n" +
                        "(select h1.bl as HANDLEDEPTNAME from   TELEPHONE_BASIC B1,TELEPHONE_HANDLE H1\n" +
                        "WHERE  b1.CREATETIME between to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  " +
                        "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss') AND   B1.ID = H1.HID  AND H1.ISDELETE='0'  AND H1.NODESTATUS='4'\n" +
                        "and HANDLEDEPTNAME is not null group by h1.bl) a left join (select  h1.bl as HANDLEDEPTNAME, b1.PROBLEMTYPE sqgll, count(*) count " +
                        "from TELEPHONE_BASIC B1,TELEPHONE_HANDLE H1 where b1.PROBLEMTYPE='社区管理类' \n" +
                        "and b1.CREATETIME between to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  " +
                        "to_date('"+arr[1]+"   12:00:00','yyyy-mm-dd hh24:mi:ss')\n" +
                        "and B1.ID = H1.HID   AND H1.ISDELETE='0' AND H1.NODESTATUS='4' group by h1.bl, b1.PROBLEMTYPE) b on a.HANDLEDEPTNAME = b.HANDLEDEPTNAME) c left join\n" +
                        "(select  h1.bl as HANDLEDEPTNAME, b1.PROBLEMTYPE csgll, count(*) countc from  TELEPHONE_BASIC B1,TELEPHONE_HANDLE H1 where b1.PROBLEMTYPE='城市管理类' \n" +
                        "and b1.CREATETIME between to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  " +
                        "to_date('"+arr[1]+"   12:00:00','yyyy-mm-dd hh24:mi:ss')\n" +
                        "and B1.ID = H1.HID  AND H1.ISDELETE='0'  AND H1.NODESTATUS='4' group by h1.bl, b1.PROBLEMTYPE) d on c.HANDLEDEPTNAME=d.HANDLEDEPTNAME) e left join \n" +
                        "(select h1.bl as HANDLEDEPTNAME, b1.PROBLEMTYPE hjjsl, count(*) counth from TELEPHONE_BASIC B1,TELEPHONE_HANDLE H1 where b1.PROBLEMTYPE='环境保护类' \n" +
                        "and b1.CREATETIME between to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  " +
                        "to_date('"+arr[1]+"   12:00:00','yyyy-mm-dd hh24:mi:ss')\n" +
                        "  AND H1.NODESTATUS='4' AND   B1.ID = H1.HID  AND H1.ISDELETE='0' group by h1.bl, b1.PROBLEMTYPE) f on e.HANDLEDEPTNAME=f.HANDLEDEPTNAME) g left join \n" +
                        "(select  h1.bl as HANDLEDEPTNAME, b1.PROBLEMTYPE wzjzl, count(*) countw from TELEPHONE_BASIC B1,TELEPHONE_HANDLE H1 where b1.PROBLEMTYPE='违章建筑类' \n" +
                        "and b1.CREATETIME between to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  " +
                        "to_date('"+arr[1]+"   12:00:00','yyyy-mm-dd hh24:mi:ss')\n" +
                        "  AND H1.NODESTATUS='4' AND H1.ISDELETE='0' AND   B1.ID = H1.HID  group by h1.bl, b1.PROBLEMTYPE) h on g.HANDLEDEPTNAME=h.HANDLEDEPTNAME) i left join \n" +
                        "(select  h1.bl as HANDLEDEPTNAME, b1.PROBLEMTYPE ggfwl, count(*) countg from TELEPHONE_BASIC B1,TELEPHONE_HANDLE H1 where b1.PROBLEMTYPE='公共服务类'\n" +
                        "and b1.CREATETIME between to_date('"+arr[0]+"  12:00:00' ,'yyyy-mm-dd hh24:mi:ss')and  " +
                        "to_date('"+arr[1]+"  12:00:00','yyyy-mm-dd hh24:mi:ss')\n" +
                        "AND H1.NODESTATUS='4' AND H1.ISDELETE='0' AND   B1.ID = H1.HID  group by h1.bl, b1.PROBLEMTYPE) j on i.HANDLEDEPTNAME=j.HANDLEDEPTNAME ) order by HANDLEDEPTNAME desc\n" ;



        //总体返回数据
        List<Map<String, Object>> info = new ArrayList<Map<String, Object>>();
        //计算总办结数量，数据库TASKSTAGE=6
        List<HashMap<String, String>> listInfo_Over = new ArrayList<HashMap<String, String>>();
        //满意率
        List<HashMap<String, String>> listInfo_satisfaction = new ArrayList<HashMap<String, String>>();
        //包含社区名称以及总条数
        List<HashMap<String, String>> listInfo = new ArrayList<HashMap<String, String>>();
        //诉求类别
        List<HashMap<String, String>> listInfo_type = new ArrayList<HashMap<String, String>>();
        //响应率
        List<HashMap<String, String>> listInfo_Over_req = new ArrayList<HashMap<String, String>>();
        //解决
        List<HashMap<String, String>> listInfo_Over_over = new ArrayList<HashMap<String, String>>();
        //满意率
        try {
            listInfo = databaseBo.prepareQuery(sql_nameCon,null);
            listInfo_Over = databaseBo.prepareQuery(sql_nameConOver,null);
            listInfo_satisfaction = databaseBo.prepareQuery(sql_satisfaction,null);
            listInfo_type = databaseBo.prepareQuery(sql_type,null);
            listInfo_Over_req = databaseBo.prepareQuery(sql_nameConOver_res,null);
            listInfo_Over_over = databaseBo.prepareQuery(sql_nameConOver_over,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("listInfo："+ listInfo);
        System.out.println("listInfo_Over："+ listInfo_Over);
        System.out.println("listInfo_satisfaction："+ listInfo_satisfaction);
        System.out.println("listInfo_type："+ listInfo_type);
        System.out.println("listInfo_Over_req："+ listInfo_Over_req);
        System.out.println("listInfo_Over_over："+ listInfo_Over_over);






        //各个社区名称
        String[] communitys_name = new String[listInfo.size()];
        //社区总诉求数量-即数据库社区姓名相同相加和
        String[] communitys_sum = new String[listInfo.size()];
        //办结总量
        String[] communitys_Over = new String[listInfo_Over.size()];
        //办结率-既总办结数量/总诉求
        String[] communitys_OverRate = new String[listInfo_Over.size()];
        //满意条数与率
        String[] communitys_satisfactionCon = new String[listInfo_satisfaction.size()];
        String[] communitys_satisfaction = new String[listInfo_satisfaction.size()];

        //响应条数 响应率
        String[] communitys_Over_req = new String[listInfo_Over_req.size()];
        String[] communitys_Over_req_2 = new String[listInfo_Over_req.size()];
        //解决条数 解决率
        String[] communitys_Over_over = new String[listInfo_Over_over.size()];
        String[] communitys_Over_over_2 = new String[listInfo_Over_over.size()];

        List<HashMap<String, Object>> infos = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> matterList = new HashMap<String, Object>();
        HashMap<String, Object> communityList = new HashMap<String, Object>();
        //诉求类别
        List<Object> matterList_type = new ArrayList<Object>();
        //按社区排列所需要用到的集合
        //社区名称
        HashMap<String, Object> communitysName = new HashMap<String, Object>();
        //社区管理类
        HashMap<String, Object> commAdmin = new HashMap<String, Object>();
        //城管类
        HashMap<String, Object> cityAdmin = new HashMap<String, Object>();
        //环监类
        HashMap<String, Object> environment = new HashMap<String, Object>();
        //违建类
        HashMap<String, Object> illegalBulid = new HashMap<String, Object>();
        //公共服务类
        HashMap<String, Object> publicServer = new HashMap<String, Object>();
        int sum_1 = 0;
        int sum_2 = 0;
        int sum_3 = 0;
        int sum_4 = 0;
        int sum_5 = 0;
        String[] adminComms = new String[listInfo.size()];
        String[] cityComms = new String[listInfo.size()];
        String[] environmentComms = new String[listInfo.size()];
        String[] illegalBulidComms = new String[listInfo.size()];
        String[] publicServerComms = new String[listInfo.size()];
        for (int i = 0; i < listInfo.size(); i++) {
            //matterList -按照事项排列
            communitys_name[i] = listInfo.get(i).get("HANDLEDEPTNAME");
            communitys_sum[i] = listInfo.get(i).get("COUNT(HANDLEDEPTNAME)");
            communitys_Over[i] = listInfo_Over.get(i).get("SUM(COUNT)");
            communitys_satisfactionCon[i] = listInfo_satisfaction.get(i).get("SUM(COUNT)");
            //办结率 保留两位 \n响应率 解决率
            communitys_Over_req[i] = listInfo_Over_req.get(i).get("SUM(COUNT)");
            communitys_Over_over[i] = listInfo_Over_over.get(i).get("SUM(COUNT)");

            System.out.println("名："+ communitys_name[i]);
            System.out.println("总："+  communitys_sum[i]);
            System.out.println("完结："+ communitys_Over[i]);
            System.out.println("满意："+    communitys_satisfactionCon[i]);
            System.out.println("响应："+  communitys_Over_req[i] );
            System.out.println("办结："+ communitys_Over_over[i]);


            NumberFormat nt = NumberFormat.getPercentInstance();
            nt.setMinimumFractionDigits(2);
            double numerator = Double.parseDouble(communitys_Over[i]);
            double denominator = Double.parseDouble(communitys_sum[i]);
            double numerator_req = Double.parseDouble(communitys_Over_req[i]);
            double denominators_over = Double.parseDouble(communitys_Over_over[i]);
            double percent =0;
            double percent2 =0;
            double percent_satisfaction_over =0;
            if(denominator!=0){
                //办结率
                percent= numerator / denominator;

                //解决率
                percent_satisfaction_over= denominators_over / denominator;
            }else{
                percent=0;

                percent_satisfaction_over=0;
            }


            //满意率  保留两位
            double denominators = Double.parseDouble(communitys_Over[i]);
            double numerator_satisfactionCon = Double.parseDouble(communitys_satisfactionCon[i]);
            double percent_satisfaction =0;
            if(denominators!=0){
                percent_satisfaction= numerator_satisfactionCon / denominators;
                //响应率
                percent2= numerator_req / denominators;
            }else{
                percent_satisfaction=0;
                percent2=0;
            }
            //拼接结果集
            communitys_OverRate[i] = nt.format(percent);
            communitys_Over_req_2[i]=nt.format(percent2);
            communitys_satisfaction[i] = nt.format(percent_satisfaction);
            communitys_Over_over_2[i]= nt.format(percent_satisfaction_over);
            //诉求类别数量
            String[] publicServerComms2 = new String[5];
            publicServerComms2[0] = listInfo_type.get(i).get("COUNTS");
            publicServerComms2[1] = listInfo_type.get(i).get("COUNTC");
            publicServerComms2[2] = listInfo_type.get(i).get("COUNTH");
            publicServerComms2[3] = listInfo_type.get(i).get("COUNTW");
            publicServerComms2[4] = listInfo_type.get(i).get("COUNTG");

            matterList_type.add(publicServerComms2);
            //添加数据
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", communitys_name[i]);
            map.put("appeal", communitys_sum[i]);
            map.put("getThrough", communitys_Over[i]);
            map.put("gtRate", communitys_OverRate[i]);
            map.put("req_2", communitys_Over_req_2[i]);
            map.put("satisfied", communitys_satisfaction[i]);
            map.put("over_2", communitys_Over_over_2[i]);
            map.put("appealArr", matterList_type.get(i));
            infos.add(map);
            //按照社区排列


            adminComms[i] = listInfo_type.get(i).get("COUNTS");
            cityComms[i] = listInfo_type.get(i).get("COUNTC");
            environmentComms[i] = listInfo_type.get(i).get("COUNTH");
            illegalBulidComms[i] = listInfo_type.get(i).get("COUNTW");
            publicServerComms[i] = listInfo_type.get(i).get("COUNTG");

            //社区管理类
            int in_1[] = new int[listInfo.size()];
            in_1[i] = Integer.parseInt(adminComms[i]);
            sum_1 += in_1[i];

            //城市管理类
            int in_2[] = new int[listInfo.size()];
            in_2[i] = Integer.parseInt(cityComms[i]);
            sum_2 += in_2[i];

            //环境建设类
            int in_3[] = new int[listInfo.size()];
            in_3[i] = Integer.parseInt(environmentComms[i]);
            sum_3 += in_3[i];

            //违章建筑类
            int in_4[] = new int[listInfo.size()];
            in_4[i] = Integer.parseInt(illegalBulidComms[i]);
            sum_4 += in_4[i];

            //公共服务类
            int in_5[] = new int[listInfo.size()];
            in_5[i] = Integer.parseInt(publicServerComms[i]);
            sum_5 += in_5[i];
        }
        matterList.put("matterList", infos);

        commAdmin.put("all", sum_1);
        commAdmin.put("comms", adminComms);
        cityAdmin.put("all", sum_2);
        cityAdmin.put("comms", cityComms);
        environment.put("all", sum_3);
        environment.put("comms", environmentComms);
        illegalBulid.put("all", sum_4);
        illegalBulid.put("comms", illegalBulidComms);
        publicServer.put("all", sum_5);
        publicServer.put("comms", publicServerComms);
//
        communitysName.put("communitys", communitys_name);
        communitysName.put("commAdmin", commAdmin);
        communitysName.put("cityAdmin", cityAdmin);
        communitysName.put("environment", environment);
        communitysName.put("illegalBulid", illegalBulid);
        communitysName.put("publicServer", publicServer);
        communityList.put("communityList", communitysName);

        info.add(matterList);
        info.add(communityList);
        //截止查询时间
        HashMap<String, Object> time = new HashMap<String, Object>();
        time.put("time", "此表统计时间为 "+ arr[0]+"至"+arr[1] +"  中午12：00");
        operation_time.put("operation_time",operation_time_info);
        info.add(time);
        info.add(operation_time);
        JSONArray array = JSONArray.parseArray(JSON.toJSONString(info));
        System.out.println(array);
        return array;
    }
}
