package org.ibase4j.core.support.dubbo.spring.schema;

import org.ibase4j.core.support.dubbo.spring.AnnotationBean;
import org.ibase4j.core.support.dubbo.spring.DubboReferenceBean;
import org.ibase4j.core.support.dubbo.spring.DubboServiceBean;
import org.ibase4j.core.support.dubbo.spring.annotation.DubboReference;
import org.ibase4j.core.support.dubbo.spring.annotation.DubboService;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.alibaba.dubbo.common.Version;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ModuleConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.schema.DubboBeanDefinitionParser;

/**
 * 
 * 功能说明：
 * 
 * DubboNamespaceHandler.java
 * 
 * Original Author: liangliangl.jia-贾亮亮,2016年9月12日下午5:38:23
 * 
 * Copyright (C)1997-2016 小树盛凯科技 All rights reserved.
 */

public class DubboNamespaceHandler extends NamespaceHandlerSupport {
	
    static {
        Version.checkDuplicate(DubboNamespaceHandler.class);
    }
   
    public void init() {
        registerBeanDefinitionParser("application", new DubboBeanDefinitionParser(ApplicationConfig.class, true));
        registerBeanDefinitionParser("module", new DubboBeanDefinitionParser(ModuleConfig.class, true));
        registerBeanDefinitionParser("registry", new DubboBeanDefinitionParser(RegistryConfig.class, true));
        registerBeanDefinitionParser("monitor", new DubboBeanDefinitionParser(MonitorConfig.class, true));
        registerBeanDefinitionParser("provider", new DubboBeanDefinitionParser(ProviderConfig.class, true));
        registerBeanDefinitionParser("consumer", new DubboBeanDefinitionParser(ConsumerConfig.class, true));
        registerBeanDefinitionParser("protocol", new DubboBeanDefinitionParser(ProtocolConfig.class, true));
        registerBeanDefinitionParser("service", new DubboBeanDefinitionParser(DubboServiceBean.class, true));
        registerBeanDefinitionParser("reference", new DubboBeanDefinitionParser(DubboReferenceBean.class, false));
        registerBeanDefinitionParser("annotation", new DubboBeanDefinitionParser(AnnotationBean.class, true));
    }

}
