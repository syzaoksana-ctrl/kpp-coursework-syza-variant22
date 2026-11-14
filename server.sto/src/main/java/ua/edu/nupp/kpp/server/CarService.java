package ua.edu.nupp.kpp.server;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
public class CarService {
    private static final Logger LOG =
            Logger.getLogger(CarService.class.getName());
    private final CarDAO dao = new CarDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    public List<Car> getAll() throws SQLException {
        return dao.getAll();
    }

    public Car getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Car car) throws SQLException {
        validate(car);
        requireClient(car.getClientId());
        dao.add(car);
        LOG.info("Додано автомобіль: VIN=" + car.getVin()
                + ", clientId=" + car.getClientId());
    }

    public void update(Car car) throws SQLException {
        validate(car);
        requireClient(car.getClientId());
        dao.update(car);
        LOG.info("Оновлено автомобіль: id=" + car.getId());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено автомобіль: id=" + id);
    }

    static void validate(Car car) {
        if (car == null
                || car.getClientId() == null || car.getClientId() <= 0
                || car.getVin() == null || car.getVin().isBlank()
                || car.getBrand() == null || car.getBrand().isBlank()
                || car.getModel() == null || car.getModel().isBlank()
                || car.getYear() == null || car.getYear() <= 0) {
            throw new IllegalArgumentException(
                    "Некоректні дані автомобіля"
            );
        }
    }

    private void requireClient(long id) throws SQLException {
        if (clientDAO.getById(id) == null) {
            throw new IllegalArgumentException("Клієнта не знайдено");
        }
    }
}