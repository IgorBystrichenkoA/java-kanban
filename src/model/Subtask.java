package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    public Subtask(Integer id, String name, String description, Status status, Epic epic) {
        super(id, name, description, status);
        this.epic = epic;
    }

    public Subtask(String name, String description, Status status, Epic epic, LocalDateTime startTime,
                    Duration duration) {
        super(name, description, status, startTime, duration);
        this.epic = epic;
    }

    public Subtask(Integer id, String name, String description, Status status, Epic epic, LocalDateTime startTime,
                    Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public Integer getEpicId() {
        return epic.getId();
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epic.getId=" + epic.getId() +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
