package com.xinyi.xframe.dubbo_onekey_api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArchivesSearchResult implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//总数
	private long total;
	
	//结果
	private List<Map<String, Object>> hits = new ArrayList<Map<String, Object>>();

	private Map<String, Long> aggregations = new HashMap<String, Long>();

	public List<Map<String, Object>> getHits() {
		return hits;
	}

	

	public Map<String, Long> getAggregations() {
		return aggregations;
	}

	

	
	
	public void addHit(Map<String, Object> hit){
		this.hits.add(hit);
	}
	
	public void addAggregations(String key, Long docCount){
		this.aggregations.put(key, docCount);
	}



	public long getTotal() {
		return total;
	}



	public void setTotal(long total) {
		this.total = total;
	}


}
