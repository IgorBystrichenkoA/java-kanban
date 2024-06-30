package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import model.Epic;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collection;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String initialPath = "/epics";
        String endpoint = getEndpoint(path, method, initialPath).name();

        if (endpoint.equals("UNKNOWN") && checkGetSubtasks(path, method)) {
            endpoint = "GET_SUBTASKS";
        }

        switch (endpoint) {
            case "GET": {
                handleGet(exchange);
                break;
            }
            case "GET_ALL": {
                handleGetAll(exchange);
                break;
            }
            case "GET_SUBTASKS": {
                handleGetSubtasks(exchange);
                break;
            }
            case "CREATE": {
                handleCreate(exchange);
                break;
            }
            case "UPDATE": {
                handleUpdate(exchange);
                break;
            }
            case "DELETE": {
                handleDelete(exchange, path);
                break;
            }
            case "DELETE_ALL": {
                handleDeleteAll(exchange);
                break;
            }
            default:
                sendResponse(exchange, "Такого эндпоинта не существует", HttpURLConnection.HTTP_NOT_FOUND);
        }
    }

    private boolean checkGetSubtasks(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        return pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks");
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        try {
            Epic epic = manager.getEpic(id);
            String response = gson.toJson(epic);
            sendResponse(exchange, response, HttpURLConnection.HTTP_OK);
        } catch (NotFoundException e) {
            sendResponse(exchange, e.getMessage(), HttpURLConnection.HTTP_NOT_FOUND);
        }
    }


    private void handleGetAll(HttpExchange exchange) throws IOException {
        Collection<Epic> epics = manager.getAllEpics();
        String response = gson.toJson(epics);
        sendResponse(exchange, response, HttpURLConnection.HTTP_OK);
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        try {
            Collection<Subtask> subtasks = manager.getEpicSubtasks(id);
            String response = gson.toJson(subtasks);
            sendResponse(exchange, response, HttpURLConnection.HTTP_OK);
        } catch (NotFoundException e) {
            sendResponse(exchange, e.getMessage(), HttpURLConnection.HTTP_NOT_FOUND);
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Epic epic = gson.fromJson(body, Epic.class);
        manager.createEpic(epic);
        sendResponse(exchange, "", HttpURLConnection.HTTP_CREATED);
    }

    private void handleUpdate(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Epic epic = gson.fromJson(body, Epic.class);
        manager.updateEpic(epic);
        sendResponse(exchange, "", HttpURLConnection.HTTP_CREATED);

    }

    private void handleDelete(HttpExchange exchange, String requestPath) throws IOException {
        String[] pathParts = requestPath.split("/");
        int id = Integer.parseInt(pathParts[pathParts.length - 1]);
        manager.deleteEpic(id);
        sendResponse(exchange, "", HttpURLConnection.HTTP_NO_CONTENT);
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.removeAllEpics();
        sendResponse(exchange, "", HttpURLConnection.HTTP_NO_CONTENT);
    }

}
