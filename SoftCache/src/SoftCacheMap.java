import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.*;

public class SoftCacheMap implements Cache<Integer, Object> {

    private HashMap<Integer, SoftReference<Object>> cache;
    private HashMap<SoftReference<Object>, HashSet<Integer>> subsidiaryMap;
    private ReferenceQueue<SoftReference<Object>> referenceQueue;
    private Deque<Object> recentlyUsed;
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

    private void putInQueue(Object object) {
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


    private void clean() {
        boolean done = false;
        while (!done) {
            SoftReference reference = (SoftReference) referenceQueue.poll();
            if (reference != null) {
                if (cache.entrySet().contains(reference)) {
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
    public Object getIfPresent(Integer key) {
        if (cache.containsKey(key)) {
            putInQueue(cache.get(key).get());
            return cache.get(key).get();
        }
        return null;
    }

    @Override
    public void put(Integer key, Object value) {
        SoftReference<Object> reference = new SoftReference(value, referenceQueue);
        cache.put(key, reference);
        putInQueue(value);
        if (subsidiaryMap.containsKey(reference)) {
            subsidiaryMap.get(reference).add(key);
        } else {
            subsidiaryMap.put(reference, new HashSet<>(key));
        }
        clean();
    }

    @Override
    public Object remove(Integer key) {
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