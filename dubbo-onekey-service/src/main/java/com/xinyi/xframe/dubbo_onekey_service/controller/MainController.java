/**
*	Copyright © 1997 - 2016 Xinyi Tech. All Rights Reserved 
*/
package com.xinyi.xframe.dubbo_onekey_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.xinyi.xframe.dubbo_onekey_service.client.RedUserInfoService;

/**
 * 功能说明：查看服务器状体，同时提供红名单数据的重新加载操作
 * 
 * MainController.java
 * 
 * Original Author: liangliangl.jia,2016年3月31日下午5:38:14
 * 
 * Copyright (C)1997-2016 深圳小树盛凯科技 All rights reserved.
 */

@RestController
@RequestMapping(value="/main")
public class MainController {
	
	private Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@Autowired
	private RedUserInfoService redUserInfoService;
	
	@RequestMapping(value="/status",method=RequestMethod.GET,produces="text/html;charset=UTF-8")
	public String getOneKeySearchStatus(){
		logger.info("The ONE KEY search is health ...");
		return "One key search, The Server Status is health...";
	}

	@RequestMapping(value="/reloadRed",method=RequestMethod.POST,produces="text/html;charset=UTF-8")
	public String reloadRedUserInfo(){
		logger.info("THE ONE KEY SEARCH RED USER INFO is reloading data ...");
		redUserInfoService.reloadRedUserInfo();
		
		logger.info("THE ONE KEY SEARCH RED USER INFO is reload data success ...");
		return "Red user Info reload is success...";
	}
}
