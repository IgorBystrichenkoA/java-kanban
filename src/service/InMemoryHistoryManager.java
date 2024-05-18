package service;

import model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    // ћожно было использовать LinkedHashMap, но в задании требовалось самому реализовать его
    private Node first;
    private Node last;
    private final Map<Integer, Node> history = new HashMap<>();
    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public void add(Task task) {
        Node node = history.get(task.getId());
        removeNode(node);
        linkLast(task);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        final Task element = node.item;
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        history.remove(node.item.getId());

        node.item = null;
    }

    private void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        }
        else {
            l.next = newNode;
        }

        history.put(task.getId(), newNode);
    }

    /**
     * ƒобавл€ет несколько задач в историю
     *
     * @param tasks - список задач на добавление. ѕредполагаетс€, что задачи в списке
     * упор€дочены по дате просмотра от самого старого до самого нового.
     */
    @Override
    public void addAll(Collection<? extends Task> tasks) {
        for (Task task : tasks) {
            add(task);
        }
    }

    @Override
    public void remove(int id) {
        Node node = history.remove(id);
        removeNode(node);
    }

    @Override
    public void removeAll(Collection<? extends Task> tasks) {
        for (Task task : tasks) {
            remove(task.getId());
        }
    }

    @Override
    public Collection<Task> getAll() {
        Collection<Task> tasks = new ArrayList<>();
        Node current = first;
        while (current != null) {
            tasks.add(current.item);
            current = current.next;
        }
        return tasks;
    }
}
