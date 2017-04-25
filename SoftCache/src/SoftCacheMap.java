import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SoftCacheMap<V> implements Cache<Integer, V> {

    private HashMap<Integer, SoftReference<V>> cache;
    private HashMap<SoftReference<V>, HashSet<Integer>> subsidiaryMap;
    private ReferenceQueue<? super Object> referenceQueue;
    private Deque<V> recentlyUsed;
    private int maxSize;
    private int cleaningFrequency;
    private int numOfPuts;

    private SoftCacheMap(int size, int frequency) {
        cache = new HashMap<>();
        /**
         * второй hashMap - вспомогательный, используется при удалении из кэша
         * тех ссылок, которые попали в referenceQueue. Чтобы не искать все ключи,
         * соответствующие одной ссылке, храню Set из таких ключей в качестве значения
         * во втором hashMap-e.
         */
        subsidiaryMap = new HashMap<>();
        referenceQueue = new ReferenceQueue();
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
                    HashSet<Integer> localSet = subsidiaryMap.remove(reference);
                    for (Integer local : localSet) {
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
    public V getIfPresent(Integer key) {
        if (cache.containsKey(key)) {
            putInQueue(cache.get(key).get());
            return cache.get(key).get();
        }
        return null;
    }

    @Override
    public void put(Integer key, V value) {
        ++numOfPuts;
        SoftReference<V> reference = new SoftReference(value, referenceQueue);
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

    public static void main(String... args) throws InterruptedException {

        /**
         * проверка корректной работы referenceQueue
         */

        SoftCacheMap cache1 = new SoftCacheMap(0, 1);
        cache1.put(0, new Integer(0));

        /**
         * должен быть 0
         */
        System.out.println(cache1.getIfPresent(0));

        try {
            Object[] big = new Object[(int) Runtime.getRuntime().maxMemory()];
        } catch (OutOfMemoryError e) {
            // ignore
        }

        /**
         * должен быть null
         */
        System.out.println(cache1.getIfPresent(0));
        cache1.clear();


        /**
         * проверка корректной работы recentlyUsed
         */

        SoftCacheMap cache2 = new SoftCacheMap(1, 1);
        cache2.put(0, new Integer(0));

        /**
         * должен быть 0
         */
        System.out.println(cache2.getIfPresent(0));

        try {
            Object[] big = new Object[(int) Runtime.getRuntime().maxMemory()];
        } catch (OutOfMemoryError e) {
            // ignore
        }

        /**
         * должен быть 0
         */
        System.out.println(cache2.getIfPresent(0));
        cache2.clear();
    }
}