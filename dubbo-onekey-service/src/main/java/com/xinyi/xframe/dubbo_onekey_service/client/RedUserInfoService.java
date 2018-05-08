/**
 *	Copyright © 1997 - 2016 Xinyi Tech. All Rights Reserved 
 */
package com.xinyi.xframe.dubbo_onekey_service.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.xinyi.xframe.dubbo_onekey_service.utils.Constant;

/**
 * 功能说明：红名单信息过滤，部分红名单数据缓存到内存中
 * 
 * RedUserInfo.java
 * 
 * Original Author: liangliangl.jia,2016年3月24日下午6:51:50
 * 
 * Copyright (C)1997-2016 深圳小树盛凯科技 All rights reserved.
 */
@Service
public class RedUserInfoService {

	@Autowired
	private DruidDataSource dataSource;

	private static Logger logger = LoggerFactory.getLogger(RedUserInfoService.class);

	private CopyOnWriteArrayList<String> redUserCarIds = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<String> redUserPhones = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<String> redUserMacs = new CopyOnWriteArrayList<String>();

	private CopyOnWriteArrayList<Connection> connClients = new CopyOnWriteArrayList<Connection>();
	// 数据库连接
	private int defaultInitMax = 3;

	/**
	 * 从连接池中获得连接客户端 小树盛凯
	 * 
	 * @return
	 */
	private Connection getConnectionInstance() {
		Connection connInstance = null;
		try {
			connInstance = dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connInstance;
	}

	private void initConnPools() {
		for (int i = 0; i < this.defaultInitMax; i++) {
			connClients.add(this.getConnectionInstance());
		}
	}

	/**
	 * 获得连接对象
	 */
	public Connection getConnInstance() {
		Connection conn = null;
		if (!CollectionUtils.isEmpty(connClients)) {
			conn = connClients.remove(0);
			try {
				if (null == conn || conn.isClosed()) {
					conn = this.getConnectionInstance();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			conn = this.getConnectionInstance();
		}
		logger.debug("connClients size--------->" + connClients.size());
		return conn;
	}

	public void releaseOneESClient(Connection conn) {
		try {
			if (null != conn && !conn.isClosed()) {
				connClients.add(conn);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		logger.debug("connClientsXINYI  size------------> "
				+ connClients.size());
		System.out.println("connClientsXINYI  size------------> "
				+ connClients.size());
	}

	/**
	 * 清除红名单信息
	 */
	public void clearRedUserInfo() {

		redUserCarIds.removeAll(redUserCarIds);

		redUserPhones.removeAll(redUserPhones);

		redUserMacs.removeAll(redUserMacs);
	}

	@PostConstruct
	public void initialRedUserInfo() {
		logger.info("load red user info .... start ...");

		initConnPools();
		Connection conn = this.getConnInstance();

		try {
			String sqlStr = "SELECT t.TYPE as TYPE,t.VAL as VAL from TAB_SYSTEM_REDLIST t";
			PreparedStatement ps = conn.prepareStatement(sqlStr);
			ResultSet rs = ps.executeQuery();
			List<Map<String, Object>> rsList = this.convertResultList(rs);
			for (Map<String, Object> map : rsList) {
				if (null != map.get("TYPE")&&null != map.get("VAL")) {
					if (map.get("TYPE").toString().trim().equals(Constant.RED_CARID)) {
						redUserCarIds.add(map.get("VAL").toString().trim());
					} else if (map.get("TYPE").toString().trim().equals(Constant.RED_WIFI)) {
						redUserMacs.add(map.get("VAL").toString().trim());
					} else if (map.get("TYPE").toString().trim().equals(Constant.RED_PHONE)) {
						redUserPhones.add(map.get("VAL").toString().trim());
					}
				}
			}
			logger.info("load red user info ... end ....success!");
			System.out.println("phone num size is =" + redUserPhones.size());
			System.out.println("car plate size is =" + redUserCarIds.size());
			System.out.println("mac address size is =" + redUserMacs.size());
		} catch (Exception e) {
			logger.info("error_message" + e.getMessage());
		} finally {
			this.releaseOneESClient(conn);
		}

	}

	//重新加载红名单数据
	public void reloadRedUserInfo() {
		// clear all red user info
		this.clearRedUserInfo();
		// reload the red user info
		this.initialRedUserInfo();
	}

	/**
	 * @return the redUserCarIds
	 */
	public CopyOnWriteArrayList<String> getRedUserCarIds() {
		return redUserCarIds;
	}

	/**
	 * @return the redUserPhones
	 */
	public CopyOnWriteArrayList<String> getRedUserPhones() {
		return redUserPhones;
	}

	/**
	 * @return the redUserMacs
	 */
	public CopyOnWriteArrayList<String> getRedUserMacs() {
		return redUserMacs;
	}

	/**
	 * Convert the ResultSet to List<Map<String,Object>>
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<Map<String, Object>> convertResultList(ResultSet rs)
			throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ResultSetMetaData md = rs.getMetaData();
		// get the Column total
		int columnCount = md.getColumnCount();
		// Map rowData;
		while (rs.next()) {
			// rowData = new HashMap(columnCount);
			Map<String, Object> rowData = new HashMap<String, Object>();
			for (int i = 1; i <= columnCount; i++) {
				//use column name
				// rowData.put(md.getColumnName(i), rs.getObject(i));
				//use alias name
				rowData.put(md.getColumnLabel(i), rs.getObject(i));
			}
			list.add(rowData);
		}
		return list;
	}
}
