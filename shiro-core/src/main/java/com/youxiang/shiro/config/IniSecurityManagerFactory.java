package com.youxiang.shiro.config;

import com.youxiang.shiro.mgt.SecurityManager;

/**
 * Author: RiversLau
 * Date: 2018/1/3 16:06
 */
public class IniSecurityManagerFactory extends IniFactorySupport<SecurityManager> {

    protected SecurityManager createInstance(Ini ini) {
        return null;
    }

    protected SecurityManager createDefaultInstance() {
        return null;
    }
}
