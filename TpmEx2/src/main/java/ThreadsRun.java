import interfaces.IBQueue;

import java.util.Random;

public class ThreadsRun {
    static final Integer[] capacities = new Integer[] {1, 2, 5, 20};
    static final Integer[] numOfThreads = new Integer[] {30, 100, 1000, 10_000};

    public static void main(String[] args) throws InterruptedException {
        if(args.length > 1) {
            Run_Type(args[1]);
            return;
        }

//        Run_Type("a");
        Run_Type("b");
//        Run_Type("c1");
//        Run_Type("c2");

//        Run_Type("d");
    }

    public static void Run_Type(String exercise) throws InterruptedException {
        System.out.printf("\n______ test exercise '%s' ____\n", exercise);

        for (Integer capacity : capacities) {
            for (Integer size : numOfThreads) {
                IBQueue<Integer> queue = getQueueByExercise(exercise, capacity);
                RunTest(queue, capacity, size);
            }
        }
    }

    private static void RunTest(IBQueue<Integer> queue, Integer capacity, Integer numOfThreads) throws InterruptedException {
        System.out.printf("\n-----------------Started test non-det [%d capacity, %d num of threads]-----------------\n", capacity, numOfThreads);

        int numOfEnqs = 0;

        Random r = new Random();
        int low = 0;
        int high = 100;

        for(var i = 0; i < numOfThreads; i++) {
            int result = r.nextInt(high-low) + low;

            if(result <= 50) {
                int finalI = i;
                (new Thread(() -> queue.enq(finalI))).start();
                numOfEnqs++;
            } else {
                (new Thread(queue::deq)).start();
            }
        }

        //equal out the enq and deq to make all threads finish
        var remainingDeq = numOfThreads - numOfEnqs * 2;
        var copyNumOfEnqs = numOfEnqs;
        if(remainingDeq > 0) {
            for(var i = 0; i < remainingDeq; i++) {
                int finalI = i;
                (new Thread(() -> queue.enq(finalI + numOfThreads))).start();
                copyNumOfEnqs++;
            }
        } else if (remainingDeq < 0) {
            for(var i = 0; i < Math.abs(remainingDeq); i++) {
                (new Thread(queue::deq)).start();
            }
        }

        Thread.sleep(2000);
        System.out.printf("\n-----------------Finished test non-det [%d capacity, %d num of threads | (enqs=%d, deqs=%d)]-----------------\n", capacity, numOfThreads, copyNumOfEnqs, numOfThreads - numOfEnqs);
    }

    public static IBQueue<Integer> getQueueByExercise(String exercise, Integer capacity) {
        return switch (exercise) {
            case "a" -> new a.BoundedQueue<>(capacity);
            case "b" -> new b.BoundedQueue<>(capacity);
            case "c1" -> new c.BoundedQueueSpinning<>(capacity);
            case "c2" -> new c.BoundedQueueSpinningMixed<>(capacity);
            case "d" -> new d.UnboundedQueue<>();
            default -> null;
        };
    }
}
