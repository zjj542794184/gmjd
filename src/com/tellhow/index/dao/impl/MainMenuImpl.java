package com.tellhow.index.dao.impl;

import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import com.siqiansoft.framework.model.PagingModel;
import com.siqiansoft.framework.model.RequestModel;
import com.siqiansoft.workflow.bo.StartupBo;
import com.tellhow.common.util.DateUtil;
import com.tellhow.common.util.GetReal;
import com.tellhow.dailyOffice.service.MemberToDuty;
import com.tellhow.index.dao.MainMenu;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.xml.XMLSerializer;

public class MainMenuImpl
        implements MainMenu
{
  List<HashMap<String, String>> list;
  List<HashMap<String, String>> listduty2 = new ArrayList();
  HashMap<String, String> hashMap;
  DatabaseBo dbo = new DatabaseBo();
  StartupBo sbo = new StartupBo();
  Calendar c = Calendar.getInstance();
  SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  MemberToDuty member = new MemberToDuty();
  String time = this.f.format(this.c.getTime());

  public static String xml2JSON(String xml) {
    return new XMLSerializer().read(xml).toString();
  }

  public List<HashMap<String, String>> getTodayBirthDay(LoginModel loginModel) {
    String sql = "SELECT * FROM EAP_CONTACT WHERE BIRTHDAY like (select '_____'||to_char(sysdate,'mm-dd') from dual)";
    try {
      this.list = this.dbo.prepareQuery(sql, new String[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public List<HashMap<String, String>> getWeekBirthDay(LoginModel loginModel) {
    String sql = "SELECT * FROM EAP_CONTACT WHERE BIRTHDAY BETWEEN (select substr(BIRTHDAY,0,5)||to_char(trunc(sysdate, 'd') ,'MM-dd') from dual) and (select substr(BIRTHDAY,0,5)||to_char(trunc(sysdate, 'd') + 6,'MM-dd') from dual)";
    try {
      this.list = this.dbo.prepareQuery(sql, new String[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public List<HashMap<String, String>> getNotice(LoginModel loginModel) {
    String sql = "select b.* from (select a.*, rownum rn from(select t.* from oa_messagecenter t  where t.reception=? order by id desc )a)b where rn between 1 and 3 ";
    try {
      this.list = this.dbo.prepareQuery(sql,
              new String[] { loginModel.getUserCode() });
      System.out.println("================list.size()=" +
              this.list.size());
      if (this.list.size() > 0)
        for (int i = 0; i < this.list.size(); i++) {
          String typename =
                  (String)((HashMap)this.list.get(i))
                          .get("TYPENAME");
          System.out.println("=============typename=" + typename);
          String title =
                  (String)((HashMap)this.list.get(i))
                          .get("TITLE");
          System.out.println("=============titlle=" + title);
          if (title.length() > 20) {
            title = title.substring(0, 20) + "...";
          }
          ((HashMap)this.list.get(i)).put("CONTENT", typename +
                  "&nbsp;&nbsp;&nbsp;&nbsp;" + title);
        }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public List<HashMap<String, String>> getCountGroup() throws Exception {
    String sql = "select * from oa_dutygroup";
    List list = this.dbo.query(sql);
    return list;
  }

  public String lunhuan(String Rid, List<HashMap<String, String>> yuan, List<HashMap<String, String>> ti)
  {
    try {
      String ridsql = "select name from oa_dutygroupmember g,Eap_Contact c where g.usercode=c.code and g.rid=" +
              Rid + " and g.status=0 order by g.id asc";

      List memberList = this.dbo.query(ridsql);
      String member = "";
      List<String> list = new ArrayList();
      for (int i = 0; i < memberList.size(); i++) {
        boolean flag = true;
        for (int j = 0; j < yuan.size(); j++) {
          System.out.println(
                  (String)((HashMap)yuan.get(j))
                          .get("PROPOSER") +
                          "-------" +
                          (String)((HashMap)memberList.get(i))
                                  .get("NAME"));

          if (((String)((HashMap)yuan.get(j)).get("PROPOSER"))
                  .equals(((HashMap)memberList.get(i)).get("NAME"))) {
            if (i == 0)
              member = member +
                      (String)((HashMap)memberList.get(i))
                              .get("NAME") +
                      "[" +
                      (String)((HashMap)yuan.get(j))
                              .get("REPLACER") + "]";
            else {
              member = member +
                      ", " +
                      (String)((HashMap)memberList.get(i))
                              .get("NAME") +
                      "[" +
                      (String)((HashMap)yuan.get(j))
                              .get("REPLACER") + "]";
            }
            list.add(
                    (String)((HashMap)memberList.get(i))
                            .get("NAME"));
            flag = false;
            break;
          }
        }
        for (int j = 0; j < ti.size(); j++) {
          boolean isalready = false;
          for (String string : list)
          {
            if (((String)((HashMap)ti.get(j)).get("REPLACER"))
                    .equals(string)) {
              isalready = true;
              break;
            }
          }
          if (isalready) {
            continue;
          }
          if (((String)((HashMap)ti.get(j)).get("REPLACER"))
                  .equals(((HashMap)memberList.get(i))
                          .get("NAME"))) {
            if (i == 0)
              member = member +
                      (String)((HashMap)memberList.get(i))
                              .get("NAME") +
                      "[" +
                      (String)((HashMap)ti.get(j))
                              .get("PROPOSER") + "]";
            else {
              member = member +
                      ", " +
                      (String)((HashMap)memberList.get(i))
                              .get("NAME") +
                      "[" +
                      (String)((HashMap)ti.get(j))
                              .get("PROPOSER") + "]";
            }
            flag = false;
            break;
          }
        }

        if (flag) {
          if (i == 0)
            member = member +
                    (String)((HashMap)memberList.get(i))
                            .get("NAME");
          else {
            member = member +
                    ", " +
                    (String)((HashMap)memberList.get(i))
                            .get("NAME");
          }
        }
      }
      String sql = "select * from oa_dutygroup t where id=" + Rid;
      List lists = this.dbo.query(sql);
      String tag = "";
      if (lists.size() > 0) {
        tag = ((String)((HashMap)lists.get(0)).get("GROUPNAME"))
                .replace(" ", ",") +
                "," +
                ((String)((HashMap)lists.get(0)).get("GROUPKZ"))
                        .replace(" ", ",");
      }
      String[] leaders = tag.split(",");
      String zgld = "";
      for (int i = 0; i < leaders.length; i++) {
        boolean temp = true;
        for (int j = 0; j < yuan.size(); j++) {
          System.out.println(
                  (String)((HashMap)yuan.get(j))
                          .get("PROPOSER") + "-------" + leaders[i]);

          if (((String)((HashMap)yuan.get(j)).get("PROPOSER"))
                  .equals(leaders[i])) {
            if (i == 0)
              zgld = zgld +
                      leaders[i] +
                      "[" +
                      (String)((HashMap)yuan.get(j))
                              .get("REPLACER") + "]";
            else {
              zgld = zgld +
                      ", " +
                      leaders[i] +
                      "[" +
                      (String)((HashMap)yuan.get(j))
                              .get("REPLACER") + "]";
            }
            temp = false;
            break;
          }
        }
        for (int j = 0; j < ti.size(); j++) {
          System.out.println(
                  (String)((HashMap)ti.get(j))
                          .get("REPLACER") + "-------" + leaders[i]);

          if (((String)((HashMap)ti.get(j)).get("REPLACER"))
                  .equals(leaders[i])) {
            if (i == 0)
              zgld = zgld +
                      leaders[i] +
                      "[" +
                      (String)((HashMap)ti.get(j))
                              .get("PROPOSER") + "]";
            else {
              zgld = zgld +
                      ", " +
                      leaders[i] +
                      "[" +
                      (String)((HashMap)ti.get(j))
                              .get("PROPOSER") + "]";
            }
            temp = false;
            break;
          }
        }
        if (temp) {
          if (i == 0)
            zgld = zgld + leaders[i];
          else {
            zgld = zgld + ", " + leaders[i];
          }
        }
      }
      System.out.println("换班之后的领导=====" + zgld);
      return zgld + "," + member;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<HashMap<String, String>> getTodayDutyPlan1() {
    Map groupMenber = new HashMap();
    try {
      List list = getCountGroup();
      System.out.println(list.size());
      for (int i = 0; i < list.size(); i++) {
        String sql = "select name from oa_dutygroupmember g,Eap_Contact c where g.usercode=c.code and g.rid=" +
                (String)((HashMap)list.get(i)).get("ID") +
                "and g.status=0 order by g.id asc";

        System.out.println(sql);
        List list2 = this.dbo.query(sql);
        String member = "";

        String[] leader =
                ((String)((HashMap)list.get(i))
                        .get("GROUPNAME")).split(" ");
        String[] kz = ((String)((HashMap)list.get(i)).get("GROUPKZ"))
                .split(" ");
        for (int m = 0; m < leader.length; m++) {
          member = leader[m] + ",";
        }
        for (int n = 0; n < kz.length; n++) {
          member = member + kz[n] + ",";
        }
        for (int j = 0; j < list2.size(); j++) {
          if (j == 0)
            member = member +
                    (String)((HashMap)list2.get(j)).get("NAME");
          else {
            member = member + ", " +
                    (String)((HashMap)list2.get(j)).get("NAME");
          }
        }
        System.out.println("今日值班======" + member);
        groupMenber.put(((HashMap)list.get(i)).get("ID"), member);
      }
      String sql = "select * from oa_dutytime where time = '" + this.time +
              "'";

      sql = sql + " order by time asc";
      List dutytimeList = this.dbo.query(sql);
      List hashMaps = new ArrayList();
      for (int i = 0; i < dutytimeList.size(); i++) {
        String yuanSql = "select * from OA_APPLY_SHIFT where PROPOSERTIME='" +
                (String)((HashMap)dutytimeList.get(i)).get("TIME") +
                "' and state='0'";

        List yuanlist = this.dbo.query(yuanSql);
        System.out.println("换班人员:" + yuanlist.size());

        String tiSql = "select * from OA_APPLY_SHIFT where REPLACERTIME='" +
                (String)((HashMap)dutytimeList.get(i)).get("TIME") +
                "' and state='0'";

        List tilist = this.dbo.query(tiSql);
        System.out.println("替班人员：" + tilist.size());
        HashMap map = new HashMap();
        if ((yuanlist.size() != 0) || (tilist.size() != 0)) {
          String member = lunhuan(
                  (String)((HashMap)dutytimeList.get(i)).get("RID"),
                  yuanlist, tilist);
          System.out.println(member);
          String[] qq = member.split(",");
          for (int j = 0; j < qq.length; j++) {
            map = new HashMap();
            map.put("member", qq[j]);
            hashMaps.add(map);
          }
        } else if ((yuanlist.size() == 0) && (tilist.size() == 0)) {
          String time =
                  (String)((HashMap)dutytimeList.get(i))
                          .get("TIME");
          String rid =
                  (String)((HashMap)dutytimeList.get(i))
                          .get("RID");
          System.out.println("报错集合===============" +
                  (String)groupMenber.get(
                          ((HashMap)dutytimeList
                                  .get(i)).get("RID")));
          String mems = "";
          if (groupMenber.get(((HashMap)dutytimeList.get(i))
                  .get("RID")) != null) {
            mems = (String)groupMenber.get(
                    ((HashMap)dutytimeList
                            .get(i)).get("RID"));
          }
          String[] qq = mems.split(",");
          for (int j = 0; j < qq.length; j++) {
            map = new HashMap();
            map.put("member", qq[j]);
            hashMaps.add(map);
          }
        }
      }
      for (int j = 0; j < hashMaps.size(); j++) {
        System.out.println(
                (String)((HashMap)hashMaps.get(j))
                        .get("member"));
      }
      return hashMaps;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<HashMap<String, String>> getTodayDutyPlan2() {
    Map groupMenber = new HashMap();
    try {
      List list = getCountGroup();
      System.out.println(list.size());
      for (int i = 0; i < list.size(); i++) {
        String sql = "select name from oa_dutygroupmember g,Eap_Contact c where g.usercode=c.code and g.rid=" +
                (String)((HashMap)list.get(i)).get("ID") +
                "and g.status=1 order by g.id asc";

        System.out.println(sql);
        List list2 = this.dbo.query(sql);
        String member = "";
        for (int j = 0; j < list2.size(); j++) {
          if (j == 0)
            member = member +
                    (String)((HashMap)list2.get(j)).get("NAME");
          else {
            member = member + ", " +
                    (String)((HashMap)list2.get(j)).get("NAME");
          }
        }
        System.out.println("备班员======" + member);
        groupMenber.put(((HashMap)list.get(i)).get("ID"), member);
      }
      String sql = "select * from oa_dutytime where time = '" + this.time +
              "'";

      sql = sql + " order by time asc";
      List dutytimeList = this.dbo.query(sql);
      List hashMaps = new ArrayList();
      for (int i = 0; i < dutytimeList.size(); i++) {
        HashMap map = new HashMap();

        String member = "";
        if (groupMenber.get(((HashMap)dutytimeList.get(i)).get("RID")) != null) {
          member = (String)groupMenber.get(
                  ((HashMap)dutytimeList
                          .get(i)).get("RID"));
        }
        String[] qq = member.split(",");
        for (int j = 0; j < qq.length; j++) {
          map = new HashMap();
          map.put("member", qq[j]);
          hashMaps.add(map);
        }
      }
      for (int j = 0; j < hashMaps.size(); j++) {
        System.out.println(
                (String)((HashMap)hashMaps.get(j))
                        .get("member"));
      }
      return hashMaps;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<HashMap<String, String>> getTodayDutyPlan(LoginModel loginModel) {
    String sql = "SELECT * FROM oa_dutyplandetail where dutytime = (select to_char(sysdate,'yyyy-mm-dd') from dual) ORDER BY OA_DUTYPLANDETAIL.ID asc";
    try {
      this.list = this.dbo.prepareQuery(sql, new String[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  private List<HashMap<String, String>> getList(List<HashMap<String, String>> list, LoginModel loginModel)
  {
    List list2 = new ArrayList();
    HashMap hashMap = null;
    for (HashMap hashMap2 : list)
      System.out.println((String)hashMap2.get("TIME"));
    try
    {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      String now = format.format(new Date());
      Date nowDate = format.parse(now);
      String noDuty = "select PROPOSERTIME,REPLACERTIME from OA_APPLY_SHIFT where USERCODE=? and PROPOSERTIME<? and REPLACERTIME>=?";
      List list3 = this.dbo.prepareQuery(noDuty, new String[] {
              loginModel.getUserCode(), now, now });
      for (int j = 0; j < list3.size(); j++) {
        hashMap = new HashMap();
        hashMap.put("TIME",
                ((HashMap)list3.get(j)).get("REPLACERTIME"));
        list2.add(hashMap);
      }
      String noDuty2 = "select PROPOSERTIME,REPLACERTIME from OA_APPLY_SHIFT where REPLACERCODE=? and PROPOSERTIME>? and REPLACERTIME<=?";
      list3 = this.dbo.prepareQuery(noDuty2,
              new String[] { loginModel.getUserCode(), now, now });
      for (int j = 0; j < list3.size(); j++) {
        hashMap = new HashMap();
        hashMap.put("TIME",
                ((HashMap)list3.get(j)).get("PROPOSERTIME"));
        list2.add(hashMap);
      }
      for (int i = 0; i < list.size(); i++) {
        hashMap = new HashMap();
        String sql = "select PROPOSERTIME,REPLACERTIME from OA_APPLY_SHIFT where USERCODE=? and PROPOSERTIME=? and state=0";
        List replace = this.dbo.prepareQuery(sql,
                new String[] { loginModel.getUserCode(),
                        (String)((HashMap)list.get(i)).get("TIME") });

        String sqlti = "select PROPOSERTIME,REPLACERTIME from OA_APPLY_SHIFT where REPLACERCODE=? and REPLACERTIME=? and state=0";
        List tiplace = this.dbo.prepareQuery(sqlti,
                new String[] { loginModel.getUserCode(),
                        (String)((HashMap)list.get(i)).get("TIME") });
        if ((replace.size() == 0) && (tiplace.size() == 0)) {
          System.out.println(
                  (String)((HashMap)list.get(i))
                          .get("TIME"));
          hashMap.put("TIME", ((HashMap)list.get(i)).get("TIME"));
        }
        if (replace.size() > 0) {
          Date date = format
                  .parse(
                          (String)((HashMap)replace.get(0))
                                  .get("REPLACERTIME"));
          if (!date.before(nowDate))
            hashMap.put("TIME",
                    ((HashMap)replace.get(0)).get("REPLACERTIME"));
        }
        else if (tiplace.size() > 0) {
          Date date = format
                  .parse(
                          (String)((HashMap)tiplace.get(0))
                                  .get("PROPOSERTIME"));
          if (!date.before(nowDate))
            hashMap.put("TIME",
                    ((HashMap)tiplace.get(0)).get("PROPOSERTIME"));
        }
        else {
          list2.add(hashMap);
        }
      }
      return list2;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<HashMap<String, String>> getMyDutyPlan1(LoginModel loginModel) {
    List listduty = new ArrayList();
    List listduty1 = new ArrayList();
    HashMap mapduty = new HashMap();
    try {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      String nowDate = format.format(new Date());

      String sql1 = "select rank from eap_account t  where code='" +
              loginModel.getUserCode() + "'";
      List list1 = this.dbo.query(sql1);
      if (list1.size() > 0) {
        if (((String)((HashMap)list1.get(0)).get("RANK")).equals("2")) {
          String sql = "select  * from oa_dutytime where rid in (select rid from oa_dutygroupmember where usercode=?   ) and time>=?  order by time asc";
          List list = new ArrayList();
          list = this.dbo.prepareQuery(sql,
                  new String[] { loginModel.getUserCode(), nowDate });
          return getMyDutyList(getList(list, loginModel));
        }
        String sql = "select  * from oa_dutytime where time>=? and rid =(select id from oa_dutygroup where groupname like '%" +
                loginModel.getUserName() +
                "%' or groupkz like '%" +
                loginModel.getUserName() + "%')  order by time asc";
        List list = new ArrayList();
        list = this.dbo.prepareQuery(sql, new String[] { nowDate });

        return getMyDutyList(getList(list, loginModel));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private List<HashMap<String, String>> getMyDutyList(List<HashMap<String, String>> list) throws ParseException
  {
    for (HashMap hashMap : list) {
      HashMap mapduty = new HashMap();
      String TIME = (String)hashMap.get("TIME");
      Date date = this.f.parse(TIME);
      String day = DateUtil.getWeekOfDate(date);
      mapduty.put("TIME", TIME);
      mapduty.put("DAY", day);
      this.listduty2.add(mapduty);
    }
    return this.listduty2;
  }

  public List<HashMap<String, String>> getMyDutyPlan(LoginModel loginModel) {
    String sql = "SELECT * FROM OA_DUTYPLANDETAIL where DUTYTIME > = (select to_char(sysdate,'yyyy-mm-dd') from dual) AND DUTYPEOPLEID = ? ORDER BY DUTYTIME";
    try {
      this.list = this.dbo.prepareQuery(sql,
              new String[] { loginModel.getUserCode() });
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public HashMap<String, String> getGraph(LoginModel loginModel) {
    this.hashMap = new HashMap();
    String sql1 = "SELECT COUNT(*) COUNT FROM EAP_VW_TODO WHERE ACTIONTYPE!='W' AND ORGCODE = ? AND ACTOR = ? ORDER BY STARTTIME DESC";
    String sql2 = "SELECT COUNT(*) COUNT FROM EAP_DONE WHERE ORGCODE = ? AND ACTOR = ? AND ENDTIME LIKE'%" +
            this.time + "%'";
    try {
      List list1 = this.dbo.prepareQuery(
              sql1,
              new String[] { loginModel.getOrgCode(),
                      loginModel.getUserCode() });

      List list2 = this.dbo.prepareQuery(
              sql2,
              new String[] { loginModel.getOrgCode(),
                      loginModel.getUserCode() });

      double todo = Double.parseDouble(
              (String)((HashMap)list1.get(0))
                      .get("COUNT"));
      double done = Double.parseDouble(
              (String)((HashMap)list2.get(0))
                      .get("COUNT"));
      System.out.println("done=" + done);
      double sum = todo + done;
      if (sum == 0.0D) {
        sum = 1.0D;
        done = 1.0D;
      }
      todo = 100.0D * todo / sum;
      done = 100.0D * done / sum;
      DecimalFormat decimalFormat = new DecimalFormat(".00");
      String todos = decimalFormat.format(todo);
      String dones = decimalFormat.format(done);
      this.hashMap.put("TODO", todos);
      this.hashMap.put("DONE", dones);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.hashMap;
  }

  public List<HashMap<String, String>> getUsually(LoginModel loginModel) {
    try {
      this.list = ((List)this.sbo.getMyFlow(loginModel).get("flows"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public List<HashMap<String, String>> getInitiateList(LoginModel loginModel) {
    String sql = "SELECT * FROM EAP_VW_DONE WHERE WID IN (select MAX(WID) from EAP_VW_DONE where orgcode = ? and SPONSORCODE =?  and actiontype != 'W' AND INSTANCEID IN (SELECT INSTANCEID FROM EAP_INSTANCE WHERE SPONSORCODE = ? AND STATUS <> 'F') GROUP BY INSTANCEID ) ORDER BY FLOWSTARTTIME DESC";
    try {
      this.list = this.dbo
              .prepareQuery(
                      sql,
                      new String[] { loginModel.getOrgCode(),
                              loginModel.getUserCode(),
                              loginModel.getUserCode() });

      GetReal.realTitle(this.list);
      for (int i = 0; i < this.list.size(); i++) {
        String flowstarttime =
                (String)((HashMap)this.list.get(i))
                        .get("FLOWSTARTTIME");
        ((HashMap)this.list.get(i)).put("FLOWSTARTTIME",
                flowstarttime.substring(0, 10));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public List<HashMap<String, String>> getTodoList(LoginModel loginModel, RequestModel requestModel, PagingModel pagingModel)
  {
    int from = pagingModel.getFrom();
    int to = pagingModel.getTo();
    int pageRows = pagingModel.getPageRows();

    int curPage = pagingModel.getCurPage();
    HashMap map = null;
    ArrayList lists = new ArrayList();

    String title = requestModel.getData() == null ? null :
            (String)requestModel.getData().get("$list-option-title");
    String flowname = requestModel.getData() == null ? null :
            (String)requestModel.getData().get("$list-option-flowname");

    String sql = "SELECT * FROM EAP_VW_TODO A LEFT JOIN OA_FLOW_COLLECT B ON A.INSTANCEID = B.INSTANCEID AND B.USERCODE= ? WHERE ACTIONTYPE!='W' AND ACTOR = ? ";
    if ((flowname != null) && (!"".equals(flowname))) {
      sql = sql + " AND flowname='" + flowname + "' ";
    }
    sql = sql +
            " ORDER BY  decode(flowcode,'teamdiscuss',1,2),STARTTIME DESC";
    try {
      this.list = this.dbo.prepareQuery(
              sql,
              new String[] { loginModel.getUserCode(),
                      loginModel.getUserCode() });

      GetReal.realTitle(this.list);
      for (int j = 0; j < this.list.size(); j++) {
        map = new HashMap();
        map = (HashMap)this.list.get(j);

        String sta = "1";

        map.put("sta", sta);
        lists.add(map);
      }
      for (int i = 0; i < lists.size(); i++)
        if ((title != null) &&
                (((String)((HashMap)lists.get(i)).get("TITLE"))
                        .indexOf(title) == -1)) {
          lists.remove(i);
          i--;
        } else {
          String flowstarttime =
                  (String)((HashMap)lists.get(i))
                          .get("FLOWSTARTTIME");
          ((HashMap)lists.get(i)).put("FLOWSTARTTIME",
                  flowstarttime.substring(0, 10));
        }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    String wenjiansql = "select  b.id,b.title title,a.jieshoucode jieshoucode,b.request requesttime,b.time time,B.SPONSORNAME SPONSORNAME from oa_documentcy a,oa_documentcircularize b where a.rid=b.id and fasongcode='" +
            loginModel.getUserCode() + "' and a.state='1' ";
    String worksql = "select * from oa_newwork a join oa_workinfo b on a.id=b.wid where b.banjie='0' and b.revicecode='" +
            loginModel.getUserCode() + "' ";
    String topicsummary_sql = "select a.id,a.title,a.year,a.periods,b.sendtime,b.sendcode from  oa_topicperiods a join oa_topicpass b on a.id=b.periodsid  where b.passingcode='" +
            loginModel.getUserCode() + "' and b.handletime is null ";
    String other_sql = "select a.*,b.id otherpassid,b.sendcode,b.sendname,b.sendtime from oa_other a join oa_otherpassing b on a.id=b.otherid where b.recipientcode='" +
            loginModel.getUserCode() + "' and readtime is null ";
    String supervise_sql = "select b.id reportid,b.sendcode,b.sendname,b.sendtime,a.id superviseid,a.item from oa_supervise a join oa_supervisereport b on a.id=b.superviseid where b.time is null and b.sendtime is not null and b.peoplecode='" +
            loginModel.getUserCode() + "' and a.status!=2 ";
    if (title != null) {
      wenjiansql = wenjiansql + " AND b.TITLE LIKE '%" + title + "%' ";
      worksql = worksql + " AND a.TITLE LIKE '%" + title + "%' ";
      topicsummary_sql = topicsummary_sql + " AND a.title LIKE '%" +
              title + "%' ";
      other_sql = other_sql + " and a.application like'%" + title + "%' ";
      supervise_sql = supervise_sql + " and a.item like'%" + title +
              "%' ";
    }
    if ((flowname != null) && (!"".equals(flowname)) &&
            (flowname != "文件传阅") && (!"文件传阅".equals(flowname))) {
      wenjiansql = wenjiansql + " AND a.id is null ";
    }
    if ((flowname != null) && (!"".equals(flowname)) &&
            (flowname != "新工作通知") && (!"新工作通知".equals(flowname))) {
      worksql = worksql + " AND a.id is null ";
    }
    if ((flowname != null) &&
            (!"".equals(flowname)) &&
            (flowname != "工委会议题会议纪要") &&
            (!"工委会议题会议纪要"
                    .equals(flowname))) {
      topicsummary_sql = topicsummary_sql + " AND a.id is null ";
    }
    if ((flowname != null) && (!"".equals(flowname)) &&
            (flowname != "其它模块") && (!"其它模块".equals(flowname))) {
      other_sql = other_sql + " AND a.id is null ";
    }
    if ((flowname != null) && (!"".equals(flowname)) &&
            (flowname != "督察督办上报") && (!"督察督办上报".equals(flowname))) {
      supervise_sql = supervise_sql + " AND a.id is null ";
    }
    wenjiansql = wenjiansql + " order by a.id desc ";
    worksql = worksql + " order by a.id desc";
    topicsummary_sql = topicsummary_sql + " order by a.id desc";
    other_sql = other_sql + " order by a.id desc";
    supervise_sql = supervise_sql + " order by b.id desc";
    String opinion_sql = "select  a.SENDNAME as ASSIGNERNAME,b.FLOWSTARTTIME as STARTTIME,b.id as INSTANCEID ,b.FAQINAME as SPONSORNAME,b.TROUBLETYPE as title ,'社情民意'  as FLOWNAME,'opinion?m=info'||'&'||'id='|| b.id as url ,b.stationname as sponsordept from OA_OPINION_HANDLE a join oa_opinion_info b on a.hid=b.id and a.actorcode =? and a.status='0'   ";

    ArrayList wenjianlist = new ArrayList();
    List worklist = new ArrayList();
    List topicSummarylist = new ArrayList();
    List otherlist = new ArrayList();
    List superviselist = new ArrayList();
    List opinionlist = new ArrayList();
    ArrayList listzong = new ArrayList();

    listzong.addAll(lists);
    try
    {
      wenjianlist = this.dbo.prepareQuery(wenjiansql, null);
      worklist = this.dbo.prepareQuery(worksql, null);
      topicSummarylist = this.dbo.prepareQuery(topicsummary_sql, null);
      otherlist = this.dbo.prepareQuery(other_sql, null);
      superviselist = this.dbo.prepareQuery(supervise_sql, null);
      opinionlist = this.dbo.prepareQuery(opinion_sql, new String[] { loginModel.getUserCode() });

      if (opinionlist.size() > 0) {
        listzong.addAll(opinionlist);
      }
      //便民电话待办工作


      List<HashMap<String, String>> phoneList = new ArrayList<HashMap<String, String>>();
      // 查询当前登录人code
      String userCode = loginModel.getUserCode();
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
      //String nodestatus = list11.get(0).get("NODESTATUS");
      System.out.println("获取节点状态："+nodestatus);
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
      //( !"".equals(checkTime) && status.equals("未审核") && checkTime != null) ||
      //						(("".equals(checkTime) || checkTime == null) && status.equals("被驳回") ) ||
      //						status.equals("已审核") || !userCode .equals(checkCode)
      String phoneSql = "SELECT id,ID INSTANCEID,TITLE title,'便民电话' as FLOWNAME,"
              + " 'convenienttelephone/taskmanagement.cmd?$ACTION=c02'"
              + " || '&' || 'id=' || id || '&' || 'nodestatus=' || nodestatus || '&' || 'bl=' || bl || '&' || 'taskstage=' || taskstage url,"
              + "CHECKCODE,CHECKTIME,CREATETIME,ASSIGNERNAME,STARTTIME,sponsorname,id,nodestatus,bl,sponsorname sponsordept FROM "
              + "(SELECT a.id id,a.taskid taskid,a.title title,a.finishtime finishtime,"
              + " CASE" +
              "    WHEN handledeptname IS  NULL" +
              "    THEN DEPTNAME" +
              "    ELSE handledeptname" +
              "  END DEPTNAME,a.termtime termtime,"
              + "a.taskstage taskstage, b.nodestatus nodestatus,b.BLCODE bl,b.status status,b.checkcode checkcode,"
              + "b.checktime checktime,a.createtime createtime,b.ASSIGNERNAME,a.STARTTIME,a.FAQINAME sponsorname "
              + "FROM TELEPHONE_BASIC a,TELEPHONE_HANDLE b WHERE A.ID      = B.HID  and b.ISDELETE = '0' "
              + "AND b.CHECKCODE = '"+userCode+"' "+value
              + " and ((status = '未审核' and checktime is null) or (checktime is not null and status = '被驳回'))) t WHERE 1=1 ORDER BY t.createtime DESC";
      phoneList = dbo.prepareQuery(phoneSql, null);
      //便民电话
      if(phoneList.size()>0){
        listzong.addAll(phoneList);
      }
      if (wenjianlist.size() > 0) {
        for (int i = 0; i < wenjianlist.size(); i++) {
          HashMap mapwenjian = new HashMap();
          Date earlydate = new Date();
          Date latedate = new Date();
          DateFormat df = DateFormat.getDateInstance();
          Calendar c = Calendar.getInstance();
          SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
          String now = f.format(c.getTime());
          if (daysBetween(df.parse(
                  (String)((HashMap)wenjianlist
                          .get(i)).get("TIME")), df.parse(now)) < 0)
            earlydate = df.parse(
                    (String)((HashMap)wenjianlist
                            .get(i)).get("TIME"));
          else {
            earlydate = df.parse(now);
          }
          latedate = df.parse(
                  (String)((HashMap)wenjianlist.get(i))
                          .get("REQUESTTIME"));

          int days = daysBetween(earlydate, latedate);

          String jieshouname = "select name from eap_account where code='" +
                  (String)((HashMap)wenjianlist.get(i))
                          .get("JIESHOUCODE") + "'";
          ArrayList listname = this.dbo.prepareQuery(jieshouname,
                  null);
          if (listname.size() > 0) {
            mapwenjian.put("ASSIGNERNAME",
                    ((HashMap)listname.get(0)).get("NAME"));
          }
          String jieshoutime = "select time,substr(time,0,10) subtime from oa_messagecenter t where t.reception='" +
                  loginModel.getUserCode() +
                  "' and url like '%id=" +
                  (String)((HashMap)wenjianlist.get(i)).get("ID") +
                  "' and title='" +
                  (String)((HashMap)wenjianlist.get(i))
                          .get("TITLE") + "' order by id desc ";
          ArrayList listtime = this.dbo.prepareQuery(jieshoutime,
                  null);
          if (listtime.size() > 0) {
            mapwenjian.put("FLOWSTARTTIME",
                    ((HashMap)listtime.get(0)).get("SUBTIME"));
            mapwenjian.put("STARTTIME",
                    ((HashMap)listtime.get(0)).get("TIME"));
          }
          String isshouc = "select IFCOLLECT from oa_flow_collect t where wid is null  and usercode='" +
                  loginModel.getUserCode() +
                  "' and instanceid='" +
                  (String)((HashMap)wenjianlist.get(i)).get("ID") +
                  "'";
          ArrayList listsc = this.dbo.prepareQuery(isshouc, null);
          if (listsc.size() > 0) {
            mapwenjian.put("IFCOLLECT",
                    ((HashMap)listsc.get(0)).get("IFCOLLECT"));
          }
          mapwenjian.put("INSTANCEID",
                  ((HashMap)wenjianlist.get(i)).get("ID"));
          mapwenjian.put("ASSIGNTYPE", "submit");
          mapwenjian.put("SPONSORNAME",
                  ((HashMap)wenjianlist.get(i)).get("SPONSORNAME"));
          mapwenjian.put("TITLE",
                  ((HashMap)wenjianlist.get(i)).get("TITLE"));
          mapwenjian.put("DAYS", Integer.valueOf(days));
          mapwenjian.put("FLOWNAME", "文件传阅");
          mapwenjian
                  .put("URL",
                          "documentcircu?stype=detail&id=" +
                                  (String)((HashMap)wenjianlist
                                          .get(i)).get("ID"));

          listzong.add(mapwenjian);
        }

      }

      if (worklist.size() > 0) {
        for (int i = 0; i < worklist.size(); i++) {
          HashMap mapwork = new HashMap();
          String sql_name = "";
          if (((HashMap)worklist.get(i)).get("SENDCODE") != null) {
            sql_name = "select name from eap_account where code='" +
                    (String)((HashMap)worklist.get(i)).get("SENDCODE") + "'";
            List namelist = this.dbo
                    .prepareQuery(sql_name, null);
            if (namelist.size() > 0)
              mapwork.put("ASSIGNERNAME",
                      ((HashMap)namelist.get(0)).get("NAME"));
            else {
              mapwork.put("ASSIGNERNAME", "");
            }

            mapwork.put("FLOWSTARTTIME",
                    ((HashMap)worklist.get(i)).get("JIESHOUTIME"));
            mapwork.put("STARTTIME",
                    ((HashMap)worklist.get(i)).get("FASONGTIME"));
            mapwork.put("INSTANCEID", ((HashMap)worklist.get(i)).get("ID"));
            mapwork.put("ASSIGNTYPE", "submit");
            mapwork.put("SPONSORNAME",
                    ((HashMap)worklist.get(i)).get("SPONSORNAME"));
            mapwork.put("TITLE", ((HashMap)worklist.get(i)).get("TITLE"));

            mapwork.put("FLOWNAME", "新工作通知");
            mapwork.put("URL",
                    "workinform.act?m=workinform&type=detail&id=" +
                            (String)((HashMap)worklist.get(i)).get("ID"));

            listzong.add(mapwork);
          }
        }

      }

      if (topicSummarylist.size() > 0) {
        for (int i = 0; i < topicSummarylist.size(); i++) {
          HashMap topicmap = new HashMap();
          if (((HashMap)topicSummarylist.get(i)).get("SENDCODE") != null) {
            String name_sql = "select name from eap_account where code=?";
            List namelist = this.dbo
                    .prepareQuery(
                            name_sql,
                            new String[] {
                                    (String)((HashMap)topicSummarylist
                                            .get(i)).get("SENDCODE") });
            if (namelist.size() > 0) {
              topicmap.put("ASSIGNERNAME",
                      ((HashMap)namelist.get(0)).get("NAME"));
              topicmap.put("SPONSORNAME",
                      ((HashMap)namelist.get(0)).get("NAME"));
            }
            else
            {
              topicmap.put("ASSIGNERNAME", "");
              topicmap.put("SPONSORNAME", "");
            }
          }
          topicmap.put("FLOWSTARTTIME",
                  ((HashMap)topicSummarylist.get(i)).get("SENDTIME"));
          topicmap.put("STARTTIME",
                  ((HashMap)topicSummarylist.get(i)).get("SENDTIME"));
          topicmap.put("INSTANCEID",
                  ((HashMap)topicSummarylist.get(i)).get("ID"));
          topicmap.put("ASSIGNTYPE", "submit");
          topicmap.put("TITLE",
                  ((HashMap)topicSummarylist.get(i)).get("TITLE"));
          topicmap.put("FLOWNAME", "工委会议题会议纪要");
          topicmap.put(
                  "URL",
                  "summaryService?type=detail&id=" +
                          (String)((HashMap)topicSummarylist
                                  .get(i)).get("ID"));

          listzong.add(topicmap);
        }

      }

      if (otherlist.size() > 0) {
        for (int i = 0; i < otherlist.size(); i++) {
          HashMap othermap = new HashMap();
          String sql_name = "";

          othermap.put("ASSIGNERNAME",
                  ((HashMap)otherlist.get(i)).get("SENDNAME"));
          othermap.put("FLOWSTARTTIME",
                  ((HashMap)otherlist.get(i)).get("APPLYTIME"));
          othermap.put("STARTTIME", ((HashMap)otherlist.get(i)).get("SENDTIME"));
          othermap.put("INSTANCEID",
                  ((HashMap)otherlist.get(i)).get("OTHERPASSID"));
          othermap.put("ASSIGNTYPE", "submit");
          othermap.put("SPONSORNAME", ((HashMap)otherlist.get(i))
                  .get("SENDNAME"));
          othermap.put("TITLE", ((HashMap)otherlist.get(i)).get("APPLICATION"));

          othermap.put("FLOWNAME", "其它模块");
          othermap.put("URL",
                  "passingService?action=passing&otherPassId=" +
                          (String)((HashMap)otherlist.get(i)).get("OTHERPASSID"));

          listzong.add(othermap);
        }

      }

      if (superviselist.size() > 0) {
        for (int i = 0; i < superviselist.size(); i++) {
          HashMap supervisemap = new HashMap();
          String sql_name = "";

          supervisemap.put("ASSIGNERNAME",
                  ((HashMap)superviselist.get(i)).get("SENDNAME"));
          supervisemap.put("FLOWSTARTTIME",
                  ((HashMap)superviselist.get(i)).get("SENDTIME"));
          supervisemap.put("STARTTIME",
                  ((HashMap)superviselist.get(i)).get("SENDTIME"));
          supervisemap.put("INSTANCEID",
                  (String)((HashMap)superviselist.get(i)).get("SUPERVISEID") + "," +
                          (String)((HashMap)superviselist.get(i)).get("REPORTID"));
          supervisemap.put("ASSIGNTYPE", "submit");

          supervisemap.put("TITLE", ((HashMap)superviselist.get(i)).get("ITEM"));
          supervisemap.put("FLOWNAME", "督察督办上报");
          supervisemap.put(
                  "URL",
                  "superviseForm?superviseid=" +
                          (String)((HashMap)superviselist.get(i)).get("SUPERVISEID") +
                          "&reportid=" +
                          (String)((HashMap)superviselist.get(i)).get("REPORTID"));

          listzong.add(supervisemap);
        }

      }

      for (int i = 0; i < this.list.size(); i++)
      {
        if ((!"资金申请".equals(((HashMap)this.list.get(i)).get("FLOWNAME"))) ||
                (!"社区理长审核".equals(((HashMap)this.list.get(i)).get("NODENAME")))) continue;
        String instanceid = (String)((HashMap)this.list.get(i)).get("INSTANCEID");

        String sqllddone = "select * from eap_todo where instanceid='" +
                instanceid + "'  and nodename='科室主管副职审核'";
        ArrayList listtodol = this.dbo
                .prepareQuery(sqllddone, null);

        String sqlkesdone = "select * from eap_todo where instanceid='" +
                instanceid + "'  and nodename='科室负责人审核'";
        ArrayList listtodoks = this.dbo
                .prepareQuery(sqlkesdone, null);
        if ((listtodol.size() > 0) || (listtodoks.size() > 0))
        {
          ((HashMap)this.list.get(i)).put("baoju", "0");
        }
        else ((HashMap)this.list.get(i)).put("baoju", "1");

      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    for (int i = 0; i < listzong.size() - 1; i++) {
      for (int j = 0; j < listzong.size() - i - 1; j++)
      {
        if (("".equals(((HashMap)listzong.get(j)).get("STARTTIME"))) &&
                (((HashMap)listzong
                        .get(j)).get("STARTTIME") == null))
          continue;
        if (("".equals(((HashMap)listzong.get(j + 1))
                .get("STARTTIME"))) &&
                (((HashMap)listzong
                        .get(j + 1)).get("STARTTIME") == null))
          continue;
        if (timetostring(
                (String)((HashMap)listzong.get(j))
                        .get("STARTTIME")) >= timetostring(
                (String)((HashMap)listzong
                        .get(j + 1)).get("STARTTIME"))) continue;
        HashMap mapp = (HashMap)listzong.get(j + 1);
        HashMap mapp2 = (HashMap)listzong.get(j);
        listzong.remove(listzong.get(j));
        listzong.add(j, mapp);
        listzong.remove(listzong.get(j + 1));
        listzong.add(j + 1, mapp2);
      }
    }

    if ((from == 1) && (to == 0)) {
      to = listzong.size();
    }
    int rowsCount = listzong.size();
    int pageCount = listzong.size() / pageRows + (
            listzong.size() % pageRows == 0 ? 0 : 1);

    pagingModel.setPageCount(pageCount);
    pagingModel.setRowsCount(rowsCount);
    if (to > listzong.size()) {
      to = listzong.size();
    }
    pagingModel.setTo(to);
    System.out
            .println("从第" + from + "条，到第" + to + "条，总共" + rowsCount + "页");

    System.out.println("这是第" + curPage + "页，总共" + pageCount + "页");
    return listzong.subList(from - 1, to);
  }

  public static final int daysBetween(Date early, Date late) {
    Calendar calst = Calendar.getInstance();
    Calendar caled = Calendar.getInstance();
    calst.setTime(early);
    caled.setTime(late);

    calst.set(11, 0);
    calst.set(12, 0);
    calst.set(13, 0);
    caled.set(11, 0);
    caled.set(12, 0);
    caled.set(13, 0);

    int days = ((int)(caled.getTime().getTime() / 1000L) -
            (int)(calst
                    .getTime().getTime() / 1000L)) / 3600 / 24;
    if (days >= 0) {
      days++;
    }
    return days;
  }

  public List<HashMap<String, String>> getDoneTodoList(LoginModel loginModel, RequestModel requestModel, PagingModel pagingModel)
  {
    int from = pagingModel.getFrom();
    int to = pagingModel.getTo();
    int pageRows = pagingModel.getPageRows();

    int curPage = pagingModel.getCurPage();
    String title = requestModel.getData() == null ? null :
            (String)requestModel.getData().get("$list-option-title");

    String folwname = requestModel.getData() == null ? null :
            (String)requestModel.getData()
                    .get("$subtab-option-flowname");

    String sql = "SELECT * FROM (SELECT * FROM EAP_VW_DONE WHERE WID IN (SELECT MAX(WID) FROM EAP_VW_DONE GROUP BY INSTANCEID)) A , OA_FLOW_COLLECT B WHERE A.INSTANCEID = B.INSTANCEID AND B.USERCODE=?  and b.wid is not  null  order by B.ID desc";
    try {
      this.list = this.dbo.prepareQuery(sql,
              new String[] { loginModel.getUserCode() });

      GetReal.realTitle(this.list);
      for (int i = 0; i < this.list.size(); i++)
        if ((title != null) &&
                (((String)((HashMap)this.list.get(i)).get("TITLE"))
                        .indexOf(title) == -1)) {
          this.list.remove(i);
          i--;
        } else {
          String flowstarttime =
                  (String)((HashMap)this.list.get(i))
                          .get("FLOWSTARTTIME");
          ((HashMap)this.list.get(i)).put("FLOWSTARTTIME",
                  flowstarttime.substring(0, 10));
        }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    ArrayList listzong = new ArrayList();

    String wenjiansql = "select * from oa_documentcircularize where id in(select t.instanceid from oa_flow_collect t where wid is null  and usercode='" +
            loginModel.getUserCode() + "')";
    try {
      ArrayList wenjianlist = this.dbo.prepareQuery(wenjiansql, null);
      if (wenjianlist.size() > 0) {
        wenjianlist = removeRepeat(wenjianlist);
        for (int i = 0; i < wenjianlist.size(); i++) {
          HashMap map = new HashMap();
          String jieshoutime = "select time time from oa_messagecenter t where t.reception='" +
                  loginModel.getUserCode() +
                  "' and url like '%id=" +
                  (String)((HashMap)wenjianlist.get(i)).get("ID") +
                  "' and title='" +
                  (String)((HashMap)wenjianlist.get(i))
                          .get("TITLE") + "' order by id desc ";
          ArrayList listtime = this.dbo.prepareQuery(jieshoutime,
                  null);
          if (listtime.size() > 0) {
            map.put("FLOWSTARTTIME",
                    ((HashMap)listtime.get(0)).get("TIME"));
          }
          String title2 =
                  (String)((HashMap)wenjianlist.get(i))
                          .get("TITLE");
          String flowname = "文件传阅";
          String status = "";
          if ("1".equals(((HashMap)wenjianlist.get(i)).get("STATE")))
            status = "I";
          else {
            status = "F";
          }
          map.put("INSTANCEID",
                  ((HashMap)wenjianlist.get(i)).get("ID"));
          map.put("TITLE", title2);
          map.put("FLOWNAME", flowname);
          map.put("STATUS", status);
          listzong.add(map);
        }
      }
      listzong.addAll(this.list);
    } catch (Exception e) {
      e.printStackTrace();
    }
    for (int i = 0; i < listzong.size() - 1; i++) {
      for (int j = 0; j < listzong.size() - i - 1; j++)
      {
        if (("".equals(((HashMap)listzong.get(j))
                .get("FLOWSTARTTIME"))) &&
                (((HashMap)listzong.get(j))
                        .get("FLOWSTARTTIME") == null))
          continue;
        if (("".equals(((HashMap)listzong.get(j + 1))
                .get("FLOWSTARTTIME"))) &&
                (((HashMap)listzong
                        .get(j + 1)).get("FLOWSTARTTIME") == null))
          continue;
        if (timetostring(
                (String)((HashMap)listzong.get(j))
                        .get("FLOWSTARTTIME")) >= timetostring(
                (String)((HashMap)listzong
                        .get(j + 1)).get("FLOWSTARTTIME"))) continue;
        HashMap mapp = (HashMap)listzong.get(j + 1);
        HashMap mapp2 = (HashMap)listzong.get(j);
        listzong.remove(listzong.get(j));
        listzong.add(j, mapp);
        listzong.remove(listzong.get(j + 1));
        listzong.add(j + 1, mapp2);
      }

    }

    if ((from == 1) && (to == 0)) {
      to = listzong.size();
    }
    int rowsCount = listzong.size();
    int pageCount = listzong.size() / pageRows + (
            listzong.size() % pageRows == 0 ? 0 : 1);
    pagingModel.setPageCount(pageCount);
    pagingModel.setRowsCount(rowsCount);
    if (to > listzong.size()) {
      to = listzong.size();
    }
    pagingModel.setTo(to);
    System.out
            .println("从第" + from + "条，到第" + to + "条，总共" + rowsCount + "页");
    System.out.println("这是第" + curPage + "条，总共" + pageCount + "页");
    return listzong.subList(from - 1, to);
  }

  public List<HashMap<String, String>> getDeptflowList(String deptcode, RequestModel requestModel, PagingModel pagingModel)
  {
    int from = pagingModel.getFrom();
    int to = pagingModel.getTo();
    int pageRows = pagingModel.getPageRows();

    int curPage = pagingModel.getCurPage();
    String title = requestModel.getData() == null ? null :
            (String)requestModel.getData().get("$list-option-title");

    String folwname = requestModel.getData() == null ? null :
            (String)requestModel.getData()
                    .get("$subtab-option-flowname");

    System.out.println("========================deptcode=" + deptcode);

    String sql = "SELECT * FROM EAP_VW_DONE WHERE WID IN (SELECT MAX(t.WID) FROM EAP_VW_DONE t,eap_instance i WHERE t.instanceid=i.instanceid AND t.assignername is null AND t.DEPTCODE=? and i.status in('I','F') GROUP BY t.INSTANCEID) ORDER BY STARTTIME DESC";
    try {
      this.list = this.dbo.prepareQuery(sql, new String[] { deptcode });

      GetReal.realTitle(this.list);
      for (int i = 0; i < this.list.size(); i++)
        if ((title != null) &&
                (((String)((HashMap)this.list.get(i)).get("TITLE"))
                        .indexOf(title) == -1)) {
          this.list.remove(i);
          i--;
        } else {
          String flowstarttime =
                  (String)((HashMap)this.list.get(i))
                          .get("FLOWSTARTTIME");
          ((HashMap)this.list.get(i)).put("FLOWSTARTTIME",
                  flowstarttime.substring(0, 10));
        }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    if ((from == 1) && (to == 0)) {
      to = this.list.size();
    }
    int rowsCount = this.list.size();
    int pageCount = this.list.size() / pageRows + (
            this.list.size() % pageRows == 0 ? 0 : 1);
    pagingModel.setPageCount(pageCount);
    pagingModel.setRowsCount(rowsCount);
    if (to > this.list.size()) {
      to = this.list.size();
    }
    pagingModel.setTo(to);
    System.out
            .println("从第" + from + "条，到第" + to + "条，总共" + rowsCount + "页");
    System.out.println("这是第" + curPage + "条，总共" + pageCount + "页");
    return this.list.subList(from - 1, to);
  }

  public List<HashMap<String, String>> getweixinList(LoginModel login) {
    String sql_weixin = "select * from oa_weixin where state='1'  order by time desc";

    ArrayList weixinlist = null;
    try {
      weixinlist = this.dbo.prepareQuery(sql_weixin, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return weixinlist;
  }

  public List<HashMap<String, String>> getDoingList(LoginModel loginModel, RequestModel requestModel, PagingModel pagingModel)
  {
    int from = pagingModel.getFrom();
    int to = pagingModel.getTo();
    int pageRows = pagingModel.getPageRows();

    int curPage = pagingModel.getCurPage();
    ArrayList lists = new ArrayList();
    HashMap map = null;
    String title = requestModel.getData() == null ? null :
            (String)requestModel.getData().get("$subtab-option-title");

    String flowcode = requestModel.getData() == null ? null :
            (String)requestModel.getData()
                    .get("$subtab-option-flowname");

    System.out.println(title);
    System.out
            .println("----------flowcode=====================" + flowcode);

    String sql = "select  t.*,case WHEN F.IFCOLLECT is NULL THEN 0 ELSE F.ifcollect END ifcollect from eap_vw_done t left join oa_flow_collect F on t.INSTANCEID=F.instanceid and t.WID=F.wid where t.actor=?";
    if ((flowcode != null) && (!"".equals(flowcode))) {
      sql = sql + " AND flowcode='" + flowcode + "' ";
    }
    sql = sql + " ORDER BY t.STARTTIME DESC";
    try {
      this.list = this.dbo.prepareQuery(sql,
              new String[] { loginModel.getUserCode() });
      GetReal.realTitle(this.list);
      for (int j = 0; j < this.list.size(); j++) {
        map = new HashMap();
        map = (HashMap)this.list.get(j);

        int nums = Integer.parseInt(
                (String)((HashMap)this.list
                        .get(j)).get("DAYS"));
        System.out.println("剩余天数===" + nums);
        String sta = "1";
        if (nums < 0) {
          nums = -nums;
          System.out.println("过期天数===" + nums);
          sta = "0";
        }
        map.put("days", Integer.valueOf(nums));
        map.put("sta", sta);
        lists.add(map);
      }
      for (int i = 0; i < lists.size(); i++)
        if ((title != null) &&
                (((String)((HashMap)lists.get(i)).get("TITLE"))
                        .indexOf(title) == -1)) {
          lists.remove(i);
          i--;
        } else {
          String flowstarttime =
                  (String)((HashMap)lists.get(i))
                          .get("FLOWSTARTTIME");
          ((HashMap)lists.get(i)).put("FLOWSTARTTIME",
                  flowstarttime.substring(0, 10));
        }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    String wenjiansql = "select  b.id,b.title title,a.jieshoucode jieshoucode,b.request requesttime,b.time time from oa_documentcy a,oa_documentcircularize b where a.rid=b.id and fasongcode='" +
            loginModel.getUserCode() +
            "' and a.state='2' order by a.id desc ";
    ArrayList wenjianlist = new ArrayList();
    ArrayList listzong = new ArrayList();
    try {
      wenjianlist = this.dbo.prepareQuery(wenjiansql, null);
      if (wenjianlist.size() > 0) {
        wenjianlist = removeRepeat(wenjianlist);
        for (int i = 0; i < wenjianlist.size(); i++) {
          HashMap mapwenjian = new HashMap();
          Date earlydate = new Date();
          Date latedate = new Date();
          DateFormat df = DateFormat.getDateInstance();
          Calendar c = Calendar.getInstance();
          SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
          String now = f.format(c.getTime());
          if (daysBetween(df.parse(
                  (String)((HashMap)wenjianlist
                          .get(i)).get("TIME")), df.parse(now)) < 0)
            earlydate = df.parse(
                    (String)((HashMap)wenjianlist
                            .get(i)).get("TIME"));
          else {
            earlydate = df.parse(now);
          }
          latedate = df.parse(
                  (String)((HashMap)wenjianlist.get(i))
                          .get("REQUESTTIME"));

          int days = daysBetween(earlydate, latedate);

          String jieshouname = "select name from eap_account where code='" +
                  (String)((HashMap)wenjianlist.get(i))
                          .get("JIESHOUCODE") + "'";
          ArrayList listname = this.dbo.prepareQuery(jieshouname,
                  null);
          if (listname.size() > 0) {
            mapwenjian.put("ASSIGNERNAME",
                    ((HashMap)listname.get(0)).get("NAME"));
          }
          String jieshoutime = "select time,substr(time,0,10) subtime from oa_messagecenter t where t.reception='" +
                  loginModel.getUserCode() +
                  "' and url like '%id=" +
                  (String)((HashMap)wenjianlist.get(i)).get("ID") +
                  "' and title='" +
                  (String)((HashMap)wenjianlist.get(i))
                          .get("TITLE") + "' order by id desc ";
          ArrayList listtime = this.dbo.prepareQuery(jieshoutime,
                  null);
          if (listtime.size() > 0) {
            mapwenjian.put("FLOWSTARTTIME",
                    ((HashMap)listtime.get(0)).get("SUBTIME"));
            mapwenjian.put("STARTTIME",
                    ((HashMap)listtime.get(0)).get("TIME"));
          } else {
            mapwenjian.put("STARTTIME",
                    ((HashMap)wenjianlist.get(i)).get("TIME"));
          }
          String isshouc = "select IFCOLLECT from oa_flow_collect t where wid is null  and usercode='" +
                  loginModel.getUserCode() +
                  "' and instanceid='" +
                  (String)((HashMap)wenjianlist.get(i)).get("ID") +
                  "'";
          ArrayList listsc = this.dbo.prepareQuery(isshouc, null);
          if (listsc.size() > 0) {
            mapwenjian.put("IFCOLLECT",
                    ((HashMap)listsc.get(0)).get("IFCOLLECT"));
          }
          mapwenjian.put("INSTANCEID",
                  ((HashMap)wenjianlist.get(i)).get("ID"));
          mapwenjian.put("TITLE",
                  ((HashMap)wenjianlist.get(i)).get("TITLE"));
          mapwenjian.put("SUBMITMODE", "submit");
          mapwenjian.put("DAYS", Integer.valueOf(days));
          mapwenjian.put("FLOWNAME", "文件传阅");
          mapwenjian
                  .put("URL",
                          "../documentcircu?stype=detail&id=" +
                                  (String)((HashMap)wenjianlist
                                          .get(i)).get("ID"));

          listzong.add(mapwenjian);
        }
      }

      String opinion_sql = "select  \r\n" +
              "a.SENDNAME as ASSIGNERNAME,\r\n" +
              "b.FLOWSTARTTIME as STARTTIME,\r\n" +
              "b.id as INSTANCEID ,\r\n" +
              "b.FAQINAME as SPONSORNAME,\r\n" +
              "b.TROUBLETYPE as title ,\r\n" +
              "'社情民意'  as FLOWNAME,\r\n" +
              "'opinion?m=info'||'&'||'id='|| b.id as url ,\r\n" +
              "b.stationname as sponsordept\r\n" +
              "from OA_OPINION_HANDLE a join oa_opinion_info b on a.hid=b.id and a.actorcode =? and  (a.status='1' or a.status='3')    \r\n" +
              "";
      List opinionlist = new ArrayList();
      opinionlist = this.dbo.prepareQuery(opinion_sql, new String[] { loginModel.getUserCode() });
      if(opinionlist.size()>0) {
        listzong.addAll(opinionlist);
      }

      //便民电话已办工作


      List<HashMap<String, String>> phoneList = new ArrayList<HashMap<String, String>>();
      // 查询当前登录人code
      String userCode = loginModel.getUserCode();
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
      //String nodestatus = list11.get(0).get("NODESTATUS");
      System.out.println("获取节点状态："+nodestatus);
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
      //( !"".equals(checkTime) && status.equals("未审核") && checkTime != null) ||
      //						(("".equals(checkTime) || checkTime == null) && status.equals("被驳回") ) ||
      //						status.equals("已审核") || !userCode .equals(checkCode)
      String phoneSql = "SELECT id,ID INSTANCEID,TITLE title,'便民电话' as FLOWNAME,"
              + " 'convenienttelephone/taskmanagement.cmd?$ACTION=co4' " +
              "|| '&' || 'id=' || id || '&' || 'nodestatus=' || nodestatus || '&' || 'bl=' || bl || '&' || 'taskstage=' || taskstage url,"
              + "CHECKCODE,CHECKTIME,CREATETIME,ASSIGNERNAME,STARTTIME,sponsorname,id,nodestatus,bl,sponsorname sponsordept FROM "
              + "(SELECT a.id id,a.taskid taskid,a.title title,a.finishtime finishtime,"
              + " CASE" +
              "    WHEN handledeptname IS  NULL" +
              "    THEN DEPTNAME" +
              "    ELSE handledeptname" +
              "  END DEPTNAME,a.termtime termtime,"
              + "a.taskstage taskstage, b.nodestatus nodestatus,b.BLCODE bl,b.status status,b.checkcode checkcode,"
              + "b.checktime checktime,a.createtime createtime,b.ASSIGNERNAME,a.STARTTIME,a.FAQINAME sponsorname "
              + "FROM TELEPHONE_BASIC a,TELEPHONE_HANDLE b WHERE A.ID      = B.HID  and b.ISDELETE = '0' "
              + "AND b.CHECKCODE = '"+userCode+"' "+value
              + " and (TASKSTAGE = '6' or status = '已审核' or (status = '未审核' and checktime is not null) or (checktime is null and status = '被驳回') or checkcode != '"+userCode+"')) t WHERE 1=1  ORDER BY t.createtime DESC";
      phoneList = dbo.prepareQuery(phoneSql, null);
      //便民电话
      if(phoneList.size()>0){
        listzong.addAll(phoneList);
      }

      listzong.addAll(lists);
    } catch (Exception e) {
      e.printStackTrace();
    }
    for (int i = 0; i < listzong.size() - 1; i++) {
      for (int j = 0; j < listzong.size() - i - 1; j++) {
        if (("".equals(((HashMap)listzong.get(j)).get("STARTTIME"))) &&
                (((HashMap)listzong
                        .get(j)).get("STARTTIME") == null))
          continue;
        if (("".equals(((HashMap)listzong.get(j + 1))
                .get("STARTTIME"))) &&
                (((HashMap)listzong
                        .get(j + 1)).get("STARTTIME") == null))
          continue;
        if (timetostring(
                (String)((HashMap)listzong.get(j))
                        .get("STARTTIME")) >= timetostring(
                (String)((HashMap)listzong
                        .get(j + 1)).get("STARTTIME"))) continue;
        HashMap mapp = (HashMap)listzong.get(j + 1);
        HashMap mapp2 = (HashMap)listzong.get(j);
        listzong.remove(listzong.get(j));
        listzong.add(j, mapp);
        listzong.remove(listzong.get(j + 1));
        listzong.add(j + 1, mapp2);
      }

    }

    if ((from == 1) && (to == 0)) {
      to = listzong.size();
    }
    int rowsCount = listzong.size();
    int pageCount = listzong.size() / pageRows + (
            listzong.size() % pageRows == 0 ? 0 : 1);
    pagingModel.setPageCount(pageCount);
    pagingModel.setRowsCount(rowsCount);
    if (to > listzong.size()) {
      to = listzong.size();
    }
    pagingModel.setTo(to);
    System.out
            .println("从第" + from + "条，到第" + to + "条，总共" + rowsCount + "页");
    System.out.println("这是第" + curPage + "条，总共" + pageCount + "页");
    return listzong.subList(from - 1, to);
  }

  public List<HashMap<String, String>> getDoneList(LoginModel loginModel, RequestModel requestModel, PagingModel pagingModel)
  {
    int from = pagingModel.getFrom();
    int to = pagingModel.getTo();
    int pageRows = pagingModel.getPageRows();

    int curPage = pagingModel.getCurPage();
    String title = requestModel.getData() == null ? null :
            (String)requestModel.getData().get("$list-option-title");

    System.out.println("title====================" + title + "======" +
            requestModel.getData());
    String sql = "SELECT A.MAXWID WID, A.INSTANCEID, A.CONTENT, A.TITLE, A.FLOWCODE,A.FLOWNAME, A.STATUS, A.FLOWSTARTTIME STARTTIME, A.FLOWENDTIME ENDTIME, B.IFCOLLECT FROM (SELECT A.*, B.WID MAXWID FROM EAP_VW_DONE A, (SELECT MAX(WID) WID, INSTANCEID FROM EAP_DONE WHERE INSTANCEID IN (SELECT INSTANCEID FROM EAP_DONE WHERE ACTOR = ?) GROUP BY INSTANCEID) B WHERE A.INSTANCEID = B.INSTANCEID AND A.WID = B.WID) A LEFT JOIN OA_FLOW_COLLECT B ON A.INSTANCEID = B.INSTANCEID AND B.USERCODE= ? WHERE A.STATUS = 'F'";

    sql = sql + " ORDER BY ENDTIME DESC";
    try {
      this.list = this.dbo.prepareQuery(
              sql,
              new String[] { loginModel.getUserCode(),
                      loginModel.getUserCode() });
      GetReal.realTitle(this.list);
      for (int i = 0; i < this.list.size(); i++)
        if ((title != null) &&
                (((String)((HashMap)this.list.get(i)).get("TITLE"))
                        .indexOf(title) == -1)) {
          this.list.remove(i);
          i--;
        } else {
          String flowstarttime =
                  (String)((HashMap)this.list.get(i))
                          .get("STARTTIME");
          ((HashMap)this.list.get(i)).put("STARTTIME",
                  flowstarttime.substring(0, 10));
        }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    if ((from == 1) && (to == 0)) {
      to = this.list.size();
    }
    int rowsCount = this.list.size();
    int pageCount = this.list.size() / pageRows + (
            this.list.size() % pageRows == 0 ? 0 : 1);
    pagingModel.setPageCount(pageCount);
    pagingModel.setRowsCount(rowsCount);
    if (to > this.list.size()) {
      to = this.list.size();
    }
    pagingModel.setTo(to);
    System.out
            .println("从第" + from + "条，到第" + to + "条，总共" + rowsCount + "页");
    System.out.println("这是第" + curPage + "条，总共" + pageCount + "页");
    return this.list.subList(from - 1, to);
  }

  public List<HashMap<String, String>> getCompleteWarn(LoginModel loginModel) {
    try {
      String sql = "SELECT F.WID,T.INSTANCEID,T.ENDTIME,T.FLOWNAME ,T.STATUS,T.SFYD  FROM EAP_INSTANCE T LEFT JOIN (SELECT MAX(WID) WID,INSTANCEID FROM EAP_VW_DONE GROUP BY INSTANCEID) F ON T.INSTANCEID = F.INSTANCEID  WHERE T.SPONSORCODE =? AND T.STATUS='F' AND T.SFYD=0";
      this.list = this.dbo.prepareQuery(sql,
              new String[] { loginModel.getUserCode() });
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public HashMap<String, String> getCount(LoginModel loginModel) {
    try {
      String sql = "SELECT COUNT(*) COUNT FROM EAP_LOG WHERE USERID = ? AND TYPE='LOGIN' AND REQTIME BETWEEN (select substr(REQTIME,0,5)||to_char(trunc(sysdate, 'd') ,'MM-dd') from dual) and (select substr(REQTIME,0,5)||to_char(trunc(sysdate, 'd') + 6,'MM-dd') from dual)";
      this.hashMap.put(
              "myLoginWeek",
              (String)((HashMap)this.dbo.prepareQuery(sql,
                      new String[] { loginModel.getUserCode() }).get(0))
                      .get("COUNT"));

      sql = "SELECT COUNT(*) COUNT FROM EAP_LOG WHERE USERID = ? AND TYPE='LOGIN' AND REQTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual)";
      this.hashMap.put(
              "myLoginMonth",
              (String)((HashMap)this.dbo.prepareQuery(sql,
                      new String[] { loginModel.getUserCode() }).get(0))
                      .get("COUNT"));

      sql = "SELECT COUNT(*) COUNT FROM EAP_DONE WHERE ACTOR = ? AND ENDTIME BETWEEN (select substr(ENDTIME,0,5)||to_char(trunc(sysdate, 'd') ,'MM-dd') from dual) and (select substr(ENDTIME,0,5)||to_char(trunc(sysdate, 'd') + 6,'MM-dd') from dual)";
      this.hashMap.put(
              "myDoneWeek",
              (String)((HashMap)this.dbo.prepareQuery(sql,
                      new String[] { loginModel.getUserCode() }).get(0))
                      .get("COUNT"));

      sql = "SELECT COUNT(*) COUNT FROM EAP_DONE WHERE ACTOR = ? AND ENDTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual)";
      this.hashMap.put(
              "myDoneMonth",
              (String)((HashMap)this.dbo.prepareQuery(sql,
                      new String[] { loginModel.getUserCode() }).get(0))
                      .get("COUNT"));

      sql = "SELECT COUNT(*) COUNT FROM EAP_LOG WHERE TYPE='LOGIN' AND REQTIME BETWEEN (select substr(REQTIME,0,5)||to_char(trunc(sysdate, 'd') ,'MM-dd') from dual) and (select substr(REQTIME,0,5)||to_char(trunc(sysdate, 'd') + 6,'MM-dd') from dual)";
      this.hashMap.put("allLoginWeek",
              (String)((HashMap)this.dbo
                      .prepareQuery(sql, new String[0]).get(0)).get("COUNT"));

      sql = "SELECT COUNT(*) COUNT FROM EAP_LOG WHERE TYPE='LOGIN' AND REQTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual)";
      this.hashMap.put("allLoginMonth",
              (String)((HashMap)this.dbo
                      .prepareQuery(sql, new String[0]).get(0)).get("COUNT"));

      sql = "SELECT COUNT(*) COUNT FROM EAP_DONE WHERE ENDTIME BETWEEN (select substr(ENDTIME,0,5)||to_char(trunc(sysdate, 'd') ,'MM-dd') from dual) and (select substr(ENDTIME,0,5)||to_char(trunc(sysdate, 'd') + 6,'MM-dd') from dual)";
      this.hashMap.put("allDoneWeek",
              (String)((HashMap)this.dbo
                      .prepareQuery(sql, new String[0]).get(0)).get("COUNT"));

      sql = "SELECT COUNT(*) COUNT FROM EAP_DONE WHERE ENDTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual)";
      this.hashMap.put("allDoneMonth",
              (String)((HashMap)this.dbo
                      .prepareQuery(sql, new String[0]).get(0)).get("COUNT"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.hashMap;
  }

  public List<HashMap<String, String>> getLoginRankOfficeList(LoginModel loginModel)
  {
    String sql = "SELECT COUNT(*) COUNT,DEPTNAME FROM EAP_LOG A LEFT JOIN (SELECT A.*,B.NAME DEPTNAME FROM EAP_ACCOUNT A,EAP_DEPARTMENT B WHERE A.DEPTCODE = B.CODE) B ON A.USERID=B.CODE WHERE DEPTCODE NOT IN ('zgld','root') AND REQTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual) GROUP BY DEPTNAME ORDER BY COUNT DESC";
    try {
      this.list = this.dbo.prepareQuery(sql, new String[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public List<HashMap<String, String>> getDelayRankOfficeList(LoginModel loginModel)
  {
    String sql = "SELECT COUNT(*) COUNT,B.NAME DEPTNAME FROM EAP_VW_TODO A,EAP_DEPARTMENT B WHERE A.DEPTCODE = B.CODE AND DEPTCODE NOT IN ('zgld','root') AND A.STARTTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual) GROUP BY B.NAME ORDER BY COUNT DESC";
    try {
      this.list = this.dbo.prepareQuery(sql, new String[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public List<HashMap<String, String>> getDoneRankLeaderList(LoginModel loginModel)
  {
    String sql = "SELECT COUNT(*) COUNT,B.NAME NAME FROM EAP_DONE A,(SELECT A.*,B.NAME DEPTNAME FROM EAP_ACCOUNT A,EAP_DEPARTMENT B WHERE A.DEPTCODE = B.CODE) B WHERE A.ACTOR = B.CODE AND B.DEPTCODE NOT IN ('zgld','root') AND B.RANK='0' AND A.ENDTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual) GROUP BY B.NAME ORDER BY COUNT DESC";
    try {
      this.list = this.dbo.prepareQuery(sql, new String[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public List<HashMap<String, String>> getDoneRankOfficerList(LoginModel loginModel)
  {
    String sql = "SELECT COUNT(*) COUNT,B.NAME NAME FROM EAP_DONE A,(SELECT A.*,B.NAME DEPTNAME FROM EAP_ACCOUNT A,EAP_DEPARTMENT B WHERE A.DEPTCODE = B.CODE) B WHERE A.ACTOR = B.CODE AND B.DEPTCODE NOT IN ('zgld','root') AND B.RANK<>'0' AND A.ENDTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual) GROUP BY B.NAME ORDER BY COUNT DESC";
    try {
      this.list = this.dbo.prepareQuery(sql, new String[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public HashMap<String, String> getIncome(LoginModel loginModel) {
    String sql = "SELECT * FROM OA_INCOME WHERE NAME = '光明街道办事处' ORDER BY TIME DESC";
    try {
      this.list = this.dbo.prepareQuery(sql, new String[0]);
      this.hashMap = (this.list.isEmpty() ? new HashMap() :
              (HashMap)this.list.get(0));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.hashMap;
  }

  public List<HashMap<String, String>> getDocuemnt(LoginModel loginModel) {
    String sql = "SELECT * FROM(SELECT * FROM (SELECT VW.ID ID,VW.SENDER SENDER,VW.TAKETIME TAKETIME,VW.THEME THEME,VW.RECIPIENT RECIPIENT,VW.ATTACHMENT ATTACHMENT,VW.BODY BODY, VW.RECIPIENTS RECIPIENTS,VW.RID RID,VW.ISREAD ISREAD,VW.STATUS STATUS,VW.CURRENTTIME CURRENTTIME,TAG.NAME DEPTNAME FROM (SELECT * FROM OA_OUTBOX_VW) VW LEFT JOIN  (SELECT A. CODE CODE,B.NAME NAME FROM EAP_ACCOUNT A,EAP_DEPARTMENT B WHERE A.DEPTCODE = B.CODE) TAG ON VW.SENDER=TAG.CODE) WHERE ID IS NOT NULL and recipients='" +
            loginModel.getUserCode() +
            "')C" +
            " left join eap_account t on t.code = C.SENDER WHERE C.STATUS=0 ORDER BY TAKETIME DESC";
    try {
      this.list = this.dbo.prepareQuery(sql, new String[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public long timetostring(String time)
  {
    long inttime = 0L;
    String[] strtime = null;
    if ((time != null) && (!"".equals(time))) {
      strtime = time.split(" ");
    }

    String str1 = "";
    if ((strtime != null) &&
            (strtime.length > 0)) {
      if (strtime[0].indexOf("-") > 0) {
        String[] str0 = strtime[0].split("-");
        if (str0.length > 0) {
          for (int j = 0; j < str0.length; j++) {
            str1 = str1 + str0[j];
          }
        }
      }

      if ((strtime.length == 2) && (strtime[1].indexOf(":") > 0)) {
        String[] str0 = strtime[1].split(":");
        if (str0.length > 0) {
          for (int j = 0; j < str0.length; j++) {
            str1 = str1 + str0[j];
          }
        }
      }

    }

    if (str1 == "")
      inttime = Long.valueOf("0").longValue();
    else {
      inttime = Long.valueOf(str1).longValue();
    }

    return inttime;
  }

  public ArrayList<HashMap<String, String>> removeRepeat(ArrayList<HashMap<String, String>> wenjianlist)
  {
    boolean flag = true;
    for (int i = 0; i < wenjianlist.size() - 1; i++) {
      for (int j = i; j < wenjianlist.size() - 1; j++)
      {
        if (((String)((HashMap)wenjianlist.get(i)).get("ID"))
                .equals(((HashMap)wenjianlist.get(j + 1)).get("ID"))) {
          String jies1 =
                  (String)((HashMap)wenjianlist.get(i))
                          .get("JIESHOUCODE");
          String jies2 =
                  (String)((HashMap)wenjianlist.get(j + 1))
                          .get("JIESHOUCODE");
          String s1 = "select SN,DEPTCODE from eap_account t WHERE CODE='" +
                  jies1 + "'";
          String s2 = "select SN,DEPTCODE from eap_account t WHERE CODE='" +
                  jies2 + "'";
          ArrayList jies11 = null;
          try {
            jies11 = this.dbo.prepareQuery(s1, null);
          } catch (Exception e) {
            e.printStackTrace();
          }
          ArrayList jies22 = null;
          try {
            jies22 = this.dbo.prepareQuery(s2, null);
          } catch (Exception e) {
            e.printStackTrace();
          }
          if ((jies11.size() <= 0) || (jies22.size() <= 0))
            continue;
          if ("zgld".equals(((HashMap)jies11.get(0))
                  .get("DEPTCODE")))
          {
            if ("zgld".equals(((HashMap)jies22.get(0))
                    .get("DEPTCODE")))
            {
              if (Integer.parseInt(
                      (String)((HashMap)jies11
                              .get(0)).get("SN")) <
                      Integer.parseInt(
                              (String)((HashMap)jies22.get(0))
                                      .get("SN"))) {
                wenjianlist.remove(j + 1);
                flag = false;
                break;
              }
              wenjianlist.remove(i);
              flag = false;
              break;
            }
          }
          if ("zgld".equals(((HashMap)jies11.get(0))
                  .get("DEPTCODE")))
          {
            if (!"zgld".equals(((HashMap)jies22.get(0))
                    .get("DEPTCODE"))) {
              wenjianlist.remove(j + 1);
              flag = false;
              break;
            }
          }
          if (!"zgld".equals(((HashMap)jies11.get(0))
                  .get("DEPTCODE")))
          {
            if ("zgld".equals(((HashMap)jies22.get(0))
                    .get("DEPTCODE"))) {
              wenjianlist.remove(i);
              flag = false;
              break;
            }
          }
          wenjianlist.remove(i);
          flag = false;
          break;
        }
      }

      if (!flag) {
        break;
      }
    }
    if (!flag) {
      removeRepeat(wenjianlist);
    }
    return wenjianlist;
  }

  public List<HashMap<String, String>> getTzList(LoginModel loginModel)
  {
    String sql = "select t.*,l.temporarycode from oa_temporarylogs l, oa_temporaryannouncement t where l.temporaryid=t.id and state >= 1 and l.readtime is null and temporarycode = '" +
            loginModel.getUserCode() + "' ";
    if ((loginModel.getUserCode() == "jianghuiqin") ||
            ("jianghuiqin".equals(loginModel.getUserCode()))) {
      sql = sql + " and t.type='7'";
    }
    sql = sql + "  order by state DESC,uptime DESC,pubtime DESC";
    try {
      this.list = this.dbo.prepareQuery(sql, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public List<HashMap<String, String>> getInboxList(LoginModel loginModel)
  {
    String sql = "SELECT * FROM (SELECT VW.ID ID,VW.SENDER SENDER,TAG.USERNAME USERNAME,VW.TAKETIME TAKETIME,VW.THEME THEME,VW.RECIPIENT RECIPIENT,VW.ATTACHMENT ATTACHMENT,VW.BODY BODY,VW.RECIPIENTS RECIPIENTS,VW.RID RID,VW.ISREAD ISREAD,VW.STATUS STATUS,VW.CURRENTTIME CURRENTTIME,VW.IFCOLLECT IFCOLLECT,TAG.NAME DEPTNAME FROM (SELECT * FROM OA_OUTBOX_VW) VW LEFT JOIN (SELECT A.CODE CODE, A.NAME USERNAME,B.NAME NAME FROM EAP_ACCOUNT A,EAP_DEPARTMENT B WHERE A.DEPTCODE = B.CODE) TAG ON VW.SENDER=TAG.CODE) WHERE ID IS NOT NULL and recipients='" +
            loginModel.getUserCode() + "' order by taketime desc";
    try {
      this.list = this.dbo.prepareQuery(sql, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return this.list;
  }

  public String getDutyDayPeople()
  {
    String datetime = null;
    String people = null;

    String sql = "select a.name, t.* from oa_dutyportal t join eap_account a on a.code=t.people where t.datetime like '%";
    List peopleList = null;
    try {
      if ((this.c.get(7) == 1) ||
              (this.c.get(7) == 7))
      {
        System.out.println("===============白班");
        datetime = this.f.format(new Date()) + "白班";
        people = "白班：";
        peopleList = this.dbo.prepareQuery(sql + datetime +
                "%' order by t.id", null);
        if (peopleList.size() > 0) {
          for (int i = 0; i < peopleList.size(); i++) {
            if (i == peopleList.size() - 1)
              people = people + (String)((HashMap)peopleList.get(i)).get("NAME");
            else {
              people = people + (String)((HashMap)peopleList.get(i)).get("NAME") +
                      ",";
            }
          }

          System.out.println("people=============" + people);
        } else {
          datetime = this.f.format(new Date());
          people = "";
          peopleList = this.dbo.prepareQuery(sql + datetime +
                  "%' order by t.id", null);
          if (peopleList.size() > 0) {
            for (int i = 0; i < peopleList.size(); i++) {
              if (i == peopleList.size() - 1)
                people = people + (String)((HashMap)peopleList.get(i)).get("NAME");
              else {
                people = people + (String)((HashMap)peopleList.get(i)).get("NAME") +
                        ",";
              }
            }

            System.out.println("people=============" + people);
          }
        }
      }
      else {
        System.out.println("===============正常");
        datetime = this.f.format(new Date());
        people = "";
        peopleList = this.dbo.prepareQuery(sql + datetime +
                "%' order by t.id", null);
        if (peopleList.size() > 0) {
          for (int i = 0; i < peopleList.size(); i++) {
            if (i == peopleList.size() - 1)
              people = people + (String)((HashMap)peopleList.get(i)).get("NAME");
            else {
              people = people + (String)((HashMap)peopleList.get(i)).get("NAME") +
                      ",";
            }
          }

          System.out.println("people=============" + people);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return people;
  }

  public String getDutyNightPeople()
  {
    String datetime = null;
    String people = null;

    String sql = "select a.name, t.datetime from oa_dutyportal t join eap_account a on a.code=t.people where t.datetime like '%";
    List peopleList = null;
    try {
      if ((this.c.get(7) == 1) ||
              (this.c.get(7) == 7))
      {
        System.out.println("===============夜班");
        datetime = this.f.format(new Date()) + "夜班";
        people = "夜班：";
        peopleList = this.dbo.prepareQuery(sql + datetime +
                "%' order by t.id", null);
        if (peopleList.size() > 0) {
          for (int i = 0; i < peopleList.size(); i++) {
            if (i == peopleList.size() - 1)
              people = people + (String)((HashMap)peopleList.get(i)).get("NAME");
            else {
              people = people + (String)((HashMap)peopleList.get(i)).get("NAME") +
                      ",";
            }
          }
          System.out.println("people=============" + people);
        } else {
          datetime = this.f.format(new Date());
          people = "";
          peopleList = this.dbo.prepareQuery(sql + datetime +
                  "%' order by t.id", null);
          if (peopleList.size() > 0) {
            for (int i = 0; i < peopleList.size(); i++) {
              if (i == peopleList.size() - 1)
                people = people + (String)((HashMap)peopleList.get(i)).get("NAME");
              else {
                people = people + (String)((HashMap)peopleList.get(i)).get("NAME") +
                        ",";
              }
            }
            System.out.println("people=============" + people);
          }
        }
      } else {
        System.out.println("===============正常");
        datetime = this.f.format(new Date());
        people = "";
        peopleList = this.dbo.prepareQuery(sql + datetime +
                "%' order by t.id", null);
        if (peopleList.size() > 0) {
          for (int i = 0; i < peopleList.size(); i++) {
            if (i == peopleList.size() - 1)
              people = people + (String)((HashMap)peopleList.get(i)).get("NAME");
            else {
              people = people + (String)((HashMap)peopleList.get(i)).get("NAME") +
                      ",";
            }
          }
          System.out.println("people=============" + people);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return people;
  }

  public static void main(String[] args) {
    Calendar c1 = Calendar.getInstance();
    SimpleDateFormat sdf1 = new SimpleDateFormat();
    System.out.println(sdf1.format(c1.getTime()));
    System.out.println(c1.get(7));
  }

  public List<HashMap<String, String>> getGmMessageList()
  {
    String sql = "select * from oa_gmmessage order by rel_time desc";
    try
    {
      this.list = this.dbo.prepareQuery(sql, null);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return this.list;
  }
}
