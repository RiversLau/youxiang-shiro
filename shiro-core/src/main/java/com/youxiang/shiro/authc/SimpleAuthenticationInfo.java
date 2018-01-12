package com.youxiang.shiro.authc;

import com.youxiang.shiro.subject.MutablePrincipalCollection;
import com.youxiang.shiro.subject.PrincipalCollection;
import com.youxiang.shiro.subject.SimplePrincipalCollection;
import com.youxiang.shiro.util.ByteSource;
import com.youxiang.shiro.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: RiversLau
 * Date: 2018/1/12 14:07
 */
public class SimpleAuthenticationInfo implements MergableAuthenticationInfo, SaltedAuthenticationInfo {

    protected PrincipalCollection principals;
    protected Object credentials;

    protected ByteSource credentialsSalt;

    public SimpleAuthenticationInfo() {
    }

    public SimpleAuthenticationInfo(Object principal, Object credentials, String realmName) {
        this.principals = new SimplePrincipalCollection(principal, realmName);
        this.credentials = credentials;
    }

    public SimpleAuthenticationInfo(Object principal, Object hashedCredentials, ByteSource credentialsSalt, String realmName) {
        this.principals = new SimplePrincipalCollection(principal, realmName);
        this.credentials = hashedCredentials;
        this.credentialsSalt = credentialsSalt;
    }

    public SimpleAuthenticationInfo(PrincipalCollection principals, Object credentials) {
        this.principals = new SimplePrincipalCollection(principals);
        this.credentials = credentials;
    }

    public SimpleAuthenticationInfo(PrincipalCollection principals, Object hashedCredentials, ByteSource credentailsSalt) {
        this.principals = new SimplePrincipalCollection(principals);
        this.credentials = hashedCredentials;
        this.credentialsSalt = credentailsSalt;
    }

    public PrincipalCollection getPrincipals() {
        return principals;
    }

    public void setPrincipals(PrincipalCollection principals) {
        this.principals = principals;
    }

    public Object getCredentials() {
        return credentials;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }

    public ByteSource getCredentialsSalt() {
        return credentialsSalt;
    }

    public void setCredentialsSalt(ByteSource credentialsSalt) {
        this.credentialsSalt = credentialsSalt;
    }

    public void merge(AuthenticationInfo info) {
        if (info == null || info.getPrincipals() == null || info.getPrincipals().isEmpty()) {
            return;
        }

        if (this.principals == null) {
            this.principals = info.getPrincipals();
        } else {
            if (!(this.principals instanceof MutablePrincipalCollection)) {
                this.principals = new SimplePrincipalCollection(this.principals);
            }
            ((MutablePrincipalCollection) this.principals).addAll(info.getPrincipals());
        }

        if (this.credentialsSalt == null && info instanceof SaltedAuthenticationInfo) {
            this.credentialsSalt = ((SaltedAuthenticationInfo) info).getCredentialsSalt();
        }

        Object thisCredentials = getCredentials();
        Object otherCredentials = info.getCredentials();
        if (otherCredentials == null) {
            return;
        }
        if (thisCredentials == null) {
            this.credentials = otherCredentials;
            return;
        }

        if (!(this.credentials instanceof Collection)) {
            Set newSet = new HashSet();
            newSet.add(this.credentials);
            setCredentials(newSet);
        }

        Collection credentialCollection = (Collection) getCredentials();
        if (otherCredentials instanceof Collection) {
            credentialCollection.addAll((Collection) otherCredentials);
        } else {
            credentialCollection.add(otherCredentials);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleAuthenticationInfo)) {
            return false;
        }

        SimpleAuthenticationInfo other = (SimpleAuthenticationInfo) o;
        if (principals != null ? !principals.equals(other.principals) : other.principals != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return principals != null ? principals.hashCode() : 0;
    }

    public String toString() {
        return principals.toString();
    }
}