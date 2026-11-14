package ua.edu.nupp.kpp.server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class CarDAO {
    public void add(Car car) throws SQLException {
        String sql = "INSERT INTO car (client_id, vin, brand, model, year) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, car.getClientId());
            p.setString(2, car.getVin());
            p.setString(3, car.getBrand());
            p.setString(4, car.getModel());
            p.setInt(5, car.getYear());

            p.executeUpdate();
        }
    }
    public List<Car> getAll() throws SQLException {
        List<Car> list = new ArrayList<>();

        String sql = "SELECT id, client_id, vin, brand, model, year FROM car ORDER BY id";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {

            while (r.next()) {
                Car car = new Car(
                        r.getLong("id"),
                        r.getLong("client_id"),
                        r.getString("vin"),
                        r.getString("brand"),
                        r.getString("model"),
                        r.getInt("year")
                );

                list.add(car);
            }
        }
        return list;
    }
    public Car getById(long id) throws SQLException {
        String sql = "SELECT id, client_id, vin, brand, model, year FROM car WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, id);

            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    return new Car(
                            r.getLong("id"),
                            r.getLong("client_id"),
                            r.getString("vin"),
                            r.getString("brand"),
                            r.getString("model"),
                            r.getInt("year")
                    );
                }
            }
        }
        return null;
    }
    public void update(Car car) throws SQLException {
        String sql = "UPDATE car SET client_id = ?, vin = ?, brand = ?, model = ?, year = ? WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, car.getClientId());
            p.setString(2, car.getVin());
            p.setString(3, car.getBrand());
            p.setString(4, car.getModel());
            p.setInt(5, car.getYear());
            p.setLong(6, car.getId());

            p.executeUpdate();
        }
    }
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM car WHERE id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setLong(1, id);
            p.executeUpdate();
        }
    }
}