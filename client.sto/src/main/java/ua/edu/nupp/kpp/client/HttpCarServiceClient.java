package ua.edu.nupp.kpp.client;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class HttpCarServiceClient {
    private static final String BASE = "http://localhost:8080/server.sto";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Client> getClients() {
        return Arrays.asList(read(get("/clients"), Client[].class));
    }

    public List<Car> getCars() {
        return Arrays.asList(read(get("/cars"), Car[].class));
    }

    public List<ServiceOrder> getOrders() {
        return Arrays.asList(read(get("/orders"), ServiceOrder[].class));
    }

    public List<Work> getWorks() {
        return Arrays.asList(read(get("/works"), Work[].class));
    }

    public void addClient(Client client) {
        post("/clients", params(
                "action", "add",
                "fullName", client.getFullName(),
                "phone", client.getPhone(),
                "email", client.getEmail()
        ));
    }

    public void updateClient(Client client) {
        post("/clients", params(
                "action", "update",
                "id", String.valueOf(client.getId()),
                "fullName", client.getFullName(),
                "phone", client.getPhone(),
                "email", client.getEmail()
        ));
    }

    public void addCar(Car car) {
        post("/cars", params(
                "action", "add",
                "clientId", String.valueOf(car.getClientId()),
                "vin", car.getVin(),
                "brand", car.getBrand(),
                "model", car.getModel(),
                "year", String.valueOf(car.getYear())
        ));
    }

    public void updateCar(Car car) {
        post("/cars", params(
                "action", "update",
                "id", String.valueOf(car.getId()),
                "clientId", String.valueOf(car.getClientId()),
                "vin", car.getVin(),
                "brand", car.getBrand(),
                "model", car.getModel(),
                "year", String.valueOf(car.getYear())
        ));
    }

    public void addOrder(ServiceOrder order) {
        post("/orders", params(
                "action", "add",
                "carId", String.valueOf(order.getCarId()),
                "description", order.getDescription(),
                "f1", String.valueOf(order.getF1()),
                "f2", String.valueOf(order.getF2())
        ));
    }

    public void updateOrder(ServiceOrder order) {
        post("/orders", params(
                "action", "update",
                "id", String.valueOf(order.getId()),
                "carId", String.valueOf(order.getCarId()),
                "description", order.getDescription(),
                "f1", String.valueOf(order.getF1()),
                "f2", String.valueOf(order.getF2())
        ));
    }

    public void transitionOrder(long id, OrderState state) {
        post("/orders", params(
                "action", "transition",
                "id", String.valueOf(id),
                "state", state.name()
        ));
    }

    public void addWork(Work work) {
        post("/works", params(
                "action", "add",
                "orderId", String.valueOf(work.getOrderId()),
                "name", work.getName(),
                "price", String.valueOf(work.getPrice()),
                "hours", String.valueOf(work.getHours())
        ));
    }

    public void updateWork(Work work) {
        post("/works", params(
                "action", "update",
                "id", String.valueOf(work.getId()),
                "orderId", String.valueOf(work.getOrderId()),
                "name", work.getName(),
                "price", String.valueOf(work.getPrice()),
                "hours", String.valueOf(work.getHours())
        ));
    }

    private Map<String, String> params(String... values) {
        Map<String, String> result = new java.util.LinkedHashMap<>();

        for (int i = 0; i < values.length; i += 2) {
            result.put(values[i], values[i + 1]);
        }

        return result;
    }

    private String get(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .GET()
                .build();
        return send(req);
    }

    public List<Client> deleteClient(Long id) {
        post("/clients", params(
                "action", "delete",
                "id", String.valueOf(id)));
        return getClients();
    }

    public List<Car> deleteCar(long id) {
        String json = post("/cars", params("action", "delete", "id", String.valueOf(id)));
        return Arrays.asList(read(json, Car[].class));
    }

    public List<ServiceOrder> deleteOrder(long id) {
        String json = post("/orders", params("action", "delete", "id", String.valueOf(id)));
        return Arrays.asList(read(json, ServiceOrder[].class));
    }

    public List<Work> deleteWork(long id) {
        String json = post("/works", params("action", "delete", "id", String.valueOf(id)));
        return Arrays.asList(read(json, Work[].class));
    }

    private String post(String path, Map<String, String> params) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form(params)))
                .build();
        return send(req);
    }

    private String form(Map<String, String> params) {
        return params.entrySet().stream()
                .map(e -> enc(e.getKey()) + "=" + enc(e.getValue()))
                .collect(Collectors.joining("&"));
    }

    private String send(HttpRequest req) {
        try {
            HttpResponse<String> r = http.send(req,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (r.statusCode() >= 400) {
                throw new RuntimeException(r.body());
            }
            return r.body();
        } catch (Exception e) {
            throw new RuntimeException("Помилка HTTP: " + e.getMessage(), e);
        }
    }

    private <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Помилка JSON", e);
        }
    }

    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}