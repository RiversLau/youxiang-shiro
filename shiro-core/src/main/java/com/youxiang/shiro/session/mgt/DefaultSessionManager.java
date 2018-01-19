package com.youxiang.shiro.session.mgt;

import com.youxiang.shiro.cache.CacheManager;
import com.youxiang.shiro.cache.CacheManagerAware;
import com.youxiang.shiro.session.mgt.eis.SessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: RiversLau
 * Date: 2018/1/19 10:58
 */
public class DefaultSessionManager extends AbstractValidatingSessionManager implements CacheManagerAware {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSessionManager.class);

    private SessionFactory sessionFactory;
    private SessionDAO sessionDAO;

    private CacheManager cacheManager;
    private boolean deleteInvalidSessions;

    public DefaultSessionManager() {
        this.deleteInvalidSessions = true;
        this.sessionFactory = new SimpleSessionFactory();
        this.sessionDAO = new MemorySessionDAO();
    }
}
