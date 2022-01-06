import list.IOptimisticList;
import list.OptimisticList;
import list.OptimisticListOptimized;

import java.util.List;
import java.util.Random;
import java.util.Vector;

class TestRun {
    final int numOfElements = 100_000;
    static Random r = new Random();

    //the list from which threads add number to optimistic list
    final List<Integer> numsToAdd;
    final List<Integer> numsToVerifyContains;
    final List<Integer> numsToRemove;

    static class TimeResults { long add, contains, remove, finalCheck; }
    //un-optimized time results
    public TimeResults timeRes_Ini = new TimeResults();
    //optimized time results
    public TimeResults timeRes_Op = new TimeResults();

    /**
     * run test for un-optimized and optimized list and print the timing side by side
     * @throws Exception
     */
    TestRun() throws Exception {
        this.numsToAdd = generateListOfRandomNumbers();
        this.numsToVerifyContains = generateListOfRandomNumbers();
        this.numsToRemove = generateNumsToRemove(numsToAdd);

        final IOptimisticList<Integer> list = new OptimisticList<>();
        final IOptimisticList<Integer> listOp = new OptimisticListOptimized<>();

        //ADD
        System.out.println("Started add(list)...");
        timeRes_Ini.add = add(list);
        System.out.println("add(list) test in nano seconds: "+ timeRes_Ini.add);

        System.out.println("Started add(list-optimized)...");
        timeRes_Op.add = add(listOp);
        System.out.println("add(list-optimized) test in nano seconds: "+ timeRes_Op.add);
        System.out.println();

        //CONTAINS
        System.out.println("Started contains(list)...");
        timeRes_Ini.contains = contains(list);
        System.out.println("contains(list) test in nano seconds: "+ timeRes_Ini.contains);

        System.out.println("Started contains(list-optimized)...");
        timeRes_Op.contains = contains(listOp);
        System.out.println("contains(list-optimized) test in nano seconds: "+ timeRes_Op.contains);
        System.out.println();

        //REMOVE
        System.out.println("Started remove(list)...");
        timeRes_Ini.remove = remove(list);
        System.out.println("remove(list) test in nano seconds: "+ timeRes_Ini.remove);

        System.out.println("Started remove(list-optimized)...");
        timeRes_Op.remove = remove(listOp);
        System.out.println("remove(list-optimized) test in nano seconds: "+ timeRes_Op.remove);
        System.out.println();

        //FINAL VERIFICATION
        System.out.println("Started finalVerification(list)...");
        timeRes_Ini.finalCheck = finalVerification(list);
        System.out.println("finalVerification(list) test in nano seconds: "+ timeRes_Ini.finalCheck);

        System.out.println("Started finalVerification(list-optimized)...");
        timeRes_Op.finalCheck = finalVerification(listOp);
        System.out.println("finalVerification(list-optimized) test in nano seconds: "+ timeRes_Op.finalCheck);
        System.out.println();
    }

    /**
     * class that keeps sums, avg, min of each operation (add, contains, remove, finalCheck - finalVerification() )
     */
    static class Results {
        public long sum_add = 0;
        public long sum_contains = 0;
        public long sum_remove = 0;
        public long sum_finalCheck = 0;

        public long max_add = 0;
        public long max_contains = 0;
        public long max_remove = 0;
        public long max_finalCheck = 0;

        public long min_add = Long.MAX_VALUE;
        public long min_contains = Long.MAX_VALUE;
        public long min_remove = Long.MAX_VALUE;
        public long min_finalCheck = Long.MAX_VALUE;

        public int count = 1;
        public Results(int count) {
            this.count = count;
        }

        public void append(TimeResults tr) {
            sum_add += tr.add;
            sum_contains += tr.contains;
            sum_remove += tr.remove;
            sum_finalCheck += tr.finalCheck;

            //MIN
            if(min_add > tr.add) { min_add = tr.add; }
            if(min_contains > tr.contains) { min_contains = tr.contains; }
            if(min_remove > tr.remove) { min_remove = tr.remove; }
            if(min_finalCheck > tr.finalCheck) { min_finalCheck = tr.finalCheck; }

            //MAX
            if(max_add < tr.add) { max_add = tr.add; }
            if(max_contains < tr.contains) { max_contains = tr.contains; }
            if(max_remove < tr.remove) { max_remove = tr.remove; }
            if(max_finalCheck < tr.finalCheck) { max_finalCheck = tr.finalCheck; }
        }

        @Override
        public String toString() {
            return "Results{" +
                    "\nsum_add=" + sum_add +
                           ", AVG_add=" + (sum_add / count) +
                           ", MIN_add=" + min_add +
                           ", MAX_add=" + max_add +

                    "\nsum_contains=" + sum_contains +
                           ", AVG_contains=" + (sum_contains / count) +
                           ", MIN_contains=" + min_contains +
                           ", MAX_contains=" + max_contains +

                    "\nsum_remove=" + sum_remove +
                           ", AVG_remove=" + (sum_remove / count) +
                           ", MIN_remove=" + min_remove +
                           ", MAX_remove=" + max_remove +

                    "\nsum_finalCheck=" + sum_finalCheck +
                           ", AVG_finalCheck=" + (sum_finalCheck / count) +
                           ", MIN_finalCheck=" + min_finalCheck +
                           ", MAX_finalCheck=" + max_finalCheck +

                    "\ncount=" + count +
                    '}';
        }
    }

    public static void main(String[] args) throws Exception {
        final int count = 2;
        var result_ini = new Results(count); // un-optimized list results
        var result_op = new Results(count); // optimized list results

        for(var i = 0; i < count; i++) {
            var testRun = new TestRun();
            result_ini.append(testRun.timeRes_Ini);
            result_op.append(testRun.timeRes_Op);

            System.out.printf("\nFinished-run-%d------------------------------------------------------------------------------------------------------------------------------------------------------\n\nn", i);
        }

        System.out.print("Results from unoptimized list: \n");
        System.out.print(result_ini);
        System.out.println();System.out.println();

        System.out.print("Results from optimized list: \n");
        System.out.print(result_op);
        System.out.println();
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
                for(int j = finalStart; j < finalStart + numsToAdd.size() / 4 && j < numsToAdd.size(); j++) {
                    list.add(numsToAdd.get(j));
                }
            });
            ths[i].start();
            start = start + numsToAdd.size() / 4;
        }
        for(var i = 0; i < 4; i++) { ths[i].join(); }

        long endTime = System.nanoTime();
        return (endTime - startTime);
    }

    long contains(IOptimisticList<Integer> list) throws Exception {
        long startTime = System.nanoTime();

        var ths = new Thread[4];

        var start = 0;
        for(var i = 0; i < 4; i++) {
            int finalStart = start;

            ths[i] = new Thread(() -> {
                for(int j = finalStart; j < finalStart + numsToVerifyContains.size() / 4 && j < numsToVerifyContains.size(); j++) {
                    var num = numsToVerifyContains.get(j);

                    //VERIFY CONTAINS
                    var value = list.contains(num);
                    var actualValue = numsToAdd.contains(num);
                    if(value != actualValue) {
                        try { throw new Exception(String.format("Contains error: <value, actualValue> = <%s, %s>", value, actualValue));
                        } catch (Exception e) {  e.printStackTrace(); }
                    }
                }
            });

            ths[i].start();
            start = start + numsToVerifyContains.size() / 4;
        }
        for(var i = 0; i < 4; i++) { ths[i].join(); }

        long endTime = System.nanoTime();
        return (endTime - startTime);
    }

    long remove(IOptimisticList<Integer> list) throws InterruptedException {
        long startTime = System.nanoTime();

        var ths = new Thread[4];

        var start = 0;
        for(var i = 0; i < 4; i++) {
            int finalStart = start;

            ths[i] = new Thread(() -> {
                for(int j = finalStart; j < finalStart + numsToRemove.size() / 4 && j < numsToRemove.size(); j++) {
                    var num = numsToRemove.get(j);
                    list.remove(num);
                }
            });

            ths[i].start();
            start = start + numsToRemove.size() / 4;
        }
        for(var i = 0; i < 4; i++) { ths[i].join(); }

        long endTime = System.nanoTime();
        return (endTime - startTime);
    }

    /**
     * verifies if all the numbers added that weren't removed are present in the optimistic list
     * @param list
     * @return
     * @throws Exception
     */
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
