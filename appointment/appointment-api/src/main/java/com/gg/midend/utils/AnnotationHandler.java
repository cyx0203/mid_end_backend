package com.gg.midend.utils;

import cn.hutool.core.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：注解处理器，辅助处理注解相关数据
 */
public class AnnotationHandler {

    /**
     * 描述：反射获取指定方法、指定注解的值 -- value
     * @param mainClassClazz 最外层类的类对象
     * @param methodName 最外层类内部指定的方法
     * @param annotationClazz 指定注解的类对象
     * @param methodParamTypes 最外层类内部指定方法的对应参数
     * @return List<Object> 获取到的值，以 list 形式返回
     */
    public static List<Object> getAnnotationValue(Class<?> mainClassClazz, String methodName,
                                            Class<? extends Annotation> annotationClazz, Class<?>... methodParamTypes) {
        try {
            // 获取当前类的指定方法
            Method currentMethod = ReflectUtil.getMethod(mainClassClazz, methodName, methodParamTypes);
            // 获取上述方法指定注解
            Annotation annotation = currentMethod.getAnnotation(annotationClazz);
            // 上述注解的全部方法
            Method[] annotationMethod = ReflectUtil.getMethods(annotation.getClass());

            boolean valueFlag = false;
            Method valueMethod = null;
            // 跟 value 相关的方法一般都叫 value()，获取这个方法
            for (Method temp : annotationMethod) {
                if ("value".equals(temp.getName())) {
                    valueMethod = temp;
                    valueFlag = true;
                }
            }
            if (!valueFlag)
                return null;
            // 执行 value()
            Object valueObj = valueMethod.invoke(annotation);
            // value() 的返回值一般是 String, String[], 这里用反射机制返回的是包含数据的 Object，要转为 List
            int length = Array.getLength(valueObj);
            List<Object> valueList = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                valueList.add(Array.get(valueObj, i));
            }

            return valueList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 描述：获取 getAnnotationValue 方法返回值的第一个
     * @param annotationValue getAnnotationValue 方法返回值
     * @return String 第一个值
     */
    public static String getFirstValue(List<Object> annotationValue) {
        if (annotationValue != null) {
            return annotationValue.get(0).toString().split("/")[2];
        }
        return "";
    }
}
