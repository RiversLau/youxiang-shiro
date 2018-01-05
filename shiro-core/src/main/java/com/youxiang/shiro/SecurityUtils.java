package com.youxiang.shiro;

import com.youxiang.shiro.subject.Subject;
import com.youxiang.shiro.util.ThreadContext;
import com.youxiang.shiro.mgt.SecurityManager;

/**
 * Author: RiversLau
 * Date: 2018/1/4 17:54
 */
public abstract class SecurityUtils {

    private static SecurityManager securityManager;

    public static Subject getSubject() {
        Subject subject = ThreadContext.getSubject() ;
        if (subject == null) {
            subject = (new Subject.Builder()).buildSubject();
            ThreadContext.bind(subject);
        }
        return subject;
    }

    public static void setSecurityManager(SecurityManager securityManager) {
        SecurityUtils.securityManager = securityManager;
    }

    public static SecurityManager getSecurityManager() throws UnavailableSecurityManagerException {
        SecurityManager securityManager = ThreadContext.getSecurityManager();
        if (securityManager == null) {
            securityManager = SecurityUtils.securityManager;
        }
        if (securityManager == null) {
            String msg = "No SecurityManager accessiable to the calling code, either bound to the " +
                    ThreadContext.class.getName() + " or as a vm static singleton. This is an invalid application configuration.";
            throw new UnavailableSecurityManagerException(msg);
        }
        return securityManager;
    }
}
