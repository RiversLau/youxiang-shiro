package com.youxiang.shiro.config;

import com.youxiang.shiro.mgt.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: RiversLau
 * Date: 2018/1/3 16:06
 */
public class IniSecurityManagerFactory extends IniFactorySupport<SecurityManager> {

    private static final String MAIN_SECTION_NAME = "main";

    private static final String SECURITY_MANAGER_NAME = "securityManager";
    private static final String INI_REALM_NAME = "iniRealm";

    private static transient final Logger log = LoggerFactory.getLogger(IniSecurityManagerFactory.class);

    protected SecurityManager createInstance(Ini ini) {
        return null;
    }

    protected SecurityManager createDefaultInstance() {
        return null;
    }
}
