package com.xinyi.xinfo.search.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
/**
 * 
 * 功能说明：用户关注内容
 * 
 * AttentionContent.java
 * 
 * Original Author: 曾凡,2015年8月12日
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
@Table(name = "TAB_SEARCH_ATT_CONTENT")
public class AttentionContent implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 主键
     */
	@Id
	@NotEmpty
	private String id;
	
	/**
	 * 用户ID
	 */
	@Column(name = "user_id")
	@NotEmpty
	private String userId;
	/**
	 * 收藏分类ID
	 */

	@Column(name = "att_id")
	@NotEmpty
	private String attId;
	/**
	 * 索引id
	 */

	@Column(name = "index_id")
	@NotEmpty
	private String indexId;

	
	@Transient
	private SearchIndexType indexType;
	
	
	/**
	 * 文档id
	 */
	@Column(name = "doc_id")
	@NotNull
	private String docId;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAttId() {
		return attId;
	}
	public void setAttId(String attId) {
		this.attId = attId;
	}
	public String getIndexId() {
		return indexId;
	}
	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}
	
	public String getEsDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public SearchIndexType getIndexType() {
		return indexType;
	}
	public void setIndexType(SearchIndexType indexType) {
		this.indexType = indexType;
	}
	public String getEsIndices() {
		return indexType.getIndexName();
	}
	public String getEsTypes() {
		return indexType.getTypeName();
	}

}
