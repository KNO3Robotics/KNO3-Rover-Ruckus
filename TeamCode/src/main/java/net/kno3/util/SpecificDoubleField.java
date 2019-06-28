package net.kno3.util;

import java.lang.reflect.Field;

/**
 * Created by jaxon on 12/10/2016.
 */
class SpecificDoubleField {
    private Field field;
    private Object onObject;

    public SpecificDoubleField(Class forClass, String fieldName, Object onObject) {
        try {
            this.field = forClass.getDeclaredField(fieldName);
            this.field.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
        this.onObject = onObject;
    }

    public void setValue(double value) {
        try {
            this.field.setDouble(onObject, value);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public double getValue() {
        try {
            return this.field.getDouble(onObject);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
