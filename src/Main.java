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

        manager.getTask(2);
        manager.getTask(1);
        manager.getSubtask(4);
        manager.getEpic(7);
        printHistory(manager);

        manager.getTask(1);
        manager.getSubtask(4);
        manager.getEpic(3);
        printHistory(manager);

        manager.deleteTask(1);
        printHistory(manager);

        manager.deleteEpic(3);
        printHistory(manager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("�������:");
        for (Task task : manager.getHistory()) {
            System.out.println("Id: " + task.getId() + "; name: " + task.getName() + ";");
        }
    }
    private static void createTest(TaskManager taskManager) {
        Task task1 = taskManager.createTask(new Task("������ 1", "�������� 1.1", Status.NEW));
        System.out.println("Create task " + task1);

        Task task2 = taskManager.createTask(new Task("������ 2", "�������� 2.1", Status.IN_PROGRESS));
        System.out.println("Create task " + task2);

        Epic epic1 = taskManager.createEpic(new Epic("���� 1", "���� � 3 �����������"));
        System.out.println("Create epic " + epic1);

        Subtask subtask1 = taskManager.createSubtask(new Subtask("��������� 1", "�������� ��������� 1.1",
                Status.IN_PROGRESS, epic1));
        System.out.println("Create subtask " + subtask1);

        Subtask subtask2 = taskManager.createSubtask(new Subtask("��������� 2", "�������� ��������� 2.1",
                Status.NEW, epic1));
        System.out.println("Create subtask " + subtask2);

        Subtask subtask3 = taskManager.createSubtask(new Subtask("��������� 3", "�������� ��������� 3.1",
                Status.NEW, epic1));
        System.out.println("Create subtask " + subtask3);

        Epic epic2 = taskManager.createEpic(new Epic("���� 2", "���� ��� ��������"));
        System.out.println("Create epic " + epic2);


        System.out.println();
    }

}
