/**
 *
 */
package com.xinyi.xframe.dubbo_onekey_api.model;

import java.io.Serializable;

/**
 * 功能说明：对ElasticSearch搜索结果的封装，此类需要序列化处理
 * 
 * SearchHitsModel.java
 * 
 * Original Author: liangliang.jia,2015年7月23日下午3:13:40
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
public class SearchHitsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long totalHits;
	private float score;

	private String source;
	private String highlight;

	public long getTotalHits() {
		return totalHits;
	}

	public void setTotalHits(long totalHits) {
		this.totalHits = totalHits;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getHighlight() {
		return highlight;
	}

	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}

}
