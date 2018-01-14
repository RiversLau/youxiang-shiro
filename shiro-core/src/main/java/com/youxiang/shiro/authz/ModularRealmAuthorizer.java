package com.youxiang.shiro.authz;

import com.youxiang.shiro.authz.permission.PermissionResolver;
import com.youxiang.shiro.authz.permission.PermissionResolverAware;
import com.youxiang.shiro.authz.permission.RolePermissionResolver;
import com.youxiang.shiro.authz.permission.RolePermissionResolverAware;
import com.youxiang.shiro.realm.Realm;
import com.youxiang.shiro.subject.PrincipalCollection;

import java.util.Collection;
import java.util.List;

/**
 * Author: Rivers
 * Date: 2018/1/14 20:53
 */
public class ModularRealmAuthorizer implements Authorizer, PermissionResolverAware, RolePermissionResolverAware {

    protected Collection<Realm> realms;

    protected PermissionResolver permissionResolver;
    protected RolePermissionResolver rolePermissionResolver;

    public ModularRealmAuthorizer() {
    }

    public ModularRealmAuthorizer(Collection<Realm> realms) {
        setRealms(realms);
    }

    public Collection<Realm> getRealms() {
        return realms;
    }

    public void setRealms(Collection<Realm> realms) {
        this.realms = realms;
        applyPermissionResolverToRealms();
        applyRolePermissionResolverToRealms();
    }

    public PermissionResolver getPermissionResolver() {
        return permissionResolver;
    }

    public void setPermissionResolver(PermissionResolver permissionResolver) {
        this.permissionResolver = permissionResolver;
        applyPermissionResolverToRealms();
    }

    protected void applyPermissionResolverToRealms() {
        PermissionResolver resolver = getPermissionResolver();
        Collection<Realm> realms = getRealms();
        if (realms != null & resolver != null && !realms.isEmpty()) {
            for (Realm realm : realms) {
                if (realm instanceof PermissionResolverAware) {
                    ((PermissionResolverAware) realm).setPermissionResolver(permissionResolver);
                }
            }
        }
    }

    public RolePermissionResolver getRolePermissionResolver() {
        return rolePermissionResolver;
    }

    public void setRolePermissionResolver(RolePermissionResolver rolePermissionResolver) {
        this.rolePermissionResolver = rolePermissionResolver;
        applyRolePermissionResolverToRealms();
    }

    protected void applyRolePermissionResolverToRealms() {
        RolePermissionResolver resolver = getRolePermissionResolver();
        Collection<Realm> realms = getRealms();
        if (resolver != null && realms != null && !realms.isEmpty()) {
            for (Realm realm : realms) {
                if (realm instanceof RolePermissionResolverAware) {
                    ((RolePermissionResolverAware) realm).setRolePermissionResolver(rolePermissionResolver);
                }
            }
        }
    }

    protected void assertRealmsConfigured() {
        Collection<Realm> realms = getRealms();
        if (realms == null || realms.isEmpty()) {
            String msg = "Configuration error:  No realms have been configured!  One or more realms must be " +
                    "present to execute an authorization operation.";
            throw new IllegalStateException(msg);
        }
    }

    public boolean isPermitted(PrincipalCollection principals, String permissions) {
        assertRealmsConfigured();
        for (Realm realm : getRealms()) {
            if (!(realm instanceof Authorizer)) {
                continue;
            }
            if (((Authorizer) realm).isPermitted(principals, permissions)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPermitted(PrincipalCollection principals, Permission permission) {
        assertRealmsConfigured();
        for (Realm realm : getRealms()) {
            if (!(realm instanceof Authorizer)) {
                continue;
            }
            if (((Authorizer) realm).isPermitted(principals, permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean[] isPermitted(PrincipalCollection principals, List<Permission> permissions) {
        assertRealmsConfigured();
        if (permissions != null && !permissions.isEmpty()) {
            boolean[] isPermitted = new boolean[permissions.size()];
            int i = 0;
            for (Permission permission : permissions) {
                isPermitted[i++] = isPermitted(principals, permission);
            }
            return isPermitted;
        }
        return new boolean[0];
    }

    public boolean[] isPermitted(PrincipalCollection principals, String... permissions) {
        assertRealmsConfigured();
        if (permissions != null && permissions.length >0) {
            boolean[] isPermitted = new boolean[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                isPermitted[i] = isPermitted(principals, permissions[i]);
            }
            return isPermitted;
        }
        return new boolean[0];
    }

    public boolean isPermittedAll(PrincipalCollection principals, String... permissions) {
        assertRealmsConfigured();
        if (permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                if (!isPermitted(principals, permission)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPermittedAll(PrincipalCollection principals, List<Permission> permissions) {
        assertRealmsConfigured();
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
                if (!isPermitted(principals, permission)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void checkPermission(PrincipalCollection subjectPrincipal, String permission) throws AuthorizationException {
        assertRealmsConfigured();
        if (!isPermitted(subjectPrincipal, permission)) {
            throw new UnauthorizedException("Subject does not have permission [" + permission + "]");
        }
    }

    public void checkPermission(PrincipalCollection subjectPrincipal, Permission permission) throws AuthorizationException {
        assertRealmsConfigured();
        if (!isPermitted(subjectPrincipal, permission)) {
            throw new UnauthorizedException("Subject does not have permission [" + permission + "]");
        }
    }

    public void checkPermissions(PrincipalCollection principals, String... permissions) throws AuthorizationException {
        assertRealmsConfigured();
        if (permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                checkPermission(principals, permission);
            }
        }
    }

    public void checkPermissions(PrincipalCollection principals, Collection<Permission> permissions) throws AuthorizationException {
        assertRealmsConfigured();
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
                checkPermission(principals, permission);
            }
        }
    }

    public boolean hasRole(PrincipalCollection subjectPrincipal, String roleIdentifier) {
        assertRealmsConfigured();
        for (Realm realm : getRealms()) {
            if (!(realm instanceof Authorizer)) {
                continue;
            }
            if (((Authorizer) realm).hasRole(subjectPrincipal, roleIdentifier)) {
                return true;
            }
        }
        return false;
    }

    public boolean[] hasRoles(PrincipalCollection subjectPrincipal, List<String> roleIdentifiers) {
        assertRealmsConfigured();
        if (roleIdentifiers != null && !roleIdentifiers.isEmpty()) {
            boolean[] hasRoles = new boolean[roleIdentifiers.size()];
            int i = 0;
            for (String roleId : roleIdentifiers) {
                hasRoles[i++] = hasRole(subjectPrincipal, roleId);
            }
            return hasRoles;
        }
        return new boolean[0];
    }

    public boolean hasAllRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) {
        assertRealmsConfigured();
        for (String roleId : roleIdentifiers) {
            if (!hasRole(subjectPrincipal, roleId)) {
                return false;
            }
        }
        return true;
    }

    public void checkRole(PrincipalCollection subjectPrincipal, String roleIdentifier) throws AuthorizationException {
        assertRealmsConfigured();
        if (!hasRole(subjectPrincipal, roleIdentifier)) {
            throw new UnauthorizedException("Subject does not have role [" + roleIdentifier + "].");
        }
    }

    public void checkRoles(PrincipalCollection subjectPrincipal, String... roleIdentifiers) throws AuthorizationException {
        assertRealmsConfigured();
        if (roleIdentifiers != null) {
            for (String role : roleIdentifiers) {
                checkRole(subjectPrincipal, role);
            }
        }
    }

    public void checkRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) throws AuthorizationException {
        if (roleIdentifiers != null && !roleIdentifiers.isEmpty()) {
            checkRoles(subjectPrincipal, roleIdentifiers.toArray(new String[roleIdentifiers.size()]));
        }
    }
}
