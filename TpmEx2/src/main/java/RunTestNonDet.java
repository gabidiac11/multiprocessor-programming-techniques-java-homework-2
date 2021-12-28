import interfaces.IBQueue;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

public class RunTestNonDet {
    static final Integer[] capacities = new Integer[] {1, 2, 5, 20};
    static final Integer[] sizes = new Integer[] {30, 100, 1000, 10_000};

    public static void main(String[] args) throws InterruptedException {
//        Run_Type("a");
//        Run_Type("b");
        Run_Type("c1");
//        Run_Type("c2");
    }

    public static void Run_Type(String exercise) throws InterruptedException {
        for (Integer capacity : capacities) {
            for (Integer size : sizes) {
                IBQueue<Integer> queue = getByExercise(exercise, capacity);
                RunTest(queue, capacity, size);
            }
        }
    }

    private static void RunTest(IBQueue<Integer> queue, Integer capacity, Integer size) throws InterruptedException {
        System.out.printf("\n-----------------Started test non-det [%d capacity, %d num of threads]-----------------\n", capacity, size);

        int numOfEnqs = 0;

        Random r = new Random();
        int low = 0;
        int high = 100;

        for(var i = 0; i < size; i++) {
            int result = r.nextInt(high-low) + low;

            if(result <= 50) {
                int finalI = i;
                (new Thread(() -> queue.enq(finalI))).start();
                numOfEnqs++;
            } else {
                (new Thread(queue::deq)).start();
            }
        }

        //some dequers are waiting
        var remainingDeq = size - numOfEnqs * 2;
        var copyNumOfEnqs = numOfEnqs;
        if(remainingDeq > 0) {
            for(var i = 0; i < remainingDeq; i++) {
                int finalI = i;
                (new Thread(() -> queue.enq(finalI + size))).start();
                copyNumOfEnqs++;
            }
        }

        Thread.sleep(2000);
        System.out.printf("\n-----------------Finished test non-det [%d capacity, %d num of threads | (enqs=%d, deqs=%d)]-----------------\n", capacity, size, copyNumOfEnqs, size - numOfEnqs);
    }

    public static IBQueue<Integer> getByExercise(String exercise, Integer capacity) {
        return switch (exercise) {
            case "a" -> new a.BoundedQueue<>(capacity);
            case "b" -> new b.BoundedQueue<>(capacity);
            case "c1" -> new c.BoundedQueueSpinning<>(capacity);
            case "c2" -> new c.BoundedQueueSpinningMixed<>(capacity);
            default -> null;
        };
    }
}
