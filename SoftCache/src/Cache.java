/**
 * Created by david on 18.04.17.
 */
public interface Cache<K, V> {
    /**
     * Возвращает соответствующее значение, если оно ещё в кэше, иначе null
     */
    V getIfPresent(K key);

    /**
     * Сохраняет value по соответствующему ключу key
     */
    void put(K key, V value);

    /**
     * Удаляет соответствующее ключу key значение
     */
    V remove(K key);

    /**
     * Очищает кэш
     */
    void clear();
}
