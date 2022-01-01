package d;

import interfaces.IBQueue;

import java.util.concurrent.locks.ReentrantLock;

public class UnboundedQueue<T> implements IBQueue<T> {
    ReentrantLock enqLock, deqLock;
    Node head, tail;

    String outputE = "";
    String outputD = "";

    public void enq(T value) {
        System.out.printf("ENQ: '%s' wants lock \n", Thread.currentThread().getName());
        enqLock.lock();
        outputE = String.format("ENQ: '%s' gets lock \n", Thread.currentThread().getName());System.out.print(outputE);

        try {
            Node newNode = new Node(value);
            tail.next = newNode;
            tail = newNode;

            System.out.printf("ENQ: '%s' added a new node %d \n", Thread.currentThread().getName(), (int) value);
        } finally {
            System.out.printf("ENQ: '%s' releases lock \n", Thread.currentThread().getName());
            enqLock.unlock();
        }
    }

    public T deq() {
        T result = null;

        if (head.next == null) {
                System.out.printf("DEQ: '%s' queue empty \n", Thread.currentThread().getName());
            try {
                throw new Exception("EMPTY EXCEPTION THROWN");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.printf("DEQ: '%s' wants lock \n", Thread.currentThread().getName());
        deqLock.lock();
        outputD = String.format("DEQ: '%s' gets lock \n", Thread.currentThread().getName()); System.out.println(outputD);

        try {
//            if (head.next == null) {
//                System.out.printf("DEQ: '%s' queue empty \n", Thread.currentThread().getName());
//                throw new Exception();
//            }
            result = head.next.value;
            head = head.next;

            System.out.printf("DEQ: '%s' removes node %d \n", Thread.currentThread().getName(), (int) result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.printf("DEQ: '%s' releases lock \n", Thread.currentThread().getName());
            deqLock.unlock();
        }
        return result;
    }

    public UnboundedQueue() {
        head = new Node(null);
        tail = head;
        enqLock = new ReentrantLock();
        deqLock = new ReentrantLock();
    }

    protected class Node {
        public T value;
        public Node next;

        public Node(T value) {
            this.value = value;
            next = null;
        }
    }
}

