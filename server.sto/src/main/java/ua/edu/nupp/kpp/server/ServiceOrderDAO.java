package ua.edu.nupp.kpp.server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class ServiceOrderDAO {
    public void add(ServiceOrder order) throws SQLException {
        String sql = "INSERT INTO service_order " +
                "(car_id, status, description, created_at, f1, f2, f3, risk_index) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            bind(p, order);
            p.executeUpdate();
        }
    }
    public List<ServiceOrder> getAll() throws SQLException {
        List<ServiceOrder> list = new ArrayList<>();

        String sql = "SELECT id, car_id, status, description, created_at, " +
                "f1, f2, f3, risk_index FROM service_order ORDER BY id";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }
    public ServiceOrder getById(long id) throws SQLException {
        String sql = "SELECT id, car_id, status, description, created_at, " +
                "f1, f2, f3, risk_index FROM service_order WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, id);

            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return map(r);
                }
            }
        }
        return null;
    }
    public void update(ServiceOrder order) throws SQLException {
        String sql = "UPDATE service_order SET car_id = ?, status = ?, description = ?, " +
                "created_at = ?, f1 = ?, f2 = ?, f3 = ?, risk_index = ? WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, order);
            p.setLong(9, order.getId());
            p.executeUpdate();
        }
    }
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM service_order WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, id);
            p.executeUpdate();
        }
    }
    private void bind(PreparedStatement p, ServiceOrder order)
            throws SQLException {

        p.setLong(1, order.getCarId());
        p.setString(2, toDbStatus(order.getStatus()));
        p.setString(3, order.getDescription());
        p.setDate(4, java.sql.Date.valueOf(order.getCreatedAt()));
        p.setDouble(5, order.getF1());
        p.setDouble(6, order.getF2());
        p.setDouble(7, order.getF3());
        p.setDouble(8, order.getRiskIndex());
    }

    private ServiceOrder map(ResultSet r) throws SQLException {

        return new ServiceOrder(
                r.getLong("id"),
                r.getLong("car_id"),
                fromDbStatus(r.getString("status")),
                r.getString("description"),
                r.getDate("created_at").toLocalDate(),
                r.getDouble("f1"),
                r.getDouble("f2"),
                r.getDouble("f3"),
                r.getDouble("risk_index")
        );
    }

    private String toDbStatus(OrderState status) {
        return switch (status) {
            case CREATED -> "СТВОРЕНО";
            case DIAGNOSED -> "ДІАГНОСТОВАНО";
            case REPAIR -> "РЕМОНТ";
            case COMPLETED -> "ВИКОНАНО";
        };
    }

    private OrderState fromDbStatus(String status) throws SQLException {
        return switch (status) {
            case "СТВОРЕНО" -> OrderState.CREATED;
            case "ДІАГНОСТОВАНО" -> OrderState.DIAGNOSED;
            case "РЕМОНТ" -> OrderState.REPAIR;
            case "ВИКОНАНО" -> OrderState.COMPLETED;
            default -> throw new SQLException("Невідомий статус замовлення");
        };
    }
    public void updateStatus(long id, OrderState status) throws SQLException {
        String sql = "UPDATE service_order SET status = ? WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, toDbStatus(status));
            p.setLong(2, id);

            p.executeUpdate();
        }
    }
}