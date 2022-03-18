package com.epolsoft.reportwriterstarter.infrastructure;


import com.epolsoft.reportwriterstarter.annotation.FieldNameTranslation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class FieldNameTranslationAnnotationProcessor {

    public Map<String, Method> getTranslations(Class<?> valueType) {
        List<Method> getters = getGetters(valueType);
        Class<FieldNameTranslation> fieldNameTranslationClass = FieldNameTranslation.class;
        List<Pair<Integer, Pair<String, Method>>> pairs = new ArrayList<>();
        for (Method getter : getters) {
            if (getter.isAnnotationPresent(fieldNameTranslationClass)) {
                addByGetterIfNotIgnored(pairs, getter);
            } else {
                Field field = getField(valueType, getter);
                if (field == null || !field.isAnnotationPresent(fieldNameTranslationClass)) {
                    pairs.add(Pair.of(Integer.MAX_VALUE, Pair.of(getPropertyNameByGetter(getter), getter)));
                } else {
                    addByFieldIfNotIgnored(pairs, field, getter);
                }
            }
        }
        return pairs.stream()
                .sorted(Comparator.comparing(Pair::getLeft))
                .map(Pair::getRight)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (o1, o2) -> o1, LinkedHashMap::new));
    }

    private void addByFieldIfNotIgnored(List<Pair<Integer, Pair<String, Method>>> pairs, Field field, Method getter) {
        Class<FieldNameTranslation> fieldNameTranslationClass = FieldNameTranslation.class;
        if (!field.getAnnotation(fieldNameTranslationClass).ignore()) {
            String value = field.getAnnotation(fieldNameTranslationClass).value();
            if (value.isEmpty()) {
                pairs.add(Pair.of(Integer.MAX_VALUE, Pair.of(getPropertyNameByGetter(getter), getter)));
            } else {
                pairs.add(Pair.of(field.getAnnotation(fieldNameTranslationClass).order(), Pair.of(value, getter)));
            }
        }
    }

    private void addByGetterIfNotIgnored(List<Pair<Integer, Pair<String, Method>>> pairs, Method getter) {
        Class<FieldNameTranslation> fieldNameTranslationClass = FieldNameTranslation.class;
        if (!getter.getAnnotation(fieldNameTranslationClass).ignore()) {
            String value = getter.getAnnotation(fieldNameTranslationClass).value();
            if (value.isEmpty()) {
                pairs.add(Pair.of(Integer.MAX_VALUE, Pair.of(getPropertyNameByGetter(getter), getter)));
            } else {
                pairs.add(Pair.of(getter.getAnnotation(fieldNameTranslationClass).order(), Pair.of(value, getter)));
            }
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
}
