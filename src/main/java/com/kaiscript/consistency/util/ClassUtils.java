package com.kaiscript.consistency.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 */
public class ClassUtils {
    private static final Map<Class<?>, Class<?>> primitiveMap = new HashMap<>(9);
    private static final ConcurrentMap<String, Class<?>> classNameMap = new ConcurrentHashMap<>();


    static {
        primitiveMap.put(String.class, String.class);
        primitiveMap.put(Boolean.class, boolean.class);
        primitiveMap.put(Byte.class, byte.class);
        primitiveMap.put(Character.class, char.class);
        primitiveMap.put(Double.class, double.class);
        primitiveMap.put(Float.class, float.class);
        primitiveMap.put(Integer.class, int.class);
        primitiveMap.put(Long.class, long.class);
        primitiveMap.put(Short.class, short.class);
        primitiveMap.put(Date.class, Date.class);
    }

    /**
     * @param clazz
     * @return boolean
     * @description 判断基本类型
     * @see String#TYPE
     * @see Boolean#TYPE
     * @see Character#TYPE
     * @see Byte#TYPE
     * @see Short#TYPE
     * @see Integer#TYPE
     * @see Long#TYPE
     * @see Float#TYPE
     * @see Double#TYPE
     * @see Boolean#TYPE
     * @see char#TYPE
     * @see byte#TYPE
     * @see short#TYPE
     * @see int#TYPE
     * @see long#TYPE
     * @see float#TYPE
     * @see double#TYPE
     */
    public static boolean isPrimitive(Class<?> clazz) {
        if (primitiveMap.containsKey(clazz)) {
            return true;
        }
        return clazz.isPrimitive();
    }

    /**
     * @param clazz
     * @return
     */
    public static boolean isCollection(Class<?> clazz) {
        return List.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz) || clazz.isArray();
    }

    /**
     * @param tartget
     * @param fieldName
     * @return Class<?>
     * @description 获取方法返回值类型
     */
    public static Class<?> getElementType(Class<?> tartget, String fieldName) {
        String generateClassKey = generateClassKey(tartget, fieldName);
        Class<?> aClass = classNameMap.get(generateClassKey);
        if (aClass != null) {
            return aClass;
        }
        Class<?> elementTypeClass = null;
        try {
            Type type = tartget.getDeclaredField(fieldName).getGenericType();
            ParameterizedType t = (ParameterizedType) type;
            Type argument = t.getActualTypeArguments()[0];
            elementTypeClass = (Class<?>) argument;
            classNameMap.put(generateClassKey, elementTypeClass);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException("get fieldName[" + fieldName + "] error", e);
        }
        return elementTypeClass;
    }

    /**
     * @param tartget
     * @param fieldName
     * @return String
     * @description 生成key
     */
    public static String generateClassKey(Class<?> tartget, String fieldName) {
        return tartget.getName() + "@" + fieldName;
    }
}
