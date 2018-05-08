package org.ibase4j.core.support.dubbo.spring;

import org.ibase4j.core.support.dubbo.spring.annotation.DubboReference;

import com.alibaba.dubbo.config.spring.ReferenceBean;

/**
 * 
 * 功能说明：
 * 
 * DubboReferenceBean.java
 * 
 * Original Author: liangliangl.jia-贾亮亮,2016年9月12日下午5:37:55
 * 
 * Copyright (C)1997-2016 小树盛凯科技 All rights reserved.
 */

@SuppressWarnings("serial")
public class DubboReferenceBean<T> extends ReferenceBean<T> {

	public DubboReferenceBean() {
		super();
	}

	public DubboReferenceBean(DubboReference reference) {
		appendAnnotation(DubboReference.class, reference);
	}
}
