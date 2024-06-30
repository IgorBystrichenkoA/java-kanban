package service;

import exception.NotFoundException;
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
    public Task getTask(Integer id) throws NotFoundException {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с id=" + id + " не найдена");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(Integer id) throws NotFoundException {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с id=" + id + " не найден");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(Integer id) throws NotFoundException {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с id=" + id + " не найдена");
        }
        historyManager.add(subtask);
        return subtask;
    }

//    Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public Task createTask(Task task) throws ValidateException {
        Task newTask = new Task(task.getName(), task.getDescription(), task.getStatus());

        newTask.setId(generateId());
        if (task.getStartTime() != null) {
            newTask.setStartTime(task.getStartTime());
            newTask.setDuration(task.getDuration());
            validateTask(newTask);
            prioritizedTasks.add(newTask);
        }
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = new Epic(epic.getName(), epic.getDescription());
        newEpic.setId(generateId());
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws ValidateException {
        Epic epicFromManager = epics.get(subtask.getEpicId());
        // Если предложенного эпика нет в таблице, то подзадачу не создаем
        if (epicFromManager == null) {
            return null;
        }

        Subtask newSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                epicFromManager, subtask.getStartTime(), subtask.getDuration());

        newSubtask.setId(generateId());
        epicFromManager.addSubtask(newSubtask);
        epicFromManager.update();
        newSubtask.setEpic(epicFromManager);
        if (newSubtask.getStartTime() != null) {
            validateTask(newSubtask);
            prioritizedTasks.add(newSubtask);
        }
        subtasks.put(newSubtask.getId(), newSubtask);
        return newSubtask;
    }

    private int generateId() {
        return ++seq;
    }

//    Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) throws ValidateException {
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
    public void updateSubtask(Subtask subtask) throws ValidateException {
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

    private void updateTaskTime(Task saved, Task newTask) throws ValidateException {
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
        try {
            for (Subtask subtask : getEpicSubtasks(id)) {
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
                subtasks.remove(subtask.getId());
            }
        } catch (NotFoundException ignored) { }
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
    public Collection<Subtask> getEpicSubtasks(int epicId) throws NotFoundException {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик с id=" + epicId + " не найден");
        }
        Collection<Subtask> subtasks = epics.get(epicId).getSubtasks();
        historyManager.addAll(subtasks);
        return subtasks;
    }

    @Override
    public Collection<Task> getHistory() {
        return historyManager.getAll();
    }

    protected void validateTask(Task task) throws ValidateException {
        Optional<Task> inter = prioritizedTasks.stream()
                .filter(temp -> !Objects.equals(temp.getId(), task.getId()))
                .filter(temp -> !temp.getStartTime().isAfter(task.getEndTime()))
                .filter(temp -> !task.getStartTime().isAfter(temp.getEndTime()))
                .findAny();

        if (inter.isPresent()) {
            throw new ValidateException("Пересечение с задачей: " + inter.get());
        }
    }

    @Override
    public Collection<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

}
