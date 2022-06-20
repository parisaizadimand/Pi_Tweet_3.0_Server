package ir.pi.project.server.db;

import java.util.LinkedList;

public interface DBSet<T> {
    T get(int id);
    LinkedList<T> all();
    void update(T t);
}
