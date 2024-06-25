package converter;

import model.Task;

import java.time.Duration;

public class TaskConverter {
    public static String toString(Task task) {
        // id,type,name,status,description,epic,duration,startTime
        Duration duration = task.getDuration();
        return task.getId() + "," +
                task.getType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getEpicId() + "," +
                (duration == null ? null : duration.toMinutes()) + "," +
                task.getStartTime();

    }
}
