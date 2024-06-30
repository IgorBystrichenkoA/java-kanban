package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import service.HttpTaskServer;
import service.TaskManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager manager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
        gson = HttpTaskServer.getGson();
    }

    protected void sendResponse(HttpExchange exchange, String response, int code) throws IOException {
        byte[] resp = response.getBytes(DEFAULT_CHARSET);
        int length = resp.length;
        if (code == HttpURLConnection.HTTP_NO_CONTENT) {
            length = -1;
        }
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, length);
        if (code != HttpURLConnection.HTTP_NO_CONTENT) {
            exchange.getResponseBody().write(resp);
        }
        exchange.close();
    }

    protected TaskEndpoint getEndpoint(String requestPath, String requestMethod, String initialPath) {
        String[] pathParts = requestPath.split("/");

        if (requestPath.equals(initialPath)) {
            return switch (requestMethod) {
                case "GET" -> TaskEndpoint.GET_ALL;
                case "POST" -> TaskEndpoint.CREATE;
                case "DELETE" -> TaskEndpoint.DELETE_ALL;
                default -> TaskEndpoint.UNKNOWN;
            };
        } else if (pathParts.length == 3 && requestPath.indexOf(initialPath) == 0) {
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