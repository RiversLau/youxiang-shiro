package com.youxiang.shiro.authc.pam;

import com.youxiang.shiro.authc.*;
import com.youxiang.shiro.realm.Realm;
import com.youxiang.shiro.subject.PrincipalCollection;
import com.youxiang.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Author: RiversLau
 * Date: 2018/1/12 11:58
 */
public class ModularRealmAuthenticator extends AbstractAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(ModularRealmAuthenticator.class);

    private Collection<Realm> realms;

    private AuthenticationStrategy authenticationStrategy;

    public ModularRealmAuthenticator() {
        this.authenticationStrategy = new AtLeastOneSuccessfulStrategy();
    }

    public void setRealms(Collection<Realm> realms) {
        this.realms = realms;
    }

    public Collection<Realm> getRealms() {
        return realms;
    }

    public AuthenticationStrategy getAuthenticationStrategy() {
        return authenticationStrategy;
    }

    public void setAuthenticationStrategy(AuthenticationStrategy authenticationStrategy) {
        this.authenticationStrategy = authenticationStrategy;
    }

    protected void assertRealmsConfigured() {
        Collection<Realm> realms = getRealms();
        if (CollectionUtils.isEmpty(realms)) {
            String msg = "Configuration error:  No realms have been configured!  One or more realms must be " +
                    "present to execute an authentication attempt.";
            throw new IllegalStateException(msg);
        }
    }

    protected AuthenticationInfo doSingleRealmAuthentication(Realm realm, AuthenticationToken token) {
        if (!realm.supports(token)) {
            String msg = "Realm [" + realm + "] does not support authentication token [" +
                    token + "].  Please ensure that the appropriate Realm implementation is " +
                    "configured correctly or that the realm accepts AuthenticationTokens of this type.";
            throw new UnsupportedTokenException(msg);
        }
        AuthenticationInfo info = realm.getAuthenticationInfo(token);
        if (info == null) {
            String msg = "Realm [" + realm + "] was unable to find account data for the " +
                    "submitted AuthenticationToken [" + token + "].";
            throw new UnknownAccountException(msg);
        }
        return info;
    }

    protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken token) {

        AuthenticationStrategy strategy = getAuthenticationStrategy();

        AuthenticationInfo aggregate = strategy.beforeAllAttempts(realms, token);
        if (logger.isTraceEnabled()) {
            logger.trace("Iterating through {} realms fro PAM authentication.", realms.size());
        }

        for (Realm realm : realms) {
            aggregate = strategy.beforeAttempt(realm, token, aggregate);
            if (realm.supports(token)) {
                logger.trace("Attempting to authenticate token [{}] using realm [{}].", token, realm);

                AuthenticationInfo info = null;
                Throwable t = null;
                try {
                    info = realm.getAuthenticationInfo(token);
                } catch (Throwable throwable) {
                    t = throwable;
                    if (logger.isDebugEnabled()) {
                        String msg = "";
                        logger.debug(msg, t);
                    }
                }
                aggregate = strategy.afterAttempt(realm, token, info, aggregate, t);
            } else {
                logger.debug("Realm [{}] does not support token {}. Skipping realm.", realm, token);
            }
        }

        strategy.afterAllAttempts(token, aggregate);
        return aggregate;
    }

    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken token) throws AuthenticationException {
        assertRealmsConfigured();
        Collection<Realm> realms = getRealms();
        if (realms.size() == 1) {
            return doSingleRealmAuthentication(realms.iterator().next(), token);
        } else {
            return doMultiRealmAuthentication(realms, token);
        }
    }

    public void onLogout(PrincipalCollection principals) {
        super.onLogout(principals);
        Collection<Realm> realms = getRealms();
        if (!CollectionUtils.isEmpty(realms)) {
            for (Realm realm : realms) {
                if (realm instanceof LogoutAware) {
                    ((LogoutAware) realm).onLogout(principals);
                }
            }
        }
    }
}
