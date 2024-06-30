package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Epic extends Task {

    private final transient Collection<Subtask> subtasks = new HashSet<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(Integer id, String name, String description) {
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

    public Collection<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void setStatus(Status status) {
    }

    public void update() {

        Set<Status> statuses = subtasks.stream().map(Task::getStatus).collect(Collectors.toSet());
        switch (statuses.size()) {
            case 0: status = Status.NEW; break;
            case 1: status = statuses.iterator().next(); break;
            default: status = Status.IN_PROGRESS;
        }

        startTime = subtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(Comparator.comparing(Task::getStartTime))
                .map(Task::getStartTime).orElse(null);
        endTime = subtasks.stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .max(Comparator.comparing(Task::getEndTime))
                .map(Task::getEndTime).orElse(null);
        if (startTime != null && endTime != null) {
            duration = Duration.between(startTime, endTime);
        } else {
            duration = null;
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasks.size=" + subtasks.size() +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
