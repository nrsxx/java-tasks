import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.*;

public class SoftCacheMap<V> implements Cache<Integer, V> {

    private HashMap<Integer, SoftReference<V>> cache;
    private HashMap<SoftReference<V>, HashSet<Integer>> subsidiaryMap;
    private ReferenceQueue<V> referenceQueue;
    private Deque<V> recentlyUsed;
    private int maxSize;

    private SoftCacheMap(int size) {
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
                    HashSet<Integer> localSet = subsidiaryMap.remove(reference);
                    for (Integer local : localSet) {
                        cache.remove(local);
                    }
                }
            } else {
                done = true;
            }
        }
    }

    @Override
    public V getIfPresent(Integer key) {
        if (cache.containsKey(key)) {
            putInQueue(cache.get(key).get());
            return cache.get(key).get();
        }
        return null;
    }

    @Override
    public void put(Integer key, V value) {
        SoftReference<V> reference = new SoftReference(value, referenceQueue);
        cache.put(key, reference);
        putInQueue(value);
        if (subsidiaryMap.containsKey(reference)) {
            subsidiaryMap.get(reference).add(key);
        } else {
            subsidiaryMap.put(reference, new HashSet<>());
            subsidiaryMap.get(reference).add(key);
        }
        clean();
    }

    @Override
    public V remove(Integer key) {
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

    public static void main(String... args) {
        SoftCacheMap cache = new SoftCacheMap(2);
        for (int i = 0; i < 100; ++i) {
            cache.put(i, i);
        }
        for (int i = 0; i < 100; ++i) {
            if (cache.getIfPresent(i) == null) {
                System.out.println("it doesn't work");
            }
        }
        System.gc();
        for (int i = 99; i >= 0; --i) {
            if (cache.getIfPresent(i) == null) {
                System.out.println("it works");
            }
        }
    }
}