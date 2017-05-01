package maven;

import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;

public class SoftCacheMapTest {

    private SoftCacheMap<Integer, Integer> getCache(int size, int frequency) {
        return new SoftCacheMap<>(size, frequency);
    }

    private void testBefore(int size, int frequency, ArrayList<Integer> values,
                        ArrayList<Integer> expectedBefore) throws Exception {

        String errorMessage = "Bad result";
        SoftCacheMap<Integer, Integer> cache = getCache(size, frequency);
        for (int i = 0; i < values.size(); ++i) {
            cache.put(i, values.get(i));
        }
        for (int i = 0; i < values.size(); ++i) {
            Assert.assertEquals(errorMessage, expectedBefore.get(i), cache.getIfPresent(i));
        }
    }

    private void testAfter(int size, int frequency, ArrayList<Integer> values,
                             ArrayList<Integer> expectedAfter) {
        String errorMessage = "Bad result";
        SoftCacheMap<Integer, Integer> cache = getCache(size, frequency);
        for (int i = 0; i < values.size(); ++i) {
            cache.put(i, new Integer(values.get(i)));
        }
        try {
            Object[] big = new Object[(int) Runtime.getRuntime().maxMemory()];
        } catch (OutOfMemoryError e) {
            // ignore
        }

        int counter = 0;
        for (int i = 0; i < values.size(); ++i) {
            if (cache.getIfPresent(i) != null) {
                Assert.assertEquals(errorMessage, expectedAfter.get(counter), cache.getIfPresent(i));
                ++counter;
            }
        }
    }

    @Test
    public void testReferenceQueueBefore() throws Exception {
        ArrayList<Integer> values = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
        ArrayList<Integer> expectedBefore = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
        testBefore(0, 1, values, expectedBefore);
    }

    @Test
    public void testReferenceQueueAfter() throws Exception {
        ArrayList<Integer> values = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
        testAfter(0, 1, values, null);
    }

    @Test
    public void testRecentlyUsedQueueAfter0() throws Exception {
        ArrayList<Integer> values = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
        ArrayList<Integer> expectedAfter = new ArrayList<>(Arrays.asList(4, 5));
        testAfter(2, 1, values, expectedAfter);
    }

    @Test
    public void testRecentlyUsedQueueAfter1() throws Exception {
        ArrayList<Integer> values = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5));
        ArrayList<Integer> expectedAfter = new ArrayList<>(Arrays.asList(2, 5));
        String errorMessage = "Bad result";
        SoftCacheMap<Integer, Integer> cache = getCache(2, 1);
        for (int i = 0; i < values.size(); ++i) {
            cache.put(i, new Integer(values.get(i)));
        }
        cache.getIfPresent(2);
        try {
            Object[] big = new Object[(int) Runtime.getRuntime().maxMemory()];
        } catch (OutOfMemoryError e) {
            // ignore
        }

        int counter = 0;
        for (int i = 0; i < values.size(); ++i) {
            if (cache.getIfPresent(i) != null) {
                Assert.assertEquals(errorMessage, expectedAfter.get(counter), cache.getIfPresent(i));
                ++counter;
            }
        }
    }
}
