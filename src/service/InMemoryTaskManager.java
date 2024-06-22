package service;

import exception.ValidateException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HistoryManager historyManager;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    protected int seq = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

//    Методы для каждого из типа задач(Задача/Эпик/Подзадача):
//    Получение списка всех задач.
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

//    Удаление всех задач.
    @Override
    public void removeAllTasks() {
        historyManager.removeAll(tasks.values());
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        historyManager.removeAll(epics.values());
        historyManager.removeAll(subtasks.values());
        prioritizedTasks.removeAll(subtasks.values());
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        historyManager.removeAll(subtasks.values());
        prioritizedTasks.removeAll(subtasks.values());
        epics.values().stream()
                .peek(Epic::deleteAllSubtasks)
                .forEach(Epic::update);
        subtasks.clear();
    }

//    Получение по идентификатору.
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

//    Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            validateTask(task);
            prioritizedTasks.add(task);
        }
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
        epicFromManager.update();
        subtask.setEpic(epicFromManager);
        if (subtask.getStartTime() != null) {
            validateTask(subtask);
            prioritizedTasks.add(subtask);
        }
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    private int generateId() {
        return ++seq;
    }

//    Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) {
        Task saved = tasks.get(task.getId());
        if (saved == null) {
            return;
        }
        updateTaskTime(saved, task);

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
        updateTaskTime(saved, subtask);

        saved.setName(subtask.getName());
        saved.setDescription(subtask.getDescription());
        saved.setStatus(subtask.getStatus());
        if (!saved.getEpic().equals(subtask.getEpic())) {
            Epic oldEpic = saved.getEpic();
            oldEpic.deleteSubtask(saved);
            oldEpic.update();

            Epic newEpic = subtask.getEpic();
            newEpic.addSubtask(saved);
            saved.setEpic(newEpic);
        }

        saved.getEpic().update();
    }

    private void updateTaskTime(Task saved, Task newTask) {
        if (newTask.getStartTime() != null && newTask.getDuration() != null) {
            validateTask(newTask);
            prioritizedTasks.remove(saved);
            saved.setStartTime(newTask.getStartTime());
            saved.setDuration(newTask.getDuration());
            prioritizedTasks.add(saved);
        } else if (saved.getStartTime() != null) { // Если время начала было удалено
            prioritizedTasks.remove(saved);
        }
    }

//    Удаление по идентификатору.
    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        Task deleted = tasks.remove(id);
        if (deleted != null && deleted.getStartTime() != null) {
            prioritizedTasks.remove(deleted);
        }
    }

    @Override
    public void deleteEpic(int id) {
        historyManager.remove(id);
        for (Subtask subtask : getEpicSubtasks(id)) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        historyManager.remove(id);
        Subtask deleted = subtasks.remove(id);
        if (deleted != null) {
            if (deleted.getStartTime() != null) {
                prioritizedTasks.remove(deleted);
            }
            Epic epic = deleted.getEpic();
            epic.deleteSubtask(deleted);
            epic.update();
        }
    }

//    Дополнительные методы:
//    Получение списка всех подзадач определённого эпика.
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

    protected void validateTask(Task task) {
        Optional<Task> inter = prioritizedTasks.stream().filter(temp -> !Objects.equals(temp.getId(), task.getId()) &&
                !temp.getStartTime().isAfter(task.getEndTime()) &&
                !task.getStartTime().isAfter(temp.getEndTime())
        ).findAny();

        if (inter.isPresent()) {
            throw new ValidateException("Пересечение с задачей: " + inter.get());
        }
    }

    @Override
    public Collection<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

}
