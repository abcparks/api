/**
 * Created by WCY on 2023/2/24
 */
public class ThreadLocalTest {

    static ThreadLocal<String> stringThreadLocal = new ThreadLocal<>();

    public static void main(String[] args) {
        stringThreadLocal.set("future");
        printThreadLocalValue();
        stringThreadLocal.remove();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 当前
        Thread thread = Thread.currentThread();

        new Thread(() -> {
            System.out.println("子线程: " + stringThreadLocal.get());
            System.out.println("Hello");
        }).start();
    }

    static void printThreadLocalValue() {
        String value = stringThreadLocal.get();
        System.out.println("value = " + value);
    }

}
