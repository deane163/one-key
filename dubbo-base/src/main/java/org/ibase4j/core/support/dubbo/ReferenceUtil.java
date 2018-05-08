package org.ibase4j.core.support.dubbo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.context.ApplicationContext;

import com.alibaba.dubbo.config.spring.ReferenceBean;

/**
 * 
 * 功能说明：
 * 
 * ReferenceUtil.java
 * 
 * Original Author: liangliangl.jia-贾亮亮,2016年9月12日下午5:37:40
 * 
 * Copyright (C)1997-2016 小树盛凯科技 All rights reserved.
 */
public class ReferenceUtil {
    private ReferenceUtil() {
    }

    private static final ConcurrentMap<String, ReferenceBean<?>> referenceConfigs = new ConcurrentHashMap<String, ReferenceBean<?>>();

    /** 获取Dubbo服务 */
    public static Object refer(ApplicationContext applicationContext, String interfaceName) {
        String key = "/" + interfaceName + ":";
        ReferenceBean<?> referenceConfig = referenceConfigs.get(key);
        if (referenceConfig == null) {
            referenceConfig = new ReferenceBean<Object>();
            referenceConfig.setInterface(interfaceName);
            if (applicationContext != null) {
                referenceConfig.setApplicationContext(applicationContext);
                try {
                    referenceConfig.afterPropertiesSet();
                } catch (RuntimeException e) {
                    throw (RuntimeException)e;
                } catch (Exception e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }
            referenceConfigs.putIfAbsent(key, referenceConfig);
            referenceConfig = referenceConfigs.get(key);
        }
        return referenceConfig.get();
    }
}
