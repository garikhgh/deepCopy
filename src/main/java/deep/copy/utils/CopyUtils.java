package deep.copy.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;


public class CopyUtils {
    private CopyUtils(){
        throw new ExceptionInInitializerError("Not allowed to create an instance!");
    }
    private static final Logger logger = LoggerFactory.getLogger(CopyUtils.class);


    public static <C> C deepCopy(C obj) {

        try {

            Class<?> aClass = Class.forName(obj.getClass().getName());
            if (!checkIfObjectHasFields(aClass)) {
                logger.warn("Object does not have fields. No need to make deepCopy!");
                return obj;
            }

            Pair<? extends Constructor<?>> aConstructor = getConstructor(aClass);
            if (aConstructor.isHasDefaultConstructor()) {
                Constructor<?> constructor = aConstructor.getConstructor();
                @SuppressWarnings("unchecked")
                C newObject = (C) constructor.newInstance();
                setFieldValues(aClass, obj, newObject);
                return newObject;
            } else {
                // this should create an object with parameters.
                int counter = 0;
                Field[] declaredFields = aClass.getDeclaredFields();
                Object[] args = new Object[declaredFields.length];
                Constructor<?> constructor = aConstructor.getConstructor();
                for (Field declaredField : declaredFields) {
                    declaredField.setAccessible(true);
                    args[counter] = declaredField.get(obj);
                    counter++;
                }

                Object[] newArgs = checkIfNotPrimitiveType(args);
                @SuppressWarnings("unchecked")
                C newObject = (C) constructor.newInstance(newArgs);
                return newObject;
            }

        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | ClassNotFoundException |
                 NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    private static Object[] checkIfNotPrimitiveType(Object[] args ) {
        // todo: in not primitive type then create new object
        return args;
    }
    private static boolean checkIfObjectHasFields(Class<?> aClass) {
        Field[] declaredFields = aClass.getDeclaredFields();
        return declaredFields.length != 0;
    }
    private static <C> void setFieldValues(Class<?> aClass, C obj, C newObject) throws NoSuchFieldException, IllegalAccessException {

        Class<?> aNewClass = newObject.getClass();

        for (Field declaredField : aClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
            String fieldName = declaredField.getName();


            Field declaredFieldOfNewClassInstance = aNewClass.getDeclaredField(fieldName);
            declaredFieldOfNewClassInstance.setAccessible(true);

            Object valueObject =  declaredField.get(obj);

            if (type == String.class) {
                String s = (String) valueObject;
                declaredFieldOfNewClassInstance.set(newObject, s);

            } else if (type == int.class) {
                int i = (int) valueObject;
                declaredFieldOfNewClassInstance.set(newObject, i);

            } else if (type == long.class) {
                long i = (long) valueObject;
                declaredFieldOfNewClassInstance.set(newObject, i);

            } else if (type == float.class) {
                float i = (float) valueObject;
                declaredFieldOfNewClassInstance.set(newObject, i);

            } else if (type == double.class) {
                double i = (double) valueObject;
                declaredFieldOfNewClassInstance.set(newObject, i);

            } else if (type == char.class) {
                char i = (char) valueObject;
                declaredFieldOfNewClassInstance.set(newObject, i);

            } else if (type == byte.class) {
                byte i = (byte) valueObject;
                declaredFieldOfNewClassInstance.set(newObject, i);

            } else if (type == short.class) {
                short i = (short) valueObject;
                declaredFieldOfNewClassInstance.set(newObject, i);

            } else if (type == boolean.class) {
                boolean i = (boolean) valueObject;
                declaredFieldOfNewClassInstance.set(newObject, i);

            } else if (type == List.class) {
                Iterator<Object> iterator = Collections.singletonList(valueObject).iterator();

                ArrayList<?> objects = new ArrayList<>((List<?>) valueObject);
                for (Object o: objects) {
                    if (isInstanceOfPrimitiveOrString(o)){
                        declaredFieldOfNewClassInstance.set(newObject, objects);
                        break;
                    } else {
                        // check if the elements are objects
                    }
                }


            } else if (type == Map.class) {
                Map<?, ?> map = (Map<?, ?>) valueObject;
                for (Map.Entry<?,?> entry : map.entrySet()) {
                    if (isInstanceOfPrimitiveOrString(entry.getKey()) && isInstanceOfPrimitiveOrString(entry.getValue())) {
                        declaredFieldOfNewClassInstance.set(newObject, valueObject);
                        break;
                    } else {
                        //check if the key or the value is object
                    }
                }
            }
        }

    }
    private static <T> Pair<Constructor<T>>  getConstructor(Class<T> tClass){

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

    private static boolean isInstanceOfPrimitiveOrString(Object obj) {
        return obj instanceof Integer || obj instanceof Double || obj instanceof Character ||
                obj instanceof Boolean || obj instanceof Byte || obj instanceof Short ||
                obj instanceof Long || obj instanceof Float || obj instanceof String;
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
