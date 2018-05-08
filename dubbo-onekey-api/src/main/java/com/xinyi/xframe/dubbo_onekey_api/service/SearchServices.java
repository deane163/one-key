/**
 *
 */
package com.xinyi.xframe.dubbo_onekey_api.service;

import java.util.List;

import com.xinyi.xframe.dubbo_onekey_api.model.QueryParamsModel;
import com.xinyi.xframe.dubbo_onekey_api.model.SearchHitsModel;

/**
 * 功能说明：普通搜索结果，复杂查询，建议使用SearchService接口类
 * 
 * SearchServices.java
 * 
 * Original Author: liangliang.jia,2015年7月23日下午3:12:11
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
public interface SearchServices {

	/**
	 * common elasticsearch method
	 * 
	 * @param queryParams
	 * @return
	 */
	public List<SearchHitsModel> search(QueryParamsModel queryParams);

	public String commonSearch(String qString);
}
