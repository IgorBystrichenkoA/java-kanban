package service;

import converter.TaskConverter;
import exception.ManagerSaveException;
import model.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    protected final Path file;
    protected final Map<TaskType, Map<Integer, ? extends Task>> taskMap;

    public FileBackedTaskManager(HistoryManager historyManager, Path file) {
        super(historyManager);
        this.file = file;
        this.taskMap = new HashMap<>();
        taskMap.put(TaskType.TASK, tasks);
        taskMap.put(TaskType.EPIC, epics);
        taskMap.put(TaskType.SUBTASK, subtasks);
        loadFromFile();
    }

    public void loadFromFile() {
        InputStream inputStream = getFileAsInputStream(file);
        if (inputStream == null) {
            return;
        }
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A");

        if (scanner.hasNext()) {
            scanner.nextLine();
        }

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                continue;
            }

            Task task = fromString(line);
            Integer id = task.getId();
            if (seq < id) {
                seq = id;
            }

            @SuppressWarnings("unchecked")
            Map<Integer, Task> temp = (Map<Integer, Task>) taskMap.get(task.getType());
            temp.put(task.getId(), task);
            if (task.getType() != TaskType.EPIC && task.getStartTime() != null && task.getDuration() != null) {
                validateTask(task);
                prioritizedTasks.add(task);
            }
        }

        epics.values().forEach(Epic::update);
    }

    private InputStream getFileAsInputStream(final Path file) {
        return this.getClass().getClassLoader().getResourceAsStream(file.getFileName().toString());
    }

    private Task fromString(String value) {
        // id,type,name,status,description,epic,duration,startTime
        String[] values = value.split(",");

        Integer id = null;
        if (!values[0].equals("null")) {
            id = Integer.parseInt(values[0]);
        }
        TaskType type = TaskType.valueOf(values[1]);
        String name = values[2];
        Status status = Status.valueOf(values[3]);
        String description = values[4];
        Integer epicId = null;
        if (!values[5].equals("null")) {
            epicId = Integer.parseInt(values[5]);
        }
        Duration duration = null;
        if (!values[6].equals("null")) {
            duration = Duration.ofMinutes(Long.parseLong(values[6]));
        }
        LocalDateTime startTime = null;
        if (!values[7].equals("null")) {
            startTime = LocalDateTime.parse(values[7]);
        }

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, startTime, duration);

            case EPIC:
                return new Epic(id, name, description);

            case SUBTASK:
                Epic epic = epics.get(epicId);
                Subtask subtask = new Subtask(id, name, description, status, epic, startTime, duration);
                epic.addSubtask(subtask);
                return subtask;
        }

        return null;
    }

    public void save() {
        if (!Files.isRegularFile(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка при создании файла сохранения: " + file, e);
            }
        }

        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(), StandardCharsets.UTF_8))) {
            writer.append("id,type,name,status,description,epic,duration,startTime");
            writer.newLine();

            Stream.of(tasks.values(), epics.values(), subtasks.values())
                    .flatMap(Collection::stream)
                    .forEach(task -> {
                        try {
                            writer.write(TaskConverter.toString(task));
                            writer.newLine();
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка записи задачи: " + task, e);
                        }
                    });

            writer.flush();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в файле: " + file, e);
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task temp = super.createTask(task);
        save();
        return temp;

    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic temp = super.createEpic(epic);
        save();
        return temp;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask temp = super.createSubtask(subtask);
        save();
        return temp;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }
}
