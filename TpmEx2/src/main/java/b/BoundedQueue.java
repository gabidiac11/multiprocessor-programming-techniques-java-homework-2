package b;
import interfaces.IBQueue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueue<T>  implements IBQueue<T> {
    ReentrantLock enqLock, deqLock;
    AtomicInteger size;
    Node head, tail;
    int capacity;

    String outputE = "";
    String outputD = "";

    public BoundedQueue(int capacity) {
        this.capacity = capacity;
        this.head = new Node(null);
        this.tail = head;
        this.size = new AtomicInteger(0);
        this.enqLock = new ReentrantLock();
        this.deqLock = new ReentrantLock();
    }
    public void enq(T x) {
        boolean mustWakeDequeuers = false;

        System.out.printf("ENQ: '%s' wants lock \n", Thread.currentThread().getName());
        head.nodelock.lock();
        int copyNodeId = head.id;
        outputE = String.format("ENQ: '%s' gets lock node-id-%d \n", Thread.currentThread().getName(), copyNodeId);System.out.print(outputE);

        try {
            while (size.get() == capacity) {
                System.out.printf("ENQ: '%s' awaits notFullCondition \n", Thread.currentThread().getName());
                head.nodeCondition.await();
            }
            Node e = new Node(x);
            tail.next = e;
            tail = tail.next;
            System.out.printf("ENQ: '%s' added a new node %d, id-%d \n", Thread.currentThread().getName(), (int) x, e.id);

            if (size.getAndIncrement() == 0) {
                mustWakeDequeuers = true;
                System.out.printf("ENQ: '%s' must wake dequeuers \n", Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(copyNodeId != head.id) {
                System.out.printf("ENQ: '%s' may be about to release lock id-%d, but has lock for node-id-%d \n", Thread.currentThread().getName(), head.id, copyNodeId);
            } else {
                System.out.printf("ENQ: '%s' releases lock \n", Thread.currentThread().getName());
            }
            head.nodelock.unlock();
        }

        if (mustWakeDequeuers) {
            System.out.printf("ENQ: '%s' wants DEQ-lock \n", Thread.currentThread().getName());
            tail.nodelock.lock();
            copyNodeId = tail.id;
            outputD = String.format("ENQ: '%s' gets DEQ-lock node-id-%d\n", Thread.currentThread().getName(), copyNodeId); System.out.println(outputD);

            try {
                tail.nodeCondition.signalAll();
            } finally {
                if(copyNodeId != tail.id) {
                    System.out.printf("ENQ: '%s' may be about to release DEQ-lock id-%d, but has lock for node-id-%d \n", Thread.currentThread().getName(), tail.id, copyNodeId);
                } else {
                    System.out.printf("ENQ: '%s' releases DEQ-lock \n", Thread.currentThread().getName());
                }

                tail.nodelock.unlock();
            }
        }
    }

    public T deq() {
        boolean mustWakeEnqueuers = false;
        T v = null;

        System.out.printf("DEQ: '%s' wants lock \n", Thread.currentThread().getName());
        tail.nodelock.lock();
        int copyNodeId = tail.id;
        outputD = String.format("DEQ: '%s' gets lock node-id-%d\n", Thread.currentThread().getName(), copyNodeId); System.out.println(outputD);

        try {
            while (head.next == null) {
                System.out.printf("DEQ: '%s' awaits notEmptyCondition \n", Thread.currentThread().getName());
                tail.nodeCondition.await();
            }
            v = head.next.value;    int nId = head.next.id;
            head = head.next;

            System.out.printf("DEQ: '%s' removes node %d, id-%d \n", Thread.currentThread().getName(), (int) v, nId);

            if (size.getAndDecrement() == capacity) {
                mustWakeEnqueuers = true;
                System.out.printf("DEQ: '%s' must wake enqueuers \n", Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(copyNodeId != tail.id) {
                System.out.printf("DEQ: '%s' may be about to release lock id-%d, but has lock for node-id-%d \n", Thread.currentThread().getName(), tail.id, copyNodeId);
            } else {
                System.out.printf("DEQ: '%s' releases lock \n", Thread.currentThread().getName());
            }
            tail.nodelock.unlock();
        }

        if (mustWakeEnqueuers) {
            System.out.printf("DEQ: '%s' wants ENQ-lock \n", Thread.currentThread().getName());
            head.nodelock.lock();
            copyNodeId = head.id;
            outputE = String.format("DEQ: '%s' gets ENQ-lock node-id-%d\n", Thread.currentThread().getName(), copyNodeId); System.out.println(outputE);

            try {
                head.nodeCondition.signalAll();
            } finally {
                if(copyNodeId != head.id) {
                    System.out.printf("DEQ: '%s' may be about to release ENQ-lock id-%d, but has lock for node-id-%d \n", Thread.currentThread().getName(), head.id, copyNodeId);
                } else {
                    System.out.printf("DEQ: '%s' releases ENQ-lock \n", Thread.currentThread().getName());
                }
                head.nodelock.unlock();
            }
        }

        return v;

    }

    protected class Node {
        private static final AtomicInteger counter = new AtomicInteger();
        final int id;

        public T value;
        public Node next;
        public ReentrantLock nodelock;
        public Condition nodeCondition;

        public Node(T x) {
            value = x;
            next = null;
            nodelock = new ReentrantLock();
            nodeCondition = nodelock.newCondition();

            id = counter.getAndIncrement();
        }
    }
}