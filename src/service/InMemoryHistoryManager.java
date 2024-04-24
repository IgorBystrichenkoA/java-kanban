package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public final int DEFAULT_MAX_SIZE = 10;
    private final LinkedList<Task> tasks = new LinkedList<>();
    private final int maxSize;


    public InMemoryHistoryManager() {
        this.maxSize = DEFAULT_MAX_SIZE;
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
     * ��������� ��������� ����� � �������
     *
     * @param tasks - ������ ����� �� ����������. ��������������, ��� ������ � ������
     * ����������� �� ���� ��������� �� ������ ������� �� ������ ������.
     */
    @Override
    public void addAll(List<? extends Task> tasks) {
        // ����� �� ��������� ������ �������� ���������� ��� ������� ������� ������ ������������� ������� �������
        // ����� ��������� ����� ����� �������, ������� ����� ��������� � �������
        int i = tasks.size() - maxSize;
        if (i < 0) {
            i = 0;
        }

        for (; i < tasks.size(); i++) {
            add(tasks.get(i));
        }
    }

    @Override
    public List<Task> getAll() {
        return tasks;
    }
}
