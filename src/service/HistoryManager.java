package service;

import model.Task;

import java.util.Collection;

public interface HistoryManager {
    void add(Task task);

    void addAll(Collection<? extends Task> tasks);

    void remove(int id);

    void removeAll(Collection<? extends Task> tasks);

    Collection<Task> getAll();
}
