package com.raffaeleconforti.datastructures.cache;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 10/09/2016.
 */
public interface Cache<K, V> extends Map<K, V> {

    V put(K key, V value);
    V get(Object key);
    void free();

}
