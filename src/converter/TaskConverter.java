package converter;

import model.Task;

public class TaskConverter {
    public static String toString(Task task) {
        // id,type,name,status,description,epic
        return task.getId() + "," +
                task.getType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getEpicId();

    }
}
