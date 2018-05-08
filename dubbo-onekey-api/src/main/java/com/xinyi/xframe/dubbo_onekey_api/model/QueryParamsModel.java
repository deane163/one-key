/**
 *
 */
package com.xinyi.xframe.dubbo_onekey_api.model;

import java.io.Serializable;

/**
 * 功能说明：ElasticSearch 查询参数的定义，此类需要序列化
 * 
 * QueryParamsModel.java
 * 
 * Original Author: liangliang.jia,2015年7月23日下午3:23:01
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
public class QueryParamsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String indices;
	private String types;

	private String highlightedField;
	private String queryStr;

	private int startPage = 0;
	private int pageSize = 10;

	public String getIndices() {
		return indices;
	}

	public void setIndices(String indices) {
		this.indices = indices;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getHighlightedField() {
		return highlightedField;
	}

	public void setHighlightedField(String highlightedField) {
		this.highlightedField = highlightedField;
	}

	public String getQueryStr() {
		return queryStr;
	}

	public void setQueryStr(String queryStr) {
		this.queryStr = queryStr;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
