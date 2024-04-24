package service;

import model.Task;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public final static int DEFAULT_MAX_SIZE = 10;
    private final LinkedList<Task> tasks = new LinkedList<>();
    private final int maxSize;


    public InMemoryHistoryManager() {
        this(DEFAULT_MAX_SIZE);
    }

    public InMemoryHistoryManager(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void add(Task task) {
        tasks.addLast(task);
        if(tasks.size() > maxSize) {
            tasks.removeFirst();
        }
    }

    /**
     * Добавляет несколько задач в историю
     *
     * @param tasks - список задач на добавление. Предполагается, что задачи в списке
     * упорядочены по дате просмотра от самого старого до самого нового.
     */
    @Override
    public void addAll(Collection<? extends Task> tasks) {
        // Чтобы не выполнять лишние операции добавления при размере массива больше максимального размера истории
        // будем добавлять сразу ровно столько, сколько будет сохранено в истории
        int i = tasks.size() - maxSize;
        if (i < 0) {
            i = 0;
        }

        List<? extends Task> taskList = tasks.stream().toList();
        for (; i < taskList.size(); i++) {
            add(taskList.get(i));
        }
    }

    @Override
    public Collection<Task> getAll() {
        return tasks;
    }
}
