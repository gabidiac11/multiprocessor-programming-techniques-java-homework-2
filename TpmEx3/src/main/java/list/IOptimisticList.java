package list;

public interface IOptimisticList<T> {
    boolean add(T item);
    boolean remove(T item);
    boolean contains(T item);
}
