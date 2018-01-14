package com.youxiang.shiro.authz.permission;

import com.youxiang.shiro.authz.Permission;

/**
 * Author: Rivers
 * Date: 2018/1/14 20:50
 */
public interface PermissionResolver {

    Permission resolvePermission(String permissionString);
}
