//import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by david on 18.04.17.
 */

public class SoftCacheMap implements Cache<Integer, String> {

    private HashMap<Integer, SoftReference<String>> hashMap;
    //private ReferenceQueue<SoftReference<String>> referenceQueue;

    public SoftCacheMap() {
        this.hashMap = new HashMap<Integer, SoftReference<String>>();
        //  this.referenceQueue = new ReferenceQueue<SoftReference<String>>();
    }

    @Override
    public String getIfPresent(Integer key) {
        if (hashMap.get(key).get() != null) {
            return hashMap.get(key).get();
        }
        return null;
    }

    @Override
    public void put(Integer key, String value) {
        hashMap.put(key, new SoftReference<String>(value));
    }

    @Override
    public String remove(Integer key) {
        if (hashMap.get(key).get() != null) {

            return hashMap.remove(key).get();
        }
        return null;
    }

    @Override
    public void clear() {
        hashMap.clear();
    }
}