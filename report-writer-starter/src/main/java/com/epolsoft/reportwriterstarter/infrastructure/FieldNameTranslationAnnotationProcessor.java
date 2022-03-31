package com.epolsoft.reportwriterstarter.infrastructure;


import com.epolsoft.reportwriterstarter.annotation.FieldNameTranslation;
import com.epolsoft.reportwriterstarter.annotation.TranslatableDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class FieldNameTranslationAnnotationProcessor {

    private static final Class<FieldNameTranslation> fieldNameTranslationClass = FieldNameTranslation.class;

    public Map<String, Method> getTranslations(Class<?> valueType) {
        boolean ignoreNonAnnotated = valueType.getAnnotation(TranslatableDTO.class).ignoreNonAnnotated();
        List<Method> getters = getGetters(valueType);
        List<Pair<Integer, Pair<String, Method>>> pairs = new ArrayList<>();
        for (Method getter : getters) {
            if (translationAnnotationPresent(getter)) {
                addIfNotIgnored(pairs, getter, getter);
            } else {
                Field field = getField(valueType, getter);
                processField(pairs, getter, field, ignoreNonAnnotated);
            }
        }
        return pairs.stream()
                .sorted(Comparator.comparing(Pair::getLeft))
                .map(Pair::getRight)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (o1, o2) -> o1, LinkedHashMap::new));
    }

    private void processField(List<Pair<Integer, Pair<String, Method>>> pairs, Method getter, Field field, boolean ignoreNonAnnotated) {
        if (field != null && translationAnnotationPresent(field)) {
            addIfNotIgnored(pairs, field, getter);
        } else if (!ignoreNonAnnotated) {
            pairs.add(Pair.of(Integer.MAX_VALUE, Pair.of(getPropertyNameByGetter(getter), getter)));
        }
    }

    private void addIfNotIgnored(List<Pair<Integer, Pair<String, Method>>> pairs, AccessibleObject getterOrField, Method getter) {
        if (notIgnored(getterOrField)) {
            String translation = getTranslationValue(getterOrField);
            pairs.add(Pair.of(getTranslationOrder(getterOrField), getTranslationGetterPair(translation, getter)));
        }
    }

    private Pair<String, Method> getTranslationGetterPair(String translation, Method getter) {
        if (translation.isEmpty()) {
            return Pair.of(getPropertyNameByGetter(getter), getter);
        } else {
            return Pair.of(translation, getter);
        }
    }

    private List<Method> getGetters(Class<?> valueType) {
        List<Method> getterList = new ArrayList<>();
        Method[] methods = valueType.getDeclaredMethods();
        for (Method method : methods) {
            if (isGetter(method)) {
                getterList.add(method);
            }
        }
        getterList.sort(Comparator.comparing(Method::getName));
        return getterList;
    }

    private boolean isGetter(Method method) {
        if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 0) {
            if (method.getName().startsWith("get") && !method.getReturnType().equals(void.class)) {
                return true;
            }
            if (method.getName().startsWith("is") && method.getReturnType().equals(boolean.class)) {
                return true;
            }
        }
        return false;
    }

    private Field getField(Class<?> valueType, Method getter) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(valueType);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors) {
                if (getter.equals(pd.getReadMethod())) {
                    return valueType.getDeclaredField(pd.getName());
                }
            }
        } catch (IntrospectionException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPropertyNameByGetter(Method getter) {
        String getterName = getter.getName();
        if (getterName.startsWith("get")) {
            return StringUtils.join(
                    StringUtils.splitByCharacterTypeCamelCase(getterName.substring(3)), " ");
        }
        return StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(getterName.substring(2)), " ");
    }

    private boolean translationAnnotationPresent(AccessibleObject getterOrField) {
        return getterOrField.isAnnotationPresent(fieldNameTranslationClass);
    }

    private boolean notIgnored(AccessibleObject getterOrField) {
        return !getterOrField.getAnnotation(fieldNameTranslationClass).ignore();
    }

    private String getTranslationValue(AccessibleObject getterOrField) {
        return getterOrField.getAnnotation(fieldNameTranslationClass).value();
    }

    private int getTranslationOrder(AccessibleObject getterOrField) {
        return getterOrField.getAnnotation(fieldNameTranslationClass).order();
    }
}
