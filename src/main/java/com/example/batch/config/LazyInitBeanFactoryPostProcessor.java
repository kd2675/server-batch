package com.example.batch.config;//package com.example.crawling.common.config;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//@Configuration
//@Profile("local | test")
//public class LazyInitBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        for (String beanName : beanFactory.getBeanDefinitionNames()) {
//            beanFactory.getBeanDefinition(beanName).setLazyInit(true);
//        }
//    }
//}
