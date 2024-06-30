package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.ValidateException;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collection;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String initialPath = "/subtasks";
        TaskEndpoint endpoint = getEndpoint(path, method, initialPath);

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
                sendResponse(exchange, "Такого эндпоинта не существует", HttpURLConnection.HTTP_NOT_FOUND);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        try {
            Subtask subtask = manager.getSubtask(id);
            String response = gson.toJson(subtask);
            sendResponse(exchange, response, HttpURLConnection.HTTP_OK);
        } catch (NotFoundException e) {
            sendResponse(exchange, e.getMessage(), HttpURLConnection.HTTP_NOT_FOUND);
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        Collection<Subtask> subtasks = manager.getAllSubtasks();
        String response = gson.toJson(subtasks);
        sendResponse(exchange, response, HttpURLConnection.HTTP_OK);
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Subtask subtask = gson.fromJson(body, Subtask.class);
            manager.createSubtask(subtask);
            sendResponse(exchange, "", HttpURLConnection.HTTP_CREATED);
        } catch (ValidateException e) {
            sendResponse(exchange, e.getMessage(), HttpURLConnection.HTTP_NOT_ACCEPTABLE);
        }
    }

    private void handleUpdate(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Subtask subtask = gson.fromJson(body, Subtask.class);
            manager.updateSubtask(subtask);
            sendResponse(exchange, "", HttpURLConnection.HTTP_CREATED);
        } catch (ValidateException e) {
            sendResponse(exchange, e.getMessage(), HttpURLConnection.HTTP_NOT_ACCEPTABLE);
        }
    }

    private void handleDelete(HttpExchange exchange, String requestPath) throws IOException {
        String[] pathParts = requestPath.split("/");
        int id = Integer.parseInt(pathParts[pathParts.length - 1]);
        manager.deleteSubtask(id);
        sendResponse(exchange, "", HttpURLConnection.HTTP_NO_CONTENT);
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.removeAllSubtasks();
        sendResponse(exchange, "", HttpURLConnection.HTTP_NO_CONTENT);
    }
}
