package service;

import exception.ValidateException;

import java.nio.file.Paths;

public class Managers {
    public static TaskManager getDefault() {
        try {
            return new FileBackedTaskManager(getDefaultHistory(), Paths.get("resources/task.csv"));
        } catch (ValidateException e) {
            throw new RuntimeException("Ошибка валидации данных из файла: resources/task.csv", e);
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
