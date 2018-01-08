package com.youxiang.shiro.config;

import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.ConstantLookup;
import org.apache.commons.configuration2.interpol.EnvironmentLookup;
import org.apache.commons.configuration2.interpol.SystemPropertiesLookup;

/**
 * Author: RiversLau
 * Date: 2018/1/8 9:50
 */
public class CommonsInterpolator implements Interpolator {

    final private ConfigurationInterpolator interpolator;
    public CommonsInterpolator() {
        this.interpolator = new ConfigurationInterpolator();
        interpolator.registerLookup("const", new ConstantLookup());
        interpolator.addDefaultLookup(new SystemPropertiesLookup());
        interpolator.addDefaultLookup(new EnvironmentLookup());
    }

    public String interpolate(String value) {
        return (String) interpolator.interpolate(value);
    }

    public ConfigurationInterpolator getConfigurationInterpolator() {
        return this.interpolator;
    }
}
