package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Менеджер задач")
class InMemoryTaskManagerTest {

    static TaskManager taskManager;

    @BeforeEach
    void setUp() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    @DisplayName("При создании задачи, эпика и подзадечи должен увеличиваться счетчик id")
    void shouldIncreaseIdCounterWhenCreate() {
        Task task1 = taskManager.createTask(new Task("1", "", Status.NEW));
        Task task2 = taskManager.createTask(new Task("2", "", Status.NEW));
        assertEquals(1, task2.getId() - task1.getId(), "Неправильное поведение счетчика id" +
                "при добавлении Task");

        Epic epic1 = taskManager.createEpic(new Epic("", ""));
        Epic epic2 = taskManager.createEpic(new Epic("", ""));
        assertEquals(1, epic2.getId() - epic1.getId(), "Неправильное поведение счетчика id" +
                "при добавлении Epic");

        Subtask subtask1 = taskManager.createSubtask(new Subtask("1", "", Status.NEW, epic1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("2", "", Status.NEW, epic2));
        assertEquals(1, subtask2.getId() - subtask1.getId(), "Неправильное поведение счетчика id" +
                "при добавлении Subtask");
    }

    @Test
    @DisplayName("Если подзадачу нельзя создать, счетсик id не должен быть увеличен")
    void shouldReturnNullWhenSubtaskNotCreate() {
        Subtask subtask = taskManager.createSubtask(new Subtask("1", "", Status.NEW,
                new Epic("", "")));

        assertNull(subtask, "При невозможности создать подзадачу функция должна возвращать null");
    }

    @Test
    @DisplayName("Если подзадачу нельзя создать, счетсик id не должен быть увеличен")
    void shouldNotIncreaseIdCounterWhenSubtaskNotCreate() {
        Subtask subtask = taskManager.createSubtask(new Subtask("1", "", Status.NEW,
                new Epic("", "")));
        Task task = taskManager.createTask(new Task("1", "", Status.NEW));
        int id = task.getId();

        assertEquals(1, id, "При отмене создания подзадачи счетчик был изменен");
    }

    @Test
    @DisplayName("При удалении всех эпиков должны удаляться и все подзадачи")
    void shouldRemoveAllSubtasksWhenRemoveAllEpics() {
        Epic epic1 = taskManager.createEpic(new Epic("", ""));
        Epic epic2 = taskManager.createEpic(new Epic("", ""));
        taskManager.createSubtask(new Subtask("1", "", Status.NEW, epic1));
        taskManager.createSubtask(new Subtask("2", "", Status.NEW, epic2));
        taskManager.removeAllEpics();

        assertEquals(0, taskManager.getAllSubtasks().size(),
                "При удалении эпиков не были удалены подзадачи");
    }

    @Test
    @DisplayName("При удалении всех подзадач они должны удаляться и у всех эпиков, при этом эпики должны быть обновлены")
    void shouldUpdateEpicsWhenRemoveAllSubtasks() {
        Epic epic1 = taskManager.createEpic(new Epic("", ""));
        Epic epic2 = taskManager.createEpic(new Epic("", ""));
        taskManager.createSubtask(new Subtask("1", "", Status.NEW, epic1));
        taskManager.createSubtask(new Subtask("2", "", Status.NEW, epic2));

        taskManager.removeAllSubtasks();
        Collection<Epic> epics = taskManager.getAllEpics();

        for (Epic epic : epics) {
            assertEquals(0, epic.getSubtasks().size(),
                    "После удаления подзадач, они удалились не у всех эпиков");
        }
    }

    @Test
    @DisplayName("При получении задач, эпиков или подзадач по id они должны добавляться в историю")
    void shouldAddTasksInHistoryWhenGetById() {
        Task task = taskManager.createTask(new Task(1, "1", "", Status.NEW));
        Epic epic = taskManager.createEpic(new Epic(2, "", ""));
        Subtask subtask = taskManager.createSubtask(new Subtask(3,"1", "", Status.NEW, epic));

        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(3);

        List<Task> historyList = taskManager.getHistory().stream().toList();

        assertEquals(task, historyList.get(0),"Task при получении по id не был добавлен в историю");
        assertEquals(epic, historyList.get(1),"Epic при получении по id не был добавлен в историю");
        assertEquals(subtask, historyList.get(2),"Subtask при получении по id не был добавлен в историю");
    }

}