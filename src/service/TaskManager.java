package service;

import exception.NotFoundException;
import exception.ValidateException;
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

    Task getTask(Integer id) throws NotFoundException;

    Epic getEpic(Integer id) throws NotFoundException;

    Subtask getSubtask(Integer id) throws NotFoundException;

    Task createTask(Task task) throws ValidateException;

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask) throws ValidateException;

    void updateTask(Task task) throws ValidateException;

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask) throws ValidateException;

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    Collection<Subtask> getEpicSubtasks(int epicId) throws NotFoundException;

    Collection<Task> getHistory();

    Collection<Task> getPrioritizedTasks();
}
