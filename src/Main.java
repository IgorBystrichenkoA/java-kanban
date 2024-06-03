import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        createTest(manager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println("Id: " + task.getId() + "; name: " + task.getName() + ";");
        }
    }

    private static void createTest(TaskManager taskManager) {
        Task task1 = taskManager.createTask(new Task("Задача 1", "Описание 1.1", Status.NEW));
        System.out.println("Create task " + task1);

        Task task2 = taskManager.createTask(new Task("Задача 2", "Описание 2.1", Status.IN_PROGRESS));
        System.out.println("Create task " + task2);

        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", "Эпик с 3 подзадачами"));
        System.out.println("Create epic " + epic1);

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1.1",
                Status.IN_PROGRESS, epic1));
        System.out.println("Create subtask " + subtask1);

        Subtask subtask2 = taskManager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2.1",
                Status.NEW, epic1));
        System.out.println("Create subtask " + subtask2);

        Subtask subtask3 = taskManager.createSubtask(new Subtask("Подзадача 3", "Описание подзадачи 3.1",
                Status.NEW, epic1));
        System.out.println("Create subtask " + subtask3);

        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", "Эпик без подзадач"));
        System.out.println("Create epic " + epic2);


        System.out.println();
    }
}
