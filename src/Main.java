import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        createTest(manager);

        List<Task> tasks = manager.getAllTasks();
        List<Epic> epics = manager.getAllEpics();
        List<Subtask> subtasks = manager.getAllSubtasks();

        Task task = tasks.get((int)(Math.random() * tasks.size()));
        Epic epic = epics.get((int)(Math.random() * epics.size()));
        Subtask subtask = subtasks.get((int)(Math.random() * subtasks.size()));

        task = manager.getTask(task.getId());
        System.out.println("Get task: " + task);
        epic = manager.getEpic(epic.getId());
        System.out.println("Get epic: " + epic);
        subtask = manager.getSubtask(subtask.getId());
        System.out.println("Get subtask: " + subtask);
        task = manager.getTask(task.getId());
        System.out.println("Get task: " + task);

        printHistory(manager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("�������:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
    private static void createTest(TaskManager taskManager) {
        Task task1 = taskManager.createTask(new Task("������ 1", "�������� 1.1", Status.NEW));
        System.out.println("Create task " + task1);

        Task task2 = taskManager.createTask(new Task("������ 2", "�������� 2.1", Status.IN_PROGRESS));
        System.out.println("Create task " + task2);

        Task task3 = taskManager.createTask(new Task("������ 3", "�������� 3.1", Status.DONE));
        System.out.println("Create task " + task3);

        Epic epic1 = taskManager.createEpic(new Epic("���� 1", "�������� ����� 1.1"));
        System.out.println("Create epic " + epic1);

        Subtask subtask1 = taskManager.createSubtask(new Subtask("��������� 1", "�������� ��������� 1.1",
                Status.IN_PROGRESS, epic1));
        System.out.println("Create subtask " + subtask1);

        Subtask subtask2 = taskManager.createSubtask(new Subtask("��������� 2", "�������� ��������� 2.1",
                Status.NEW, epic1));
        System.out.println("Create subtask " + subtask2);

        Epic epic2 = taskManager.createEpic(new Epic("���� 2", "�������� ����� 2.1"));
        System.out.println("Create epic " + epic2);

        Subtask subtask3 = taskManager.createSubtask(new Subtask("��������� 3", "�������� ��������� 3.1",
                Status.DONE, epic2));
        System.out.println("Create subtask " + subtask3);

        System.out.println();
    }

}
