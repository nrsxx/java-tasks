public interface CustomQueue<E> {
    void enqueue(E e);
    E dequeue();
}
