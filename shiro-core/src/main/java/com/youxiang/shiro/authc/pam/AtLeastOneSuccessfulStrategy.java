package com.youxiang.shiro.authc.pam;

import com.youxiang.shiro.authc.AuthenticationException;
import com.youxiang.shiro.authc.AuthenticationInfo;
import com.youxiang.shiro.authc.AuthenticationToken;
import com.youxiang.shiro.subject.PrincipalCollection;

/**
 * Author: RiversLau
 * Date: 2018/1/12 13:59
 */
public class AtLeastOneSuccessfulStrategy extends AbstractAuthenticationStrategy {

    private static boolean isEmpty(PrincipalCollection principals) {
        return principals == null || principals.isEmpty();
    }

    @Override
    public AuthenticationInfo afterAllAttempts(AuthenticationToken token, AuthenticationInfo aggregate) throws AuthenticationException {
        if (aggregate == null || isEmpty(aggregate.getPrincipals())) {
            String msg = "Authentication token of type [" + token.getClass() + "] " +
                    "could not be authenticated by any configured realms.  Please ensure that at least one realm can " +
                    "authenticate these tokens.";
            throw new AuthenticationException(msg);
        }
        return aggregate;
    }
}
