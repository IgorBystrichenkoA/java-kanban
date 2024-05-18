package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("�������� �������")
class InMemoryHistoryManagerTest {

    @Test
    @DisplayName("��� ���������� ������, ��� ������ ����������� � �����")
    void shouldAddTaskInEnd() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        for (int id = 1; id <= 3; id++) {
            historyManager.add(new Task(id, "Task " + id, "Description", Status.NEW));
        }

        Collection<Task> history = historyManager.getAll();
        int id = 1;
        for (Task task : history) {
            assertEquals(id++, task.getId(),"����������� ����������� ���������� ������: ������� ����� " +
                    "����� ���������� �� �� ����������");
        }
    }

    @Test
    @DisplayName("��� ���������� ������ ����� �� ������ ����������� � ��� �� �������, ��� � � ������������ ������ ")
    void shouldAddTasksInAddAllCorrect() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        ArrayList<Task> tasks = new ArrayList<>(4);
        for (int id = 1; id <= 3; id++) {
            tasks.add(new Task(id, "Task " + id, "Description", Status.NEW));
        }

        historyManager.addAll(tasks);
        Collection<Task> history = historyManager.getAll();

        int id = 1;
        for (Task task : history) {
            assertEquals(id++, task.getId(),"����������� ����������� ���������� ����� " +
                    "�� ������, ������������� � addAll");
        }
    }

    @Test
    @DisplayName("�������� ������������ �������� ������ �� id")
    void shouldRemoveCorrect() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        int testSize = 5;
        for (int id = 1; id <= testSize; id++) {
            historyManager.add(new Task(id, "Task " + id, "Description", Status.NEW));
        }
        historyManager.remove(1);
        historyManager.remove(4);

        Collection<Task> history = historyManager.getAll();
        assertEquals(testSize - 2, history.size(), "������������ �������� ������ �� ������ ������");

        int id = 2;
        for (Task task : history) {
            if (id == 4) {
                id++;
            }
            assertEquals(id++, task.getId(),"����������� ����������� ������� ����� �� id");
        }
    }
}