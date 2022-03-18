package com.epolsoft.reportwriterstarter.infrastructure;

import com.epolsoft.reportwriterstarter.annotation.TranslatableDTO;
import com.epolsoft.reportwriterstarter.writer.TranslatableDTORegistry;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class TranslatableDTORegistrarBeanPostProcessor implements BeanPostProcessor {

    private final TranslatableDTORegistry registry;

    public TranslatableDTORegistrarBeanPostProcessor(TranslatableDTORegistry registry) {
        this.registry = registry;
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        FieldNameTranslationAnnotationProcessor processor = new FieldNameTranslationAnnotationProcessor();
        if (bean.getClass().isAnnotationPresent(SpringBootApplication.class)) {
            Package basePackage = bean.getClass().getPackage();
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(TranslatableDTO.class));
            Set<BeanDefinition> definitions = scanner.findCandidateComponents(basePackage.getName());
            for (BeanDefinition definition : definitions) {
                Class<?> dtoType = Class.forName(definition.getBeanClassName());
                Map<String, Method> translatedNamesGettersMap = processor.getTranslations(dtoType);
                registry.registerTranslations(dtoType, translatedNamesGettersMap);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
