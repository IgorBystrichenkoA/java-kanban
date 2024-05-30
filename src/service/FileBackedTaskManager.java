package service;

import converter.TaskConverter;
import model.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;

public class FileBackedTaskManager extends InMemoryTaskManager{
    private final Path file;

    public FileBackedTaskManager(HistoryManager historyManager, Path file) {
        super(historyManager);
        this.file = file;
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

            switch (task.getType()) {
                case TASK:
                    tasks.put(task.getId(), task);
                    break;

                case SUBTASK:
                    subtasks.put(task.getId(), (Subtask) task);
                    break;

                case EPIC:
                    epics.put(task.getId(), (Epic) task);
                    break;
            }
        }
    }

    private InputStream getFileAsInputStream(final Path file)
    {
        return this.getClass()
                .getClassLoader()
                .getResourceAsStream(file.getFileName().toString());
    }

    private Task fromString(String value) {
        // id,type,name,status,description,epic
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

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);

            case EPIC:
                return new Epic(id, name, description);

            case SUBTASK:
                Epic epic = epics.get(epicId);
                return new Subtask(id, name, description, status, epic);
        }

        return null;
    }

    public void save() {
        if (!Files.isRegularFile(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(), StandardCharsets.UTF_8))) {
            writer.append("id,type,name,status,description,epic");
            writer.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(TaskConverter.toString(entry.getValue()));
                writer.newLine();
            }

            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(TaskConverter.toString(entry.getValue()));
                writer.newLine();
            }

            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                writer.append(TaskConverter.toString(entry.getValue()));
                writer.newLine();
            }

            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Îרטבךא ג פאיכו: " + file, e);
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
