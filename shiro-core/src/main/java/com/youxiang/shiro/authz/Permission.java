package com.youxiang.shiro.authz;

/**
 * Author: RiversLau
 * Date: 2018/1/4 9:45
 */
public interface Permission {

    /**
     * 如果当前的实例包含所有的功能或者描述的资源，返回true，否则返回false
     * @param p
     * @return
     */
    boolean implies(Permission p);
}
