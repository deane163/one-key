/**
 *	Copyright © 1997 - 2015 Xinyi Tech. All Rights Reserved 
 */
package com.xinyi.xframe.dubbo_onekey_service.client;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;

import com.xinyi.xframe.dubbo_onekey_service.utils.EsProperties;

/**
 * 功能说明：ElasticSearch 获得Client 客户端的功能
 * 
 * ClientInstanceService.java
 * 
 * Original Author: liangliang.jia,2015年7月23日下午6:08:05
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
public class ClientInstanceService {

	protected Client client;
	protected TransportClient transportClient;
	
	@Autowired
	private EsProperties esProperties;

	protected Client getClient() {
		//paramMap.put("cluster.name", esProperties.getClusterName());/
		if (client == null) {
			Settings settings = ImmutableSettings
					.settingsBuilder()
					.put("cluster.name", esProperties.getClusterName())
					.build();
			// 这里可以同时连接集群的服务器,可以多个,并且连接服务是可访问的
			transportClient = new TransportClient(settings);
			client = transportClient
					.addTransportAddress(new InetSocketTransportAddress(
							 esProperties.getServerAddress(),
							Integer.valueOf(esProperties.getServerPort())));
		}
		return client;
	}

	public void closeClient() {
		if (client != null) {
			client.close();
		}
		if (transportClient != null) {
			transportClient.close();
		}
	}

}
