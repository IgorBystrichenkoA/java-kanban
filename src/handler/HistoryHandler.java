package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collection;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String initialPath = "/history";
        TaskEndpoint endpoint = getEndpoint(path, method, initialPath);

        if (endpoint == TaskEndpoint.GET_ALL) {
            handleGet(exchange);
        } else {
            sendResponse(exchange, "Такого эндпоинта не существует", HttpURLConnection.HTTP_NOT_FOUND);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        Collection<Task> history = manager.getHistory();
        String response = gson.toJson(history);
        sendResponse(exchange, response, HttpURLConnection.HTTP_OK);
    }
}
