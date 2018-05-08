/**
 *	Copyright © 1997 - 2015 Xinyi Tech. All Rights Reserved 
 */
package com.xinyi.xframe.dubbo_onekey_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * 功能说明：启动ElasticSearch 查询检索的服务端，提供各类Index的检索和部分分析功能
 * 
 * ApplicationStart.java
 * 
 * Original Author: liangliang.jia,2015年10月28日下午5:35:54
 * 
 * Copyright (C)1997-2015 深圳小树盛凯科技 All rights reserved.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@ImportResource("classpath:application-dubbo-onekey-provider.xml")
public class ApplicationStart {

	private static Logger log = LoggerFactory.getLogger(ApplicationStart.class);
	
	public static void main(String[] args) {
		log.info("start up One Key Server ...");
		SpringApplication.run(ApplicationStart.class);
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		registrationBean.setFilter(characterEncodingFilter);
		return registrationBean;
	}
}
