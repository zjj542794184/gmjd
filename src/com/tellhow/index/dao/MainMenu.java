package com.tellhow.index.dao;

import com.siqiansoft.framework.model.LoginModel;
import com.siqiansoft.framework.model.PagingModel;
import com.siqiansoft.framework.model.RequestModel;
import java.util.HashMap;
import java.util.List;

public abstract interface MainMenu
{
  public abstract List<HashMap<String, String>> getTodoList(LoginModel paramLoginModel, RequestModel paramRequestModel, PagingModel paramPagingModel);

  public abstract List<HashMap<String, String>> getTzList(LoginModel paramLoginModel);

  public abstract List<HashMap<String, String>> getInboxList(LoginModel paramLoginModel);

  public abstract String getDutyDayPeople();

  public abstract String getDutyNightPeople();

  public abstract List<HashMap<String, String>> getGmMessageList();
}