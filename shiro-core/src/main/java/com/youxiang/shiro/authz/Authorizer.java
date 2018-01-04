package com.youxiang.shiro.authz;

import com.youxiang.shiro.subject.PrincipalCollection;

import java.util.Collection;
import java.util.List;

/**
 * Author: RiversLau
 * Date: 2018/1/3 16:18
 */
public interface Authorizer {

    /**
     * 如果主体/用户被允许去执行某个由特定的权限字符串代表的操作或资源，则返回true
     * @param principals
     * @param permission
     * @return
     */
    boolean isPermitted(PrincipalCollection principals, String permission);

    /**
     * 如果主体/用户被允许执行某个动作或访问某个资源，返回true
     * @param principals
     * @param permission
     * @return
     */
    boolean isPermitted(PrincipalCollection principals, Permission permission);

    /**
     * 检查对应的主体/用户是否具有给定的字符串权限信息，返回对应的boolean数组
     * @param principals
     * @param permissions
     * @return
     */
    boolean[] isPermitted(PrincipalCollection principals, String... permissions);

    /**
     * 检查对应的主体/用户是否具有给定数组内的权限，返回对应的boolean数组
     * @param principals
     * @param permissions
     * @return
     */
    boolean[] isPermitted(PrincipalCollection principals, List<Permission> permissions);

    /**
     * 如果用户具备给定的所有permissions，则返回true，否则返回false
     * @param principals
     * @param permissions
     * @return
     */
    boolean isPermittedAll(PrincipalCollection principals, String... permissions);

    /**
     * 如果用户具备给定的所有permissions，则返回true，否则返回false
     * @param principals
     * @param permission
     * @return
     */
    boolean isPermittedAll(PrincipalCollection principals, List<Permission> permission);

    /**
     * 确保相应的主体/用户具有指定的字符串格式权限
     * 如果主体、用户所拥有的权限不包含指定的权限，则抛出AuthorizationException
     * @param subjectPrincipal
     * @param permission
     */
    void checkPermission(PrincipalCollection subjectPrincipal, String permission) throws AuthorizationException;

    /**
     * 确保相应的主体/用户具有指定的权限
     * 如果主体、用户所拥有的权限不包含指定的权限，则抛出AuthorizationException
     * @param subjectPrincipal
     * @param permission
     */
    void checkPermission(PrincipalCollection subjectPrincipal, Permission permission) throws AuthorizationException;

    /**
     * 确保用户具备指定的所有权限
     * 如果用户不具备所有的权限，则抛出AuthorizationException
     * @param principals
     * @param permissions
     */
    void checkPermissions(PrincipalCollection principals, String... permissions) throws AuthorizationException;

    /**
     * 确保用户具备指定的所有权限
     * 如果用户不具备所有的权限，则抛出AuthorizationException
     * @param principals
     * @param permissions
     */
    void checkPermissions(PrincipalCollection principals, Collection<Permission> permissions) throws AuthorizationException;

    /**
     * 如果用户存在指定的角色，返回true，否则，返回false
     * @param subjectPrincipal
     * @param roleIdentifier
     * @return
     */
    boolean hasRole(PrincipalCollection subjectPrincipal, String roleIdentifier);

    /**
     * 检测用户是否具有指定的角色，并返回表示用户是否具有指定角色的boolean数组
     * @param subjectPrincipal
     * @param roleIdentifiers
     * @return
     */
    boolean[] hasRoles(PrincipalCollection subjectPrincipal, List<String> roleIdentifiers);

    /**
     * 检测用户是否具有指定的所有角色，如果都具有则返回true，否则返回false
     * @param subjectPrincipal
     * @param roleIdentifiers
     * @return
     */
    boolean hasAllRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers);

    /**
     * 确保用户具有指定的角色，如果不具有此角色，则抛出AuthorizationException
     * @param subjectPrincipal
     * @param roleIdentifier
     */
    void checkRole(PrincipalCollection subjectPrincipal, String roleIdentifier) throws AuthorizationException;

    /**
     * 确保用户具有指定的所有角色，如果不具有所有角色，抛出AuthorizationException
     * @param subjectPrincipal
     * @param roleIdentifiers
     * @throws AuthorizationException
     */
    void checkRoles(PrincipalCollection subjectPrincipal, Collection<String> roleIdentifiers) throws AuthorizationException;

    /**
     * 确保用户具有指定的所有角色，如果不具有所有角色，抛出AuthorizationException
     * @param subjectPrincipal
     * @param roleIdentifiers
     */
    void checkRoles(PrincipalCollection subjectPrincipal, String... roleIdentifiers) throws AuthorizationException;
}
