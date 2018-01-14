package com.youxiang.shiro.authz.permission;

import com.youxiang.shiro.authz.Permission;

import java.util.Collection;

/**
 * Author: Rivers
 * Date: 2018/1/14 20:51
 */
public interface RolePermissionResolver {

    Collection<Permission> resolvePermissionsInRole(String roleString);
}
