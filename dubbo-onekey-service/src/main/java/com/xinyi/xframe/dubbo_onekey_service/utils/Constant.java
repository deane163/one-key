/**
*
*/
package com.xinyi.xframe.dubbo_onekey_service.utils;

/**
 * 功能说明：超级搜索服务器方的常量配置
 * 
 * Constant.java
 * 
 * Original Author: liangliang.jia,2015年7月27日上午9:53:51
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
public class Constant {

	//the return status of manipulate index ok
	public static String INDEX_RETURN_STATUS_OK = "true";
	
	//the return status of manipulate index error
	public static String INDEX_RETURN_STATUS_ERROR = "false";
	
	
	//红名单过滤常量 1---车牌  2---wifi  3-----手机号码
	public final static String  RED_CARID="1";
	
	public final static String  RED_WIFI="2";
	
	public final static String  RED_PHONE="3";
	
	public static String PARAM_USER_INFO_CAR_ID ="car_id";
	
	public static String PARAM_USER_INFO_MAC_ADDRESS ="mac";
	
	public static String PARAM_FORMAT_XDATA_RKSJ="xdata_rksj";
	
	public static String UNIDENTIFIED_CAR_ID_NO1 = "车牌";
	
	public static String UNIDENTIFIED_CAR_ID_NO2 = "?";
	
	public static String UNIDENTIFIED_CAR_ID_NO3 = "??";
	
	public static String UNIDENTIFIED_CAR_ID_NO4 = "???";
}
