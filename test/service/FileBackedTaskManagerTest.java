package service;

import exception.ValidateException;
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
    static void beforeAll() throws URISyntaxException {
        URI uri = FileBackedTaskManagerTest.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        file = Paths.get(uri).resolve(Paths.get("test.csv"));
    }

    @BeforeEach
    void setUp() throws IOException {
        new FileWriter(file.toFile(), false).close();
    }

    @Test
    @DisplayName("Корректное сохранение задач")
    void shouldSaveTasksCorrect() throws IOException, ValidateException {
        taskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);

        taskManager.createTask(new Task("TaskName", "TaskDescription", Status.NEW));
        Epic epic1 = taskManager.createEpic(new Epic("EpicName", "EpicDescription"));
        taskManager.createSubtask(new Subtask("SubtaskName", "SubtaskDescription", Status.NEW, epic1));

        BufferedReader br = new BufferedReader(new FileReader(file.toFile()));
        String line = br.readLine();
        assertEquals("id,type,name,status,description,epic,duration,startTime", line,
                "Ошибка сохранения: шапка таблицы в начале файла не найдена");

        line = br.readLine();
        assertEquals("1,TASK,TaskName,NEW,TaskDescription,null,null,null", line,
                "Ошибка сохранения: задача сохранена некорректно");

        line = br.readLine();
        assertEquals("2,EPIC,EpicName,NEW,EpicDescription,null,null,null", line,
                "Ошибка сохранения: эпик сохранен некорректно");

        line = br.readLine();
        assertEquals("3,SUBTASK,SubtaskName,NEW,SubtaskDescription,2,null,null", line,
                "Ошибка сохранения: эпик сохранен некорректно");

        line = br.readLine();
        assertTrue(line == null || line.isBlank(), "Ошибка сохранения: обнаружены лишние символы");
    }

    @Test
    @DisplayName("Корректная загрузка задач")
    void shouldLoadTasksCorrect() throws IOException, ValidateException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()));
        writer.append("""
                id,type,name,status,description,epic,duration,startTime
                1,TASK,TaskName,NEW,TaskDescription,null,null,null
                2,EPIC,EpicName,NEW,EpicDescription,null,null,null
                3,SUBTASK,SubtaskName,NEW,SubtaskDescription,2,null,null
                """);
        writer.flush();

        taskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);

        assertEquals(1, taskManager.getTask(1).getId(), "Ошибка загрузки данных из файла");
        assertEquals(2, taskManager.getEpic(2).getId(), "Ошибка загрузки данных из файла");
        assertEquals(3, taskManager.getSubtask(3).getId(), "Ошибка загрузки данных из файла");
    }

}
