package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.Collection;

public interface TaskManager {

    Collection<Task> getAllTasks();

    Collection<Epic> getAllEpics();

    Collection<Subtask> getAllSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTask(Integer id);

    Epic getEpic(Integer id);

    Subtask getSubtask(Integer id);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    Collection<Subtask> getEpicSubtasks(int epicId);

    Collection<Task> getHistory();

    Collection<Task> getPrioritizedTasks();
}
