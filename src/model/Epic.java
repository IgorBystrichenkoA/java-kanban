package model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Epic extends Task {

    private final Set<Subtask> subtasks = new HashSet<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void deleteSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    public List<Subtask> getSubtasks() {
        return subtasks.stream().toList();
    }

    @Override
    public void setStatus(Status status) {
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            status = Status.NEW;
            return;
        }
        Set<Status> statuses = new HashSet<>();
        for (Subtask subtask : subtasks) {
            statuses.add(subtask.getStatus());
        }
        if (statuses.size() == 1) {
            status = statuses.iterator().next();
        } else {
            status = Status.IN_PROGRESS;
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasks.size=" + subtasks.size() +
                '}';
    }
}
