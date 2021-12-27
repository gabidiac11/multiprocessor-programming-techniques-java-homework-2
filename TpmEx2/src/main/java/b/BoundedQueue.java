package b;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueue<T> {
    ReentrantLock enqLock, deqLock;
    AtomicInteger size;
    Node head, tail;
    int capacity;


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

        System.out.printf("ENQ: Thread: '%s' wants lock \n", Thread.currentThread().getName());
        head.nodelock.lock();
        System.out.printf("ENQ: Thread: '%s' gets lock \n", Thread.currentThread().getName());
        try {
            while (size.get() == capacity) {
                System.out.printf("ENQ: Thread: '%s' awaits notFullCondition \n", Thread.currentThread().getName());
                head.nodeCondition.await();
            }
            Node e = new Node(x);
            tail.next = e;
            tail = tail.next;
            System.out.printf("ENQ: Thread: '%s' added a new node %d \n", Thread.currentThread().getName(), (int) x);

            if (size.getAndIncrement() == 0) {
                mustWakeDequeuers = true;
                System.out.printf("ENQ: Thread: '%s' must wake dequeuers \n", Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            head.nodelock.unlock();
            System.out.printf("ENQ: Thread: '%s' releases lock \n", Thread.currentThread().getName());
        }

        if (mustWakeDequeuers) {
            System.out.printf("ENQ: Thread: '%s' wants DEQ-lock \n", Thread.currentThread().getName());
            tail.nodelock.lock();
            System.out.printf("ENQ: Thread: '%s' gets DEQ-lock \n", Thread.currentThread().getName());
            try {
                tail.nodeCondition.signalAll();
            } finally {
                tail.nodelock.unlock();
                System.out.printf("ENQ: Thread: '%s' releases DEQ-lock \n", Thread.currentThread().getName());
            }
        }
    }

    public T deq() {
        boolean mustWakeEnqueuers = false;
        T v = null;

        System.out.printf("DEQ: Thread: '%s' wants lock \n", Thread.currentThread().getName());
        tail.nodelock.lock();
        System.out.printf("DEQ: Thread: '%s' gets lock \n", Thread.currentThread().getName());

        try {
            while (head.next == null) {
                System.out.printf("DEQ: Thread: '%s' awaits notEmptyCondition \n", Thread.currentThread().getName());
                tail.nodeCondition.await();
            }
            v = head.next.value;
            head = head.next;

            System.out.printf("DEQ: Thread: '%s' removes node %d \n", Thread.currentThread().getName(), (int) v);

            if (size.getAndDecrement() == capacity) {
                mustWakeEnqueuers = true;
                System.out.printf("DEQ: Thread: '%s' must wake enqueuers \n", Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            tail.nodelock.unlock();
            System.out.printf("DEQ: Thread: '%s' releases lock \n", Thread.currentThread().getName());
        }

        if (mustWakeEnqueuers) {
            System.out.printf("DEQ: Thread: '%s' wants ENQ-lock \n", Thread.currentThread().getName());
            head.nodelock.lock();
            System.out.printf("DEQ: Thread: '%s' gets ENQ-lock \n", Thread.currentThread().getName());
            try {
                head.nodeCondition.signalAll();
            } finally {
                head.nodelock.unlock();
                System.out.printf("DEQ: Thread: '%s' releases ENQ-lock \n", Thread.currentThread().getName());
            }
        }

        return v;

    }

    protected class Node {

        public T value;
        public Node next;
        public ReentrantLock nodelock;
        public Condition nodeCondition;

        public Node(T x) {
            value = x;
            next = null;
            nodelock = new ReentrantLock();
            nodeCondition = nodelock.newCondition();
        }
    }
}