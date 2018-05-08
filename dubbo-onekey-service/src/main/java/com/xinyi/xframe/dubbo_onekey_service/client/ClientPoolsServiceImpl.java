package com.xinyi.xframe.dubbo_onekey_service.client;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinyi.xframe.dubbo_onekey_api.ClientPoolsService;
import com.xinyi.xframe.dubbo_onekey_service.utils.EsProperties;

/**
 * 功能说明：ElasticSearch server Connection Client Pools  
 * ElasticSearch 客户端连接池，可以做连接池使用
 * 
 * ClientPoolsServiceImpl.java
 * 
 * Original Author: liangliangl.jia-贾亮亮,2016年4月5日下午2:49:04
 * 
 * Copyright (C)1997-2016 深圳小树盛凯科技 All rights reserved.
 */
@Service
public class ClientPoolsServiceImpl implements ClientPoolsService {

	private static Logger log = LoggerFactory.getLogger(ClientPoolsServiceImpl.class);
	private CopyOnWriteArrayList<Client> clientPools = new CopyOnWriteArrayList<Client>();
	private CopyOnWriteArrayList<TransportClient> transPortClients = new CopyOnWriteArrayList<TransportClient>();
	private int defaultInitMax = 3;

	@Autowired
	private EsProperties esProperties;

	@PostConstruct
	public void initClientPools(){
		for (int i = 0; i < this.defaultInitMax; i++) {
			clientPools.add(getClient());
		}
		log.info("====> Initial the ElasticSearch Size is :{}", clientPools.size());
	}

	@Override
	public Client getESClientInstance() {
		Client client = null;
		if (clientPools != null && !clientPools.isEmpty()) {
			client = clientPools.remove(0);
			if(null == client ){
				client = this.getClient();
			}
		} else {
			client = this.getClient();
		}
		return client;
	}

	// return the client to client pools
	@Override
	public void releaseOneESClient(Client client) {
		if (client != null) {
			clientPools.add(client);
		}
	}

	@Override
	public void closeESClient() {
		// close the client Pools
		if (CollectionUtils.isNotEmpty(clientPools)) {
			for (Client client : clientPools) {
				if (client != null) {
					client.close();
				}
			}
		}
		// close the TransportClient
		if (CollectionUtils.isNotEmpty(transPortClients)) {
			for (TransportClient trans : transPortClients) {
				trans.close();
			}
		}
	}

	/**
	 * @return the elasticSearch client
	 */
	private Client getClient() {
		// 这里可以同时连接集群的服务器,可以多个,并且连接服务是可访问的
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", esProperties.getClusterName()).build();
		TransportClient transportClient = new TransportClient(settings);
		Client client = transportClient.addTransportAddress(new InetSocketTransportAddress(
						esProperties.getServerAddress(), Integer.valueOf(esProperties.getServerPort())));
		transPortClients.add(transportClient);
		return client;
	}
}
