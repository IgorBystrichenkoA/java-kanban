package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import exception.NotFoundException;
import exception.ValidateException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    private HttpResponse<String> getResponse(String requestUrl, String method, String body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(requestUrl);
        HttpRequest.Builder builder = HttpRequest.newBuilder(url);
        switch (method) {
            case "GET" -> builder.GET();
            case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(body));
            case "DELETE" -> builder.DELETE();
        }

        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getResponse(String url, String method) throws IOException, InterruptedException {
        return getResponse(url, method, null);
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Testing task",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        String url = "http://localhost:8080/tasks";
        String method = "POST";
        String body = gson.toJson(task);

        HttpResponse<String> response = getResponse(url, method, body);
        List<Task> tasksFromManager = manager.getAllTasks().stream().toList();

        assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test task", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic");

        String url = "http://localhost:8080/epics";
        String method = "POST";
        String body = gson.toJson(epic);

        HttpResponse<String> response = getResponse(url, method, body);
        List<Epic> tasksFromManager = manager.getAllEpics().stream().toList();

        assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());
        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test epic", tasksFromManager.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Эпик 1", "Эпик"));
        Subtask subtask = new Subtask("Test subtask", "Testing subtask", Status.NEW, epic);

        String url = "http://localhost:8080/subtasks";
        String method = "POST";
        String body = gson.toJson(subtask);

        HttpResponse<String> response = getResponse(url, method, body);
        List<Subtask> tasksFromManager = manager.getAllSubtasks().stream().toList();

        assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());
        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test subtask", tasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException, ValidateException {
        Task task1 = new Task("Test task1", "Testing task", Status.NEW);
        Task task2 = new Task("Test task2", "Testing task", Status.NEW);

        manager.createTask(task1);
        manager.createTask(task2);

        String url = "http://localhost:8080/tasks/1";
        String method = "DELETE";

        HttpResponse<String> response = getResponse(url, method);
        List<Task> tasksFromManager = manager.getAllTasks().stream().toList();

        assertEquals(HttpURLConnection.HTTP_NO_CONTENT, response.statusCode());
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test task2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test epic1", "Testing epic");
        Epic epic2 = new Epic("Test epic2", "Testing epic");

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        String url = "http://localhost:8080/epics/1";
        String method = "DELETE";

        HttpResponse<String> response = getResponse(url, method);
        List<Epic> tasksFromManager = manager.getAllEpics().stream().toList();

        assertEquals(HttpURLConnection.HTTP_NO_CONTENT, response.statusCode());
        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test epic2", tasksFromManager.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException, ValidateException {
        Epic epic = manager.createEpic(new Epic("Эпик", "Эпик"));
        Subtask subtask1 = new Subtask("Test subtask1", "Testing subtask", Status.NEW, epic);
        Subtask subtask2 = new Subtask("Test subtask2", "Testing subtask", Status.NEW, epic);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        String url = "http://localhost:8080/subtasks/2";
        String method = "DELETE";

        HttpResponse<String> response = getResponse(url, method);
        List<Subtask> tasksFromManager = manager.getAllSubtasks().stream().toList();

        assertEquals(HttpURLConnection.HTTP_NO_CONTENT, response.statusCode());
        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test subtask2", tasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException, ValidateException, NotFoundException {
        Task task = new Task("Test task1", "Testing task", Status.NEW);
        Epic epic = new Epic(2, "Test epic1", "Testing epic");
        Subtask subtask = new Subtask("Test subtask1", "Testing subtask", Status.NEW, epic);

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        manager.getSubtask(3);
        manager.getTask(1);

        String url = "http://localhost:8080/history";
        String method = "GET";

        HttpResponse<String> response = getResponse(url, method);
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Integer> testHistoryIds = jsonArray.asList().stream()
                .map(item -> item.getAsJsonObject().get("id").getAsInt())
                .toList();

        List<Integer> historyFromManagerIds = manager.getHistory().stream().map(Task::getId).toList();

        assertNotNull(historyFromManagerIds, "История не возвращаются");
        assertEquals(historyFromManagerIds, testHistoryIds, "Возвращаемая история с сервера не соответствует хранимой");
        assertEquals(2, testHistoryIds.size(), "Некорректный размер истории");
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException, ValidateException {
        LocalDateTime some = LocalDateTime.of(2000, 10, 10, 10, 10);
        manager.createTask(new Task("Test task1", "Testing task",
                Status.NEW, some, Duration.ofMinutes(5)));
        manager.createTask(new Task("Test task2", "Testing task",
                Status.NEW, some.minusHours(1), Duration.ofMinutes(5)));
        manager.createTask(new Task("Test task2", "Testing task",
                Status.NEW, some.plusHours(1), Duration.ofMinutes(5)));

        String url = "http://localhost:8080/prioritized";
        String method = "GET";

        HttpResponse<String> response = getResponse(url, method);
        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Integer> testHistoryIds = jsonArray.asList().stream()
                .map(item -> item.getAsJsonObject().get("id").getAsInt())
                .toList();

        List<Integer> prioritizedFromManagerIds = manager.getPrioritizedTasks().stream().map(Task::getId).toList();

        assertNotNull(prioritizedFromManagerIds, "Приоритетные задачи не возвращаются");
        assertEquals(prioritizedFromManagerIds, testHistoryIds, "Возвращаемые задачи с сервера не соответствует хранимым");
        assertEquals(3, testHistoryIds.size(), "Некорректный размер приоритетных задач");
    }
}