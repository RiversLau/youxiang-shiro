package com.youxiang.shiro.subject;

import com.youxiang.shiro.SecurityUtils;
import com.youxiang.shiro.authc.AuthenticationException;
import com.youxiang.shiro.authc.AuthenticationToken;
import com.youxiang.shiro.authz.AuthorizationException;
import com.youxiang.shiro.authz.Permission;
import com.youxiang.shiro.session.Session;
import com.youxiang.shiro.subject.support.DefaultSubjectContext;
import com.youxiang.shiro.util.StringUtils;
import com.youxiang.shiro.mgt.SecurityManager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Subject表示应用中单一用户的状态和安全操作。
 * 这些操作包括认证（登录、退出），授权（权限控制）以及session访问。它是Shiro针对单一用户安全操作的主要机制。
 * Author: RiversLau
 * Date: 2018/1/4 15:29
 */
public interface Subject {

    /**
     * 返回当前Subject在整个应用中唯一的身份信息，如果应用中还没有与之相关的账户数据，则返回null
     * "Principal"仅仅是表示应用中用户身份属性的安全单词，比如用户名、用户ID或者其他能够标明用户的
     * 身份的事物。
     * @return
     */
    Object getPrincipal();

    /**
     * 以PrincipalCollection对象形式返回当前Subject的所有用户身份信息，如果当前Subject是匿名的，也就是还没有与之
     * 相关的账户信息，则返回null（比如用户还没有登录）
     * @return
     */
    PrincipalCollection getPrincipals();

    /**
     * 如果当前用户能够执行操作或访问资源（指定的权限字符串）则返回true，否则返回false
     * @param permission
     * @return
     */
    boolean isPermitted(String permission);

    /**
     * 如果当前用户能够执行操作或访问资源，则返回true，否则返回false
     * @param permission
     * @return
     */
    boolean isPermitted(Permission permission);

    /**
     * 检测用户是否拥有指定的多个权限，并且返回标明权限状态的boolean数组
     * @param permissons
     * @return
     */
    boolean[] isPermitted(String... permissons);

    /**
     * 检测用户是否拥有指定的权限，并且返回标明权限状态的boolean数组
     * @param permissions
     * @return
     */
    boolean[] isPermitted(List<Permission> permissions);

    /**
     * 如果用户拥有指定的所有权限，返回true，否则，返回false
     * @param permissions
     * @return
     */
    boolean isPermittedAll(String... permissions);

    /**
     * 如果用户拥有指定的所有权限，返回true，否则，返回false
     * @param permissions
     * @return
     */
    boolean isPermittedAll(Collection<Permission> permissions);

    /**
     * 确保用户拥有指定的字符串形式的权限，如果当前用户不具有此权限，则抛出AuthorizationException
     * @param permission
     * @throws AuthorizationException
     */
    void checkPermission(String permission) throws AuthorizationException;

    /**
     * 确保用户拥有指定的权限，如果当前用户不具有此权限，则抛出AuthorizationException
     * @param permission
     * @throws AuthorizationException
     */
    void checkPermission(Permission permission) throws AuthorizationException;

    /**
     * 确保用户拥有指定的多个权限，如果存在当前用户未拥有的权限，则抛出AuthorizationException
     * @param permissions
     * @throws AuthorizationException
     */
    void checkPermissions(String... permissions) throws AuthorizationException;

    /**
     * 确保用户拥有指定的多个权限，如果存在当前用户未拥有的权限，则抛出AuthorizationException
     * @param permissions
     * @throws AuthorizationException
     */
    void checkPermissions(Collection<Permission> permissions) throws AuthorizationException;

    /**
     * 如果当前用户具有指定的角色，返回true，否则返回false
     * @param roleIdentifier
     * @return
     */
    boolean hasRole(String roleIdentifier);

    /**
     * 检测用户是否具有指定的角色，并返回标明对应角色的boolean数组
     * @param roleIdentifiers
     * @return
     */
    boolean[] hasRoles(List<String> roleIdentifiers);

    /**
     * 判断用户是否拥有所有的角色，如果拥有所有的角色，返回true，否则返回false
     * @param roleIdentifiers
     * @return
     */
    boolean hasAllRoles(Collection<String> roleIdentifiers);

    /**
     * 确保用户具有指定的角色，否则抛出AuthorizationException
     * @param roleIdentifier
     * @throws AuthorizationException
     */
    void checkRole(String roleIdentifier) throws AuthorizationException;

    /**
     * 确保用户具有指定的多个角色，否则抛出AuthorizationException
     * @param roleIdentifiers
     * @throws AuthorizationException
     */
    void checkRoles(Collection<String> roleIdentifiers) throws AuthorizationException;

    /**
     * 确保用户具有指定的多个角色，否则抛出AuthorizationException
     * @param roleIdentifiers
     * @throws AuthorizationException
     */
    void checkRoles(String... roleIdentifiers) throws AuthorizationException;

    /**
     * 为当前的用户的执行登录尝试。如果不成功，抛出AuthenticationException，表示认证失败
     * 如果成功，则登录时提交的用户身份、认证信息将会与Subject关联起来。
     * @param token
     * @throws AuthenticationException
     */
    void login(AuthenticationToken token) throws AuthenticationException;

    /**
     * 如果当前用户、主体通过提供有效的认证资料证实了自身的身份，返回true，否则返回false
     * @return
     */
    boolean isAuthenticated();

    /**
     * 如果当前用户拥有非匿名的身份，并且这个身份是在上次成功认证的回话中被记住的，则返回true，否则返回false
     * @return
     */
    boolean isRemembered();

    /**
     * 返回与当前用户相关的Session信息，如果调用时还不存在session，则创建新的session然后与当前用户关联
     * @return
     */
    Session getSession();

    /**
     * 返回与当前用户相关的session信息，根据传递的参数决定session不存在时，是否创建
     * 如果调用时，session已存在，则直接返回session，
     * 如果不存在，且参数为true时，创建新的session，如果为false，则不创建session，直接返回null
     * @param create
     * @return
     */
    Session getSession(boolean create);

    /**
     * 退出当前的用户，并且移除与之相关的实体信息，比如session或者授权数据。在这个方法调用之后，
     * 用户将被认定为是匿名的，如果想要继续使用则需要再次登录
     */
    void logout();

    /**
     * 将当前用户与指定的Callable进行关联，然后会在当前运行的线程中执行它。如果想在不同的线程执行Callable，
     * 最好调用associateWith(Callable)方法
     * @param callable
     * @param <V>
     * @return
     * @throws ExecutionException
     */
    <V> V execute(Callable<V> callable) throws ExecutionException;

    <V> V associateWith(Callable<V> callable);

    /**
     * 将当前用户与指定的Runnable进行关联，然后在当前运行的线程中执行它。如果想在不同的线程执行Runnable执行，
     * 应该调用associateWith(Runnable)方法
     * @param runnable
     */
    void execute(Runnable runnable);

    Runnable associateWith(Runnable runnable);

    /**
     * 允许用户无限期的使用其他身份，该方法只能在该用户已经拥有身份（比如在之前的登录时选择"记住我"或者
     * 在当前的session的已经被认证）
     * @param principals
     * @throws NullPointerException
     * @throws IllegalStateException
     */
    void runAs(PrincipalCollection principals) throws NullPointerException, IllegalStateException;

    /**
     * 如果用户正在用其他的身份访问应用，而不是自己原有的身份，则返回true，否则返回false
     * @return
     */
    boolean isRunAs();

    PrincipalCollection getPreviousPrincipals();

    PrincipalCollection releaseRunAs();

    class Builder {

        private final SubjectContext subjectContext;

        private final SecurityManager securityManager;

        public Builder() {
            this(SecurityUtils.getSecurityManager());
        }

        public Builder(SecurityManager securityManager) {
            if (securityManager == null) {
                throw new NullPointerException("SecurityManager method argument cannot be null.");
            }
            this.securityManager = securityManager;
            this.subjectContext = newSubjectContextInstance();
            if (this.subjectContext == null) {
                throw new IllegalStateException("Subect return from 'newSubjectContextInstance' " +
                        "cannot be null.");
            }
            this.subjectContext.setSecurityManager(securityManager);
        }

        protected SubjectContext newSubjectContextInstance() {
            return new DefaultSubjectContext();
        }

        protected SubjectContext getSubjectContext() {
            return this.subjectContext;
        }

        public Builder sessionId(Serializable sessionId) {
            if (sessionId != null) {
                this.subjectContext.setSessionId(sessionId);
            }
            return this;
        }

        public Builder host(String host) {
            if (StringUtils.hasText(host)) {
                this.subjectContext.setHost(host);
            }
            return this;
        }

        public Builder session(Session session) {
            if (session != null) {
                this.subjectContext.setSession(session);
            }
            return this;
        }

        public Builder principals(PrincipalCollection principals) {
            if (principals != null && !principals.isEmpty()) {
                this.subjectContext.setPrincipals(principals);
            }
            return this;
        }

        public Builder sessionCreationEnabled(boolean enabled) {
            this.subjectContext.setSessionCreationEnabled(enabled);
            return this;
        }

        public Builder authenticated(boolean authenticated) {
            this.subjectContext.setAuthenticated(authenticated);
            return this;
        }

        public Builder contextAttribute(String attributeKey, Object attributeValue) {
            if (attributeKey == null) {
                String msg = "";
                throw new IllegalArgumentException(msg);
            }
            if (attributeValue == null) {
                this.subjectContext.remove(attributeKey);
            } else {
                this.subjectContext.put(attributeKey, attributeValue);
            }
            return this;
        }

        public Subject buildSubject() {
            return this.securityManager.createSubject(this.subjectContext);
        }
    }
}
