package c;

import interfaces.IBQueue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueueSpinning<T> implements IBQueue<T> {
    volatile ReentrantLock enqLock, deqLock;
    volatile AtomicInteger size;
    volatile Node head, tail;
    int capacity;
    Condition notFullCondition, notEmptyCondition;

    String outputE = "";
    String outputD = "";

    public BoundedQueueSpinning(int capacity) {
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
            var hasToSpin = size.get() == 0;
            if(hasToSpin) System.out.printf("ENQ: '%s' started spinning... \n", Thread.currentThread().getName());

            while (size.get() == capacity) {}; //spinning

            if(hasToSpin) System.out.printf("ENQ: '%s' stopped spinning... \n", Thread.currentThread().getName());

            Node e = new Node(x);
            tail.next = e;
            tail = tail.next;

            System.out.printf("ENQ: '%s' added a new node %d \n", Thread.currentThread().getName(), (int) x);

            size.getAndIncrement();

        } finally {
            System.out.printf("ENQ: '%s' releases lock \n", Thread.currentThread().getName());
            enqLock.unlock();
        }
    }

    public T deq() {
        boolean mustWakeEnqueuers = false;
        T v = null;

        System.out.printf("DEQ: '%s' wants lock \n", Thread.currentThread().getName());
        deqLock.lock();
        outputD = String.format("DEQ: '%s' gets lock \n", Thread.currentThread().getName()); System.out.println(outputD);

        try {

            var hasToSpin = size.get() == 0;
            if(hasToSpin) System.out.printf("DEQ: '%s' started spinning... \n", Thread.currentThread().getName());

            while (head.next == null) {}; //spinning

            if(hasToSpin) System.out.printf("DEQ: '%s' stopped spinning... \n", Thread.currentThread().getName());

            v = head.next.value;
            head = head.next;

            System.out.printf("DEQ: '%s' removes node %d \n", Thread.currentThread().getName(), (int) v);

            size.getAndDecrement();
        } finally {
            System.out.printf("DEQ: '%s' releases lock \n", Thread.currentThread().getName());
            deqLock.unlock();
            return v;
        }

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
