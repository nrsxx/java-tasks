import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ThreadPool {

    private final int threadPoolCapacity;
    private MyQueue<Callable> myQueue = new MyQueue();
    private ArrayList<Thread> threads = new ArrayList();

    private ThreadPool(int capacity) {
        this.threadPoolCapacity = capacity;
        initAllWorkers();
    }

    private void initAllWorkers() {
        for (Integer i = 0; i < threadPoolCapacity; ++i) {
            Thread thread = new Thread(new Worker(myQueue, i.toString()));
            thread.setName("Thread - " + i.toString());
            thread.start();
            threads.add(thread);
        }
    }

    private void stopAll() {
        for (int i = 0; i < threadPoolCapacity; ++i) {
            threads.get(i).interrupt();
        }
        System.out.println("Good bye!");
    }

    private void submitTask(Callable r) {
        myQueue.enqueue(r);
    }

    public static void main(String... args) throws InterruptedException, ExecutionException {

        Integer i = 3;
        int num = Integer.parseInt(i.toString());//args[0]);
        ThreadPool threadPool = new ThreadPool(num);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("Выберите: добавить задачу в пул - 1 \n выход - 2");
            int choice = sc.nextInt();

            if (choice == 1) {
                System.out.println("Введите длительность задачи");
                int time = sc.nextInt();
                Callable task = new Task(time);
                threadPool.submitTask(task);
            }

            if (choice == 2) {
                threadPool.stopAll();
                break;
            }
        }
    }
}