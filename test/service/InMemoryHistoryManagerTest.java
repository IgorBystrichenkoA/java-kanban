package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("ћенеджер историй")
class InMemoryHistoryManagerTest {

    @Test
    @DisplayName("≈сли размер списка исчерпан, из него нужно удалить самый старый элемент")
    void shouldDeleteOldItemWhenAddIfSizeMoreThen10() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        for (int id = 1; id <= 11; id++) {
            Task task = new Task(id, "Task " + id, "Description", Status.NEW);
            historyManager.add(task);
        }

        Collection<Task> history = historyManager.getAll();

        assertEquals(InMemoryHistoryManager.DEFAULT_MAX_SIZE, history.size(),
                "–азмер списка стал больше максимального");

        for (Task task : history) {
            assertNotEquals(1, task.getId(),
                    "ѕосле того, как размер списка исчерпан, из него был удален не самый старый элемент");
        }
    }

    @Test
    @DisplayName("ѕри добавлении списка задач, количество которых больше максимального размера истории, должны " +
            "остатьс€ только последние элементы из списка в количестве равном максимальному размеру истории")
    void shouldAddLastTasksInAddAll() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        ArrayList<Task> tasks = new ArrayList<>(21);
        for (int id = 1; id <= 20; id++) {
            tasks.add(new Task(id, "Task " + id, "Description", Status.NEW));
        }

        historyManager.addAll(tasks);
        Collection<Task> history = historyManager.getAll();

        assertEquals(InMemoryHistoryManager.DEFAULT_MAX_SIZE, history.size(),
                "–азмер списка стал больше максимального");

        int id = 11;
        for (Task task : history) {
            assertEquals(id++, task.getId(),"Ќеправильно реализовано добавление последних задач " +
                    "из списка, передаваетого в addAll");
        }
    }
}