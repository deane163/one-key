package com.xinyi.xframe.dubbo_onekey_sample.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xinyi.xframe.dubbo_onekey_api.service.SearchService;

/**
 * 功能说明：
 * 
 * ElasticSearchService.java
 * 
 * Original Author: liangliang.jia,2015年7月24日上午11:29:39
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */

public class ElasticSearchService {

//	@Reference(version = "1.0.0")
//	private SearchServices searchService;
//
//	@Reference(version = "1.0.0")
//	private IndexService indexService;

	@Reference(version = "3.0.0")
	private SearchService searchService2;

//	public SearchServices getSearchService() {
//		return searchService;
//	}
//
//	public void setSearchService(SearchServices searchService) {
//		this.searchService = searchService;
//	}
//
//	public IndexService getIndexService() {
//		return indexService;
//	}
//
//	public void setIndexService(IndexService indexService) {
//		this.indexService = indexService;
//	}

	public SearchService getSearchService2() {
		return searchService2;
	}

	public void setSearchService2(SearchService searchService2) {
		this.searchService2 = searchService2;
	}

}
