package com.youxiang.shiro.realm;

import java.util.Collection;

/**
 * Author: RiversLau
 * Date: 2018/1/11 11:28
 */
public interface RealmFactory {

    Collection<Realm> getRealms();
}
