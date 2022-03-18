package com.epolsoft.reportwriterstarter.writer;

import com.epolsoft.reportwriterstarter.infrastructure.FieldNameTranslationAnnotationProcessor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TranslatableDTORegistry {
    private final Map<Class<?>, Map<String, Method>> dtoMap = new HashMap<>();

    public void registerTranslations(Class<?> dtoType, Map<String, Method> translations) {
        dtoMap.putIfAbsent(dtoType, translations);
    }

    public void registerTranslations(Class<?> dtoType) {
        FieldNameTranslationAnnotationProcessor processor = new FieldNameTranslationAnnotationProcessor();
        Map<String, Method> translatedNamesGettersMap = processor.getTranslations(dtoType);
        this.registerTranslations(dtoType, translatedNamesGettersMap);
    }

    public Map<String, Method> getTranslationsForDto(Class<?> dtoType) {
        return dtoMap.get(dtoType);
    }
}
