package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    private int seq = 0;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }
//    Методы для каждого из типа задач(Задача/Эпик/Подзадача):
//    a. Получение списка всех задач.
    public List<Task> getAllTasks() {
        return tasks.values().stream().toList();
    }

    public List<Epic> getAllEpics() {
        return epics.values().stream().toList();
    }

    public List<Subtask> getAllSubtasks() {
        return subtasks.values().stream().toList();
    }

//    b. Удаление всех задач.
    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear(); // Каждая подзадача закреплена под эпиком
    }

    public void removeAllSubtasks() {
        // Сперва удалим все подзадачи в эпиках
        for(Map.Entry<Integer, Epic> epicEntry : epics.entrySet()) {
            Epic epic = epicEntry.getValue();
            epic.deleteAllSubtasks();
            epic.updateStatus();
        }
        subtasks.clear();
    }

//    c. Получение по идентификатору.
    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpic(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtask(Integer id) {
        return subtasks.get(id);
    }

//    d. Создание. Сам объект должен передаваться в качестве параметра.
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        Epic epicFromManager = subtask.getEpic();
        // Если предложенного эпика нет в таблице, то подзадачу не создаем
        if (!epics.containsKey(epicFromManager.getId())) {
            return null;
        }
        subtask.setId(generateId());
        epicFromManager.addSubtask(subtask);
        epicFromManager.updateStatus();
        subtask.setEpic(epicFromManager);
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    private int generateId() {
        return ++seq;
    }

//    e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task) {
        Task saved = tasks.get(task.getId());
        if (saved == null) {
            return;
        }
        saved.setName(task.getName());
        saved.setDescription(task.getDescription());
        saved.setStatus(task.getStatus());
    }

    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }

    public void updateSubtask(Subtask subtask) {
        Subtask saved = subtasks.get(subtask.getId());
        if (saved == null) {
            return;
        }
        saved.setName(subtask.getName());
        saved.setDescription(subtask.getDescription());
        saved.setStatus(subtask.getStatus());
        if (!saved.getEpic().equals(subtask.getEpic())) {
            Epic oldEpic = saved.getEpic();
            oldEpic.deleteSubtask(saved);
            oldEpic.updateStatus();

            Epic newEpic = subtask.getEpic();
            newEpic.addSubtask(saved);
            saved.setEpic(newEpic);
        }

        saved.getEpic().updateStatus();

    }

//    f. Удаление по идентификатору.
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        List<Subtask> epicSubtasks = getEpicSubtasks(id);
        for (Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask deleted = subtasks.get(id);
        Epic epic = deleted.getEpic();
        epic.deleteSubtask(deleted);
        epic.updateStatus();
        subtasks.remove(id);
    }

//    Дополнительные методы:
//    a. Получение списка всех подзадач определённого эпика.
    public List<Subtask> getEpicSubtasks(int epicId) {
        return epics.get(epicId).getSubtasks();
    }
}
