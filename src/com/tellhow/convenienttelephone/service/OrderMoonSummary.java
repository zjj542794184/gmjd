package com.tellhow.convenienttelephone.service;

import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import com.siqiansoft.framework.model.PagingModel;
import com.siqiansoft.framework.model.db.ConditionModel;
import com.siqiansoft.framework.util.PageControl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrderMoonSummary {
    DatabaseBo dbo = new DatabaseBo();


    public List<HashMap<String, String>> getNormal(LoginModel login, ConditionModel[] cs, PagingModel page) {

        /*
         * 获得当前年月
         * 获得当前时间并与20号12：00比较
         * 判断number大小，如果number=1，当前时间大于20号12：00，number=-1，当前时间小于20号12：00
         * */
        int yearNow = Calendar.YEAR;
        int moonNow = Calendar.MONTH + 1;

        SimpleDateFormat df = new SimpleDateFormat("dd HH:mm:ss");
        String dat = df.format(new Date());

        Date nowDate = new Date();
        Date endDate = new Date();
        try {
            nowDate = df.parse(dat);
            endDate = df.parse("20 12:00:00");

            System.out.println("最后测试结果为-----当前时间：" + nowDate);
            System.out.println("最后测试结果为-----比较时间：" + endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //起止时间
        double startTime = 0;
        double endTime = 0;

        String fh = "";
        if (nowDate.after(endDate)) {
            /*
             *日期大于20，既本月20号12：00之后，月份加1
             * 当月天使起始时间固定为1，+19.5及当月20号12：00，开始时间就为固定
             * 大月月份31日，结束时间为下个月20，即31+19.5=50.5
             * 小月30日，30+19.5=49.5
             * 闰年2月29天 29+19.5
             * 平年28， 28+19.5
             * */
            fh = "+";
            startTime = 19.5;

            //润年
            if (yearNow % 4 == 0) {
                //大月
                if (moonNow == 1 || moonNow == 3 || moonNow == 5 ||
                        moonNow == 7 || moonNow == 8 || moonNow == 10 ||
                        moonNow == 12) {
                    endTime = 50.5;
                }
                //2月 29天
                else if (moonNow == 2) {
                    endTime = 48.5;

                } else {
                    endTime = 49.5;
                }
            }
            //平年
            if (yearNow % 4 != 0) {
                //大月
                if (moonNow == 1 || moonNow == 3 || moonNow == 5 ||
                        moonNow == 7 || moonNow == 8 || moonNow == 10 ||
                        moonNow == 12) {
                    endTime = 50.5;

                }
                //2月 28天
                else if (moonNow == 2) {
                    endTime = 47.5;

                } else {
                    endTime = 50.5;
                }
            }
        } else {
            /*
             *日期小于20，既本月20号12：00之前，月份减1
             * 当月天使起始时间固定为1，+19.5及当月20号12：00 结束时间就为固定
             * 1、5、7、10、12月四个月前一个月固定30天，开始时间倒推10.5
             * */
            fh = "-";
            endTime = 19.5;
            //润年
            if (yearNow % 4 == 0) {
                //前一个月为30天
                if ( moonNow == 5 || moonNow == 7 ||
                        moonNow == 10 || moonNow == 12) {
                    startTime = 10.5;
                }
                //2月 29天
                else if (moonNow == 3) {
                    startTime = 9.5;

                } else {//前一个月31天，1、2、4、6、8、9、11
                    startTime = 11.5;
                }
            }
            //平年
            if (yearNow % 4 != 0) {
                //大月
                if ( moonNow == 5 || moonNow == 7 ||
                        moonNow == 10 || moonNow == 12) {
                    startTime = 10.5;
                }
                //2月 28天
                else if (moonNow == 3) {
                    startTime = 8.5;

                } else {//前一个月31天，1、2、4、6、8、9、11
                    startTime = 11.5;
                }
            }
        }


        String selectSql="SELECT\n" +
                "\tHANDLEDEPTNAME AS \"社区\",\n" +
                "\tZONG AS \"数量\",\n" +
                "\txyl * 100 || '%' AS \"响应率\",\n" +
                "\tjjl * 100 || '%' AS \"解决率\",\n" +
                "\tmyl * 100 || '%' AS \"满意率\",\n" +
                "\tZH * 100 AS \"综合评分\" \n" +
                "FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "\t\tHANDLEDEPTNAME,\n" +
                "\t\tZONG,\n" +
                "\t\txyl,\n" +
                "\t\tjjl,\n" +
                "\t\tmyl,\n" +
                "\t\tTOTAL,\n" +
                "\t\tround ((( 1-round ( ZONG / TOTAL, 2 )) + xyl + jjl + myl ) / 4, 4 ) ZH \n" +
                "\tFROM\n" +
                "\t\t(\n" +
                "\t\tSELECT\n" +
                "\t\t\tHANDLEDEPTNAME,\n" +
                "\t\t\tZONG,\n" +
                "\t\t\tdecode(\n" +
                "\t\t\t\tyjj,\n" +
                "\t\t\t\t0,\n" +
                "\t\t\t\t0,\n" +
                "\t\t\tround( yhfs / yjj, 4 )) xyl,\n" +
                "\t\t\tround( yjj / zong, 4 ) jjl,\n" +
                "\t\t\tdecode(\n" +
                "\t\t\t\tyjj,\n" +
                "\t\t\t\t0,\n" +
                "\t\t\t\t0,\n" +
                "\t\t\tround( my / yjj, 4 )) myl,\n" +
                "\t\t\t( SELECT COUNT( * ) FROM TELEPHONE_BASIC ) TOTAL \n" +
                "\t\tFROM\n" +
                "\t\t\t(\n" +
                "\t\t\tSELECT\n" +
                "\t\t\t\te.*,\n" +
                "\t\t\t\tNVL2( f.my, f.my, 0 ) my \n" +
                "\t\t\tFROM\n" +
                "\t\t\t\t(\n" +
                "\t\t\t\tSELECT\n" +
                "\t\t\t\t\tc.*,\n" +
                "\t\t\t\t\tNVL2( d.yjj, d.yjj, 0 ) yjj \n" +
                "\t\t\t\tFROM\n" +
                "\t\t\t\t\t(\n" +
                "\t\t\t\t\tSELECT\n" +
                "\t\t\t\t\t\ta.*,\n" +
                "\t\t\t\t\t\tNVL2( b.yhfs, b.yhfs, 0 ) YHFS \n" +
                "\t\t\t\t\tFROM\n" +
                "\t\t\t\t\t\t(\n" +
                "\t\t\t\t\t\tSELECT\n" +
                "\t\t\t\t\t\t\th.bl as HANDLEDEPTNAME,\n" +
                "\t\t\t\t\t\t\tcount( 1 ) zong \n" +
                "\t\t\t\t\t\tFROM\n" +
                "\t\t\t\t\t\t\tTELEPHONE_BASIC B,TELEPHONE_HANDLE H\n" +
                "\t\t\t\t\t\tWHERE\n" +
                "\t\t\t\t\t\t\tb.CREATETIME > ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' ) " + fh + startTime + ", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\t\t\tAND b.CREATETIME < ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' ) + "+endTime+", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\t\t\tAND B.ID = H.HID   AND H.NODESTATUS='4'  AND h.ISDELETE='0'\n" +
                "\t\t\t\t\t\tGROUP BY\n" +
                "\t\t\t\t\t\t\th.bl\n" +
                "\t\t\t\t\t\t) a\n" +
                "\t\t\t\t\t\tLEFT JOIN (\n" +
                "\t\t\t\t\t\tSELECT\n" +
                "\t\t\t\t\t\t\th.bl as HANDLEDEPTNAME,\n" +
                "\t\t\t\t\t\t\tcount( * ) yhfs \n" +
                "\t\t\t\t\t\tFROM\n" +
                "\t\t\t\t\t\t\tTELEPHONE_BASIC B,TELEPHONE_HANDLE H\n" +
                "\t\t\t\t\t\tWHERE\n" +
                "\t\t\t\t\t\t\tb.TASKSTAGE=6 and b.BREPLY='已回复'\n" +
                "\t\t\t\t\t\t\tAND to_date( b.OVERTIME, 'yyyy-mm-dd hh24:mi:ss' ) > ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' ) "+ fh + startTime +", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\t\t\tAND to_date( b.OVERTIME, 'yyyy-mm-dd hh24:mi:ss' ) < ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' ) + "+endTime+", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\t\t\tAND B.ID = H.HID   AND H.NODESTATUS='4'  AND h.ISDELETE='0'\n" +
                "\t\t\t\t\t\tGROUP BY\n" +
                "\t\t\t\t\t\t\th.bl \n" +
                "\t\t\t\t\t\t) b ON a.HANDLEDEPTNAME = b.HANDLEDEPTNAME \n" +
                "\t\t\t\t\t) c\n" +
                "\t\t\t\t\tLEFT JOIN (\n" +
                "\t\t\t\t\tSELECT\n" +
                "\t\t\t\t\t\t\th.bl as HANDLEDEPTNAME,\n" +
                "\t\t\t\t\t\tcount( * ) yjj \n" +
                "\t\t\t\t\tFROM\n" +
                "\t\t\t\t\t\tTELEPHONE_BASIC B,TELEPHONE_HANDLE H\n" +
                "\t\t\t\t\tWHERE\n" +
                "\t\t\t\t\t\tb.TASKSTAGE=6 \n" +
                "\t\t\t\t\t\tAND to_date( b.OVERTIME, 'yyyy-mm-dd hh24:mi:ss' ) > ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' ) "+ fh + startTime +", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) " +
                "AND to_date( b.OVERTIME, 'yyyy-mm-dd hh24:mi:ss' ) < ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' ) + "+endTime+", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\t\tAND B.ID = H.HID   AND H.NODESTATUS='4'  AND h.ISDELETE='0'\n" +
                "\t\t\t\t\tGROUP BY\n" +
                "\t\t\t\t\t\th.bl \n" +
                "\t\t\t\t\t) d ON c.HANDLEDEPTNAME = d.HANDLEDEPTNAME \n" +
                "\t\t\t\t) e\n" +
                "\t\t\t\tLEFT JOIN (\n" +
                "\t\t\t\tSELECT\n" +
                "\t\t\t\t\th.bl as HANDLEDEPTNAME,\n" +
                "\t\t\t\t\tcount( * ) my \n" +
                "\t\t\t\tFROM\n" +
                "\t\t\t\t\tTELEPHONE_BASIC B,TELEPHONE_HANDLE H\n" +
                "\t\t\t\tWHERE\n" +
                "\t\t\t\t\tb.TASKSTAGE=6 and b.BSATISFACTION = '满意' \n" +
                "\t\t\t\t\tAND to_date( b.OVERTIME, 'yyyy-mm-dd hh24:mi:ss' ) > ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' ) "+ fh + startTime +", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\tAND to_date( b.OVERTIME, 'yyyy-mm-dd hh24:mi:ss' ) < ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' ) + "+endTime+", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\tAND B.ID = H.HID   AND H.NODESTATUS='4'  AND h.ISDELETE='0'\n" +
                "\t\t\t\tGROUP BY\n" +
                "\t\t\t\t\t\th.bl \n" +
                "\t\t\t\t) f ON e.HANDLEDEPTNAME = f.HANDLEDEPTNAME \n" +
                "\t\t\t))) \n" +
                "ORDER BY\n" +
                "\t综合评分 DESC";


        String selectSql1 = "SELECT\n" +
                "\tHANDLEDEPTNAME AS \"社区\",\n" +
                "\tZONG AS \"数量\",\n" +
                "\txyl * 100 || '%' AS \"响应率\",\n" +
                "\tjjl * 100 || '%' AS \"解决率\",\n" +
                "\tmyl * 100 || '%' AS \"满意率\",\n" +
                "\tZH * 100 AS \"综合评分\" \n" +
                "FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "\t\tHANDLEDEPTNAME,\n" +
                "\t\tZONG,\n" +
                "\t\txyl,\n" +
                "\t\tjjl,\n" +
                "\t\tmyl,\n" +
                "\t\tTOTAL,\n" +
                "\t\tround((( 1-round ( ZONG / TOTAL, 2 )) + xyl + jjl + myl ) / 4, 4 ) ZH \n" +
                "\tFROM\n" +
                "\t\t(\n" +
                "\t\tSELECT\n" +
                "\t\t\tHANDLEDEPTNAME,\n" +
                "\t\t\tZONG,\n" +
                "\t\t\tdecode(\n" +
                "\t\t\t\tyjj,\n" +
                "\t\t\t\t0,\n" +
                "\t\t\t\t0,\n" +
                "\t\t\tround( yhfs / yjj, 4 )) xyl,\n" +
                "\t\t\tround( yjj / zong, 4 ) jjl,\n" +
                "\t\t\tdecode(\n" +
                "\t\t\t\tyjj,\n" +
                "\t\t\t\t0,\n" +
                "\t\t\t\t0,\n" +
                "\t\t\tround( my / yjj, 4 )) myl,\n" +
                "\t\t\t( SELECT COUNT( * ) FROM TELEPHONE_BASIC ) TOTAL \n" +
                "\t\tFROM\n" +
                "\t\t\t(\n" +
                "\t\t\tSELECT\n" +
                "\t\t\t\te.*,\n" +
                "\t\t\t\tNVL2( f.my, f.my, 0 ) my \n" +
                "\t\t\tFROM\n" +
                "\t\t\t\t(\n" +
                "\t\t\t\tSELECT\n" +
                "\t\t\t\t\tc.*,\n" +
                "\t\t\t\t\tNVL2( d.yjj, d.yjj, 0 ) yjj \n" +
                "\t\t\t\tFROM\n" +
                "\t\t\t\t\t(\n" +
                "\t\t\t\t\tSELECT\n" +
                "\t\t\t\t\t\ta.*,\n" +
                "\t\t\t\t\t\tNVL2( b.yhfs, b.yhfs, 0 ) YHFS \n" +
                "\t\t\t\t\tFROM\n" +
                "\t\t\t\t\t\t(\n" +
                "\t\t\t\t\t\tSELECT\n" +
                "\t\t\t\t\t\t\tHANDLEDEPTNAME,\n" +
                "\t\t\t\t\t\t\tcount( 1 ) zong \n" +
                "\t\t\t\t\t\tFROM\n" +
                "\t\t\t\t\t\t\tTELEPHONE_BASIC \n" +
                "\t\t\t\t\t\tWHERE\n" +
                "\t\t\t\t\t\t\tCREATETIME > ( SELECT \n" +
                "\t\t\t\t\t\t\tTO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' )  " + fh + startTime + ", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\t\t\tAND CREATETIME < ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' ) + " + endTime + ", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\t\t\tAND HANDLEDEPTNAME IS NOT NULL \n" +
                "\t\t\t\t\t\tGROUP BY\n" +
                "\t\t\t\t\t\t\tHANDLEDEPTNAME \n" +
                "\t\t\t\t\t\t) a\n" +
                "\t\t\t\t\t\tLEFT JOIN (\n" +
                "\t\t\t\t\t\tSELECT\n" +
                "\t\t\t\t\t\t\tHANDLEDEPTNAME,\n" +
                "\t\t\t\t\t\t\tcount( * ) yhfs \n" +
                "\t\t\t\t\t\tFROM\n" +
                "\t\t\t\t\t\t\tTELEPHONE_BASIC \n" +
                "\t\t\t\t\t\tWHERE\n" +
                "\t\t\t\t\t\t\tbreply = '已回复' \n" +
                "\t\t\t\t\t\t\tAND to_date( OVERTIME, 'yyyy-mm-dd hh24:mi:ss' ) > ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' )  " + fh + startTime + ", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\t\t\tAND to_date( OVERTIME, 'yyyy-mm-dd hh24:mi:ss' ) < ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' ) + " + endTime + ", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\t\t\tAND HANDLEDEPTNAME IS NOT NULL \n" +
                "\t\t\t\t\t\tGROUP BY\n" +
                "\t\t\t\t\t\t\tHANDLEDEPTNAME \n" +
                "\t\t\t\t\t\t) b ON a.HANDLEDEPTNAME = b.HANDLEDEPTNAME \n" +
                "\t\t\t\t\t) c\n" +
                "\t\t\t\t\tLEFT JOIN (\n" +
                "\t\t\t\t\tSELECT\n" +
                "\t\t\t\t\t\tHANDLEDEPTNAME,\n" +
                "\t\t\t\t\t\tcount( * ) yjj \n" +
                "\t\t\t\t\tFROM\n" +
                "\t\t\t\t\t\tTELEPHONE_BASIC \n" +
                "\t\t\t\t\tWHERE\n" +
                "\t\t\t\t\t\tTASKSTAGE = '6' \n" +
                "\t\t\t\t\t\tAND BRESOLVE = '已解决' \n" +
                "\t\t\t\t\t\tAND to_date( OVERTIME, 'yyyy-mm-dd hh24:mi:ss' ) > ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' )  " + fh + startTime + ", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\t\tAND to_date( OVERTIME, 'yyyy-mm-dd hh24:mi:ss' ) < ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' ) + " + endTime + ", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\t\tAND HANDLEDEPTNAME IS NOT NULL \n" +
                "\t\t\t\t\tGROUP BY\n" +
                "\t\t\t\t\t\tHANDLEDEPTNAME \n" +
                "\t\t\t\t\t) d ON c.HANDLEDEPTNAME = d.HANDLEDEPTNAME \n" +
                "\t\t\t\t) e\n" +
                "\t\t\t\tLEFT JOIN (\n" +
                "\t\t\t\tSELECT\n" +
                "\t\t\t\t\tHANDLEDEPTNAME,\n" +
                "\t\t\t\t\tcount( * ) my \n" +
                "\t\t\t\tFROM\n" +
                "\t\t\t\t\tTELEPHONE_BASIC \n" +
                "\t\t\t\tWHERE\n" +
                "\t\t\t\t\tBSATISFACTION = '满意' \n" +
                "\t\t\t\t\tAND to_date( OVERTIME, 'yyyy-mm-dd hh24:mi:ss' ) > ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' )  " + fh + startTime + ", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\tAND to_date( OVERTIME, 'yyyy-mm-dd hh24:mi:ss' ) < ( SELECT TO_DATE(( SELECT to_char( trunc( SYSDATE, 'mm' ) + " + endTime + ", 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ), 'yyyy-mm-dd hh24:mi:ss' ) FROM dual ) \n" +
                "\t\t\t\t\tAND HANDLEDEPTNAME IS NOT NULL \n" +
                "\t\t\t\tGROUP BY\n" +
                "\t\t\t\t\tHANDLEDEPTNAME \n" +
                "\t\t\t\t) f ON e.HANDLEDEPTNAME = f.HANDLEDEPTNAME \n" +
                "\t\t\t))) \n" +
                "ORDER BY\n" +
                "\t综合评分 DESC";


        String usercode = login.getUserCode();

        List<HashMap<String, String>> list = new ArrayList();
        List<HashMap<String, String>> applist = new ArrayList();
        List<HashMap<String, String>> alist = new ArrayList();
        List<HashMap<String, String>> rolelist = new ArrayList();
        HashMap<String, String> map = null;
        ArrayList pagelist = new ArrayList();

        int from = page.getFrom();
        int to = page.getTo();
        int pageRows = page.getPageRows();

        int i;

        try {

            list = dbo.prepareQuery(selectSql, null);
            if (list.size() > 0) {
                for (int j = 0; j < list.size(); j++) {

                    map = list.get(j);
                    applist.add(map);


                }
            }
            if (applist.size() > 0) {
                for (i = 0; i < applist.size(); ++i) {
                    if (!pagelist.contains(applist.get(i))) {
                        pagelist.add(applist.get(i));
                    }
                }
            }

            if (pagelist.size() == 0) {
                PageControl.calcPage(page, 0, 0);
                return null;
            } else {
                if (from == 1 && to == 0) {
                    to = pagelist.size();
                }

                i = pagelist.size();
                int pageCount = pagelist.size() / pageRows + (pagelist.size() % pageRows == 0 ? 0 : 1);
                page.setPageCount(pageCount);
                page.setRowsCount(i);
                if (to > pagelist.size()) {
                    to = pagelist.size();
                }

                page.setTo(to);
                return pagelist.subList(from - 1, to);
            }
        } catch (Exception var14) {
            var14.printStackTrace();
            return applist;
        }
    }
}
