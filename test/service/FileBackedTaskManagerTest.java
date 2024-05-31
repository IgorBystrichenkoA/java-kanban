package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Менеджер задач с сохранением в файл")
public class FileBackedTaskManagerTest {
    static TaskManager taskManager;
    static Path file;

    @BeforeAll
    static void beforeAll() {
        try {
            URI uri = FileBackedTaskManagerTest.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            file = Paths.get(uri).resolve(Paths.get("test.csv"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        try {
            new FileWriter(file.toFile(), false).close();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при очистке файла в setUp: " + file, e);
        }
    }

    @Test
    @DisplayName("Корректное сохранение задач")
    void shouldSaveTasksCorrect() {
        taskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);

        taskManager.createTask(new Task("TaskName", "TaskDescription", Status.NEW));
        Epic epic1 = taskManager.createEpic(new Epic("EpicName", "EpicDescription"));
        taskManager.createSubtask(new Subtask("SubtaskName", "SubtaskDescription", Status.NEW, epic1));

        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            String line = br.readLine();
            assertEquals("id,type,name,status,description,epic", line,
                    "Ошибка сохранения: шапка таблицы в начале файла не найдена");

            line = br.readLine();
            assertEquals("1,TASK,TaskName,NEW,TaskDescription,null", line,
                    "Ошибка сохранения: задача сохранена некорректно");

            line = br.readLine();
            assertEquals("2,EPIC,EpicName,NEW,EpicDescription,null", line,
                    "Ошибка сохранения: эпик сохранен некорректно");

            line = br.readLine();
            assertEquals("3,SUBTASK,SubtaskName,NEW,SubtaskDescription,2", line,
                    "Ошибка сохранения: эпик сохранен некорректно");

            line = br.readLine();
            assertTrue(line == null || line.isBlank(), "Ошибка сохранения: обнаружены лишние символы");

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при работе с тестовым файлом: " + file, e);
        }
    }

    @Test
    @DisplayName("Корректная загрузка задач")
    void shouldLoadTasksCorrect() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.append("""
                    id,type,name,status,description,epic
                    1,TASK,TaskName,NEW,TaskDescription,null
                    2,EPIC,EpicName,NEW,EpicDescription,null
                    3,SUBTASK,SubtaskName,NEW,SubtaskDescription,2
                    """);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи во временный файл: " + file, e);
        }

        taskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);

        assertEquals(1, taskManager.getTask(1).getId(), "Ошибка загрузки данных из файла");
        assertEquals(2, taskManager.getEpic(2).getId(), "Ошибка загрузки данных из файла");
        assertEquals(3, taskManager.getSubtask(3).getId(), "Ошибка загрузки данных из файла");
    }
}
