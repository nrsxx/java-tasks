package maven;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class SoftCacheMap<K, V> implements Cache<K, V> {

    private HashMap<K, SoftReference<V>> cache;
    private HashMap<SoftReference<V>, HashSet<K>> subsidiaryMap;
    private ReferenceQueue<V> referenceQueue;
    private Deque<V> recentlyUsed;
    private int maxSize;
    private int cleaningFrequency;
    private int numOfPuts;

    protected SoftCacheMap(int size, int frequency) {
        cache = new HashMap<>();
        /**
         * второй hashMap - вспомогательный, используется при удалении из кэша
         * тех ссылок, которые попали в referenceQueue. Чтобы не искать все ключи,
         * соответствующие одной ссылке, храню Set из таких ключей в качестве значения
         * во втором hashMap-e.
         */
        subsidiaryMap = new HashMap<>();
        referenceQueue = new ReferenceQueue<>();
        recentlyUsed = new LinkedList<>();
        maxSize = size;
        cleaningFrequency = frequency;
        numOfPuts = 0;
    }

    private void putInQueue(V object) {
        if (maxSize > 0) {
            if (object != null) {
                recentlyUsed.remove(object);
                if (recentlyUsed.size() < maxSize) {
                    recentlyUsed.addLast(object);
                } else {
                    recentlyUsed.addLast(object);
                    recentlyUsed.removeFirst();
                }
            }
        }
    }


    private void clean() {
        boolean done = false;
        while (!done) {
            SoftReference reference = (SoftReference) referenceQueue.poll();
            if (reference != null) {
                if (subsidiaryMap.containsKey(reference)) {
                    HashSet<K> localSet = subsidiaryMap.remove(reference);
                    for (K local : localSet) {
                        if (reference.get() == null) {
                            cache.remove(local);
                        }
                    }
                }
            } else {
                done = true;
            }
        }
    }

    @Override
    public V getIfPresent(K key) {
        if (cache.containsKey(key)) {
            putInQueue(cache.get(key).get());
            return cache.get(key).get();
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        ++numOfPuts;
        SoftReference<V> reference = new SoftReference<>(value, referenceQueue);
        cache.put(key, reference);
        putInQueue(value);
        if (subsidiaryMap.containsKey(reference)) {
            subsidiaryMap.get(reference).add(key);
        } else {
            subsidiaryMap.put(reference, new HashSet<>());
            subsidiaryMap.get(reference).add(key);
        }
        if (numOfPuts % cleaningFrequency == 0) {
            clean();
        }
    }

    @Override
    public V remove(K key) {
        if ((cache.containsKey(key)) && (cache.get(key).get() != null)) {
            subsidiaryMap.remove(cache.get(key));
            return cache.remove(key).get();
        }
        return null;
    }

    @Override
    public void clear() {
        cache.clear();
        subsidiaryMap.clear();
    }

}
