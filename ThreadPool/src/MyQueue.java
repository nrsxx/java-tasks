import java.util.LinkedList;
import java.util.Queue;

public class MyQueue<T> implements CustomQueue<T> {

    private Queue<T> queue = new LinkedList();

    /**
     * Добавляет задачу в очередь и оповещает потоки
     */
    @Override
    public synchronized void enqueue(T task) {
        queue.add(task);
        // Wake up anyone waiting on the queue to put some item.
        notifyAll();
    }

    /**
     * Возвращаем задачу из очереди или ждем, пока она там появится
     */
    @Override
    public synchronized T dequeue() {
        T task = null;
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e1) {
                return task;
            }
        }
        task = queue.remove();
        return task;
    }
}
