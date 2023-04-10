package repositories;

import java.util.List;

public interface Repository<T> {
    T get(Object element);

    void add(T element);

    void remove(T... elements);

    void update(T... elements);

    List<T> find(Object... elements);

    List<T> getAll();
}
