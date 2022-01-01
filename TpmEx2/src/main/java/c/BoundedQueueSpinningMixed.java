package c;

import interfaces.IBQueue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueueSpinningMixed<T> implements IBQueue<T> {
    ReentrantLock enqLock, deqLock;
    volatile AtomicInteger size;
    Node head, tail;
    int capacity;
    Condition notFullCondition, notEmptyCondition;

    String outputE = "";
    String outputD = "";

    public BoundedQueueSpinningMixed(int capacity) {
        this.capacity = capacity;
        this.head = new Node(null);
        this.tail = head;
        this.size = new AtomicInteger(0);
        this.enqLock = new ReentrantLock();
        this.notFullCondition = enqLock.newCondition();
        this.deqLock = new ReentrantLock();
        this.notEmptyCondition = deqLock.newCondition();
    }

    public void enq(T x) {
        boolean mustWakeDequeuers = false;

        System.out.printf("ENQ: '%s' wants lock \n", Thread.currentThread().getName());
        enqLock.lock();
        outputE = String.format("ENQ: '%s' gets lock \n", Thread.currentThread().getName());System.out.print(outputE);

        try {
            while (size.get() == capacity) {
                System.out.printf("ENQ: '%s' awaits notFullCondition \n", Thread.currentThread().getName());
                notFullCondition.await();
            }
            Node e = new Node(x);
            tail.next = e;
            tail = tail.next;
            System.out.printf("ENQ: '%s' added a new node %d \n", Thread.currentThread().getName(), (int) x);

            if (size.getAndIncrement() == 0) {
                mustWakeDequeuers = true;
                System.out.printf("ENQ: '%s' must wake dequeuers \n", Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.printf("ENQ: '%s' releases lock \n", Thread.currentThread().getName());
            enqLock.unlock();
        }

        if (mustWakeDequeuers) {
            System.out.printf("ENQ: '%s' wants DEQ-lock \n", Thread.currentThread().getName());
            deqLock.lock();
            outputD = String.format("ENQ: '%s' gets DEQ-lock \n", Thread.currentThread().getName()); System.out.println(outputD);

            try {
                notEmptyCondition.signalAll();
            } finally {
                System.out.printf("ENQ: '%s' releases DEQ-lock \n", Thread.currentThread().getName());
                deqLock.unlock();
            }
        }
    }

    public T deq() {
        boolean mustWakeEnqueuers = false;
        T v = null;

        System.out.printf("DEQ: '%s' wants lock \n", Thread.currentThread().getName());
        deqLock.lock();
        outputD = String.format("DEQ: '%s' gets lock \n", Thread.currentThread().getName()); System.out.println(outputD);

        try {
            if (head.next == null) {
                System.out.printf("DEQ: '%s' waits notEmptyCondition... \n", Thread.currentThread().getName());
                notEmptyCondition.await();
            }

            var hasToSpin = size.get() == 0;
            if(hasToSpin) System.out.printf("DEQ: '%s' started spinning... \n", Thread.currentThread().getName());

            while (size.get() == 0) {}; //spinning

            if(hasToSpin) System.out.printf("DEQ: '%s' stopped spinning... \n", Thread.currentThread().getName());

            /*
                if this is not true: (size.get() == 0) -> (head.next == null)
                an exception will get caught (reading property of null)
            */

            v = head.next.value;
            head = head.next;

            System.out.printf("DEQ: '%s' removes node %d \n", Thread.currentThread().getName(), (int) v);

            if (size.getAndDecrement() == capacity) {
                mustWakeEnqueuers = true;
                System.out.printf("DEQ: '%s' must wake enqueuers \n", Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.printf("DEQ: '%s' releases lock \n", Thread.currentThread().getName());
            deqLock.unlock();
        }

        if (mustWakeEnqueuers) {
            System.out.printf("DEQ: '%s' wants ENQ-lock \n", Thread.currentThread().getName());
            enqLock.lock();
            outputE = String.format("DEQ: '%s' gets ENQ-lock \n", Thread.currentThread().getName()); System.out.println(outputE);

            try {
                notFullCondition.signalAll();
            } finally {
                System.out.printf("DEQ: '%s' releases ENQ-lock \n", Thread.currentThread().getName());
                enqLock.unlock();
            }
        }

        return v;
    }


    protected class Node {

        public T value;
        public Node next;

        public Node(T x) {
            value = x;
            next = null;
        }
    }
}