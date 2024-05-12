package deep.copy.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;


public class CopyUtils {
    private CopyUtils() {
        throw new ExceptionInInitializerError("Not allowed to create an instance!");
    }

    private static final Logger logger = LoggerFactory.getLogger(CopyUtils.class);


    public static <C> C deepCopy(C obj) {

        try {

            Class<?> aClass = Class.forName(obj.getClass().getName());


            if (Utils.checkIfObjectHasFields(aClass)) {
                logger.warn("Object does not have fields. No need to make deepCopy!");
                return obj;
            }
            Map<String, Object> nameValueMap = getClassFields(aClass, obj);
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

    private static Object[] checkIfNotPrimitiveType(Object[] args) {
        // todo: in not primitive type then create new object
        return args;
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

    private static <C> Map<String, Object> getClassFields(Class<?> aClass, C obj) throws NoSuchFieldException, IllegalAccessException {

        Map<String, Object> newClassMapFields = new HashMap<>();
        for (Field declaredField : aClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
            String fieldName = declaredField.getName();
            Object valueObject = declaredField.get(obj);
            newClassMapFields.put(fieldName, valueObject);
        }
        return newClassMapFields;
    }

    private static byte[] serialize(final Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Object deserialize(byte[] obj) {
        try {
            return new ObjectInputStream(new ByteArrayInputStream(obj)).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> cloneObject(Map<String, Object> map) {
        byte[] serialize = serialize(map);
        @SuppressWarnings("unchecked")
        Map<String, Object> deserialize = (Map<String, Object>) deserialize(serialize);
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

    private static class Pair<T> {
        public T constructor;
        public boolean hasDefaultConstructor;

        public T getConstructor() {
            return constructor;
        }

        public void setConstructor(T constructor) {
            this.constructor = constructor;
        }

        public boolean isHasDefaultConstructor() {
            return hasDefaultConstructor;
        }

        public void setHasDefaultConstructor(boolean hasDefaultConstructor) {
            this.hasDefaultConstructor = hasDefaultConstructor;
        }
    }


}
