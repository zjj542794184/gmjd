
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

public class MainMenuImplc implements MainMenu {
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
			System.out.println("================list.size()="
					+ this.list.size());
			if (this.list.size() > 0) {
				for (int i = 0; i < this.list.size(); i++) {
					String typename = (String) ((HashMap) this.list.get(i))
							.get("TYPENAME");
					System.out.println("=============typename=" + typename);
					String title = (String) ((HashMap) this.list.get(i))
							.get("TITLE");
					System.out.println("=============titlle=" + title);
					if (title.length() > 20) {
						title = title.substring(0, 20) + "...";
					}
					((HashMap) this.list.get(i)).put("CONTENT", typename
							+ "&nbsp;&nbsp;&nbsp;&nbsp;" + title);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.list;
	}

	public List<HashMap<String, String>> getCountGroup() throws Exception {
		String sql = "select * from oa_dutygroup";
		List list = this.dbo.query(sql);
		return list;
	}

	public String lunhuan(String Rid, List<HashMap<String, String>> yuan,
			List<HashMap<String, String>> ti) {
		try {
			String ridsql = "select name from oa_dutygroupmember g,Eap_Contact c where g.usercode=c.code and g.rid="
					+ Rid + " and g.status=0 order by g.id asc";

			List memberList = this.dbo.query(ridsql);
			String member = "";
			List<String> list = new ArrayList();
			for (int i = 0; i < memberList.size(); i++) {
				boolean flag = true;
				for (int j = 0; j < yuan.size(); j++) {
					System.out.println((String) ((HashMap) yuan.get(j))
							.get("PROPOSER")
							+ "-------"
							+ (String) ((HashMap) memberList.get(i))
									.get("NAME"));
					if (((String) ((HashMap) yuan.get(j)).get("PROPOSER"))
							.equals(((HashMap) memberList.get(i)).get("NAME"))) {
						if (i == 0) {
							member = member
									+ (String) ((HashMap) memberList.get(i))
											.get("NAME")
									+ "["
									+ (String) ((HashMap) yuan.get(j))
											.get("REPLACER") + "]";
						} else {
							member = member
									+ ", "
									+ (String) ((HashMap) memberList.get(i))
											.get("NAME")
									+ "["
									+ (String) ((HashMap) yuan.get(j))
											.get("REPLACER") + "]";
						}
						list.add((String) ((HashMap) memberList.get(i))
								.get("NAME"));
						flag = false;
						break;
					}
				}
				for (int j = 0; j < ti.size(); j++) {
					boolean isalready = false;
					for (String string : list) {
						if (((String) ((HashMap) ti.get(j)).get("REPLACER"))
								.equals(string)) {
							isalready = true;
							break;
						}
					}
					if (!isalready) {
						if (((String) ((HashMap) ti.get(j)).get("REPLACER"))
								.equals(((HashMap) memberList.get(i))
										.get("NAME"))) {
							if (i == 0) {
								member = member
										+ (String) ((HashMap) memberList.get(i))
												.get("NAME")
										+ "["
										+ (String) ((HashMap) ti.get(j))
												.get("PROPOSER") + "]";
							} else {
								member = member
										+ ", "
										+ (String) ((HashMap) memberList.get(i))
												.get("NAME")
										+ "["
										+ (String) ((HashMap) ti.get(j))
												.get("PROPOSER") + "]";
							}
							flag = false;
							break;
						}
					}
				}
				if (flag) {
					if (i == 0) {
						member = member
								+ (String) ((HashMap) memberList.get(i))
										.get("NAME");
					} else {
						member = member
								+ ", "
								+ (String) ((HashMap) memberList.get(i))
										.get("NAME");
					}
				}
			}
			String sql = "select * from oa_dutygroup t where id=" + Rid;
			List lists = this.dbo.query(sql);
			String tag = "";
			if (lists.size() > 0) {
				tag = ((String) ((HashMap) lists.get(0)).get("GROUPNAME"))
						.replace(" ", ",")
						+ ","
						+ ((String) ((HashMap) lists.get(0)).get("GROUPKZ"))
								.replace(" ", ",");
			}
			String[] leaders = tag.split(",");
			String zgld = "";
			for (int i = 0; i < leaders.length; i++) {
				boolean temp = true;
				for (int j = 0; j < yuan.size(); j++) {
					System.out.println((String) ((HashMap) yuan.get(j))
							.get("PROPOSER") + "-------" + leaders[i]);
					if (((String) ((HashMap) yuan.get(j)).get("PROPOSER"))
							.equals(leaders[i])) {
						if (i == 0) {
							zgld = zgld
									+ leaders[i]
									+ "["
									+ (String) ((HashMap) yuan.get(j))
											.get("REPLACER") + "]";
						} else {
							zgld = zgld
									+ ", "
									+ leaders[i]
									+ "["
									+ (String) ((HashMap) yuan.get(j))
											.get("REPLACER") + "]";
						}
						temp = false;
						break;
					}
				}
				for (int j = 0; j < ti.size(); j++) {
					System.out.println((String) ((HashMap) ti.get(j))
							.get("REPLACER") + "-------" + leaders[i]);
					if (((String) ((HashMap) ti.get(j)).get("REPLACER"))
							.equals(leaders[i])) {
						if (i == 0) {
							zgld = zgld
									+ leaders[i]
									+ "["
									+ (String) ((HashMap) ti.get(j))
											.get("PROPOSER") + "]";
						} else {
							zgld = zgld
									+ ", "
									+ leaders[i]
									+ "["
									+ (String) ((HashMap) ti.get(j))
											.get("PROPOSER") + "]";
						}
						temp = false;
						break;
					}
				}
				if (temp) {
					if (i == 0) {
						zgld = zgld + leaders[i];
					} else {
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
				String sql = "select name from oa_dutygroupmember g,Eap_Contact c where g.usercode=c.code and g.rid="
						+ (String) ((HashMap) list.get(i)).get("ID")
						+ "and g.status=0 order by g.id asc";

				System.out.println(sql);
				List list2 = this.dbo.query(sql);
				String member = "";

				String[] leader = ((String) ((HashMap) list.get(i))
						.get("GROUPNAME")).split(" ");
				String[] kz = ((String) ((HashMap) list.get(i)).get("GROUPKZ"))
						.split(" ");
				for (int m = 0; m < leader.length; m++) {
					member = leader[m] + ",";
				}
				for (int n = 0; n < kz.length; n++) {
					member = member + kz[n] + ",";
				}
				for (int j = 0; j < list2.size(); j++) {
					if (j == 0) {
						member = member
								+ (String) ((HashMap) list2.get(j)).get("NAME");
					} else {
						member = member + ", "
								+ (String) ((HashMap) list2.get(j)).get("NAME");
					}
				}
				System.out.println("今日值班======" + member);
				groupMenber.put(((HashMap) list.get(i)).get("ID"), member);
			}
			String sql = "select * from oa_dutytime where time = '" + this.time
					+ "'";

			sql = sql + " order by time asc";
			List dutytimeList = this.dbo.query(sql);
			List hashMaps = new ArrayList();
			for (int i = 0; i < dutytimeList.size(); i++) {
				String yuanSql = "select * from OA_APPLY_SHIFT where PROPOSERTIME='"
						+ (String) ((HashMap) dutytimeList.get(i)).get("TIME")
						+ "' and state='0'";

				List yuanlist = this.dbo.query(yuanSql);
				System.out.println("换班人员:" + yuanlist.size());

				String tiSql = "select * from OA_APPLY_SHIFT where REPLACERTIME='"
						+ (String) ((HashMap) dutytimeList.get(i)).get("TIME")
						+ "' and state='0'";

				List tilist = this.dbo.query(tiSql);
				System.out.println("替班人员：" + tilist.size());
				HashMap map = new HashMap();
				if ((yuanlist.size() != 0) || (tilist.size() != 0)) {
					String member = lunhuan(
							(String) ((HashMap) dutytimeList.get(i)).get("RID"),
							yuanlist, tilist);
					System.out.println(member);
					String[] qq = member.split(",");
					for (int j = 0; j < qq.length; j++) {
						map = new HashMap();
						map.put("member", qq[j]);
						hashMaps.add(map);
					}
				} else if ((yuanlist.size() == 0) && (tilist.size() == 0)) {
					String time = (String) ((HashMap) dutytimeList.get(i))
							.get("TIME");
					String rid = (String) ((HashMap) dutytimeList.get(i))
							.get("RID");
					System.out.println("报错集合==============="
							+ (String) groupMenber.get(((HashMap) dutytimeList
									.get(i)).get("RID")));
					String mems = "";
					if (groupMenber.get(((HashMap) dutytimeList.get(i))
							.get("RID")) != null) {
						mems = (String) groupMenber.get(((HashMap) dutytimeList
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
				System.out.println((String) ((HashMap) hashMaps.get(j))
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
				String sql = "select name from oa_dutygroupmember g,Eap_Contact c where g.usercode=c.code and g.rid="
						+ (String) ((HashMap) list.get(i)).get("ID")
						+ "and g.status=1 order by g.id asc";

				System.out.println(sql);
				List list2 = this.dbo.query(sql);
				String member = "";
				for (int j = 0; j < list2.size(); j++) {
					if (j == 0) {
						member = member
								+ (String) ((HashMap) list2.get(j)).get("NAME");
					} else {
						member = member + ", "
								+ (String) ((HashMap) list2.get(j)).get("NAME");
					}
				}
				System.out.println("备班员======" + member);
				groupMenber.put(((HashMap) list.get(i)).get("ID"), member);
			}
			String sql = "select * from oa_dutytime where time = '" + this.time
					+ "'";

			sql = sql + " order by time asc";
			List dutytimeList = this.dbo.query(sql);
			List hashMaps = new ArrayList();
			for (int i = 0; i < dutytimeList.size(); i++) {
				HashMap map = new HashMap();

				String member = "";
				if (groupMenber.get(((HashMap) dutytimeList.get(i)).get("RID")) != null) {
					member = (String) groupMenber.get(((HashMap) dutytimeList
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
				System.out.println((String) ((HashMap) hashMaps.get(j))
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

	private List<HashMap<String, String>> getList(
			List<HashMap<String, String>> list, LoginModel loginModel) {
		List list2 = new ArrayList();
		HashMap hashMap = null;
		for (HashMap hashMap2 : list) {
			System.out.println((String) hashMap2.get("TIME"));
		}
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String now = format.format(new Date());
			Date nowDate = format.parse(now);
			String noDuty = "select PROPOSERTIME,REPLACERTIME from OA_APPLY_SHIFT where USERCODE=? and PROPOSERTIME<? and REPLACERTIME>=?";
			List list3 = this.dbo.prepareQuery(noDuty, new String[] {
					loginModel.getUserCode(), now, now });
			for (int j = 0; j < list3.size(); j++) {
				hashMap = new HashMap();
				hashMap.put("TIME",
						((HashMap) list3.get(j)).get("REPLACERTIME"));
				list2.add(hashMap);
			}
			String noDuty2 = "select PROPOSERTIME,REPLACERTIME from OA_APPLY_SHIFT where REPLACERCODE=? and PROPOSERTIME>? and REPLACERTIME<=?";
			list3 = this.dbo.prepareQuery(noDuty2,
					new String[] { loginModel.getUserCode(), now, now });
			for (int j = 0; j < list3.size(); j++) {
				hashMap = new HashMap();
				hashMap.put("TIME",
						((HashMap) list3.get(j)).get("PROPOSERTIME"));
				list2.add(hashMap);
			}
			for (int i = 0; i < list.size(); i++) {
				hashMap = new HashMap();
				String sql = "select PROPOSERTIME,REPLACERTIME from OA_APPLY_SHIFT where USERCODE=? and PROPOSERTIME=? and state=0";
				List replace = this.dbo.prepareQuery(sql,
						new String[] { loginModel.getUserCode(),
								(String) ((HashMap) list.get(i)).get("TIME") });

				String sqlti = "select PROPOSERTIME,REPLACERTIME from OA_APPLY_SHIFT where REPLACERCODE=? and REPLACERTIME=? and state=0";
				List tiplace = this.dbo.prepareQuery(sqlti,
						new String[] { loginModel.getUserCode(),
								(String) ((HashMap) list.get(i)).get("TIME") });
				if ((replace.size() == 0) && (tiplace.size() == 0)) {
					System.out.println((String) ((HashMap) list.get(i))
							.get("TIME"));
					hashMap.put("TIME", ((HashMap) list.get(i)).get("TIME"));
				}
				if (replace.size() > 0) {
					Date date = format
							.parse((String) ((HashMap) replace.get(0))
									.get("REPLACERTIME"));
					if (!date.before(nowDate)) {
						hashMap.put("TIME",
								((HashMap) replace.get(0)).get("REPLACERTIME"));
					}
				} else if (tiplace.size() > 0) {
					Date date = format
							.parse((String) ((HashMap) tiplace.get(0))
									.get("PROPOSERTIME"));
					if (!date.before(nowDate)) {
						hashMap.put("TIME",
								((HashMap) tiplace.get(0)).get("PROPOSERTIME"));
					}
				} else {
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

			String sql1 = "select rank from eap_account t  where code='"
					+ loginModel.getUserCode() + "'";
			List list1 = this.dbo.query(sql1);
			if (list1.size() > 0) {
				if (((String) ((HashMap) list1.get(0)).get("RANK")).equals("2")) {
					String sql = "select  * from oa_dutytime where rid in (select rid from oa_dutygroupmember where usercode=?   ) and time>=?  order by time asc";
					List list = new ArrayList();
					list = this.dbo.prepareQuery(sql,
							new String[] { loginModel.getUserCode(), nowDate });
					return getMyDutyList(getList(list, loginModel));
				}
				String sql = "select  * from oa_dutytime where time>=? and rid =(select id from oa_dutygroup where groupname like '%"
						+ loginModel.getUserName()
						+ "%' or groupkz like '%"
						+ loginModel.getUserName() + "%')  order by time asc";
				List list = new ArrayList();
				list = this.dbo.prepareQuery(sql, new String[] { nowDate });

				return getMyDutyList(getList(list, loginModel));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<HashMap<String, String>> getMyDutyList(
			List<HashMap<String, String>> list) throws ParseException {
		for (HashMap hashMap : list) {
			HashMap mapduty = new HashMap();
			String TIME = (String) hashMap.get("TIME");
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
		String sql2 = "SELECT COUNT(*) COUNT FROM EAP_DONE WHERE ORGCODE = ? AND ACTOR = ? AND ENDTIME LIKE'%"
				+ this.time + "%'";
		try {
			List list1 = this.dbo.prepareQuery(
					sql1,
					new String[] { loginModel.getOrgCode(),
							loginModel.getUserCode() });

			List list2 = this.dbo.prepareQuery(
					sql2,
					new String[] { loginModel.getOrgCode(),
							loginModel.getUserCode() });

			double todo = Double.parseDouble((String) ((HashMap) list1.get(0))
					.get("COUNT"));
			double done = Double.parseDouble((String) ((HashMap) list2.get(0))
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
			this.list = ((List) this.sbo.getMyFlow(loginModel).get("flows"));
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
				String flowstarttime = (String) ((HashMap) this.list.get(i))
						.get("FLOWSTARTTIME");
				((HashMap) this.list.get(i)).put("FLOWSTARTTIME",
						flowstarttime.substring(0, 10));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.list;
	}

	public List<HashMap<String, String>> getTodoList(LoginModel loginModel,
			RequestModel requestModel, PagingModel pagingModel) {
		int from = pagingModel.getFrom();
		int to = pagingModel.getTo();
		int pageRows = pagingModel.getPageRows();

		int curPage = pagingModel.getCurPage();
		HashMap map = null;
		ArrayList lists = new ArrayList();

		String title = requestModel.getData() == null ? null
				: (String) requestModel.getData().get("$list-option-title");
		String flowname = requestModel.getData() == null ? null
				: (String) requestModel.getData().get("$list-option-flowname");

		// System.out.println("flowcode==========-------------==========="+flowcode);

		
		
		
	
		ArrayList listzong = new ArrayList();

		listzong.addAll(lists);

		try {
			

/*
			 * 便民电话待办工作
			 */

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
					" A.ID = B.HID and b.CHECKCODE = '"+userCode+"') t where 1=1";
			List<HashMap<String,String>> list11 = dbo.prepareQuery(sql1,null);
			String nodestatus = list11.get(0).get("NODESTATUS");
			System.out.println("获取节点状态："+nodestatus);
			String sql2 = "select ID ,TASKID,TITLE,FINISHTIME,CASE WHEN DEPTNAME1 IS NOT NULL THEN DEPTNAME1 ELSE DEPTNAME" +
					" END DEPTNAME,TERMTIME,TASKSTAGE,NODESTATUS,STATUS,CHECKCODE,CHECKTIME,CREATETIME from (SELECT a.id id,a.taskid taskid,a.title title," +
					" a.finishtime finishtime,case when a.HANDLEKESHI is null  then a.deptname when a.deptname is null then a.HANDLEKESHI when a.deptname is not null" +
					" and a.HANDLEKESHI is not null then a.HANDLEKESHI||','|| a.deptname end deptname,CASE WHEN A.HANDLEDEPTNAME IS NULL THEN A.HANDLEDEPARTNAME" +
					" WHEN A.HANDLEDEPARTNAME IS NULL THEN A.HANDLEDEPTNAME WHEN A.HANDLEDEPARTNAME   IS NOT NULL AND A.HANDLEDEPTNAME IS NOT NULL" +
					" THEN A.HANDLEDEPTNAME ||',' || A.HANDLEDEPARTNAME  END DEPTNAME1,a.termtime termtime,a.taskstage taskstage,b.nodestatus nodestatus, " +
					" b.status status,b.checkcode checkcode,b.checktime checktime,a.createtime createtime from TELEPHONE_BASIC a,TELEPHONE_HANDLE b where " +
					" A.ID = B.HID and b.CHECKCODE = '"+userCode+"') t where 1=1 and NODESTATUS <> '"+nodestatus+"'";
			List<HashMap<String,String>> list22 = dbo.prepareQuery(sql2,null);
			String value = "";
			if(list22.size()>0){
				if(!nodestatus.equals("17") && !nodestatus.equals("18")){
					//节点状态不一样
					value = "and b.id in (select max(b.id)bid from TELEPHONE_BASIC a,TELEPHONE_HANDLE b " +
							" where A.ID = B.HID and b.CHECKCODE = '"+userCode+"' group by hid)";
				}else{
					String sql3 = "select ID ,TASKID,TITLE,FINISHTIME,CASE WHEN DEPTNAME1 IS NOT NULL THEN DEPTNAME1 ELSE DEPTNAME" +
							" END DEPTNAME,TERMTIME,TASKSTAGE,NODESTATUS,STATUS,CHECKCODE,CHECKTIME,CREATETIME from (SELECT a.id id,a.taskid taskid,a.title title," +
							" a.finishtime finishtime,case when a.HANDLEKESHI is null  then a.deptname when a.deptname is null then a.HANDLEKESHI when a.deptname is not null" +
							" and a.HANDLEKESHI is not null then a.HANDLEKESHI||','|| a.deptname end deptname,CASE WHEN A.HANDLEDEPTNAME IS NULL THEN A.HANDLEDEPARTNAME" +
							" WHEN A.HANDLEDEPARTNAME IS NULL THEN A.HANDLEDEPTNAME WHEN A.HANDLEDEPARTNAME   IS NOT NULL AND A.HANDLEDEPTNAME IS NOT NULL" +
							" THEN A.HANDLEDEPTNAME ||',' || A.HANDLEDEPARTNAME  END DEPTNAME1,a.termtime termtime,a.taskstage taskstage,b.nodestatus nodestatus, " +
							" b.status status,b.checkcode checkcode,b.checktime checktime,a.createtime createtime from TELEPHONE_BASIC a,TELEPHONE_HANDLE b where " +
							" A.ID = B.HID and b.CHECKCODE = '"+userCode+"') t where 1=1 and NODESTATUS not in ('17','18')";
					List<HashMap<String,String>> list33 = dbo.prepareQuery(sql3,null);
					if(list33.size()>0){
						value = "and b.id in (select max(b.id)bid from TELEPHONE_BASIC a,TELEPHONE_HANDLE b " +
								" where A.ID = B.HID and b.CHECKCODE = '"+userCode+"' group by hid)";
					}else{
						value = "and 1=1";
					}

				}

			}
			String phoneSql = "SELECT ID INSTANCEID,TITLE title,'便民电话' as FLOWNAME,"
					+ "case when TASKSTAGE = '6' or status = '已审核' or (status = '未审核' and checktime is not null) then 'convenienttelephone/taskmanagement.cmd?$ACTION=co4' "
					+ "|| '&' || 'id=' || id || '&' || 'nodestatus=' || nodestatus else  'convenienttelephone/taskmanagement.cmd?$ACTION=c02'"
					+ " || '&' || 'id=' || id || '&' || 'nodestatus=' || nodestatus || '&' || 'bl=' || bl end url,"
					+ "CHECKCODE,CHECKTIME,CREATETIME,ASSIGNERNAME,STARTTIME,FAQINAME,id,nodestatus,bl sponsorname FROM "
					+ "(SELECT a.id id,a.taskid taskid,a.title title,a.finishtime finishtime,"
					+ " a.termtime termtime,b.BL"
					+ "a.taskstage taskstage, b.nodestatus nodestatus,b.status status,b.checkcode checkcode,"
					+ "b.checktime checktime,a.createtime createtime,b.ASSIGNERNAME,b.BL,a.STARTTIME,a.FAQINAME "
					+ "FROM TELEPHONE_BASIC a,TELEPHONE_HANDLE b WHERE A.ID      = B.HID "
					+ "AND b.CHECKCODE = '"+userCode+"' "+value
					+ " ) t WHERE 1=1 ORDER BY t.createtime DESC";
			phoneList = dbo.prepareQuery(phoneSql, null);
			System.out.println("获取便民电话待办工作列表为："+phoneList);
			//便民电话
			if(phoneList.size()>0){
				listzong.addAll(phoneList);
			}

			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < listzong.size() - 1; i++) {
			for (int j = 0; j < listzong.size() - i - 1; j++) {
				// System.out.println("===========FLOWNAME============"+(String)((HashMap)listzong.get(j)).get("FLOWNAME"));
				// System.out.println("===========STARTTIME============"+(String)((HashMap)listzong.get(j)).get("STARTTIME"));
				if (((!"".equals(((HashMap) listzong.get(j)).get("STARTTIME"))) || (((HashMap) listzong
						.get(j)).get("STARTTIME") != null))
						&& ((!"".equals(((HashMap) listzong.get(j + 1))
								.get("STARTTIME"))) || (((HashMap) listzong
								.get(j + 1)).get("STARTTIME") != null))
						&& (timetostring((String) ((HashMap) listzong.get(j))
								.get("STARTTIME")) < timetostring((String) ((HashMap) listzong
								.get(j + 1)).get("STARTTIME")))) {
					HashMap mapp = (HashMap) listzong.get(j + 1);
					HashMap mapp2 = (HashMap) listzong.get(j);
					listzong.remove(listzong.get(j));
					listzong.add(j, mapp);
					listzong.remove(listzong.get(j + 1));
					listzong.add(j + 1, mapp2);
				}
			}
		}
		if ((from == 1) && (to == 0)) {
			to = listzong.size();
		}
		int rowsCount = listzong.size();
		int pageCount = listzong.size() / pageRows
				+ (listzong.size() % pageRows == 0 ? 0 : 1);

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

		int days = ((int) (caled.getTime().getTime() / 1000L) - (int) (calst
				.getTime().getTime() / 1000L)) / 3600 / 24;
		if (days >= 0) {
			days++;
		}
		return days;
	}

	// 收藏工作列表
	public List<HashMap<String, String>> getDoneTodoList(LoginModel loginModel,
			RequestModel requestModel, PagingModel pagingModel) {
		int from = pagingModel.getFrom();
		int to = pagingModel.getTo();
		int pageRows = pagingModel.getPageRows();

		int curPage = pagingModel.getCurPage();
		String title = requestModel.getData() == null ? null
				: (String) requestModel.getData().get("$list-option-title");

		String folwname = requestModel.getData() == null ? null
				: (String) requestModel.getData()
						.get("$subtab-option-flowname");

		String sql = "SELECT * FROM (SELECT * FROM EAP_VW_DONE WHERE WID IN (SELECT MAX(WID) FROM EAP_VW_DONE GROUP BY INSTANCEID)) A , OA_FLOW_COLLECT B WHERE A.INSTANCEID = B.INSTANCEID AND B.USERCODE=?  and b.wid is not  null  order by B.ID desc";
		try {
			this.list = this.dbo.prepareQuery(sql,
					new String[] { loginModel.getUserCode() });

			GetReal.realTitle(this.list);
			for (int i = 0; i < this.list.size(); i++) {
				if ((title != null)
						&& (((String) ((HashMap) this.list.get(i)).get("TITLE"))
								.indexOf(title) == -1)) {
					this.list.remove(i);
					i--;
				} else {
					String flowstarttime = (String) ((HashMap) this.list.get(i))
							.get("FLOWSTARTTIME");
					((HashMap) this.list.get(i)).put("FLOWSTARTTIME",
							flowstarttime.substring(0, 10));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList listzong = new ArrayList();

		String wenjiansql = "select * from oa_documentcircularize where id in(select t.instanceid from oa_flow_collect t where wid is null  and usercode='"
				+ loginModel.getUserCode() + "')";
		try {
			ArrayList wenjianlist = this.dbo.prepareQuery(wenjiansql, null);
			if (wenjianlist.size() > 0) {
				wenjianlist = removeRepeat(wenjianlist);
				for (int i = 0; i < wenjianlist.size(); i++) {
					HashMap map = new HashMap();
					String jieshoutime = "select time time from oa_messagecenter t where t.reception='"
							+ loginModel.getUserCode()
							+ "' and url like '%id="
							+ (String) ((HashMap) wenjianlist.get(i)).get("ID")
							+ "' and title='"
							+ (String) ((HashMap) wenjianlist.get(i))
									.get("TITLE") + "' order by id desc ";
					ArrayList listtime = this.dbo.prepareQuery(jieshoutime,
							null);
					if (listtime.size() > 0) {
						map.put("FLOWSTARTTIME",
								((HashMap) listtime.get(0)).get("TIME"));
					}
					String title2 = (String) ((HashMap) wenjianlist.get(i))
							.get("TITLE");
					String flowname = "文件传阅";
					String status = "";
					if ("1".equals(((HashMap) wenjianlist.get(i)).get("STATE"))) {
						status = "I";
					} else {
						status = "F";
					}
					map.put("INSTANCEID",
							((HashMap) wenjianlist.get(i)).get("ID"));
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
			for (int j = 0; j < listzong.size() - i - 1; j++) {
				if (((!"".equals(((HashMap) listzong.get(j))
						.get("FLOWSTARTTIME"))) || (((HashMap) listzong.get(j))
						.get("FLOWSTARTTIME") != null))
						&& ((!"".equals(((HashMap) listzong.get(j + 1))
								.get("FLOWSTARTTIME"))) || (((HashMap) listzong
								.get(j + 1)).get("FLOWSTARTTIME") != null))) {
					if (timetostring((String) ((HashMap) listzong.get(j))
							.get("FLOWSTARTTIME")) < timetostring((String) ((HashMap) listzong
							.get(j + 1)).get("FLOWSTARTTIME"))) {
						HashMap mapp = (HashMap) listzong.get(j + 1);
						HashMap mapp2 = (HashMap) listzong.get(j);
						listzong.remove(listzong.get(j));
						listzong.add(j, mapp);
						listzong.remove(listzong.get(j + 1));
						listzong.add(j + 1, mapp2);
					}
				}
			}
		}
		if ((from == 1) && (to == 0)) {
			to = listzong.size();
		}
		int rowsCount = listzong.size();
		int pageCount = listzong.size() / pageRows
				+ (listzong.size() % pageRows == 0 ? 0 : 1);
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

	public List<HashMap<String, String>> getDeptflowList(String deptcode,
			RequestModel requestModel, PagingModel pagingModel) {
		int from = pagingModel.getFrom();
		int to = pagingModel.getTo();
		int pageRows = pagingModel.getPageRows();

		int curPage = pagingModel.getCurPage();
		String title = requestModel.getData() == null ? null
				: (String) requestModel.getData().get("$list-option-title");

		String folwname = requestModel.getData() == null ? null
				: (String) requestModel.getData()
						.get("$subtab-option-flowname");

		System.out.println("========================deptcode=" + deptcode);

		String sql = "SELECT * FROM EAP_VW_DONE WHERE WID IN (SELECT MAX(t.WID) FROM EAP_VW_DONE t,eap_instance i WHERE t.instanceid=i.instanceid AND t.assignername is null AND t.DEPTCODE=? and i.status in('I','F') GROUP BY t.INSTANCEID) ORDER BY STARTTIME DESC";
		try {
			this.list = this.dbo.prepareQuery(sql, new String[] { deptcode });

			GetReal.realTitle(this.list);
			for (int i = 0; i < this.list.size(); i++) {
				if ((title != null)
						&& (((String) ((HashMap) this.list.get(i)).get("TITLE"))
								.indexOf(title) == -1)) {
					this.list.remove(i);
					i--;
				} else {
					String flowstarttime = (String) ((HashMap) this.list.get(i))
							.get("FLOWSTARTTIME");
					((HashMap) this.list.get(i)).put("FLOWSTARTTIME",
							flowstarttime.substring(0, 10));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((from == 1) && (to == 0)) {
			to = this.list.size();
		}
		int rowsCount = this.list.size();
		int pageCount = this.list.size() / pageRows
				+ (this.list.size() % pageRows == 0 ? 0 : 1);
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

	public List<HashMap<String, String>> getDoingList(LoginModel loginModel,
			RequestModel requestModel, PagingModel pagingModel) {
		int from = pagingModel.getFrom();
		int to = pagingModel.getTo();
		int pageRows = pagingModel.getPageRows();

		int curPage = pagingModel.getCurPage();
		ArrayList lists = new ArrayList();
		HashMap map = null;
		String title = requestModel.getData() == null ? null
				: (String) requestModel.getData().get("$subtab-option-title");

		String flowcode = requestModel.getData() == null ? null
				: (String) requestModel.getData()
						.get("$subtab-option-flowname");

		System.out.println(title);
		System.out
				.println("----------flowcode=====================" + flowcode);

		String sql = "select  t.*,case WHEN F.IFCOLLECT is NULL THEN 0 ELSE F.ifcollect END ifcollect from eap_vw_done t left join oa_flow_collect F on t.INSTANCEID=F.instanceid and t.WID=F.wid where t.actor=?";
		if (flowcode != null && !("".equals(flowcode))) {
			sql = sql + " AND flowcode='" + flowcode + "' ";
		}
		sql = sql + " ORDER BY t.STARTTIME DESC";
		try {
			this.list = this.dbo.prepareQuery(sql,
					new String[] { loginModel.getUserCode() });
			GetReal.realTitle(this.list);
			for (int j = 0; j < this.list.size(); j++) {
				map = new HashMap();
				map = (HashMap) this.list.get(j);

				int nums = Integer.parseInt((String) ((HashMap) this.list
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
			for (int i = 0; i < lists.size(); i++) {
				if ((title != null)
						&& (((String) ((HashMap) lists.get(i)).get("TITLE"))
								.indexOf(title) == -1)) {
					lists.remove(i);
					i--;
				} else {
					String flowstarttime = (String) ((HashMap) lists.get(i))
							.get("FLOWSTARTTIME");
					((HashMap) lists.get(i)).put("FLOWSTARTTIME",
							flowstarttime.substring(0, 10));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String wenjiansql = "select  b.id,b.title title,a.jieshoucode jieshoucode,b.request requesttime,b.time time from oa_documentcy a,oa_documentcircularize b where a.rid=b.id and fasongcode='"
				+ loginModel.getUserCode()
				+ "' and a.state='2' order by a.id desc ";
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
					if (daysBetween(df.parse((String) ((HashMap) wenjianlist
							.get(i)).get("TIME")), df.parse(now)) < 0) {
						earlydate = df.parse((String) ((HashMap) wenjianlist
								.get(i)).get("TIME"));
					} else {
						earlydate = df.parse(now);
					}
					latedate = df.parse((String) ((HashMap) wenjianlist.get(i))
							.get("REQUESTTIME"));

					int days = daysBetween(earlydate, latedate);

					String jieshouname = "select name from eap_account where code='"
							+ (String) ((HashMap) wenjianlist.get(i))
									.get("JIESHOUCODE") + "'";
					ArrayList listname = this.dbo.prepareQuery(jieshouname,
							null);
					if (listname.size() > 0) {
						mapwenjian.put("ASSIGNERNAME",
								((HashMap) listname.get(0)).get("NAME"));
					}
					String jieshoutime = "select time,substr(time,0,10) subtime from oa_messagecenter t where t.reception='"
							+ loginModel.getUserCode()
							+ "' and url like '%id="
							+ (String) ((HashMap) wenjianlist.get(i)).get("ID")
							+ "' and title='"
							+ (String) ((HashMap) wenjianlist.get(i))
									.get("TITLE") + "' order by id desc ";
					ArrayList listtime = this.dbo.prepareQuery(jieshoutime,
							null);
					if (listtime.size() > 0) {
						mapwenjian.put("FLOWSTARTTIME",
								((HashMap) listtime.get(0)).get("SUBTIME"));
						mapwenjian.put("STARTTIME",
								((HashMap) listtime.get(0)).get("TIME"));
					} else {
						mapwenjian.put("STARTTIME",
								((HashMap) wenjianlist.get(i)).get("TIME"));
					}
					String isshouc = "select IFCOLLECT from oa_flow_collect t where wid is null  and usercode='"
							+ loginModel.getUserCode()
							+ "' and instanceid='"
							+ (String) ((HashMap) wenjianlist.get(i)).get("ID")
							+ "'";
					ArrayList listsc = this.dbo.prepareQuery(isshouc, null);
					if (listsc.size() > 0) {
						mapwenjian.put("IFCOLLECT",
								((HashMap) listsc.get(0)).get("IFCOLLECT"));
					}
					mapwenjian.put("INSTANCEID",
							((HashMap) wenjianlist.get(i)).get("ID"));
					mapwenjian.put("TITLE",
							((HashMap) wenjianlist.get(i)).get("TITLE"));
					mapwenjian.put("SUBMITMODE", "submit");
					mapwenjian.put("DAYS", Integer.valueOf(days));
					mapwenjian.put("FLOWNAME", "文件传阅");
					mapwenjian
							.put("URL",
									"../documentcircu?stype=detail&id="
											+ (String) ((HashMap) wenjianlist
													.get(i)).get("ID"));

					listzong.add(mapwenjian);
				}
			}
			listzong.addAll(lists);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < listzong.size() - 1; i++) {
			for (int j = 0; j < listzong.size() - i - 1; j++) {
				if (((!"".equals(((HashMap) listzong.get(j)).get("STARTTIME"))) || (((HashMap) listzong
						.get(j)).get("STARTTIME") != null))
						&& ((!"".equals(((HashMap) listzong.get(j + 1))
								.get("STARTTIME"))) || (((HashMap) listzong
								.get(j + 1)).get("STARTTIME") != null))) {
					if (timetostring((String) ((HashMap) listzong.get(j))
							.get("STARTTIME")) < timetostring((String) ((HashMap) listzong
							.get(j + 1)).get("STARTTIME"))) {
						HashMap mapp = (HashMap) listzong.get(j + 1);
						HashMap mapp2 = (HashMap) listzong.get(j);
						listzong.remove(listzong.get(j));
						listzong.add(j, mapp);
						listzong.remove(listzong.get(j + 1));
						listzong.add(j + 1, mapp2);
					}
				}
			}
		}
		if ((from == 1) && (to == 0)) {
			to = listzong.size();
		}
		int rowsCount = listzong.size();
		int pageCount = listzong.size() / pageRows
				+ (listzong.size() % pageRows == 0 ? 0 : 1);
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

	public List<HashMap<String, String>> getDoneList(LoginModel loginModel,
			RequestModel requestModel, PagingModel pagingModel) {
		int from = pagingModel.getFrom();
		int to = pagingModel.getTo();
		int pageRows = pagingModel.getPageRows();

		int curPage = pagingModel.getCurPage();
		String title = requestModel.getData() == null ? null
				: (String) requestModel.getData().get("$list-option-title");

		System.out.println("title====================" + title + "======"
				+ requestModel.getData());
		String sql = "SELECT A.MAXWID WID, A.INSTANCEID, A.CONTENT, A.TITLE, A.FLOWCODE,A.FLOWNAME, A.STATUS, A.FLOWSTARTTIME STARTTIME, A.FLOWENDTIME ENDTIME, B.IFCOLLECT FROM (SELECT A.*, B.WID MAXWID FROM EAP_VW_DONE A, (SELECT MAX(WID) WID, INSTANCEID FROM EAP_DONE WHERE INSTANCEID IN (SELECT INSTANCEID FROM EAP_DONE WHERE ACTOR = ?) GROUP BY INSTANCEID) B WHERE A.INSTANCEID = B.INSTANCEID AND A.WID = B.WID) A LEFT JOIN OA_FLOW_COLLECT B ON A.INSTANCEID = B.INSTANCEID AND B.USERCODE= ? WHERE A.STATUS = 'F'";

		sql = sql + " ORDER BY ENDTIME DESC";
		try {
			this.list = this.dbo.prepareQuery(
					sql,
					new String[] { loginModel.getUserCode(),
							loginModel.getUserCode() });
			GetReal.realTitle(this.list);
			for (int i = 0; i < this.list.size(); i++) {
				if ((title != null)
						&& (((String) ((HashMap) this.list.get(i)).get("TITLE"))
								.indexOf(title) == -1)) {
					this.list.remove(i);
					i--;
				} else {
					String flowstarttime = (String) ((HashMap) this.list.get(i))
							.get("STARTTIME");
					((HashMap) this.list.get(i)).put("STARTTIME",
							flowstarttime.substring(0, 10));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((from == 1) && (to == 0)) {
			to = this.list.size();
		}
		int rowsCount = this.list.size();
		int pageCount = this.list.size() / pageRows
				+ (this.list.size() % pageRows == 0 ? 0 : 1);
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
					(String) ((HashMap) this.dbo.prepareQuery(sql,
							new String[] { loginModel.getUserCode() }).get(0))
							.get("COUNT"));

			sql = "SELECT COUNT(*) COUNT FROM EAP_LOG WHERE USERID = ? AND TYPE='LOGIN' AND REQTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual)";
			this.hashMap.put(
					"myLoginMonth",
					(String) ((HashMap) this.dbo.prepareQuery(sql,
							new String[] { loginModel.getUserCode() }).get(0))
							.get("COUNT"));

			sql = "SELECT COUNT(*) COUNT FROM EAP_DONE WHERE ACTOR = ? AND ENDTIME BETWEEN (select substr(ENDTIME,0,5)||to_char(trunc(sysdate, 'd') ,'MM-dd') from dual) and (select substr(ENDTIME,0,5)||to_char(trunc(sysdate, 'd') + 6,'MM-dd') from dual)";
			this.hashMap.put(
					"myDoneWeek",
					(String) ((HashMap) this.dbo.prepareQuery(sql,
							new String[] { loginModel.getUserCode() }).get(0))
							.get("COUNT"));

			sql = "SELECT COUNT(*) COUNT FROM EAP_DONE WHERE ACTOR = ? AND ENDTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual)";
			this.hashMap.put(
					"myDoneMonth",
					(String) ((HashMap) this.dbo.prepareQuery(sql,
							new String[] { loginModel.getUserCode() }).get(0))
							.get("COUNT"));

			sql = "SELECT COUNT(*) COUNT FROM EAP_LOG WHERE TYPE='LOGIN' AND REQTIME BETWEEN (select substr(REQTIME,0,5)||to_char(trunc(sysdate, 'd') ,'MM-dd') from dual) and (select substr(REQTIME,0,5)||to_char(trunc(sysdate, 'd') + 6,'MM-dd') from dual)";
			this.hashMap.put("allLoginWeek", (String) ((HashMap) this.dbo
					.prepareQuery(sql, new String[0]).get(0)).get("COUNT"));

			sql = "SELECT COUNT(*) COUNT FROM EAP_LOG WHERE TYPE='LOGIN' AND REQTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual)";
			this.hashMap.put("allLoginMonth", (String) ((HashMap) this.dbo
					.prepareQuery(sql, new String[0]).get(0)).get("COUNT"));

			sql = "SELECT COUNT(*) COUNT FROM EAP_DONE WHERE ENDTIME BETWEEN (select substr(ENDTIME,0,5)||to_char(trunc(sysdate, 'd') ,'MM-dd') from dual) and (select substr(ENDTIME,0,5)||to_char(trunc(sysdate, 'd') + 6,'MM-dd') from dual)";
			this.hashMap.put("allDoneWeek", (String) ((HashMap) this.dbo
					.prepareQuery(sql, new String[0]).get(0)).get("COUNT"));

			sql = "SELECT COUNT(*) COUNT FROM EAP_DONE WHERE ENDTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual)";
			this.hashMap.put("allDoneMonth", (String) ((HashMap) this.dbo
					.prepareQuery(sql, new String[0]).get(0)).get("COUNT"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.hashMap;
	}

	public List<HashMap<String, String>> getLoginRankOfficeList(
			LoginModel loginModel) {
		String sql = "SELECT COUNT(*) COUNT,DEPTNAME FROM EAP_LOG A LEFT JOIN (SELECT A.*,B.NAME DEPTNAME FROM EAP_ACCOUNT A,EAP_DEPARTMENT B WHERE A.DEPTCODE = B.CODE) B ON A.USERID=B.CODE WHERE DEPTCODE NOT IN ('zgld','root') AND REQTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual) GROUP BY DEPTNAME ORDER BY COUNT DESC";
		try {
			this.list = this.dbo.prepareQuery(sql, new String[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.list;
	}

	public List<HashMap<String, String>> getDelayRankOfficeList(
			LoginModel loginModel) {
		String sql = "SELECT COUNT(*) COUNT,B.NAME DEPTNAME FROM EAP_VW_TODO A,EAP_DEPARTMENT B WHERE A.DEPTCODE = B.CODE AND DEPTCODE NOT IN ('zgld','root') AND A.STARTTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual) GROUP BY B.NAME ORDER BY COUNT DESC";
		try {
			this.list = this.dbo.prepareQuery(sql, new String[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.list;
	}

	public List<HashMap<String, String>> getDoneRankLeaderList(
			LoginModel loginModel) {
		String sql = "SELECT COUNT(*) COUNT,B.NAME NAME FROM EAP_DONE A,(SELECT A.*,B.NAME DEPTNAME FROM EAP_ACCOUNT A,EAP_DEPARTMENT B WHERE A.DEPTCODE = B.CODE) B WHERE A.ACTOR = B.CODE AND B.DEPTCODE NOT IN ('zgld','root') AND B.RANK='0' AND A.ENDTIME like (select '_____'||to_char(sysdate,'mm')||'%' from dual) GROUP BY B.NAME ORDER BY COUNT DESC";
		try {
			this.list = this.dbo.prepareQuery(sql, new String[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.list;
	}

	public List<HashMap<String, String>> getDoneRankOfficerList(
			LoginModel loginModel) {
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
			this.hashMap = (this.list.isEmpty() ? new HashMap()
					: (HashMap) this.list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.hashMap;
	}

	public List<HashMap<String, String>> getDocuemnt(LoginModel loginModel) {
		String sql = "SELECT * FROM("
				+ "SELECT * FROM ("
				+ "SELECT VW.ID ID,VW.SENDER SENDER,VW.TAKETIME TAKETIME,VW.THEME THEME,VW.RECIPIENT RECIPIENT,VW.ATTACHMENT ATTACHMENT,VW.BODY BODY, VW.RECIPIENTS RECIPIENTS,VW.RID RID,VW.ISREAD ISREAD,VW.STATUS STATUS,VW.CURRENTTIME CURRENTTIME,TAG.NAME DEPTNAME FROM (SELECT * FROM OA_OUTBOX_VW) VW LEFT JOIN  (SELECT A. CODE CODE,B.NAME NAME FROM EAP_ACCOUNT A,EAP_DEPARTMENT B WHERE A.DEPTCODE = B.CODE) TAG ON VW.SENDER=TAG.CODE) WHERE ID IS NOT NULL and recipients='"
				+ loginModel.getUserCode()
				+ "')C"
				+ " left join eap_account t on t.code = C.SENDER WHERE C.STATUS=0 ORDER BY TAKETIME DESC";
		try {
			this.list = this.dbo.prepareQuery(sql, new String[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.list;
	}

	public long timetostring(String time) {
		// System.out.println("============jinrutimetostring=============");
		// System.out.println("time===============++++++=========="+time);
		long inttime = 0L;
		String[] strtime = null;
		if (time != null && !("".equals(time))) {
			strtime = time.split(" ");
		}

		String str1 = "";
		if (strtime != null) {
			if (strtime.length > 0) {
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
		}
		if (str1 == "") {
			inttime = Long.valueOf("0").longValue();
		} else {
			inttime = Long.valueOf(str1).longValue();
		}
		// System.out.println("===============inttime===================="+inttime);
		return inttime;
	}

	public ArrayList<HashMap<String, String>> removeRepeat(
			ArrayList<HashMap<String, String>> wenjianlist) {
		boolean flag = true;
		for (int i = 0; i < wenjianlist.size() - 1; i++) {
			for (int j = i; j < wenjianlist.size() - 1; j++) {
				if (((String) ((HashMap) wenjianlist.get(i)).get("ID"))
						.equals(((HashMap) wenjianlist.get(j + 1)).get("ID"))) {
					String jies1 = (String) ((HashMap) wenjianlist.get(i))
							.get("JIESHOUCODE");
					String jies2 = (String) ((HashMap) wenjianlist.get(j + 1))
							.get("JIESHOUCODE");
					String s1 = "select SN,DEPTCODE from eap_account t WHERE CODE='"
							+ jies1 + "'";
					String s2 = "select SN,DEPTCODE from eap_account t WHERE CODE='"
							+ jies2 + "'";
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
					if ((jies11.size() > 0) && (jies22.size() > 0)) {
						if (("zgld".equals(((HashMap) jies11.get(0))
								.get("DEPTCODE")))
								&& ("zgld".equals(((HashMap) jies22.get(0))
										.get("DEPTCODE")))) {
							if (Integer.parseInt((String) ((HashMap) jies11
									.get(0)).get("SN")) < Integer
									.parseInt((String) ((HashMap) jies22.get(0))
											.get("SN"))) {
								wenjianlist.remove(j + 1);
								flag = false;
								break;
							}
							wenjianlist.remove(i);
							flag = false;
							break;
						}
						if (("zgld".equals(((HashMap) jies11.get(0))
								.get("DEPTCODE")))
								&& (!"zgld".equals(((HashMap) jies22.get(0))
										.get("DEPTCODE")))) {
							wenjianlist.remove(j + 1);
							flag = false;
							break;
						}
						if ((!"zgld".equals(((HashMap) jies11.get(0))
								.get("DEPTCODE")))
								&& ("zgld".equals(((HashMap) jies22.get(0))
										.get("DEPTCODE")))) {
							wenjianlist.remove(i);
							flag = false;
							break;
						}
						wenjianlist.remove(i);
						flag = false;
						break;
					}
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

	@Override
	// 通知公告
	public List<HashMap<String, String>> getTzList(LoginModel loginModel) {

		String sql = "select t.*,l.temporarycode from oa_temporarylogs l, oa_temporaryannouncement t where l.temporaryid=t.id and state >= 1 and l.readtime is null and temporarycode = '"
				+ loginModel.getUserCode() + "' ";
		if (loginModel.getUserCode() == "jianghuiqin"
				|| "jianghuiqin".equals(loginModel.getUserCode())) {
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

	@Override
	// 收资料
	public List<HashMap<String, String>> getInboxList(LoginModel loginModel) {
		String sql = "SELECT * FROM "
				+ "(SELECT VW.ID ID,VW.SENDER SENDER,TAG.USERNAME USERNAME,VW.TAKETIME TAKETIME,VW.THEME THEME,VW.RECIPIENT RECIPIENT,VW.ATTACHMENT ATTACHMENT,VW.BODY BODY,VW.RECIPIENTS RECIPIENTS,VW.RID RID,VW.ISREAD ISREAD,VW.STATUS STATUS,VW.CURRENTTIME CURRENTTIME,VW.IFCOLLECT IFCOLLECT,TAG.NAME DEPTNAME FROM "
				+ "(SELECT * FROM OA_OUTBOX_VW) VW LEFT JOIN (SELECT A.CODE CODE, A.NAME USERNAME,B.NAME NAME FROM EAP_ACCOUNT A,EAP_DEPARTMENT B WHERE A.DEPTCODE = B.CODE) TAG ON VW.SENDER=TAG.CODE) WHERE ID IS NOT NULL and recipients='"
				+ loginModel.getUserCode() + "' order by taketime desc";
		try {
			this.list = this.dbo.prepareQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.list;
	}

	@Override
	// 获取当天白班值班人员
	public String getDutyDayPeople() {
		String datetime = null;
		String people = null;
		// 查询当天值班人员
		String sql = "select a.name, t.* from oa_dutyportal t join eap_account a on a.code=t.people where t.datetime like '%";
		List<HashMap<String, String>> peopleList = null;
		try {
			if (c.get(Calendar.DAY_OF_WEEK) == 1
					|| c.get(Calendar.DAY_OF_WEEK) == 7) {

				System.out.println("===============白班");
				datetime = f.format(new Date()) + "白班";
				people = "白班：";
				peopleList = dbo.prepareQuery(sql + datetime
						+ "%' order by t.id", null);
				if (peopleList.size() > 0) {
					for (int i = 0; i < peopleList.size(); i++) {
						if (i == peopleList.size() - 1) {
							people = people + peopleList.get(i).get("NAME");
						} else {
							people = people + peopleList.get(i).get("NAME")
									+ ",";
						}

					}
					System.out.println("people=============" + people);
				} else {
					datetime = f.format(new Date());
					people = "";
					peopleList = dbo.prepareQuery(sql + datetime
							+ "%' order by t.id", null);
					if (peopleList.size() > 0) {
						for (int i = 0; i < peopleList.size(); i++) {
							if (i == peopleList.size() - 1) {
								people = people + peopleList.get(i).get("NAME");
							} else {
								people = people + peopleList.get(i).get("NAME")
										+ ",";
							}

						}
						System.out.println("people=============" + people);
					}
				}

			} else {
				System.out.println("===============正常");
				datetime = f.format(new Date());
				people = "";
				peopleList = dbo.prepareQuery(sql + datetime
						+ "%' order by t.id", null);
				if (peopleList.size() > 0) {
					for (int i = 0; i < peopleList.size(); i++) {
						if (i == peopleList.size() - 1) {
							people = people + peopleList.get(i).get("NAME");
						} else {
							people = people + peopleList.get(i).get("NAME")
									+ ",";
						}

					}
					System.out.println("people=============" + people);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return people;
	}

	@Override
	public String getDutyNightPeople() {
		String datetime = null;
		String people = null;
		// 查询当天值班人员
		String sql = "select a.name, t.datetime from oa_dutyportal t join eap_account a on a.code=t.people where t.datetime like '%";
		List<HashMap<String, String>> peopleList = null;
		try {
			if (c.get(Calendar.DAY_OF_WEEK) == 1
					|| c.get(Calendar.DAY_OF_WEEK) == 7) {

				System.out.println("===============夜班");
				datetime = f.format(new Date()) + "夜班";
				people = "夜班：";
				peopleList = dbo.prepareQuery(sql + datetime
						+ "%' order by t.id", null);
				if (peopleList.size() > 0) {
					for (int i = 0; i < peopleList.size(); i++) {
						if (i == peopleList.size() - 1) {
							people = people + peopleList.get(i).get("NAME");
						} else {
							people = people + peopleList.get(i).get("NAME")
									+ ",";
						}
					}
					System.out.println("people=============" + people);
				} else {
					datetime = f.format(new Date());
					people = "";
					peopleList = dbo.prepareQuery(sql + datetime
							+ "%' order by t.id", null);
					if (peopleList.size() > 0) {
						for (int i = 0; i < peopleList.size(); i++) {
							if (i == peopleList.size() - 1) {
								people = people + peopleList.get(i).get("NAME");
							} else {
								people = people + peopleList.get(i).get("NAME")
										+ ",";
							}
						}
						System.out.println("people=============" + people);
					}
				}
			} else {
				System.out.println("===============正常");
				datetime = f.format(new Date());
				people = "";
				peopleList = dbo.prepareQuery(sql + datetime
						+ "%' order by t.id", null);
				if (peopleList.size() > 0) {
					for (int i = 0; i < peopleList.size(); i++) {
						if (i == peopleList.size() - 1) {
							people = people + peopleList.get(i).get("NAME");
						} else {
							people = people + peopleList.get(i).get("NAME")
									+ ",";
						}
					}
					System.out.println("people=============" + people);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return people;
	}

	public static void main(String[] args) {
		Calendar c1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat();
		System.out.println(sdf1.format(c1.getTime()));
		System.out.println(c1.get(Calendar.DAY_OF_WEEK));

	}

	@Override
	public List<HashMap<String, String>> getGmMessageList() {
		String sql = "select * from oa_gmmessage order by rel_time desc";

		// String sql="select * from oa_gmmessage where rel_time='"+time+"'";

		try {
			this.list = dbo.prepareQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.list;
	}

}

