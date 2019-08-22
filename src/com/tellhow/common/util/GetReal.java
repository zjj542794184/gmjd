
package com.tellhow.common.util;

import com.siqiansoft.framework.bo.DatabaseBo;
import com.siqiansoft.workflow.bo.StartupBo;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

public class GetReal
{
  HashMap<String, String> hashMap;
  DatabaseBo dbo = new DatabaseBo();
  StartupBo sbo = new StartupBo();

  public static String xml2JSON(String xml) {
    return new XMLSerializer().read(xml).toString();
  }

 public static List realTitle(List<HashMap<String, String>> list)
  {
    List titleList = new ArrayList();
    String sign = "";

    for (int i = 0; i < list.size(); i++)
    {
      if ((!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("leven")) || (!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("teamdiscuss")) || 
        (!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("wjcy")) || (!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("dcdb")) || 
        (!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("dcsb"))) {
        String content = ((String)((HashMap)list.get(i)).get("CONTENT")).toString();
        String flowcode = (String)((HashMap)list.get(i)).get("FLOWCODE");

        if (flowcode.equals("f03")) {
          sign = "reason";
        }
        else if (flowcode.equals("f06")) {
          sign = "vehicleincident";
        }
        else if (flowcode.equals("f02")) {
          sign = "cause";
        }
        else if (flowcode.equals("f07")) {
          String cause = (String)((HashMap)list.get(i)).get("CAUSE");

          if ((cause != null) || (!"".equals(cause)))
            sign = "cause";
          else {
            sign = "title";
          }

        }
        else if (flowcode.equals("zjsq")) {
          sign = "xmname";
        }
        else if (flowcode.equals("f17")) {
          sign = "item";
        }
        else if (flowcode.equals("f13")) {
          sign = "reason";
        }
        else if (flowcode.equals("f10")) {
          sign = "content";
        }
        else
        {
          sign = "title";
        }

        content = content.trim();
        int begin = content.indexOf("<string>" + sign + "</string>");
        if (begin != -1) {
          content = content.substring(begin);
          content = content.substring(content.indexOf("</string>"));
          content = content.substring(content.indexOf("<string>") + 8);
          content = content.substring(0, content.indexOf("</string>"));

          content = content.replace("&amp;", "");
          content = content.replace("&#xd;", "");
          content = content.replace("#xa;", "");
          if ((content != null) && (!content.equals("{}")) && (!content.equals("")))
            ((HashMap)list.get(i)).put("TITLE", content);
        }
      }
      else {
        titleList.add((String)((HashMap)list.get(i)).get("TITLE"));
      }
    }
    return titleList;
  }
  public static List<HashMap<String, String>> realTitle1(List<HashMap<String, String>> list) throws Exception {
    List titleList = new ArrayList();
    String sign = "";
    DatabaseBo dbo = new DatabaseBo();
    String sql1 = "SELECT MAX(WID) WID,instanceid FROM EAP_DONE GROUP BY INSTANCEID";
    List instList = dbo.query(sql1);
    for (int i = 0; i < list.size(); i++) {
      if ((!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("leven")) && (!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("teamdiscuss")) && 
        (!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("wjcy")) && (!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("dcdb")) && 
        (!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("dcsb")) && (!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("hyyd")) && 
        (!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("changeshifts")) && (!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("lianhezhifa"))) {
        String content = ((String)((HashMap)list.get(i)).get("CONTENT")).toString();
        String flowcode = (String)((HashMap)list.get(i)).get("FLOWCODE");

        if (flowcode.equals("dispatchflow")) {
          sign = "title";
        } else if (flowcode.equals("shouwennew")) {
          sign = "title";
        } else if (flowcode.equals("dzsy")) {
          sign = "filetitle";
        } else if (flowcode.equals("sealapply")) {
          sign = "filetitle";
        } else if (flowcode.equals("noteissueapply")) {
          sign = "notecontent";
        } else if (flowcode.equals("leave")) {
          sign = "cause";
        } else if (flowcode.equals("xiangmu")) {
          sign = "name";
        } else if (flowcode.equals("contract")) {
          sign = "name";
        } else if (flowcode.equals("jiesuan")) {
          sign = "note";
        } else if (flowcode.equals("conveniencephone")) {
          sign = "title";
        }
        else if (flowcode.equals("sickleave")) {
          sign = "cause";
        }
        else if (flowcode.equals("dispatchflowdw")) {
          sign = "title";
        }
        else if (flowcode.equals("lijing")) {
          sign = "titles";
        }
        else if (flowcode.equals("projectfound")) {
          sign = "prosubject";
        }
        else if (flowcode.equals("procontract")) {
          sign = "proname";
        }
        else if (flowcode.equals("protender")) {
          sign = "proname";
        }
        else if (flowcode.equals("zichanbaofei")) {
          System.out.println("资产报废流程code=====" + flowcode);
          sign = "title";
        }
        else if (flowcode.equals("shengou")) {
          sign = "title";
        }
        else if (flowcode.equals("zichandiaobo")) {
          sign = "title";
        }
        else if (flowcode.equals("zichanlingyon")) {
          sign = "title";
        } else if (flowcode.equals("vehicleapply")) {
          sign = "vehicleincident";
        }
        else
        {
          sign = "title";
        }
        content = content.trim();
        int begin = content.indexOf("<string>" + sign + "</string>");
        if (begin != -1) {
          content = content.substring(begin);
          content = content.substring(content.indexOf("</string>"));
          content = content.substring(content.indexOf("<string>") + 8);
          content = content.substring(0, content.indexOf("</string>"));

          content = content.replace("&amp;", "");
          content = content.replace("&#xd;", "");
          content = content.replace("#xa;", "");
          if ((content != null) && (!content.equals("{}")) && (!content.equals(""))) {
            ((HashMap)list.get(i)).put("TITLE", content);
          }

          if (instList.size() > 0) {
            for (int j = 0; j < instList.size(); j++) {
              if (((String)((HashMap)list.get(i)).get("INSTANCEID")).equals(((HashMap)instList.get(j)).get("INSTANCEID"))) {
                ((HashMap)list.get(i)).put("WID", (String)((HashMap)instList.get(j)).get("WID"));
              }
            }
          }
          titleList.add((HashMap)list.get(i));
        }
      } else {
        titleList.add((HashMap)list.get(i));
      }
    }
    return titleList;
  }

  public static List realTitleold(List<HashMap<String, String>> list) {
    List titleList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      if (!((String)((HashMap)list.get(i)).get("FLOWCODE")).equals("leven")) {
        String content = xml2JSON(((String)((HashMap)list.get(i)).get("CONTENT")).toString());
        JSONArray json = (JSONArray)((JSONObject)JSONArray.fromObject(content).get(0)).get("map");
        for (int j = 0; j < json.size(); j++) {
          String flowcode = (String)((HashMap)list.get(i)).get("FLOWCODE");
          String sign;
          if (flowcode.equals("dispatchflow")) {
            sign = "title";
          }
          else
          {
            if (flowcode.equals("shouwennew")) {
              sign = "title";
            }
            else
            {
              if (flowcode.equals("teamdiscuss")) {
                sign = "name";
              }
              else
              {
                if (flowcode.equals("dzsy")) {
                  sign = "filetitle";
                }
                else
                {
                  if (flowcode.equals("sealapply")) {
                    sign = "filetitle";
                  }
                  else
                  {
                    if (flowcode.equals("noteissueapply")) {
                      sign = "notecontent";
                    }
                    else
                    {
                      if (flowcode.equals("leave")) {
                        sign = "cause";
                      }
                      else
                      {
                        if (flowcode.equals("xiangmu")) {
                          sign = "name";
                        }
                        else
                        {
                          if (flowcode.equals("contract")) {
                            sign = "name";
                          }
                          else
                          {
                            if (flowcode.equals("jiesuan")) {
                              sign = "note";
                            }
                            else
                            {
                              if (flowcode.equals("conveniencephone"))
                                sign = "title";
                              else
                                sign = "title"; 
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          String title = null;
          if (JSONObject.fromObject(json.get(j).toString().replace(",", ":").replace("[", "{").replace("]", "}")).get(sign) != null) {
            title = JSONObject.fromObject(json.get(j).toString().replace(",", ":").replace("[", "{").replace("]", "}")).get(sign).toString();
          }
          if (flowcode.equals("leven")) {
            String count2 = null; String count = null;
            if (JSONObject.fromObject(json.get(j).toString().replace(",", ":").replace("[", "{").replace("]", "}")).get("count2") != null) {
              count2 = JSONObject.fromObject(json.get(j).toString().replace(",", ":").replace("[", "{").replace("]", "}")).get("count2").toString();
            }
            if (JSONObject.fromObject(json.get(j).toString().replace(",", ":").replace("[", "{").replace("]", "}")).get("count") != null)
              count = JSONObject.fromObject(json.get(j).toString().replace(",", ":").replace("[", "{").replace("]", "}")).get("count").toString();
            /*try
            {
              title = String.valueOf(VersionDay.daysBetween(count, count2));
            } catch (ParseException e) {
              e.printStackTrace();
            }*/
          }

          if ((title != null) && (!title.equals("{}")) && (!title.equals(""))) {
            ((HashMap)list.get(i)).put("TITLE", title);
          }
          titleList.add((String)((HashMap)list.get(i)).get("TITLE"));
        }
      }
      else {
        titleList.add((String)((HashMap)list.get(i)).get("TITLE"));
      }
    }
    return titleList;
  }
}
