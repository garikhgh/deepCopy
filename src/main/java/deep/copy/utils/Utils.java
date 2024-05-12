package deep.copy.utils;

import java.lang.reflect.Field;

public class Utils {
    private Utils(){
        throw new ExceptionInInitializerError("Not allowed to create an instance!");
    }

    public static boolean checkIfObjectHasFields(Class<?> aClass) {
        Field[] declaredFields = aClass.getDeclaredFields();
        return declaredFields.length == 0;
    }

}
