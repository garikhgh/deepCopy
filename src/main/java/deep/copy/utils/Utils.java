package deep.copy.utils;

import java.io.*;
import java.lang.reflect.Field;

public class Utils {
    private Utils(){
        throw new ExceptionInInitializerError("Not allowed to create an instance!");
    }

    public static boolean checkIfObjectHasFields(Class<?> aClass) {
        Field[] declaredFields = aClass.getDeclaredFields();
        return declaredFields.length == 0;
    }

    public static Object deserialize(byte[] obj) {
        try {
            return new ObjectInputStream(new ByteArrayInputStream(obj)).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] serialize(final Object obj) {
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

    public static boolean isPrimitive(Object obj) {
        return obj instanceof Integer || obj instanceof Double || obj instanceof Character ||
                obj instanceof Boolean || obj instanceof Byte || obj instanceof Short ||
                obj instanceof Long || obj instanceof Float;
    }

}
