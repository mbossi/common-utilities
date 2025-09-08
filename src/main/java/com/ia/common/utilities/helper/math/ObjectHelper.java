package com.ia.common.utilities.helper.math;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Objects;

@UtilityClass
public class ObjectHelper {

    public boolean isNumber(Object obj) {
        return obj != null && obj.getClass().isAssignableFrom(Number.class);
    }

    public Object getObject(Object obj) {
        return isNumber(obj) ? Math.round(new BigDecimal(obj.toString()).doubleValue()) : obj;
    }

    public boolean isEqual(Object obj1, Object obj2) {
        final Object o1 = getObject(obj1);
        final Object o2 = getObject(obj2);
        return Objects.equals(o1, o2);
    }
}
