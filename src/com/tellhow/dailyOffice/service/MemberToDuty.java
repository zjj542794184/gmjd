
package com.tellhow.dailyOffice.service;

import com.google.common.collect.Lists;
import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.framework.model.LoginModel;
import com.siqiansoft.framework.model.PagingModel;
import com.siqiansoft.framework.model.RequestModel;
import com.siqiansoft.framework.model.db.ConditionModel;
import com.tellhow.common.util.DateUtil;

import java.io.FileInputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class MemberToDuty
{
 /* DatabaseBo dbo = new DatabaseBo();

  DutyPlanDetailDao dutydetailBo = new DutyPlanDetailDaoImpl();
  DutyportalDao dutyportalbo = new DutyportalDaoImpl();

  public List<HashMap<String, String>> getCountGroup() throws Exception {
    String sql = "select * from oa_dutygroup";
    List list = this.dbo.query(sql);
    return list;
  }
  public String saveGroup(HashMap<String, String> map) throws Exception {
    String mess = "";
    String names = ((String)map.get("groupname")).replaceAll(",", " ");
    String namekz = ((String)map.get("groupkz")).replaceAll(",", " ");
    String namepeople = ((String)map.get("groupky")).replaceAll(",", " ");
    String namebb = ((String)map.get("groupbby")).replaceAll(",", " ");
    String id = (String)map.get("id");

    String sql = "UPDATE OA_DUTYGROUP SET GROUPNAME='" + names + "',GROUPKZ='" + namekz + "',GROUPKY='" + namepeople + "',GROUPBBY='" + namebb + "' WHERE ID='" + id + "'";
    this.dbo.prepareUpdate(sql, null);

    String groupcode = ((String)map.get("groupcode")).replaceAll(",", " ").trim();
    String groupkzcode = ((String)map.get("groupkzcode")).replaceAll(",", " ").trim();
    String groupkycode = ((String)map.get("groupkycode")).replaceAll(",", " ").trim();
    String groupbbcode = ((String)map.get("groupbbcode")).replaceAll(",", " ").trim();
    System.out.println(groupcode + "==============groupcode======");
    String[] groupsplit = groupcode.split(" ");
    String[] groupkzsplit = groupkzcode.split(" ");
    String[] groupkysplit = groupkycode.split(" ");
    String[] groupbbsplit = groupbbcode.split(" ");
    if (groupsplit.length > 0)
      group(groupsplit, "0", id);
    if (groupkzsplit.length > 0)
      group(groupkzsplit, "1", id);
    if (groupkysplit.length > 0)
      group(groupkysplit, "2", id);
    if (groupbbsplit.length > 0)
      group(groupbbsplit, "3", id);
    return mess;
  }

  public void group(String[] group, String num, String id)
    throws Exception
  {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String nowtime = sdf.format(date);
    String user = group[0].trim();

    if ((group.length > 0) && (user != null) && (user != "")) {
      this.dbo.prepareUpdate("DELETE FROM OA_DUTYGROUPMEMBER WHERE RID='" + id + "' AND STATUS='" + num + "'", null);
      for (int i = 0; i < group.length; i++) {
        Map map2 = new HashMap();
        Map mappp = new HashMap();

        if ((!group[i].equals(null)) && (group[i] != "")) {
          mappp.put("ORGCODE", "root");
          mappp.put("USERCODE", group[i]);
          mappp.put("ROLECODE", "huanbanren");
          mappp.put("TIMESLICE", "timeslice1");
          mappp.put("MODIFYTIME", nowtime);
          this.dbo.insert(mappp, "EAP_USERROLE");
        }
        map2.put("RID", id);
        map2.put("USERCODE", group[i]);
        map2.put("STATUS", num);
        this.dbo.insert(map2, "OA_DUTYGROUPMEMBER");
      }
    }
  }

  public String dutyservicePeople(List<HashMap<String, String>> peoplelist)
  {
    String people = "";
    if (peoplelist.size() > 0) {
      for (int b = 0; b < peoplelist.size(); b++) {
        if (b == 0)
          people = people + (String)((HashMap)peoplelist.get(b)).get("name");
        else {
          people = people + ", " + (String)((HashMap)peoplelist.get(b)).get("name");
        }
      }
    }

    return people;
  }

  public Map<String, String> getDataPeople(List<HashMap<String, String>> list, String tag) throws Exception
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String now = format.format(new Date());
    Map map = new HashMap();
    for (int i = 0; i < list.size(); i++) {
      String sql = "select name from oa_dutygroupmember g,Eap_Contact c where g.usercode=c.code and g.rid=" + 
        (String)((HashMap)list.get(i)).get("ID") + " and g.status=" + tag + " order by g.id asc";
      System.out.println(sql);
      List list2 = this.dbo.query(sql);
      String name = "";

      for (int b = 0; b < list2.size(); b++)
      {
        if (b == 0)
          name = name + (String)((HashMap)list2.get(b)).get("NAME");
        else {
          name = name + ", " + (String)((HashMap)list2.get(b)).get("NAME");
        }
      }

      map.put((String)((HashMap)list.get(i)).get("ID"), name);
    }

    return map;
  }

  public String huanbanpeople(String[] leaderlist, List<HashMap<String, String>> yuanlist, List<HashMap<String, String>> tilist) throws Exception {
    DutyAttendanceService dutyservice = new DutyAttendanceService();
    String leader = "";
    if (leaderlist.length > 0) {
      for (int m = 0; m < leaderlist.length; m++)
      {
        String temp = dutyservice.huanban(leaderlist[m], yuanlist, tilist);
        if ((temp != "") && (!"".equals(temp))) {
          if (m == 0)
            leader = leader + temp;
          else {
            leader = leader + ", " + temp;
          }
        }
        else if (m == 0)
          leader = leader + leaderlist[m];
        else {
          leader = leader + ", " + leaderlist[m];
        }
      }

    }

    return leader;
  }

  public List<HashMap<String, String>> getGroupMember(ConditionModel[] cond, PagingModel pageCtl, RequestModel requestModel)
  {
    DutyAttendanceService dutyservice = new DutyAttendanceService();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String now = format.format(new Date());
    String condition = requestModel.getData() == null ? null : (String)requestModel.getData().get("$list-option-condition");
    String name = requestModel.getData() == null ? null : (String)requestModel.getData().get("$list-option-name");
    try {
      List hashMaps = findDutytimeList(now, condition, name);
      int from = pageCtl.getFrom();
      int to = pageCtl.getTo();
      int pageRows = pageCtl.getPageRows();

      int curPage = pageCtl.getCurPage();
      int rowsCount = hashMaps.size();
      int pageCount = hashMaps.size() / pageRows + (
        hashMaps.size() % pageRows == 0 ? 0 : 1);
      pageCtl.setPageCount(pageCount);
      pageCtl.setRowsCount(rowsCount);
      if (to > hashMaps.size()) {
        to = hashMaps.size();
      }
      pageCtl.setTo(to);
      System.out.println("从第" + from + "条，到第" + to + "条，总共" + rowsCount + 
        "条");
      System.out.println("这是第" + curPage + "页，总共" + pageCount + "页");
      return hashMaps.subList(from - 1, to);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<HashMap<String, String>> findDutytimeList(String now, String condition, String name)
    throws Exception
  {
    String sql = "";
    System.out.println("111111111111111111111111111111111111condtion:" + condition);
    System.out.println("111111111111111111111111111111111111name:" + name);

    if ((("".equals(condition)) || (condition == null)) && (("".equals(name)) || (name == null)))
      sql = "select d.datetime,d.week,d.people,c.code,c.name,d.type, d.shiftleadertel from OA_DUTYPORTAL d,EAP_CONTACT c where d.people=c.code and d.datetime>='" + now + "' order by d.datetime";
    else if ((!"".equals(condition)) && (condition != null) && (("".equals(name)) || (name == null)))
      sql = "select d.datetime,d.week,d.people,c.code,c.name,d.type, d.shiftleadertel from OA_DUTYPORTAL d,EAP_CONTACT c where d.people=c.code and d.datetime like '%" + condition + "%' order by d.datetime";
    else if ((("".equals(condition)) || (condition == null)) && (!"".equals(name)) && (name != null))
      sql = "select d.datetime,d.week,d.people,c.code,c.name,d.type, d.shiftleadertel from OA_DUTYPORTAL d,EAP_CONTACT c  where d.datetime in (select o.datetime from OA_DUTYPORTAL o,EAP_CONTACT e where o.people=e.code and e.name like '%" + name + "%') and d.people=c.code and d.datetime>='" + now + "' order by d.datetime";
    else if ((!"".equals(condition)) && (condition != null) && (!"".equals(name)) && (name != null)) {
      sql = "select d.datetime,d.week,d.people,c.code,c.name,d.type, d.shiftleadertel from OA_DUTYPORTAL d,EAP_CONTACT c  where d.datetime in (select o.datetime from OA_DUTYPORTAL o,EAP_CONTACT e where o.people=e.code and e.name like '%" + name + "%') and d.people=c.code and d.datetime like '%" + condition + "%' order by d.datetime";
    }

    List dutytimeList = this.dbo.query(sql);

    List hashMaps = new ArrayList();
    HashMap map = null;
    HashMap map1 = null;
    HashMap map2 = null;
    String time = "";
    String week = "";
    String code = "";
    String status = "";
    String leader = "";
    String shiftleadertel = "";
    String kz = "";
    String member = "";
    String bbpeople = "";
    String time1 = "";

    for (int i = 0; i < dutytimeList.size(); i++) {
      map1 = new HashMap();
      map = (HashMap)dutytimeList.get(i);

      time = (String)map.get("DATETIME");

      week = (String)map.get("WEEK");

      if (i + 1 < dutytimeList.size())
        time1 = (String)((HashMap)dutytimeList.get(i + 1)).get("DATETIME");
      else {
        time1 = "";
      }

      code = (String)map.get("CODE");
      status = (String)map.get("TYPE");

      if (status.equals("D")) {
        if (!leader.equals("")) leader = leader + ",";
        leader = leader + (String)map.get("NAME");
        shiftleadertel = (String)map.get("SHIFTLEADERTEL");
      }

      if (status.equals("Z")) {
        if (!kz.equals("")) kz = kz + ",";
        kz = kz + (String)map.get("NAME");
      }

      if (status.equals("S")) {
        if (!bbpeople.equals("")) bbpeople = bbpeople + ",";
        bbpeople = bbpeople + (String)map.get("NAME");
      }

      if (status.equals("P")) {
        if (!member.equals("")) member = member + ",";
        member = member + (String)map.get("NAME");
      }
      if (!time.equals(time1)) {
        System.out.println(time1);
        map2 = new HashMap();
        map2.put("time", time);
        map2.put("week", week);
        map2.put("leader", leader);
        map2.put("shiftleadertel", shiftleadertel);
        map2.put("kz", kz);
        map2.put("dbpeople", bbpeople);
        map2.put("member", member);
        hashMaps.add(map2);
        time = "";
        week = "";
        leader = "";
        shiftleadertel = "";
        kz = "";
        bbpeople = "";
        member = "";
      }
    }
    return hashMaps;
  }

  public List<HashMap<String, String>> findDutytimeList1(String now, String code)
    throws Exception
  {
    String sql = "select d.datetime,d.week,d.people,c.code,c.name,d.type from OA_DUTYPORTAL d,EAP_CONTACT c where d.people=c.code and c.code = '" + code + "' and d.datetime>='" + now + "' order by d.datetime";
    List dutytimeList = this.dbo.query(sql);

    List hashMaps = new ArrayList();

    return dutytimeList;
  }

  public void ToDuty(LoginModel login, String startTime, String beginTeam) throws Exception
  {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Calendar c = Calendar.getInstance();
    c.setTime(format.parse(startTime));
    c.add(1, 1);
    String endTime = format.format(c.getTime());
    System.out.println("startTime：" + startTime);
    System.out.println("endTime：" + endTime);
    System.out.println("beginTeam：" + beginTeam);
    int dates = daysBetween(startTime, endTime);
    System.out.println("计算天数:" + dates);

    String[] da = new String[20];
    da = startTime.split("-");
    String yearDa = da[0];
    String monthDa = da[1];
    String dayDa = da[2];
    MemberToDuty td = new MemberToDuty();
    Calendar calendar = new GregorianCalendar(Integer.valueOf(yearDa)
      .intValue(), Integer.valueOf(monthDa).intValue() - 1, 
      Integer.valueOf(dayDa).intValue());
    int yearT = 0;
    int monthT = 0;
    int dayT = 0;

    System.out.println("dates:" + dates);
    String sql = "delete from oa_dutytime";
    this.dbo.prepareUpdate(sql, null);
    for (int j = 0; j <= dates; j++) {
      int year = calendar.get(1);
      int month = calendar.get(2) + 1;
      int dayOfMonth = calendar.get(5);
      System.out.println(year + "/" + month + "/" + dayOfMonth);
      yearT = year;
      monthT = month;
      dayT = dayOfMonth;
      int gro = Integer.valueOf(td.CountGroup()).intValue() + 1;
      System.out.println("gro:" + gro);
      if (Integer.valueOf(beginTeam).intValue() >= gro) {
        beginTeam = "1";
      }
      td.ToUpdateOrNew(yearT, monthT, dayT, beginTeam, login);
      beginTeam = Integer.valueOf(beginTeam).intValue() + 1;
      calendar.add(5, 1);
    }
  }

  public String ToUpdateOrNew(int year, int month, int day, String group, LoginModel login) throws ParseException
  {
    List list = new ArrayList();
    List listportal = new ArrayList();
    String createtime = DateUtil.formatCurrentTime("yyyy-MM-dd hh:mm:ss");

    MemberToDuty mtd = new MemberToDuty();

    String years = year;
    String months = month;
    String days = day;
    System.out.println("years:" + years);
    System.out.println("months:" + months);
    System.out.println("days:" + days);
    System.out.println("group:" + group);
    if (month < 10)
      months = "0" + months;
    if (day < 10)
      days = "0" + days;
    String datetime = years + "-" + months + "-" + days;
    try
    {
      DutyTimeVo dutyTimeVo = new DutyTimeVo();
      dutyTimeVo.setRid(Integer.parseInt(group));
      dutyTimeVo.setTime(datetime);
      this.dbo.insert(dutyTimeVo, "oa_dutytime");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return group;
  }

  public static int daysBetween(String smdate, String bdate) throws ParseException
  {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();
    cal.setTime(sdf.parse(smdate));
    long time1 = cal.getTimeInMillis();
    cal.setTime(sdf.parse(bdate));
    long time2 = cal.getTimeInMillis();
    long between_days = (time2 - time1) / 86400000L;

    return Integer.parseInt(String.valueOf(between_days));
  }

  public String CountGroup() throws Exception {
    String countG = null;
    String sql = "select count(id) as COU from oa_dutygroup t ";
    ArrayList a = this.dbo.query(sql);

    if (a.size() > 0) {
      countG = (String)((HashMap)a.get(0)).get("COU");
    }
    return countG;
  }

  public int FindPeople(String datetime) throws Exception {
    String sql = "select t.*, t.rowid from oa_dutyportal t where t.datetime='" + 
      datetime + "'";
    ArrayList a = this.dbo.query(sql);

    return a.size();
  }

  public String findcode(String name) throws Exception {
    String code = null;
    String sql = "select * from EAP_ACCOUNT where name='" + name + "'";
    ArrayList a = this.dbo.query(sql);
    if (a.size() > 0) {
      code = (String)((HashMap)a.get(0)).get("CODE");
    }
    return code;
  }

  public void deteleduty(DutyplanModel dutyplan) throws Exception {
    long id = dutyplan.getId();
    long year = dutyplan.getYear();
    long month = dutyplan.getMonth();
    this.dbo.prepareUpdate("delete oa_dutyplan  WHERE id= " + id + " ", 
      null);
    this.dbo.prepareUpdate("delete oa_dutyplandetail WHERE dutyplanid= " + 
      id + " ", null);
    this.dbo.prepareUpdate("delete oa_dutyportal  WHERE year= " + year + 
      " and month=" + month + " ", null);
  }

  public static String getWeekOfDate(Date date) {
    String[] weekOfDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
    Calendar calendar = Calendar.getInstance();
    if (date != null) {
      calendar.setTime(date);
    }
    int w = calendar.get(7) - 1;
    if (w < 0) {
      w = 0;
    }
    return weekOfDays[w];
  }

  public void importDutyGroupMenber(String id, LoginModel login, RequestModel req) throws Exception
  {
    AttachDao attachDao = new AttachDaoImpl();
    if ((id == null) || ("".equals(id))) {
      req.setMessage("值班表不能为空！");
    }

    String sql = "select * from eap_attach where pk='" + id + "'";
    ArrayList list = this.dbo.query(sql);

    String savepath = (String)((HashMap)list.get(0)).get("SAVEPATH");
    String savefile = (String)((HashMap)list.get(0)).get("SAVEFILE");
    if ((savefile == null) || (savepath == null)) {
      req.setMessage("值班表不能为空！");
    }
    String message = getExcel(savepath + "/" + savefile);
    if (message != null)
      req.setMessage(message);
    else
      req.setMessage("用户值班表导入成功！");
  }

  public String publicExcel(HSSFCell hssfCell)
  {
    String value = null;
    if (hssfCell.getCellType() == 0)
    {
      Date d = hssfCell.getDateCellValue();
      DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
      value = formater.format(d);
    }
    else {
      switch (hssfCell.getCellType()) {
      case 0:
        value = hssfCell.getNumericCellValue();
        break;
      case 1:
        value = hssfCell.getStringCellValue();
        break;
      case 2:
      case 3:
      }

    }

    return value;
  }

  public String getExcel(String pathname)
    throws Exception
  {
    HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(pathname));

    String message = "";
    if (workbook.getNumberOfSheets() != 0) {
      this.dutyportalbo.removes();
    }
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      if (workbook.getSheetAt(i).getPhysicalNumberOfRows() > 0) {
        HSSFSheet childSheet = workbook.getSheetAt(i);

        if (childSheet.getRow(2).getCell(0) == null) {
          continue;
        }
        String num = publicExcel(childSheet.getRow(2).getCell(0));
        if (num == null) {
          return "值班人员不能为空";
        }

        System.out.println(childSheet.getLastRowNum() + "/////////////////////////////////最后一行行数");
        for (int rowi = 2; rowi <= childSheet.getLastRowNum(); rowi++)
        {
          Row row = childSheet.getRow(rowi);

          if (row == null) {
            continue;
          }
          Cell cell = row.getCell(0);
          String names = "";
          if (cell != null)
          {
            names = cell.toString();
          }
          String[] leadersplit = names.split(" ");
          List leaderlist = Lists.newArrayList();
          List leaderlist2 = Lists.newArrayList();
          List leaderlist3 = Lists.newArrayList();

          leaderlist = utilName(leadersplit, leaderlist, leaderlist2, leaderlist3, i);

          Cell cell1 = row.getCell(1);
          String namekz = "";
          if (cell1 != null)
          {
            namekz = cell1.toString();
          }
          String[] kzsplit = namekz.split(" ");
          List kzlist = Lists.newArrayList();
          List kzlist2 = Lists.newArrayList();
          List kzlist3 = Lists.newArrayList();

          kzlist = utilName(kzsplit, kzlist, kzlist2, kzlist3, i);

          Cell cell2 = row.getCell(2);
          String namepeople = "";
          if (cell2 != null)
          {
            namepeople = cell2.toString();
          }
          String[] namesplit = namepeople.split(" ");
          List strlist = Lists.newArrayList();
          List strlist2 = Lists.newArrayList();
          List strlist3 = Lists.newArrayList();

          strlist = utilName(namesplit, strlist, strlist2, strlist3, i);

          Cell cell3 = row.getCell(3);
          String namebb = "";
          List list = Lists.newArrayList();
          if (cell3 != null)
          {
            namebb = cell3.toString();
            String[] namesplit1 = namebb.split(" ");
            System.out.println("namebb+++++++++++++++++++++++++++++++++++++++;" + namebb);
            List list2 = Lists.newArrayList();
            List list3 = Lists.newArrayList();
            list = utilName(namesplit1, list, list2, list3, i);
            System.out.println("namebb=========================================;" + namebb);
          }

          System.out.println(names + "=================names===========rowi===" + rowi);

          message = this.dutyportalbo.importDutyGroupMenber(names, leaderlist, kzlist, strlist, list);
          message = this.dutyportalbo.importDutyGroupMenbers(names, namekz, namepeople, namebb);
        }
      }
    }
    return message;
  }

  public List<String> utilName(String[] namesplit, List<String> list1, List<String> list2, List<String> list3, int rowi) {
    for (String strings : namesplit) {
      list1.add(strings);
    }

    for (String strs : list1) {
      if ((strs.equals("")) || (strs == null)) {
        list2.add(strs);
      }
    }

    list1.removeAll(list2);

    for (int j = 0; j < list1.size(); j++) {
      if ((((String)list1.get(j)).length() != 1) || 
        (j == list1.size() - 1)) continue;
      name = (String)list1.get(j) + (String)list1.get(j + 1);
      list1.add(j, name);
      j += 2;
    }

    for (Object name = list1.iterator(); ((Iterator)name).hasNext(); ) { String strs = (String)((Iterator)name).next();
      if ((strs.length() != 1) && (strs.length() != 0) && 
        (strs != null) && (!"".equals(strs))) continue;
      list3.add(strs);
    }

    list1.removeAll(list3);
    for (name = list1.iterator(); ((Iterator)name).hasNext(); ) { String strs = (String)((Iterator)name).next();
      System.out.println(strs + "////////////////////////////////////////////所有人名" + rowi);
    }

    return (List<String>)list1;
  }

  public HashMap<String, String> returnform() throws Exception {
    HashMap map = new HashMap();
    map.put("attachment", "");
    map.put("starttime", "");
    map.put("endtime", "");
    map.put("beginteam", "");
    return map;
  }*/
}
