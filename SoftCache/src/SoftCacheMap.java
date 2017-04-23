import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;

public class SoftCacheMap implements Cache<Integer, String> {

    private HashMap<Integer, SoftReference<String>> hashMap1;
    private HashMap<SoftReference<String>, HashSet<Integer>> hashMap2;
    private ReferenceQueue<SoftReference<String>> referenceQueue;

    private SoftCacheMap() {
        hashMap1 = new HashMap();
        hashMap2 = new HashMap();
        referenceQueue = new ReferenceQueue();
    }

    private boolean delete() {
        SoftReference reference = (SoftReference) referenceQueue.poll();
        if (reference != null) {
            if (hashMap1.entrySet().contains(reference)) {
                HashSet localSet = hashMap2.remove(reference);
                for (Object local : localSet) {
                    hashMap1.remove(local);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getIfPresent(Integer key) {
        if (hashMap1.get(key).get() != null) {
            return hashMap1.get(key).get();
        }
        return null;
    }

    @Override
    public void put(Integer key, String value) {
        hashMap1.put(key, new SoftReference(value, referenceQueue));
        if (hashMap2.get(hashMap1.get(key)) != null) {
            hashMap2.get(hashMap1.get(key)).add(key);
        } else {
            hashMap2.put(hashMap1.get(key), new HashSet(key));
        }
        while (delete()) {
            //delete references
        }
    }

    @Override
    public String remove(Integer key) {
        if (hashMap1.get(key).get() != null) {
            hashMap2.remove(hashMap1.get(key));
            return hashMap1.remove(key).get();
        }
        return null;
    }

    @Override
    public void clear() {
        hashMap1.clear();
        hashMap2.clear();
    }
}