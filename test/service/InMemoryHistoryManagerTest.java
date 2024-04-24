package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("�������� �������")
class InMemoryHistoryManagerTest {

    @Test
    @DisplayName("���� ������ ������ ��������, �� ���� ����� ������� ����� ������ �������")
    void shouldDeleteOldItemWhenAddIfSizeMoreThen10() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        for (int id = 1; id <= 11; id++) {
            Task task = new Task(id, "Task " + id, "Description", Status.NEW);
            historyManager.add(task);
        }

        Collection<Task> history = historyManager.getAll();

        assertEquals(InMemoryHistoryManager.DEFAULT_MAX_SIZE, history.size(),
                "������ ������ ���� ������ �������������");

        for (Task task : history) {
            assertNotEquals(1, task.getId(),
                    "����� ����, ��� ������ ������ ��������, �� ���� ��� ������ �� ����� ������ �������");
        }
    }

    @Test
    @DisplayName("��� ���������� ������ �����, ���������� ������� ������ ������������� ������� �������, ������ " +
            "�������� ������ ��������� �������� �� ������ � ���������� ������ ������������� ������� �������")
    void shouldAddLastTasksInAddAll() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        ArrayList<Task> tasks = new ArrayList<>(21);
        for (int id = 1; id <= 20; id++) {
            tasks.add(new Task(id, "Task " + id, "Description", Status.NEW));
        }

        historyManager.addAll(tasks);
        Collection<Task> history = historyManager.getAll();

        assertEquals(InMemoryHistoryManager.DEFAULT_MAX_SIZE, history.size(),
                "������ ������ ���� ������ �������������");

        int id = 11;
        for (Task task : history) {
            assertEquals(id++, task.getId(),"����������� ����������� ���������� ��������� ����� " +
                    "�� ������, ������������� � addAll");
        }
    }
}