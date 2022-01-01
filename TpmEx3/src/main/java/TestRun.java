import list.IOptimisticList;
import list.OptimisticList;
import list.OptimisticListOptimized;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

class TestRun {
    final int numOfElements = 100_000;
    static Random r = new Random();
    final List<Integer> numsToAdd;
    final List<Integer> numsToVerifyContains;
    final List<Integer> numsToRemove;

    TestRun() throws Exception {
        this.numsToAdd = generateListOfRandomNumbers();
        this.numsToVerifyContains = generateListOfRandomNumbers();
        this.numsToRemove = generateNumsToRemove(numsToAdd);

        final IOptimisticList<Integer> list = new OptimisticList<>();
        final IOptimisticList<Integer> listOp = new OptimisticListOptimized<>();
        long time;

        //ADD
        System.out.println("Started add(list)...");
        time = add(list);
        System.out.println("add(list) test in nano seconds: "+ time);

        System.out.println("Started add(list-optimized)...");
        time = add(listOp);
        System.out.println("add(list-optimized) test in nano seconds: "+ time);
        System.out.println();

        //CONTAINS
        System.out.println("Started contains(list)...");
        time = contains(list);
        System.out.println("contains(list) test in nano seconds: "+ time);

        System.out.println("Started contains(list-optimized)...");
        time = contains(listOp);
        System.out.println("contains(list-optimized) test in nano seconds: "+ time);
        System.out.println();

        //REMOVE
        System.out.println("Started remove(list)...");
        time = remove(list);
        System.out.println("remove(list) test in nano seconds: "+ time);

        System.out.println("Started remove(list-optimized)...");
        time = remove(listOp);
        System.out.println("remove(list-optimized) test in nano seconds: "+ time);
        System.out.println();

        //FINAL VERIFICATION
        System.out.println("Started finalVerification(list)...");
        try { time = finalVerification(list); }  catch (Exception e) {  e.printStackTrace(); }
        System.out.println("finalVerification(list) test in nano seconds: "+ time);

        System.out.println("Started finalVerification(list-optimized)...");
        try { time = finalVerification(listOp); }  catch (Exception e) {  e.printStackTrace(); }
        System.out.println("finalVerification(list-optimized) test in nano seconds: "+ time);
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        new TestRun();
    }

    private List<Integer> generateNumsToRemove(List<Integer> numsToAdd) {
        var numbers = new Vector<Integer>();
        for(int num : numsToAdd) {
            if(num <= 2500 && num % 2 == 0) {
                numbers.add(num);
            }
        }
        return numbers;
    }

    private List<Integer> generateListOfRandomNumbers() {
        var numbers = new Vector<Integer>();
        for(var i = 0; i < numOfElements; i++) {
            numbers.add(generateRandomNumber());
        }

        return numbers;
    }

    private Integer generateRandomNumber() {
        int low = 1;
        int high = 10_001;
        return r.nextInt(high - low) + low;
    }

    long add(IOptimisticList<Integer> list) throws InterruptedException {
        long startTime = System.nanoTime();

        var ths = new Thread[4];

        var start = 0;
        for(var i = 0; i < 4; i++) {
            int finalStart = start;
            ths[i] = new Thread(() -> {
                for(int j = finalStart; j < finalStart + numOfElements / 4 && j < numsToAdd.size(); j++) {
                    list.add(numsToAdd.get(j));
                }
            });
            ths[i].start();
            start = start + numOfElements / 4;
        }
        for(var i = 0; i < 4; i++) { ths[i].join(); }

        long endTime = System.nanoTime();
        return (endTime - startTime);
    }

    long contains(IOptimisticList<Integer> list) throws Exception {
        long startTime = System.nanoTime();

        var ths = new Thread[4];
        for(var i = 0; i < 4; i++) {
            ths[i] = new Thread(() -> {
                for(int num : numsToVerifyContains) {
                    //VERIFY CONTAINS
                    var value = list.contains(num);
                    var actualValue = numsToAdd.contains(num);
                    if(value != actualValue) {
                        try {
                            throw new Exception(String.format("Contains error: <value, actualValue> = <%s, %s>", value, actualValue));
                        } catch (Exception e) {  e.printStackTrace(); }
                    }
                }
            });
            ths[i].start();
        }
        for(var i = 0; i < 4; i++) { ths[i].join(); }

        long endTime = System.nanoTime();
        return (endTime - startTime);
    }

    long remove(IOptimisticList<Integer> list) throws InterruptedException {
        long startTime = System.nanoTime();

        var ths = new Thread[4];
        for(var i = 0; i < 4; i++) {
            ths[i] = new Thread(() -> {
                for(int num : numsToRemove) {
                    list.remove(num);
                }
            });
            ths[i].start();
        }
        for(var i = 0; i < 4; i++) { ths[i].join(); }

        long endTime = System.nanoTime();
        return (endTime - startTime);
    }

    long finalVerification(IOptimisticList<Integer> list) throws Exception {
        long startTime = System.nanoTime();

        for(int num : numsToAdd) {
            if(numsToRemove.contains(num)) {
                continue;
            }

            if(!list.contains(num)) {
                throw new Exception("list doesn't contain: " + num);
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime);
    }
}
