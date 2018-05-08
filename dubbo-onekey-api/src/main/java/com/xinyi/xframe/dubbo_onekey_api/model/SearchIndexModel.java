/**
 *
 */
package com.xinyi.xframe.dubbo_onekey_api.model;

import java.io.Serializable;

/**
 * 功能说明：对ElasticSearch索引参数查询参数的定义
 * 
 * SearchIndexModel.java
 * 
 * Original Author: liangliang.jia,2015年7月23日下午3:00:38
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
public class SearchIndexModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String indexName;
	private String typeName;

	private int shards = 5;
	private int replicas = 2;

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

	public int getShards() {
		return shards;
	}

	public void setShards(int shards) {
		this.shards = shards;
	}

	public int getReplicas() {
		return replicas;
	}

	public void setReplicas(int replicas) {
		this.replicas = replicas;
	}

}
