package org.openfast.util;

import java.util.HashMap;
import java.util.Map;

import org.openfast.FieldValue;

public class UnboundedCache implements Cache {
    private int nextIndex = 1;
    private final Map<Integer, FieldValue> indexToValueMap = new HashMap<Integer, FieldValue>();
    private final Map<FieldValue, Integer> valueToIndexMap = new HashMap<FieldValue, Integer>();

    @Override
    public int getIndex(FieldValue value) {
        return valueToIndexMap.get(value).intValue();
    }

    @Override
    public int store(FieldValue value) {
        Integer next = new Integer(nextIndex);
        indexToValueMap.put(next, value);
        valueToIndexMap.put(value, next);
        nextIndex++;
        return next.intValue();
    }

    @Override
    public void store(int index, FieldValue value) {
        Integer indexVal = new Integer(index);
        indexToValueMap.put(indexVal, value);
        valueToIndexMap.put(value, indexVal);
    }

    @Override
    public boolean containsValue(FieldValue value) {
        return valueToIndexMap.containsKey(value);
    }

    @Override
    public FieldValue lookup(int index) {
        return indexToValueMap.get(new Integer(index));
    }
}
