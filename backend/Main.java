import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Main {

    // Frontend folder
    private static final Path FRONTEND_DIR =
            Paths.get("../frontend").toAbsolutePath().normalize();

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        // Backend API
        server.createContext("/register", Main::register);
        server.createContext("/login", Main::login);
        server.createContext("/items", Main::items);

        // Frontend files
        server.createContext("/", Main::serveFrontend);

        server.setExecutor(null);
        server.start();

        System.out.println("======================================");
        System.out.println("LifeORA Java Backend Started");
        System.out.println("======================================");
        System.out.println("Open LifeORA in your browser:");
        System.out.println("http://localhost:8080");
        System.out.println();
        System.out.println("Keep this CMD window open.");
    }


    // =========================================================
    // REGISTER
    // =========================================================

    private static void register(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        String name = getJsonValue(body, "name");
        String email = getJsonValue(body, "email");
        String password = getJsonValue(body, "password");
        String role = getJsonValue(body, "role");

        if (name.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty() ||
                role.isEmpty()) {

            sendResponse(
                    exchange,
                    400,
                    "{\"error\":\"Please fill all fields\"}"
            );
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {

            // Check if email already exists
            String checkSql =
                    "SELECT user_id FROM Users WHERE email = ?";

            PreparedStatement checkStatement =
                    connection.prepareStatement(checkSql);

            checkStatement.setString(1, email);

            ResultSet result = checkStatement.executeQuery();

            if (result.next()) {

                sendResponse(
                        exchange,
                        409,
                        "{\"error\":\"Email already registered\"}"
                );

                return;
            }

            // Insert user
            String insertSql =
                    "INSERT INTO Users(name,email,password,role) " +
                    "VALUES(?,?,?,?)";

            PreparedStatement statement =
                    connection.prepareStatement(insertSql);

            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.setString(4, role);

            statement.executeUpdate();

            sendResponse(
                    exchange,
                    200,
                    "{\"message\":\"Registration successful\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    500,
                    "{\"error\":\"Database error\"}"
            );
        }
    }


    // =========================================================
    // LOGIN
    // =========================================================

    private static void login(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        String email = getJsonValue(body, "email");
        String password = getJsonValue(body, "password");

        if (email.isEmpty() || password.isEmpty()) {

            sendResponse(
                    exchange,
                    400,
                    "{\"error\":\"Please enter email and password\"}"
            );

            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {

            String sql =
                    "SELECT user_id,name,email,role " +
                    "FROM Users " +
                    "WHERE email=? AND password=?";

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (result.next()) {

                int userId = result.getInt("user_id");
                String name = result.getString("name");
                String userEmail = result.getString("email");
                String role = result.getString("role");

                String json =
                        "{"
                        + "\"userId\":" + userId + ","
                        + "\"name\":\"" + escapeJson(name) + "\","
                        + "\"email\":\"" + escapeJson(userEmail) + "\","
                        + "\"role\":\"" + escapeJson(role) + "\""
                        + "}";

                sendResponse(exchange, 200, json);

            } else {

                sendResponse(
                        exchange,
                        401,
                        "{\"error\":\"Invalid email or password\"}"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    500,
                    "{\"error\":\"Database error\"}"
            );
        }
    }


    // =========================================================
    // ITEMS
    // =========================================================

    private static void items(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        List<String> itemList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {

            String sql =
                    "SELECT item_id,title,description,category," +
                    "price,condition " +
                    "FROM Items " +
                    "WHERE status='Available' " +
                    "ORDER BY item_id DESC";

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                String item =
                        "{"
                        + "\"itemId\":" + result.getInt("item_id") + ","
                        + "\"title\":\"" +
                        escapeJson(result.getString("title")) + "\","
                        + "\"description\":\"" +
                        escapeJson(result.getString("description")) + "\","
                        + "\"category\":\"" +
                        escapeJson(result.getString("category")) + "\","
                        + "\"price\":" + result.getDouble("price") + ","
                        + "\"condition\":\"" +
                        escapeJson(result.getString("condition")) + "\""
                        + "}";

                itemList.add(item);
            }

            String json = "[" + String.join(",", itemList) + "]";

            sendResponse(exchange, 200, json);

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    500,
                    "{\"error\":\"Database error\"}"
            );
        }
    }


    // =========================================================
    // SERVE FRONTEND
    // =========================================================

    private static void serveFrontend(HttpExchange exchange)
            throws IOException {

        String path = exchange.getRequestURI().getPath();

        // Homepage
        if (path.equals("/")) {
            path = "/index.html";
        }

        // Remove first /
        String relativePath = path.substring(1);

        Path filePath =
                FRONTEND_DIR.resolve(relativePath).normalize();

        // Security check
        if (!filePath.startsWith(FRONTEND_DIR)) {
            sendResponse(
                    exchange,
                    403,
                    "Access denied"
            );
            return;
        }

        if (!Files.exists(filePath) ||
                Files.isDirectory(filePath)) {

            sendResponse(
                    exchange,
                    404,
                    "File not found"
            );
            return;
        }

        byte[] fileBytes = Files.readAllBytes(filePath);

        String contentType = getContentType(filePath);

        exchange.getResponseHeaders()
                .set("Content-Type", contentType);

        exchange.sendResponseHeaders(
                200,
                fileBytes.length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(fileBytes);
        output.close();
    }


    // =========================================================
    // CONTENT TYPE
    // =========================================================

    private static String getContentType(Path file) {

        String name =
                file.getFileName().toString().toLowerCase();

        if (name.endsWith(".html")) {
            return "text/html; charset=UTF-8";
        }

        if (name.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }

        if (name.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }

        if (name.endsWith(".jpg") ||
                name.endsWith(".jpeg")) {
            return "image/jpeg";
        }

        if (name.endsWith(".png")) {
            return "image/png";
        }

        if (name.endsWith(".gif")) {
            return "image/gif";
        }

        return "application/octet-stream";
    }


    // =========================================================
    // SEND RESPONSE
    // =========================================================

    private static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response) throws IOException {

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );

        byte[] bytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(bytes);
        output.close();
    }


    // =========================================================
    // READ JSON VALUE
    // =========================================================

    private static String getJsonValue(
            String json,
            String key) {

        String search =
                "\"" + key + "\":\"";

        int start =
                json.indexOf(search);

        if (start == -1) {
            return "";
        }

        start += search.length();

        int end =
                json.indexOf("\"", start);

        if (end == -1) {
            return "";
        }

        String value =
                json.substring(start, end);

        return value
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }


    // =========================================================
    // ESCAPE JSON
    // =========================================================

    private static String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}