/*
 *  Copyright 2019-2025 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jie
 * @date 2019-01-07
 */
@Slf4j
@SuppressWarnings({"unchecked","all"})
public class SpringBeanHolder implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;
    private static final List<SpringBeanHolder.CallBack> CALL_BACKS = new ArrayList<>();
    private static boolean addCallback = true;

    /**
     * For some initialization methods, submit a callback when SpringContextHolder is not yet initialized.
     * Used as a callback after SpringContextHolder is initialized
     *
     * @param callBack Callback function
     */
    public synchronized static void addCallBacks(SpringBeanHolder.CallBack callBack) {
        if (addCallback) {
            SpringBeanHolder.CALL_BACKS.add(callBack);
        } else {
            log.warn("CallBack: {} can no longer be added! Execute now", callBack.getCallBackName());
            callBack.executor();
        }
    }

    /**
     * Get from the static variable applicationContext Bean, Automatically cast to the type of the assigned object.
     */
    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    /**
     * Get from the static variable applicationContext Bean, Automatically cast to the type of the assigned object.
     */
    public static <T> T getBean(Class<T> requiredType) {
        assertContextInjected();
        return applicationContext.getBean(requiredType);
    }

    /**
     * Get SpringBoot configuration
     *
     * @param property     Property key
     * @param defaultValue Default value
     * @param requiredType Return type
     * @return /
     */
    public static <T> T getProperties(String property, T defaultValue, Class<T> requiredType) {
        T result = defaultValue;
        try {
            result = getBean(Environment.class).getProperty(property, requiredType);
        } catch (Exception ignored) {}
        return result;
    }

    /**
     * Get SpringBoot configuration
     *
     * @param property Property key
     * @return /
     */
    public static String getProperties(String property) {
        return getProperties(property, null, String.class);
    }

    /**
     * Get SpringBoot configuration
     *
     * @param property     Property key
     * @param requiredType Return type
     * @return /
     */
    public static <T> T getProperties(String property, Class<T> requiredType) {
        return getProperties(property, null, requiredType);
    }

    /**
     * Check that ApplicationContext is not null.
     */
    private static void assertContextInjected() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext Property not injected, Please in applicationContext" +
                    ".xml Define SpringContextHolder in or register it in the SpringBoot startup class SpringContextHolder.");
        }
    }

    /**
     * Clear the ApplicationContext in SpringContextHolder:Null.
     */
    private static void clearHolder() {
        log.debug("Clear the one in SpringContextHolder ApplicationContext:"
                + applicationContext);
        applicationContext = null;
    }

    @Override
    public void destroy() {
        SpringBeanHolder.clearHolder();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringBeanHolder.applicationContext != null) {
            log.warn("SpringContextHolder the ApplicationContext in it is overwritten, The original ApplicationContext is:" + SpringBeanHolder.applicationContext);
        }
        SpringBeanHolder.applicationContext = applicationContext;
        if (addCallback) {
            for (SpringBeanHolder.CallBack callBack : SpringBeanHolder.CALL_BACKS) {
                callBack.executor();
            }
            CALL_BACKS.clear();
        }
        SpringBeanHolder.addCallback = false;
    }

    /**
     * Get @Service  all bean names
     * @return /
     */
    public static List<String> getAllServiceBeanName() {
        return new ArrayList<>(Arrays.asList(applicationContext
                .getBeanNamesForAnnotation(Service.class)));
    }

    interface CallBack {

        /**
         * Callback execution method
         */
        void executor();

        /**
         * Name of this callback task
         * @return /
         */
        default String getCallBackName() {
            return Thread.currentThread().getId() + ":" + this.getClass().getName();
        }
    }
}
