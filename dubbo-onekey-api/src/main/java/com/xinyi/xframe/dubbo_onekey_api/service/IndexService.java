/**
*
*/
package com.xinyi.xframe.dubbo_onekey_api.service;

import com.xinyi.xframe.dubbo_onekey_api.model.SearchIndexModel;

/**
 * 功能说明：对ElasticSearch 的索引进行相应操作的封装
 * 
 * IndexService.java
 * 
 * Original Author: liangliang.jia,2015年7月23日下午2:22:14
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
public interface IndexService {

	/**
	 * alias the name of index
	 * @param srcIndex
	 * @param targetIndex
	 */
	public String aliasIndexName(String srcIndex, String targetIndex);
	
	/**
	 * create the index , and number_of_shards , and number_of_replicas
	 * @param indexName
	 * @param typeName
	 * @param shards
	 * @param replicas
	 */
	public String createIndex(SearchIndexModel searchIndex);
	
	/**
	 * delete the elasticSearch index
	 * @param indexName
	 */
	public String deleteIndex(String...strings);
	
	/**
	 * @param strings
	 * @return
	 */
	public String existsIndex(String...strings);
	
	/**
	 * close the search index
	 * @param strings
	 * @return
	 */
	public String closeIndex(String...strings);
	
	/**
	 * open the search index API
	 * @param strings
	 * @return
	 */
	public String openIndex(String...strings);
}
