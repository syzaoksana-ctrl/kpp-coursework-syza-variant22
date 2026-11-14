package ua.edu.nupp.kpp.server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class ClientDAO {
    public void add(Client client) throws SQLException {
        String sql = "INSERT INTO client (full_name, phone, email) VALUES (?, ?, ?)";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            p.setString(1, client.getFullName());
            p.setString(2, client.getPhone());
            p.setString(3, client.getEmail());

            p.executeUpdate();

            try (ResultSet keys = p.getGeneratedKeys()) {
                if (keys.next()) {
                    client.setId(keys.getLong(1));
                }
            }
        }
    }
    public List<Client> getAll() throws SQLException {
        List<Client> list = new ArrayList<>();

        String sql = "SELECT id, full_name, phone, email FROM client ORDER BY id";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {

            while (r.next()) {
                Client client = new Client(
                        r.getLong("id"),
                        r.getString("full_name"),
                        r.getString("phone"),
                        r.getString("email")
                );

                list.add(client);
            }
        }
        return list;
    }
    public Client getById(long id) throws SQLException {
        String sql = "SELECT id, full_name, phone, email FROM client WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, id);

            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return new Client(
                            r.getLong("id"),
                            r.getString("full_name"),
                            r.getString("phone"),
                            r.getString("email")
                    );
                }
            }
        }
        return null;
    }
    public void update(Client client) throws SQLException {
        String sql = "UPDATE client SET full_name = ?, phone = ?, email = ? WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, client.getFullName());
            p.setString(2, client.getPhone());
            p.setString(3, client.getEmail());
            p.setLong(4, client.getId());

            p.executeUpdate();
        }
    }
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM client WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, id);
            p.executeUpdate();
        }
    }
}