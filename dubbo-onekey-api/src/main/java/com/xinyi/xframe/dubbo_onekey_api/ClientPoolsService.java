/**
*
*/
package com.xinyi.xframe.dubbo_onekey_api;

import java.util.concurrent.CopyOnWriteArrayList;

import org.elasticsearch.client.Client;

/**
 * 功能说明：Elastic Connection Client Simple pools
 * 
 * ClientInstanceService.java
 * 
 * Original Author: liangliang.jia,2015年7月23日下午2:52:13
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
public interface ClientPoolsService {


	
	public CopyOnWriteArrayList<Client> clientPools = new CopyOnWriteArrayList<Client>();
	
	/**
	 * get ElasticSearch Client Instance
	 * @return
	 */
	public  Client getESClientInstance();
	
	/**
	 * release one Client to Client Pools
	 * @param client
	 */
	public  void releaseOneESClient(Client client);
	
	/**
	 * close all ElasticSearch clients 
	 */
	public  void closeESClient();
}
