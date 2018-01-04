package com.youxiang.shiro.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * session：在某段时间内，用户用于与系统进行交互的有状态的数据上下文
 * shiro session的有点不与其他的web容器相关，可以非常方便的进行扩展
 * Author: RiversLau
 * Date: 2018/1/4 12:04
 */
public interface Session {

    /**
     * 获取可序列化的session唯一ID
     * @return
     */
    Serializable getId();

    /**
     * 获取session开始的时间，也就是系统创建session实例的时间
     * @return
     */
    Date getStartTimestamp();

    /**
     * 获取用户使用该session进行请求或方法调用的最新一次的时间
     * @return
     */
    Date getLastAccessTime();

    /**
     * 获取session过期时间（毫秒）
     * @return
     * @throws InvalidSessionException
     */
    long getTimeout() throws InvalidSessionException;

    /**
     * 设置session有效时间
     * @param maxIdleTimeInMillis
     * @throws InvalidSessionException
     */
    void setTimeout(long maxIdleTimeInMillis) throws InvalidSessionException;

    /**
     * 获取session过来的host名称或者ip地址
     * @return
     */
    String getHost();

    void touch() throws InvalidSessionException;

    /**
     * 显示的的作废session，并释放与之相关的资源
     * @throws InvalidSessionException
     */
    void stop() throws InvalidSessionException;

    /**
     * 返回当前session下存储的所有的属性的key
     * @return
     * @throws InvalidSessionException
     */
    Collection<Object> getAttributeKeys() throws InvalidSessionException;

    /**
     * 获取session中存储的对应key下的属性value
     * @param key
     * @return
     * @throws InvalidSessionException
     */
    Object getAttribute(Object key) throws InvalidSessionException;

    /**
     * 在session中存储key-value，如果key已经存在，则覆盖原有的value值
     * @param key
     * @param value
     * @throws InvalidSessionException
     */
    void setAttribute(Object key, Object value) throws InvalidSessionException;

    /**
     * 移除session中存储的指定key的属性
     * @param key
     * @return
     * @throws InvalidSessionException
     */
    Object removeAttribute(Object key) throws InvalidSessionException;
}
