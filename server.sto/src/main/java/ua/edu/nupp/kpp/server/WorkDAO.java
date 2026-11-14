package ua.edu.nupp.kpp.server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class WorkDAO {
    public void add(Work work) throws SQLException {
        String sql = "INSERT INTO work (order_id, name, price, hours) VALUES (?, ?, ?, ?)";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, work.getOrderId());
            p.setString(2, work.getName());
            p.setDouble(3, work.getPrice());
            p.setDouble(4, work.getHours());

            p.executeUpdate();
        }
    }
    public List<Work> getAll() throws SQLException {
        List<Work> list = new ArrayList<>();

        String sql = "SELECT id, order_id, name, price, hours FROM work ORDER BY id";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {

            while (r.next()) {
                Work work = new Work(
                        r.getLong("id"),
                        r.getLong("order_id"),
                        r.getString("name"),
                        r.getDouble("price"),
                        r.getDouble("hours")
                );

                list.add(work);
            }
        }
        return list;
    }
    public Work getById(long id) throws SQLException {
        String sql = "SELECT id, order_id, name, price, hours FROM work WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, id);

            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return new Work(
                            r.getLong("id"),
                            r.getLong("order_id"),
                            r.getString("name"),
                            r.getDouble("price"),
                            r.getDouble("hours")
                    );
                }
            }
        }
        return null;
    }
    public void update(Work work) throws SQLException {
        String sql = "UPDATE work SET order_id = ?, name = ?, price = ?, hours = ? WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, work.getOrderId());
            p.setString(2, work.getName());
            p.setDouble(3, work.getPrice());
            p.setDouble(4, work.getHours());
            p.setLong(5, work.getId());

            p.executeUpdate();
        }
    }
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM work WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, id);
            p.executeUpdate();
        }
    }
}