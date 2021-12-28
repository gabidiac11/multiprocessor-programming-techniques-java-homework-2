package interfaces;

public interface IBQueue<T> {
    void enq(T x);
    T deq();
}
