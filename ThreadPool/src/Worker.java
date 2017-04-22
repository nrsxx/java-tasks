import java.util.concurrent.Callable;

public class Worker implements Runnable {
    private MyQueue<Callable> myQueue;
    private String name;

    Worker(MyQueue<Callable> myQueue, String name){
        this.myQueue = myQueue;
        this.name = name;
    }

    @Override
    public void run() {

        while (true) {
            try {
                Callable r = myQueue.dequeue();
                System.out.println(" Задачу принял поток : Thread - " + this.name);
                r.call();
                System.out.println(" Задачу выполнил поток: Thread - " + this.name);
            } catch (Exception e) {
                System.out.println(" Выполнение задачи было прервано: Thread - " + this.name);
                break;
            }
        }
    }
}
