import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Vector;

public class RunTest {
    public static void main(String[] args) throws InterruptedException {
//        var queue = new a.a.BoundedQueue<Integer>(2);
        var queue = new b.BoundedQueue<Integer>(2);
        Vector<Thread> ths = new Vector<>();

        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        ths.add(new Thread(() -> queue.enq(0))); ths.lastElement().start();
        ths.add(new Thread(() -> queue.enq(1))); ths.lastElement().start();
        ths.add(new Thread(() -> queue.enq(2))); ths.lastElement().start();

        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        Thread.sleep(500);
        ths.add(new Thread(() -> queue.enq(3))); ths.lastElement().start();

        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        Thread.sleep(3000);
        ths.add(new Thread(() -> queue.enq(4))); ths.lastElement().start();
        ths.add(new Thread(() -> queue.enq(4))); ths.lastElement().start();

        ths.add(new Thread(queue::deq)); ths.lastElement().start(); Thread.sleep(250);
        ths.add(new Thread(queue::deq)); ths.lastElement().start(); Thread.sleep(150);
        ths.add(new Thread(() -> queue.enq(5))); ths.lastElement().start();
        ths.add(new Thread(() -> queue.enq(6))); ths.lastElement().start();


        ths.add(new Thread(queue::deq)); ths.lastElement().start(); Thread.sleep(300);
        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        ths.add(new Thread(queue::deq)); ths.lastElement().start(); Thread.sleep(100);
        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        ths.add(new Thread(queue::deq)); ths.lastElement().start(); Thread.sleep(450);
        for(var i = 0; i < 7; i++) {
            int finalI = i;
            ths.add(new Thread(() -> queue.enq(finalI))); ths.lastElement().start();
        }

        ths.add(new Thread(() -> {
            for(var i = 0; i < 30; i++) {
                int finalI = i;
                try { Thread.sleep(130 + i); } catch (InterruptedException e) { e.printStackTrace(); }
                ths.add(new Thread(() -> queue.enq(finalI))); ths.lastElement().start();
            }
        })); ths.lastElement().start();

        for(var i = 0; i < 30; i++) {
            ths.add(new Thread(queue::deq)); ths.lastElement().start();
        }

        for(var i = 0; i < 130; i++) {
            int finalI = i;
            ths.add(new Thread(() -> queue.enq(finalI))); ths.lastElement().start();
        }

        ths.add(new Thread(() -> {
            for(var i = 0; i < 130; i++) {
                try { Thread.sleep(110 + i); } catch (InterruptedException e) { e.printStackTrace(); }
                ths.add(new Thread(queue::deq)); ths.lastElement().start();
            }
        })); ths.lastElement().start();

        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] threadIds = bean.findDeadlockedThreads(); // Returns null if no threads are deadlocked.
        if (threadIds != null) {
            ThreadInfo[] infos = bean.getThreadInfo(threadIds);
            for (ThreadInfo info : infos) {
                StackTraceElement[] stack = info.getStackTrace();
                System.out.println(Arrays.toString(stack));
            }
        }

        for(var i = 0; i < ths.size(); i++) {
            ths.get(i).join();
        }

        System.out.println("Finished...");
    }
}
