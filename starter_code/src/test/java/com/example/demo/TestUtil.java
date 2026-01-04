package com.example.demo;


import java.lang.reflect.Field;

public class TestUtil {

    public static void injectObject(Object target, String fieldName, Object toInject) {

        boolean isPrivate = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);

            if (!field.canAccess(target)) {
                field.setAccessible(true);
                isPrivate = true;
            }

            field.set(target, toInject);

            if (isPrivate) {
                field.setAccessible(false);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
