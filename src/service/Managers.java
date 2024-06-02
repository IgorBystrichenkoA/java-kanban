package service;

import java.nio.file.Paths;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(getDefaultHistory(), Paths.get("resources/task.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
