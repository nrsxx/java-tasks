import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;

public class SoftCacheMap implements Cache<Integer, Object> {

    private HashMap<Integer, SoftReference<Object>> cache;
    private HashMap<SoftReference<Object>, HashSet<Integer>> subsidiaryMap;
    private ReferenceQueue<SoftReference<Object>> referenceQueue;

    private SoftCacheMap() {
        cache = new HashMap<>();
        /**
         * второй hashMap - вспомогательный, используется при удалении из кэша
         * тех ссылок, которые попали в referenceQueue. Чтобы не искать все ключи,
         * соответствующие одной ссылке, храню Set из таких ключей в качестве значения
         * во втором hashMap-e.
         */
        subsidiaryMap = new HashMap<>();
        referenceQueue = new ReferenceQueue<>();
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
            return cache.get(key).get();
        }
        return null;
    }

    @Override
    public void put(Integer key, Object value) {
        SoftReference<Object> reference = new SoftReference(value, referenceQueue);
        cache.put(key, reference);
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