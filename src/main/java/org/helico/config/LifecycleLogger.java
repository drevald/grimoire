package org.helico.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class LifecycleLogger implements BeanPostProcessor {

    Logger LOG = LoggerFactory.getLogger(LifecycleLogger.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        LOG.info("BEFORE " + bean.getClass().getName() + " name " + beanName);
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        LOG.info("AFTER " + bean.getClass().getSimpleName() + " name " + beanName);
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

}
