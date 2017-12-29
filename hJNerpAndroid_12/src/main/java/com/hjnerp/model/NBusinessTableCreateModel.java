package com.hjnerp.model;

import java.util.ArrayList;
import java.util.List;

import com.hjnerp.util.myscom.StringUtils;

public class NBusinessTableCreateModel
{
	public String table;
	public String condition;
	public String cols;
	public List<String> values;
	public List<String> insertSqlsValues = new ArrayList<>();
	public String insertSqls;
	public String deleteSql;
	
	public void create()
	{
		if(!values.isEmpty())
		{
			StringBuffer sb = new StringBuffer();
			sb.append("delete from ").append(table).append(" where ").append(condition);
			deleteSql = sb.toString();
			sb = new StringBuffer();
//			sb.append("insert or replace into ")
//					.append(table)
//					.append(" (")
//					.append(cols).append(")")
//					.append(" select ")
//					.append(StringUtils.join(values, " union select "));
			sb.append("insert or replace into ")
				.append(table)
				.append(" (")
				.append(cols).append(")values");
			String fontSql=sb.toString();
			for(int i=0;i<values.size();i++){
				insertSqlsValues.add(fontSql+"("+values.get(i)+")");
			}
		}
		values = null;
	}
}
