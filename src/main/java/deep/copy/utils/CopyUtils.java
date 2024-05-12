package deep.copy.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;


public class CopyUtils {
    private CopyUtils() {
        throw new ExceptionInInitializerError("Not allowed to create an instance!");
    }

    //todo: handle circular reference
    private static final Logger logger = LoggerFactory.getLogger(CopyUtils.class);


    public static <C> C deepCopy(C obj) {

        try {

            Class<?> aClass = Class.forName(obj.getClass().getName());

            if (Utils.checkIfObjectHasFields(aClass)) {
                logger.warn("Object does not have fields. No need to make deepCopy!");
                return obj;
            }
            Map<String, Object> nameValueMap = getClassFields(aClass, obj, "");
            Map<String, Object> clonedObjects = cloneObject(nameValueMap);

            Pair<? extends Constructor<?>> aConstructor = getConstructor(aClass);
            if (aConstructor.isHasDefaultConstructor()) {
                Constructor<?> constructor = aConstructor.getConstructor();
                @SuppressWarnings("unchecked")
                C newObject = (C) constructor.newInstance();
                setClonedFieldsIntoNewClassInstance(clonedObjects, newObject);

                return newObject;
            } else {
                // this should create an object with parameters.
                int counter = 0;
                Field[] declaredFields = aClass.getDeclaredFields();

                Constructor<?> constructor = aConstructor.getConstructor();
                Parameter[] parameters = constructor.getParameters();
                Object[] args = new Object[parameters.length];

                for (Field declaredField : declaredFields) {
                    declaredField.setAccessible(true);
                    Class<?> type = declaredField.getType();
                    if (constructorParametersContainArgs(type, parameters)) {
                        args[counter] = declaredField.get(obj);
                        counter++;
                    }
                }
                @SuppressWarnings("unchecked")
                C newObject = (C) constructor.newInstance(args);
                setClonedFieldsIntoNewClassInstance(clonedObjects, newObject);
                return newObject;
            }

        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | ClassNotFoundException |
                 NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean constructorParametersContainArgs(Class<?> fieldType, Parameter[] parameters) {
        for (Parameter parameter : parameters) {
            if (parameter.getType().equals(fieldType))
                return true;
        }
        return false;
    }

    private static <C> void setClonedFieldsIntoNewClassInstance(Map<String, Object> clonedObjects, C newObject) {
        try {
            Class<?> aClass = Class.forName(newObject.getClass().getName());
            for (Field declaredField : aClass.getDeclaredFields()) {
                declaredField.setAccessible(true);
                String fieldName = declaredField.getName();
                Object o = clonedObjects.get(fieldName);
                declaredField.set(newObject, o);
            }
        } catch (ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <C> Map<String, Object> getClassFields(Class<?> aClass, C obj, String prefix) throws NoSuchFieldException, IllegalAccessException {

        Map<String, Object> newClassMapFields = new HashMap<>();
        for (Field declaredField : aClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
            String fieldName = declaredField.getName();
            Object valueObject = declaredField.get(obj);

            if (valueObject != null) {
                Class<?>[] interfaces = valueObject.getClass().getInterfaces();
                boolean contains = Arrays.asList(valueObject.getClass().getInterfaces()).contains(Serializable.class);
                if (!contains && !Utils.isPrimitive(valueObject)) {
                    Class<?> aNestedClass = valueObject.getClass();
                    Map<String, Object> nestedClassMapFields = getClassFields(aNestedClass, valueObject, "." + fieldName);
                    newClassMapFields.putAll(nestedClassMapFields);
                } else
                    newClassMapFields.put(fieldName + prefix, valueObject);
            }
            else {
                newClassMapFields.put(fieldName + prefix, null);
            }
        }
        return newClassMapFields;
    }


    public static Map<String, Object> cloneObject(Map<String, Object> map) {
        byte[] serialize = Utils.serialize(map);
        @SuppressWarnings("unchecked")
        Map<String, Object> deserialize = (Map<String, Object>) Utils.deserialize(serialize);
        return deserialize;
    }

    private static <T> Pair<Constructor<T>> getConstructor(Class<T> tClass) {
        Constructor<?>[] constructors = tClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                @SuppressWarnings("unchecked")
                Constructor<T> typedConstructor = (Constructor<T>) constructor;
                return createConstractorPair(typedConstructor, true);
            } else {
                try {
                    Constructor<T> constructorWithParameters = tClass.getConstructor(constructor.getParameterTypes());
                    return createConstractorPair(constructorWithParameters, false);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return new Pair<>();
    }


    private static <T> Pair<Constructor<T>> createConstractorPair(Constructor<T> constructor, boolean isPresent) {
        Pair<Constructor<T>> constructorPair = new Pair<>();
        constructorPair.setConstructor(constructor);
        constructorPair.setHasDefaultConstructor(isPresent);
        return constructorPair;
    }

}
