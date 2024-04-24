package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;

    private int seq = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }
//    Методы для каждого из типа задач(Задача/Эпик/Подзадача):
//    a. Получение списка всех задач.
    @Override
    public Collection<Task> getAllTasks() {
        return this.tasks.values();
    }

    @Override
    public Collection<Epic> getAllEpics() {
        return this.epics.values();
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        return this.subtasks.values();
    }

//    b. Удаление всех задач.
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear(); // Каждая подзадача закреплена под эпиком
    }

    @Override
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
    @Override
    public Task getTask(Integer id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(Integer id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

//    d. Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
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
    @Override
    public void updateTask(Task task) {
        Task saved = tasks.get(task.getId());
        if (saved == null) {
            return;
        }
        saved.setName(task.getName());
        saved.setDescription(task.getDescription());
        saved.setStatus(task.getStatus());
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }

    @Override
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
    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Collection<Subtask> epicSubtasks = getEpicSubtasks(id);
        for (Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask deleted = subtasks.get(id);
        Epic epic = deleted.getEpic();
        epic.deleteSubtask(deleted);
        epic.updateStatus();
        subtasks.remove(id);
    }

//    Дополнительные методы:
//    a. Получение списка всех подзадач определённого эпика.
    @Override
    public Collection<Subtask> getEpicSubtasks(int epicId) {
        Collection<Subtask> subtasks = epics.get(epicId).getSubtasks();
        historyManager.addAll(subtasks);
        return subtasks;
    }

    @Override
    public Collection<Task> getHistory() {
        return historyManager.getAll();
    }
}
