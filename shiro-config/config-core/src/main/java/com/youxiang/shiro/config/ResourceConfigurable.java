package com.youxiang.shiro.config;

/**
 * Author: RiversLau
 * Date: 2018/1/10 11:48
 */
public interface ResourceConfigurable {

    void setConfigurations(String locations);

    void setConfigurations(String[] locations);
}
