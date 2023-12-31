package com.dtflys.forest.utils;

import com.dtflys.forest.beans.ClientFactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-24 20:00
 */
public class ClientFactoryBeanUtils {

    private final static Class CLIENT_FACTORY_BEAN_CLASS = ClientFactoryBean.class;

    public static void setupClientFactoryBean(AbstractBeanDefinition beanDefinition, String configurationId, String clientClassName) {
        beanDefinition.setBeanClass(CLIENT_FACTORY_BEAN_CLASS);
        if (configurationId != null && configurationId.length() > 0) {
            beanDefinition.getPropertyValues().add("forestConfiguration", new RuntimeBeanReference(configurationId));
        }
        beanDefinition.getPropertyValues().add("interfaceClass", clientClassName);
        beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
    }

    public static String getBeanId(String id, Class beanClass, ParserContext parserContext) {
        return getBeanId(id, beanClass, parserContext.getRegistry());
    }


    public static String getBeanId(String id, Class beanClass, BeanDefinitionRegistry registry) {
        if (id == null || id.length() == 0) {
            String generatedBeanName = beanClass.getName();
            id = generatedBeanName;
            int counter = 2;
            while(registry.containsBeanDefinition(id)) {
                id = generatedBeanName + (counter ++);
            }
        }
        return id;
    }
}
