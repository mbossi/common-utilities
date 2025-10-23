package com.ia.common.utilities.helper.math;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    public <K,V> Set<K> getMaxKeys(Map<K,V> map1, Map<K,V> map2) {
        return getMaxKeys(map1, map2, List.of());
    }

    public <K,V> Set<K> getMaxKeys(Map<K,V> map1, Map<K,V> map2, List<K> excludedKeys) {
        final var keys = map1.size() >= map2.size() ? map1.keySet() : map2.keySet();
        return getFilteredKeys(keys, excludedKeys);
    }

    private  <K,V> Set<K> getFilteredKeys(Set<K> keys, List<K> excludedKeys) {
        return keys.stream()
                .filter(k -> !excludedKeys.contains(k))
                .collect(java.util.stream.Collectors.toSet());
    }
}
