package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("�������� ����� ����������� � ����")
public class FileBackedTaskManagerTest {
    static TaskManager taskManager;
    static Path file;

    @BeforeEach
    void setUp() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        try {
            file = Files.createTempFile("test", ".csv");
        } catch (IOException e) {
            throw new RuntimeException("�� ������� ������� �������� ����", e);
        }
        taskManager = new FileBackedTaskManager(historyManager, file);
    }

    @AfterEach
    void after() {
        try {
            Files.delete(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("���������� ���������� �����")
    void shouldSaveTasksCorrect() {
        taskManager.createTask(new Task("TaskName", "TaskDescription", Status.NEW));
        Epic epic1 = taskManager.createEpic(new Epic("EpicName", "EpicDescription"));
        taskManager.createSubtask(new Subtask("SubtaskName", "SubtaskDescription", Status.NEW, epic1));

        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            String line = br.readLine();
            assertEquals("id,type,name,status,description,epic", line,
                    "������ ����������: ����� ������� � ������ ����� �� �������");

            line = br.readLine();
            assertEquals("1,TASK,TaskName,NEW,TaskDescription,null", line,
                    "������ ����������: ������ ��������� �����������");

            line = br.readLine();
            assertEquals("2,EPIC,EpicName,NEW,EpicDescription,null", line,
                    "������ ����������: ���� �������� �����������");

            line = br.readLine();
            assertEquals("3,SUBTASK,SubtaskName,NEW,SubtaskDescription,2", line,
                    "������ ����������: ���� �������� �����������");

            line = br.readLine();
            assertTrue(line == null || line.isBlank(), "������ ����������: ���������� ������ �������");

        } catch (IOException e) {
            throw new RuntimeException("������ ��� ������ � �������� ������: " + file, e);
        }
    }

    @Test
    @DisplayName("���������� �������� �����")
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
            throw new RuntimeException("������ ������ �� ��������� ����: " + file, e);
        }

        taskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);

        assertEquals(1, taskManager.getTask(1).getId(), "������ �������� ������ �� �����");
        assertEquals(2, taskManager.getEpic(2).getId(), "������ �������� ������ �� �����");
        assertEquals(3, taskManager.getSubtask(3).getId(), "������ �������� ������ �� �����");
    }
}
