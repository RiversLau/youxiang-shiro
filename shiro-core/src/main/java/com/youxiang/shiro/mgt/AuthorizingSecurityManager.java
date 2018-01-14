package com.youxiang.shiro.mgt;

import com.youxiang.shiro.authz.AuthorizationException;
import com.youxiang.shiro.authz.Authorizer;
import com.youxiang.shiro.authz.ModularRealmAuthorizer;
import com.youxiang.shiro.authz.Permission;
import com.youxiang.shiro.subject.PrincipalCollection;
import com.youxiang.shiro.util.LifecycleUtils;

import java.util.Collection;
import java.util.List;

/**
 * Author: Rivers
 * Date: 2018/1/13 23:39
 */
public abstract class AuthorizingSecurityManager extends AuthenticatingSecurityManager {

    private Authorizer authorizer;

    public AuthorizingSecurityManager() {
        super();
        this.authorizer = new ModularRealmAuthorizer();
    }

    public Authorizer getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(Authorizer authorizer) {
        if (authorizer == null) {
            String msg = "Authorizer argument cannot be null.";
            throw new IllegalArgumentException(msg);
        }
        this.authorizer = authorizer;
    }

    @Override
    protected void afterRealmsSet() {
        super.afterRealmsSet();
        if (this.authorizer instanceof ModularRealmAuthorizer) {
            ((ModularRealmAuthorizer) this.authorizer).setRealms(getRealms());
        }
    }

    @Override
    public void destroy() {
        LifecycleUtils.destroy(this.authorizer);
        this.authorizer = null;
        super.destroy();
    }

    public boolean isPermitted(PrincipalCollection principals, String permission) {
        return this.authorizer.isPermitted(principals, permission);
    }

    public boolean isPermitted(PrincipalCollection principals, Permission permission) {
        return this.authorizer.isPermitted(principals, permission);
    }

    public boolean[] isPermitted(PrincipalCollection principals, String... permissions) {
        return this.authorizer.isPermitted(principals, permissions);
    }

    public boolean[] isPermitted(PrincipalCollection principals, List<Permission> permissions) {
        return this.authorizer.isPermitted(principals, permissions);
    }

    public boolean isPermittedAll(PrincipalCollection principals, String... permissions) {
        return this.authorizer.isPermittedAll(principals, permissions);
    }

    public boolean isPermittedAll(PrincipalCollection principals, List<Permission> permission) {
        return this.authorizer.isPermittedAll(principals, permission);
    }

    public void checkPermission(PrincipalCollection subjectPrincipal, String permission) throws AuthorizationException {
        this.authorizer.checkPermission(subjectPrincipal, permission);
    }

    public void checkPermission(PrincipalCollection subjectPrincipal, Permission permission) throws AuthorizationException {
        this.authorizer.checkPermission(subjectPrincipal, permission);
    }

    public void checkPermissions(PrincipalCollection principals, String... permissions) throws AuthorizationException {
        this.authorizer.checkPermissions(principals, permissions);
    }

    public void checkPermissions(PrincipalCollection principals, Collection<Permission> permissions) throws AuthorizationException {
        this.authorizer.checkPermissions(principals, permissions);
    }

    public boolean hasRole(PrincipalCollection subjectPrincipal, String roleIdentifier) {
        return this.authorizer.hasRole(subjectPrincipal, roleIdentifier);
    }

    public boolean[] hasRoles(PrincipalCollection subjectPrincipal, List<String> roleIdentifiers) {
        return this.authorizer.hasRoles(subjectPrincipal, roleIdentifiers);
    }

    public boolean hasAllRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) {
        return this.authorizer.hasAllRoles(subjectPrincipal, roleIdentifiers);
    }

    public void checkRole(PrincipalCollection subjectPrincipal, String roleIdentifier) throws AuthorizationException {
        this.authorizer.checkRole(subjectPrincipal, roleIdentifier);
    }

    public void checkRoles(PrincipalCollection subjectPrincipal, String... roleIdentifiers) throws AuthorizationException {
        this.authorizer.checkRoles(subjectPrincipal, roleIdentifiers);
    }

    public void checkRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) throws AuthorizationException {
        this.authorizer.checkRoles(subjectPrincipal, roleIdentifiers);
    }
}
