import java.util.ArrayList;
import java.util.LinkedList;

public class MyThreadPool {
    private final int numberOfThreads;
    private ArrayList<MyThread> threads;
    private final LinkedList<Runnable> tasks;

    public MyThreadPool(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        threads = new ArrayList<>(numberOfThreads);
        tasks = new LinkedList<>();

        for (int i = 0; i < numberOfThreads; ++i) {
            threads.add(new MyThread());
            threads.get(i).start();
        }
    }

    public class MyThread extends Thread {
        @Override
        public void run() {
            Runnable r;
            while (true) {
                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    r = tasks.removeFirst();
                } try {
                    r.run();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void execute(Runnable r) {
        synchronized (tasks) {
            tasks.addLast(r);
            tasks.notify();
        }
    }

    public static class Task implements Runnable {
        @Override
        public void run() {
            System.out.println("Executed.");
        }
    }

    public static void main(String... args) {
        MyThreadPool pool = new MyThreadPool(2);
        for (int i = 0; i < 100; ++i) {
            pool.execute(new Task());
        }
    }
}