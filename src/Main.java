import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.TaskManager;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        createTest(taskManager);
        updateTest(taskManager);
        printTest(taskManager);
        deleteTest(taskManager);
        printTest(taskManager);

    }

    private static void createTest(TaskManager taskManager) {
        Task task1 = taskManager.createTask(new Task("Задача 1", "Описание 1.1", Status.NEW));
        System.out.println("Create task " + task1);

        Task task2 = taskManager.createTask(new Task("Задача 2", "Описание 2.1", Status.IN_PROGRESS));
        System.out.println("Create task " + task2);

        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1.1"));
        System.out.println("Create epic " + epic1);

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1.1",
                Status.IN_PROGRESS, epic1));
        System.out.println("Create subtask " + subtask1);

        Subtask subtask2 = taskManager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2.1",
                Status.NEW, epic1));
        System.out.println("Create subtask " + subtask2);

        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", "Описание эпика 2.1"));
        System.out.println("Create epic " + epic2);

        Subtask subtask3 = taskManager.createSubtask(new Subtask("Подзадача 3", "Описание подзадачи 3.1",
                Status.DONE, epic2));
        System.out.println("Create subtask " + subtask3);

        System.out.println();
    }

    private static void printTest(TaskManager taskManager) {
        List<Task> tasks = taskManager.getAllTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }

        List<Epic> epics = taskManager.getAllEpics();
        for (Epic epic : epics) {
            System.out.println(epic);
        }

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        for (Subtask subtask : subtasks) {
            System.out.println(subtask);
        }

        System.out.println();
    }

    private static void updateTest(TaskManager taskManager) {
        List<Task> tasks = taskManager.getAllTasks();
        for (Task taskFromManager : tasks) {
            // Меняем статус на следующий по порядку
            int ord = taskFromManager.getStatus().ordinal();
            ord = (ord + 1) % Status.values().length;
            Task taskUpdated = new Task(taskFromManager.getId(), taskFromManager.getName(),
                    taskFromManager.getDescription(), Status.values()[ord]);
            taskManager.updateTask(taskUpdated);
            System.out.println("Updated " + taskUpdated);
        }

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        for (Subtask subtaskFromManager : subtasks) {
            int ord = subtaskFromManager.getStatus().ordinal();
            ord = (ord + 1) % Status.values().length;
            Subtask subtaskUpdated = new Subtask(subtaskFromManager.getId(), subtaskFromManager.getName(),
                    subtaskFromManager.getDescription(), Status.values()[ord], subtaskFromManager.getEpic());
            taskManager.updateSubtask(subtaskUpdated);
            System.out.println("Updated " + subtaskUpdated);
        }

        System.out.println();
    }

    private static void deleteTest(TaskManager taskManager) {
        Task taskFromManager = taskManager.getAllTasks().get(0);
        taskManager.deleteTask(taskFromManager.getId());
        System.out.println("Deleted task " + taskFromManager);

        Epic epicFromManager = taskManager.getAllEpics().get(1);
        taskManager.deleteEpic(epicFromManager.getId());
        System.out.println("Deleted epic " + epicFromManager);

        Subtask subtaskFromManager = taskManager.getAllSubtasks().get(1);
        taskManager.deleteSubtask(subtaskFromManager.getId());
        System.out.println("Deleted subtask " + subtaskFromManager);

        System.out.println();
    }
}
