package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("����")
class EpicTest {
    TaskManager taskManager;
    Epic epic;

    @BeforeEach
    void beforeEach() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        epic = new Epic("Test Epic", "Descriprion");
    }

    @Test
    @DisplayName("��� ���������� ����� ���� ������ �������� ����, ������ ������ ������������ �� NEW")
    public void shouldSetStatusNewWhenUpdateAndZeroSubtasks() {
        Subtask subtask1 = new Subtask("Test Subtask", "", Status.IN_PROGRESS, epic);
        epic.addSubtask(subtask1);
        epic.updateStatus();

        epic.deleteAllSubtasks();
        epic.updateStatus();
        assertEquals(Status.NEW, epic.getStatus(),"��� ������ ������ �������� � ���������� ����� ������ �� " +
                "��������������� �� NEW");
    }

    @Test
    @DisplayName("���� ��������� ����� ���������� ������, �� ������ ��������������� � � �����")
    public void shouldCorrectUpdateStatusWhenAllSubtaskStatusesSame() {
        Subtask subtask1 = new Subtask("Test Subtask", "", Status.IN_PROGRESS, epic);
        Subtask subtask2 = new Subtask("Test Subtask", "", Status.IN_PROGRESS, epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.updateStatus();
        assertEquals(Status.IN_PROGRESS, epic.getStatus(),"���� ��� ��������� ����� ������ IN_PROGRESS, " +
                "�� � ���� ������ ����� ������ IN_PROGRESS");

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        epic.updateStatus();
        assertEquals(Status.DONE, epic.getStatus(),"���� ��� ��������� ����� ������ DONE, " +
                "�� � ���� ������ ����� ������ DONE");

        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        epic.updateStatus();
        assertEquals(Status.NEW, epic.getStatus(),"���� ��� ��������� ����� ������ NEW, " +
                "�� � ���� ������ ����� ������ NEW");
    }

    @Test
    @DisplayName("���� ��������� ����� �� ���������� ������, �� ������ ����� ������ ����� IN_PROGRESS")
    public void shouldSetStatusInProgressWhenSubtaskStatusesDifferent() {
        Subtask subtask1 = new Subtask("Test Subtask", "", Status.NEW, epic);
        Subtask subtask2 = new Subtask("Test Subtask", "", Status.DONE, epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.updateStatus();
        assertEquals(Status.IN_PROGRESS, epic.getStatus(),"������ ����� �� ����������� � IN_PROGRESS ��� " +
                "������ �������� ��������");
    }
}