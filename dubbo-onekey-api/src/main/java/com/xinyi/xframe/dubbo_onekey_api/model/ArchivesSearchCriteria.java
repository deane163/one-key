package com.xinyi.xframe.dubbo_onekey_api.model;

import java.io.Serializable;

public class ArchivesSearchCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//索引
	private String indexName;
    //索引type
	private String typeName;
    //查询内容
	private String query;
    //查询字段
	private String fieldName;
	//起始位置
	private int from = 0;
	//结束位置
	private int size = 10;
	
	
	public ArchivesSearchCriteria(String indexName,String typeName,String query,String fieldName) {
		this(indexName, typeName, query, fieldName, 0, 10);
	}
	public ArchivesSearchCriteria(String indexName,String typeName,String query,String fieldName,int from ,int size) {
		this.indexName = indexName;
		this.typeName = typeName;
		this.query = query;
		this.fieldName = fieldName;
		this.from = from;
		this.size = size;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
