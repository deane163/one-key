package org.ibase4j.core.support.dubbo.spring;

import org.ibase4j.core.support.dubbo.spring.annotation.DubboService;

import com.alibaba.dubbo.config.spring.ServiceBean;

/**
 * 
 * 功能说明：
 * 
 * DubboServiceBean.java
 * 
 * Original Author: liangliangl.jia-贾亮亮,2016年9月12日下午5:38:03
 * 
 * Copyright (C)1997-2016 小树盛凯科技 All rights reserved.
 */

@SuppressWarnings("serial")
public class DubboServiceBean<T> extends ServiceBean<T> {
	public DubboServiceBean() {
		super();
	}

	public DubboServiceBean(DubboService service) {
		appendAnnotation(DubboService.class, service);
	}
}
