import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Task implements Callable {
    private int time;

    Task(int lifeTime) {
        time = lifeTime;
    }

    @Override
    public Object call() throws InterruptedException {
        System.out.println("Начало задачи");
        TimeUnit.SECONDS.sleep(time);
        System.out.println("Конец задачи");
        return null;
    }
}
