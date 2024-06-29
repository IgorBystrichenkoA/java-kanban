package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.ValidateException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collection;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        TaskEndpoint endpoint = getEndpoint(path, method);

        switch (endpoint) {
            case GET: {
                handleGet(exchange);
                break;
            }
            case GET_ALL: {
                handleGetAll(exchange);
                break;
            }
            case CREATE: {
                handleCreate(exchange);
                break;
            }
            case UPDATE: {
                handleUpdate(exchange);
                break;
            }
            case DELETE: {
                handleDelete(exchange, path);
                break;
            }
            case DELETE_ALL: {
                handleDeleteAll(exchange);
                break;
            }
            default:
                sendResponce(exchange, "Такого эндпоинта не существует", HttpURLConnection.HTTP_NOT_FOUND);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        try {
            Task task = manager.getTask(id);
            String response = gson.toJson(task);
            sendResponce(exchange, response, HttpURLConnection.HTTP_OK);
        } catch (NotFoundException e) {
            sendResponce(exchange, e.getMessage(), HttpURLConnection.HTTP_NOT_FOUND);
        }
    }


    private void handleGetAll(HttpExchange exchange) throws IOException {
        Collection<Task> tasks = manager.getAllTasks();
        String response = gson.toJson(tasks);
        sendResponce(exchange, response, HttpURLConnection.HTTP_OK);
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Task task = gson.fromJson(body, Task.class);
            manager.createTask(task);
            sendResponce(exchange, "", HttpURLConnection.HTTP_CREATED);
        } catch (ValidateException e) {
            sendResponce(exchange, e.getMessage(), HttpURLConnection.HTTP_NOT_ACCEPTABLE);
        }
    }

    private void handleUpdate(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Task task = gson.fromJson(body, Task.class);
            manager.updateTask(task);
            sendResponce(exchange, "", HttpURLConnection.HTTP_CREATED);
        } catch (ValidateException e) {
            sendResponce(exchange, e.getMessage(), HttpURLConnection.HTTP_NOT_ACCEPTABLE);
        }
    }

    private void handleDelete(HttpExchange exchange, String requestPath) throws IOException {
        String[] pathParts = requestPath.split("/");
        int id = Integer.parseInt(pathParts[pathParts.length - 1]);
        manager.deleteTask(id);
        sendResponce(exchange, "", HttpURLConnection.HTTP_NO_CONTENT);
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.removeAllTasks();
        sendResponce(exchange, "", HttpURLConnection.HTTP_NO_CONTENT);
    }

    private TaskEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            return switch (requestMethod) {
                case "GET" -> TaskEndpoint.GET_ALL;
                case "POST" -> TaskEndpoint.CREATE;
                case "DELETE" -> TaskEndpoint.DELETE_ALL;
                default -> TaskEndpoint.UNKNOWN;
            };
        } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            return switch (requestMethod) {
                case "GET" -> TaskEndpoint.GET;
                case "POST" -> TaskEndpoint.UPDATE;
                case "DELETE" -> TaskEndpoint.DELETE;
                default -> TaskEndpoint.UNKNOWN;
            };
        }
        return TaskEndpoint.UNKNOWN;
    }
}