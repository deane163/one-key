/**
 *
 */
package com.xinyi.xframe.dubbo_onekey_service.utils;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * 功能说明：
 * 
 * QueryBuilderOptions.java
 * 
 * Original Author: liangliang.jia,2015年7月23日下午3:57:25
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
public class QueryBuilderOptions {

	private static QueryBuilder srcqueryBuilder;

	public static QueryBuilder getQueryBuilder(String queryType, String[] object) {
		QueryBuilder myQuery = null;
		if (queryType.equals("matchQuery")) {
			myQuery = QueryBuilders.matchQuery(object[0], object[1]);
		}
		setQueryBuilder(myQuery);
		return srcqueryBuilder;
	}

	public static void setQueryBuilder(QueryBuilder queryBuilder) {
		srcqueryBuilder = queryBuilder;
	}
}
