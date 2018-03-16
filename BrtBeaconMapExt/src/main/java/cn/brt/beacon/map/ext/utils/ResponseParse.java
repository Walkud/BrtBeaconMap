package cn.brt.beacon.map.ext.utils;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by Administrator on 2016/8/23 0023.
 */
public class ResponseParse {

    public static <T> T parse(Cursor cursor, Class<T> resultClass)
            throws IllegalAccessException, InstantiationException {
        T t = resultClass.newInstance();

        Field[] fields = resultClass.getDeclaredFields();
        String[] columns = cursor.getColumnNames();

        for (String column : columns) {
            for (Field field : fields) {
                String fieldName = field.getName();
                if (fieldName.equalsIgnoreCase(column)) {
                    setValue(cursor, cursor.getColumnIndex(column), t, field);
                    break;
                }
            }
        }
        return t;
    }

    public static <E> ContentValues converContentValue(E entry, List<String> excludeField) {
        ContentValues result = new ContentValues();

        Field[] fields = entry.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            if (excludeField != null && !excludeField.isEmpty() && excludeField.contains(field.getName())) {
                continue;
            }
            setValue(result, field, entry);
        }
        return result;
    }

    private static <E> void setValue(ContentValues result, Field field, E entry) {
        String fieldName = field.getName();
        try {
            Class type = field.getType();
            field.setAccessible(true);
            if (type == String.class) {
                result.put(fieldName, field.get(entry) != null ? String.valueOf(field.get(entry)) : "");
            } else if (type == int.class) {
                result.put(fieldName, field.getInt(entry));
            } else if (type == double.class) {
                result.put(fieldName, field.getDouble(entry));
            } else if (type == float.class) {
                result.put(fieldName, field.getFloat(entry));
            } else if (type == long.class) {
                result.put(fieldName, field.getLong(entry));
            } else if (type == short.class) {
                result.put(fieldName, field.getShort(entry));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void setValue(Cursor cursor, int index, Object instance, Field field) {
        try {
            Class type = field.getType();
            field.setAccessible(true);
            if (type == String.class) {
                field.set(instance, cursor.getString(index));
            } else if (type == int.class) {
                field.set(instance, cursor.getInt(index));
            } else if (type == double.class) {
                field.set(instance, cursor.getDouble(index));
            } else if (type == float.class) {
                field.set(instance, cursor.getFloat(index));
            } else if (type == long.class) {
                field.set(instance, cursor.getLong(index));
            } else if (type == short.class) {
                field.set(instance, cursor.getShort(index));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
