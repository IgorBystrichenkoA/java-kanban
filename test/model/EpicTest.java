package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Эпик")
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
    @DisplayName("При обновлении эпика если список подзадач пуст, статус должен установиться на NEW")
    public void shouldSetStatusNewWhenUpdateAndZeroSubtasks() {
        Subtask subtask1 = new Subtask("Test Subtask", "", Status.IN_PROGRESS, epic);
        epic.addSubtask(subtask1);
        epic.updateStatus();

        epic.deleteAllSubtasks();
        epic.updateStatus();
        assertEquals(Status.NEW, epic.getStatus(),"При пустом списке подзадач и обновлении эпика статус не " +
                "устанавливается на NEW");
    }

    @Test
    @DisplayName("Если подзадачи имеют одинаковый статус, он должен устанавливаться и у эпика")
    public void shouldCorrectUpdateStatusWhenAllSubtaskStatusesSame() {
        Subtask subtask1 = new Subtask(1, "Test Subtask", "", Status.IN_PROGRESS, epic);
        Subtask subtask2 = new Subtask(2, "Test Subtask", "", Status.IN_PROGRESS, epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.updateStatus();
        assertEquals(Status.IN_PROGRESS, epic.getStatus(),"Если все подзадачи имеют статус IN_PROGRESS, " +
                "то и эпик должен иметь статус IN_PROGRESS");

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        epic.updateStatus();
        assertEquals(Status.DONE, epic.getStatus(),"Если все подзадачи имеют статус DONE, " +
                "то и эпик должен иметь статус DONE");

        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        epic.updateStatus();
        assertEquals(Status.NEW, epic.getStatus(),"Если все подзадачи имеют статус NEW, " +
                "то и эпик должен иметь статус NEW");
    }

    @Test
    @DisplayName("Если подзадачи имеют не одинаковый статус, то статус эпика должен стать IN_PROGRESS")
    public void shouldSetStatusInProgressWhenSubtaskStatusesDifferent() {
        Subtask subtask1 = new Subtask(1 ,"Test Subtask", "", Status.NEW, epic);
        Subtask subtask2 = new Subtask(2, "Test Subtask", "", Status.DONE, epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.updateStatus();
        assertEquals(Status.IN_PROGRESS, epic.getStatus(),"Статус эпика не установился в IN_PROGRESS при " +
                "разных статусах подзадач");
    }
}