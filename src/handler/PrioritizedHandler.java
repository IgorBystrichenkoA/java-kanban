package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collection;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        TaskEndpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        if (endpoint == TaskEndpoint.GET) {
            handleGet(exchange);
        } else {
            sendResponce(exchange, "Такого эндпоинта не существует", HttpURLConnection.HTTP_NOT_FOUND);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        Collection<Task> prioritized = manager.getPrioritizedTasks();
        String response = gson.toJson(prioritized);
        sendResponce(exchange, response, HttpURLConnection.HTTP_OK);
    }

    private TaskEndpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("prioritized")) {
            if (requestMethod.equals("GET")) {
                return TaskEndpoint.GET;
            }
        }
        return TaskEndpoint.UNKNOWN;
    }
}
