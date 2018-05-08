/**
 *	Copyright © 1997 - 2015 Xinyi Tech. All Rights Reserved 
 */
package com.xinyi.xframe.dubbo_onekey_service.utils;

/**
 * 功能说明：
 * 
 * EsProperties.java
 * 
 * Original Author: liangliang.jia,2015年8月21日下午2:55:18
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
public class EsProperties {

	private String clusterName;
	private String serverAddress;
	private String serverPort;
	private String searchClusterList;

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getSearchClusterList() {
		return searchClusterList;
	}

	public void setSearchClusterList(String searchClusterList) {
		this.searchClusterList = searchClusterList;
	}
}
