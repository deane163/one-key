/**
 * 
 */
package com.xinyi.xinfo.search.bean;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;


/**
 * 功能说明：搜索类别实体类
 * 
 * SearchType.java
 * 
 * Original Author: 罗文磊,2015年3月17日
 *
 * Copyright (C)1997-2014 深圳小树盛凯科技 All rights reserved.
 */
@Table(name="TAB_SEARCH_SUPERS_TYPE")
public class SearchIndexType implements Serializable{

	private static final long serialVersionUID = -4084429608925217346L;

	@Id
	String id;
	
	String idParent;
	
	String resId;
	
	String indexName;
	
	String typeName;
	
	String indexTypeCn;
	
	String indexTypeFullcn;
	
	String orderNum;
	
	String dataLevel;

	public String getIndexTypeFullcn() {
		return indexTypeFullcn;
	}

	public void setIndexTypeFullcn(String indexTypeFullcn) {
		this.indexTypeFullcn = indexTypeFullcn;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdParent() {
		return idParent;
	}

	public void setIdParent(String idParent) {
		this.idParent = idParent;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getIndexTypeCn() {
		return indexTypeCn;
	}

	public void setIndexTypeCn(String indexTypeCn) {
		this.indexTypeCn = indexTypeCn;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getDataLevel() {
		return dataLevel;
	}

	public void setDataLevel(String dataLevel) {
		this.dataLevel = dataLevel;
	}
	
	

	
	
}
