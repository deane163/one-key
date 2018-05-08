/**
* 对EalsticSearch 接口进行二次封装，方便用户调用
 */
package com.xinyi.xframe.dubbo_onekey_api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.elasticsearch.common.Nullable;

import com.xinyi.xframe.dubbo_onekey_api.declare.MySearchOption.SearchLogic;
import com.xinyi.xinfo.search.bean.AttentionContent;
import com.xinyi.xinfo.search.bean.SearchAppMeta;
import com.xinyi.xinfo.search.bean.SearchIndexType;

/**
 * 功能说明：接口定义，定义ElasticSearch 调用接口的定义函数
 * 建议使用接口，对ElasticSearch进行相应的查询操作，方便用户调用
 * 
 * SearchService.java
 * 
 * Original Author: liangliang.jia,2015年7月28日上午10:38:15
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */

/* new Object[] { "TESTNAME1,TESTNAME2"}会识别为一个搜索条件 */
/* new Object[] { "TESTNAME1","TESTNAME2"}会识别为两个搜索条件 */
public interface SearchService {

	// 批量更新数据，先删除，再插入，只需要传递新数据的差异键值
	boolean autoBulkUpdateData(String indexName,
			HashMap<String, Object[]> oldContentMap,
			HashMap<String, Object[]> newContentMap);

	// 批量删除数据，危险
	boolean bulkDeleteData(String indexName,
			HashMap<String, Object[]> contentMap);

	// 批量插入数据
	boolean bulkInsertData(String indexName,
			HashMap<String, Object[]> contentMap);

	// 批量更新数据，先删除，再插入，需要传递新数据的完整数据
	boolean bulkUpdateData(String indexName,
			HashMap<String, Object[]> oldContentMap,
			HashMap<String, Object[]> newContentMap);

	/*
	 * 搜索 indexNames 索引名称 mustSearchContentMap must内容HashMap
	 * shouldSearchContentMap should内容HashMap from 从第几条记录开始（必须大于等于0） offset
	 * 一共显示多少条记录（必须大于0） sortField 排序字段名称 sortType 排序方式（asc，desc）
	 */
	List<Map<String, Object>> complexSearch(String[] indexNames,
			@Nullable HashMap<String, Object[]> mustSearchContentMap,
			@Nullable HashMap<String, Object[]> shouldSearchContentMap,
			int from, int offset, @Nullable String sortField,
			@Nullable String sortType);

	/*
	 * 获得搜索结果总数 indexNames 索引名称 mustSearchContentMap must内容HashMap
	 * shouldSearchContentMap should内容HashMap
	 */
	long getComplexCount(String[] indexNames,
			@Nullable HashMap<String, Object[]> mustSearchContentMap,
			@Nullable HashMap<String, Object[]> shouldSearchContentMap);

	/*
	 * 获得搜索结果总数，支持json版本
	 */
	long getCount(String[] indexNames, byte[] queryString);

	/*
	 * 获得搜索结果总数 indexNames 索引名称 searchContentMap 搜索内容HashMap filterContentMap
	 * 过滤内容HashMap
	 */
	long getCount(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			@Nullable HashMap<String, Object[]> filterContentMap);

	/*
	 * 获得搜索结果总数 indexNames 索引名称 searchContentMap 搜索内容HashMap searchLogic
	 * 搜索条件之间的逻辑关系（must表示条件必须都满足，should表示只要有一个条件满足就可以） filterContentMap
	 * 过滤内容HashMap filterLogic 过滤条件之间的逻辑关系（must表示条件必须都满足，should表示只要有一个条件满足就可以）
	 */
	long getCount(String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			@Nullable HashMap<String, Object[]> filterContentMap,
			@Nullable SearchLogic filterLogic);

	// 获得推荐列表
	List<String> getSuggest(String[] indexNames, String fieldName,
			String value, int count);

	/*
	 * 分组统计 indexName 索引名称 mustSearchContentMap must内容HashMap
	 * shouldSearchContentMap should内容HashMap groupFields 分组字段
	 */
	Map<String, String> group(String indexName,
			@Nullable HashMap<String, Object[]> mustSearchContentMap,
			@Nullable HashMap<String, Object[]> shouldSearchContentMap,
			String[] groupFields);

	/*
	 * 搜索，支持json版本
	 */
	List<Map<String, Object>> simpleSearch(boolean isCheckRed,String[] indexNames,
			HashMap<String, Object[]> searchContentMap, int from, int offset,
			@Nullable String sortField, @Nullable String sortType);
	/*
	 * 搜索 indexNames 索引名称 searchContentMap 搜索内容HashMap filterContentMap
	 * 过滤内容HashMap from 从第几条记录开始（必须大于等于0） offset 一共显示多少条记录（必须大于0） sortField
	 * 排序字段名称 sortType 排序方式（asc，desc）
	 */
	List<Map<String, Object>> simpleSearch(boolean isCheckRed, String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			@Nullable HashMap<String, Object[]> filterContentMap, int from,
			int offset, @Nullable String sortField, @Nullable String sortType);

	/*
	 * 去掉排序参数的简化版本
	 */
	List<Map<String, Object>> simpleSearch(boolean isCheckRed, String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			@Nullable HashMap<String, Object[]> filterContentMap, int from,
			int offset);

	/*
	 * 搜索 indexNames 索引名称 searchContentMap 搜索内容HashMap searchLogic
	 * 搜索条件之间的逻辑关系（must表示条件必须都满足，should表示只要有一个条件满足就可以） filterContentMap
	 * 过滤内容HashMap filterLogic 过滤条件之间的逻辑关系（must表示条件必须都满足，should表示只要有一个条件满足就可以）
	 * from 从第几条记录开始（必须大于等于0） offset 一共显示多少条记录（必须大于0） sortField 排序字段名称 sortType
	 * 排序方式（asc，desc）
	 */
	List<Map<String, Object>> simpleSearch(boolean isCheckRed, String[] indexNames,
			HashMap<String, Object[]> searchContentMap,
			SearchLogic searchLogic,
			@Nullable HashMap<String, Object[]> filterContentMap,
			@Nullable SearchLogic filterLogic, int from, int offset,
			@Nullable String sortField, @Nullable String sortType);

	



   List<Map<String,Object>> simpleSearch(boolean isCheckRed,String queryString,SearchIndexType indexType,
		   Map<String,Object> fieldNamesAndValues,List<SearchAppMeta> viewFieldList,Map<String,String> fieldNamesAndTypes,long pageIndex,long pageSize) ;

	
	/*
	 * 多字段查询搜索，搜索 indexNames 索引名称 searchContentMap 搜索字段名称  String[] fileds
	 * 搜索的关键字信息  Object queryObject 需要高亮的字段和查询关键字 HashMap<String, Object[]> highlightSearchContentMap
	 * from 从第几条记录开始（必须大于等于0） offset 一共显示多少条记录（必须大于0） sortField 排序字段名称 sortType
	 * 排序方式（asc，desc）
	 */
	List<Map<String, Object>> multiMatchSearch(String[] indexNames, String[] fileds, Object queryObject
			,HashMap<String, Object[]> highlightSearchContentMap
    		, int from, int offset,@Nullable String sortField,@Nullable String sortType);
	
	/**
	 * 根据文档信息查询文档内容
	 * @param multiGetRequestItems
	 * @return
	 * @author 曾凡
	 */
	List<Map<String, Object>> multiGetByDocumentItem(List<AttentionContent> attContents);

	List<Map<String, Object>> simpleSearch(String keyword,String[] indices, long pageIndex, long pageSize);

	List<Map<String, Object>> fullTextQuery(boolean isRedCheck, String queryString,SearchIndexType type, String[] fieldNames,String analyzer, @Nullable Map<String, Object> fieldNamesAndValues, String[] highlightedFields,@Nullable Map<String, String> fieldNamesAndTypes, long pageIndex, long pageSize);

	List<Map<String, Object>> fullTextQuery(boolean isRedCheck, String queryString,SearchIndexType type, String[] fieldNames,String analyzer, @Nullable Map<String, Object> fieldNamesAndValues, String[] highlightedFields,@Nullable Map<String, String> fieldNamesAndTypes, long pageIndex, long pageSize, @Nullable String sortField,@Nullable String sortType);
	
	long wildCardCount(String keyword, String fieldName,SearchIndexType indice, @Nullable Map<String, Object> fieldNamesAndValues,@Nullable Map<String, String> fieldNamesAndTypes);
	long wildCardFilteredCount(String keyword, String fieldName,SearchIndexType indice, Map<String, Object> fieldNamesAndValues,Map<String, String> fieldNamesAndTypes);
	
	long getCount(String keyword,SearchIndexType indice,@Nullable Map<String, Object> fieldNamesAndValues,@Nullable Map<String, String> fieldNamesAndTypes);


	List<Map<String, Object>> wildCardQuery(boolean isRedCheck, String keyword,
			SearchIndexType type, String filedName,
			@Nullable Map<String, Object> fieldNamesAndValues, List<SearchAppMeta> highlightedFields,
			@Nullable Map<String, String> fieldNamesAndTypes, long pageIndex, long pageSize);

	Map<String,Long> getCount(String keyword, String indexName, String[] fieldNames,
			String analyzer, Map<String, Object> condition, Map<String, String> conditionTypes);
	
	//List<Map<String, Object>> wildCardQuery(String keyword,SearchIndexType type, String filedName,@Nullable Map<String, Object> fieldNamesAndValues, List<SearchAppMeta> highlightedFields,@Nullable Map<String, String> fieldNamesAndTypes, long pageIndex, long pageSize);
	List<Map<String, Object>> wildCardFilteredQuery(boolean isRedCheck, String search,SearchIndexType type, String filedName,Map<String, Object> fieldNamesAndValues,List<SearchAppMeta> highlightedFields,Map<String, String> fieldNamesAndTypes, long pageIndex,long pageSize);

	List<Map<String, Object>> wildCardFilteredSuggest(boolean isRedCheck,SearchIndexType type,Map<String, Object> fieldNamesAndValues,Map<String, String> fieldNamesAndTypes, long pageIndex, long pageSize);
	
	Map<String, Long> getFilteredCount(SearchIndexType indexType,Map<String, Object> fieldNamesAndValues,Map<String, String> fieldNamesAndTypes);

	List<Map<String, Object>> fullTextQueryFilter(boolean isRedCheck, SearchIndexType type,
			Map<String, Object> fieldNamesAndValues,
			Map<String, String> fieldNamesAndTypes, long pageIndex,
			long pageSize);
	
	public List<Map<String,Object>> aggreationFilterQuery(boolean isRedCheck,SearchIndexType type,Map<String, Object> fieldNamesAndValues,
			Map<String, String> fieldNamesAndTypes,@NotNull Map<String,Object> aggreFieldAndValue, long pageIndex, long pageSize);
}
