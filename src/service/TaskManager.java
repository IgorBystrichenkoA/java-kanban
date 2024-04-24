package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<Subtask> getAllSubtasks();

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

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();
}
