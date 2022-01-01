import interfaces.IBQueue;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Vector;

public class ThreadsRun {
    public static void main(String[] args) throws InterruptedException {
//        Run_A();
//        Run_B();
//        Run_C1();
        Run_C2();
    }

    public static void Run_A() throws InterruptedException {
        RunTest(1, new a.BoundedQueue<>(1));
        RunTest(2, new a.BoundedQueue<>(2));
        RunTest(5, new a.BoundedQueue<>(5));
        RunTest(101, new a.BoundedQueue<>(100));
    }

    public static void Run_B() throws InterruptedException {
        RunTest(1, new b.BoundedQueue<>(1));
        RunTest(2, new b.BoundedQueue<>(2));
        RunTest(5, new b.BoundedQueue<>(5));
        RunTest(101, new b.BoundedQueue<>(100));
    }

    public static void Run_C1() throws InterruptedException {
        RunTest(1, new c.BoundedQueueSpinning<>(1));
        RunTest(2, new c.BoundedQueueSpinning<>(2));
        RunTest(5, new c.BoundedQueueSpinning<>(5));
        RunTest(101, new c.BoundedQueueSpinning<>(101));
    }

    public static void Run_C2() throws InterruptedException {
        RunTest(1, new c.BoundedQueueSpinningMixed<>(1));
        RunTest(2, new c.BoundedQueueSpinningMixed<>(2));
        RunTest(5, new c.BoundedQueueSpinningMixed<>(5));
        RunTest(101, new c.BoundedQueueSpinningMixed<>(101));
    }

    private static void RunTest(int capacity, IBQueue<Integer> queue) throws InterruptedException {
        System.out.printf("\n-----------------Started [%d capacity]-----------------\n", capacity);

        Vector<Thread> ths = new Vector<>();
        Vector<Thread> starterThs = new Vector<>(); //threads that start other threads (use for async delays)

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
        ths.add(new Thread(queue::deq)); ths.lastElement().start();            Thread.sleep(3000);
        ths.add(new Thread(() -> queue.enq(4))); ths.lastElement().start();
        ths.add(new Thread(() -> queue.enq(5))); ths.lastElement().start();

        ths.add(new Thread(queue::deq)); ths.lastElement().start();             Thread.sleep(250);
        ths.add(new Thread(queue::deq)); ths.lastElement().start();             Thread.sleep(150);
        ths.add(new Thread(() -> queue.enq(6))); ths.lastElement().start();
        ths.add(new Thread(() -> queue.enq(7))); ths.lastElement().start();

        ths.add(new Thread(queue::deq)); ths.lastElement().start();             Thread.sleep(300);
        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        ths.add(new Thread(queue::deq)); ths.lastElement().start();             Thread.sleep(100);
        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        ths.add(new Thread(queue::deq)); ths.lastElement().start();
        ths.add(new Thread(queue::deq)); ths.lastElement().start();             Thread.sleep(450);
        for(var i = 0; i < 7; i++) {
            int finalI = i+8;
            ths.add(new Thread(() -> queue.enq(finalI))); ths.lastElement().start();
        }

        starterThs.add(new Thread(() -> {
            for(var i = 0; i < 30; i++) {
                int finalI = i + 30;
                try { Thread.sleep(130 + i); } catch (InterruptedException e) { e.printStackTrace(); }
                ths.add(new Thread(() -> queue.enq(finalI))); ths.lastElement().start();
            }
        })); starterThs.lastElement().start();

        for(var i = 0; i < 30; i++) {
            ths.add(new Thread(queue::deq)); ths.lastElement().start();
        }

        for(var i = 0; i < 130; i++) {
            int finalI = i + 1000;
            ths.add(new Thread(() -> queue.enq(finalI))); ths.lastElement().start();
        }

        starterThs.add(new Thread(() -> {
            for(var i = 0; i < 130; i++) {
                try { Thread.sleep(110 + i); } catch (InterruptedException e) { e.printStackTrace(); }
                ths.add(new Thread(queue::deq)); ths.lastElement().start();
            }
        })); starterThs.lastElement().start();

        //deadlock detection
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] threadIds = bean.findDeadlockedThreads(); // Returns null if no threads are deadlocked.
        if (threadIds != null) {
            ThreadInfo[] infos = bean.getThreadInfo(threadIds);
            for (ThreadInfo info : infos) {
                StackTraceElement[] stack = info.getStackTrace();
                System.out.println(Arrays.toString(stack));
            }
        }

        //wait for all threads to stop
        for (var i = 0; i < ths.size(); i++) {
            if(ths.get(i).isAlive()) {
                ths.get(i).join();
            }
        }

        for (var i = 0; i < starterThs.size(); i++) {
            if(starterThs.get(i).isAlive()) {
                starterThs.get(i).join();
            }
        }

        //keep 'Finished ...' at last (some printed output from dead threads may appear later)
        Thread.sleep(2000);

        System.out.printf("\n-----------------Finished [%d capacity] (%d threads) -----------------\n", capacity, ths.size());
    }
}
