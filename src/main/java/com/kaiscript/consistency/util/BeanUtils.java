package com.kaiscript.consistency.util;

import com.google.common.collect.Maps;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 */
public class BeanUtils {
    /**
     * the beanCopierMap
     */
    private static final ConcurrentMap<String, BeanCopier> beanCopierMap = new ConcurrentHashMap<>();

    /**
     * 两个类对象之间转换
     *
     * @param source
     * @return T
     */
    public static <T> T convert(T source) {
        T ret = null;
        if (source != null) {
            Class<T> sourceClass = (Class<T>) source.getClass();
            try {
                ret = sourceClass.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("create class[" + sourceClass.getName()
                        + "] instance error", e);
            }
            BeanCopier beanCopier = getBeanCopier(sourceClass, sourceClass);
            beanCopier.copy(source, ret, new DeepCopyConverter(sourceClass));
        }
        return ret;
    }

    /**
     * 两个类对象之间转换
     *
     * @param source
     * @param target
     * @return T
     */
    public static <T> T convert(Object source, Class<T> target) {
        T ret = null;
        if (source != null) {
            try {
                ret = target.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("create class[" + target.getName()
                        + "] instance error", e);
            }
            BeanCopier beanCopier = getBeanCopier(source.getClass(), target);
            beanCopier.copy(source, ret, new DeepCopyConverter(target));
        }
        return ret;
    }

    public static class DeepCopyConverter implements Converter {

        /**
         * The Target.
         */
        private Class<?> target;

        /**
         * Instantiates a new Deep copy converter.
         *
         * @param target the target
         */
        public DeepCopyConverter(Class<?> target) {
            this.target = target;
        }

        @Override
        public Object convert(Object value, Class targetClazz, Object methodName) {
            if (value == null) {
                return null;
            }

            boolean primitive = ClassUtils.isPrimitive(targetClazz);
            // 基础型数据，直接使用
            if (primitive) {
                return value;
            }
            // 集合数据
            if (ClassUtils.isCollection(targetClazz)) {
                return convertCollection(value);
            }

            return BeanUtils.convert(value, targetClazz);
        }

        private Object convertCollection(Object value) {
            if (value instanceof List) {
                List values = (List) value;
                List retList = new ArrayList<>(values.size());
                values.forEach(source -> {
                    if (source == null) {
                        retList.add(null);
                    } else {
                        if (ClassUtils.isCollection(source.getClass())) {
                            retList.add(convertCollection(source));
                        } else {
                            boolean isPrimitive = ClassUtils.isPrimitive(source.getClass());
                            Object convert = isPrimitive ? source : BeanUtils.convert(source, source.getClass());
                            retList.add(convert);
                        }
                    }

                });
            } else if (value instanceof Map) {
                Map values = (Map) value;
                Map retMap = Maps.newLinkedHashMap();

                values.forEach((k, v) -> {
                    if (v == null) {
                        retMap.put(k, null);
                    } else {
                        if (ClassUtils.isCollection(v.getClass())) {
                            retMap.put(k, convertCollection(v));
                        } else {
                            boolean isPrimitive = ClassUtils.isPrimitive(v.getClass());
                            Object convert = isPrimitive ? v : BeanUtils.convert(v, v.getClass());
                            retMap.put(k, convert);
                        }
                    }
                });
                return retMap;
            } else if (value.getClass().isArray()) {
                Class<?> clazz = value.getClass().getComponentType();
                // 不考虑集合的数组了，没这么用的
                boolean isPrimitive = ClassUtils.isPrimitive(clazz);
                return Arrays.stream((Object[]) value).map(v -> isPrimitive ? v : BeanUtils.convert(v, clazz)).toArray();
            }

            return value;
        }
    }

    /**
     * @param source
     * @param target
     * @return BeanCopier
     * @description 获取BeanCopier
     */
    public static BeanCopier getBeanCopier(Class<?> source, Class<?> target) {
        String beanCopierKey = generateBeanKey(source, target);
        if (beanCopierMap.containsKey(beanCopierKey)) {
            return beanCopierMap.get(beanCopierKey);
        } else {
            BeanCopier beanCopier = BeanCopier.create(source, target, true);
            beanCopierMap.putIfAbsent(beanCopierKey, beanCopier);
        }
        return beanCopierMap.get(beanCopierKey);
    }

    /**
     * @param source
     * @param target
     * @return String
     * @description 生成两个类的key
     */
    public static String generateBeanKey(Class<?> source, Class<?> target) {
        return source.getName() + "@" + target.getName();
    }

}
