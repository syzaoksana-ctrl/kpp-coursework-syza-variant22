package ua.edu.nupp.kpp.server;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
public class ClientService {
    private static final Logger LOG =
            Logger.getLogger(ClientService.class.getName());
    private final ClientDAO dao = new ClientDAO();
    public List<Client> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(Client client) throws SQLException {
        validate(client);
        dao.add(client);
        LOG.info("Додано клієнта: " + client.getFullName());
    }

    public void update(Client client) throws SQLException {
        validate(client);
        dao.update(client);
        LOG.info("Оновлено клієнта: id=" + client.getId());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено клієнта: id=" + id);
    }

    static void validate(Client client) {
        if (client == null
                || client.getFullName() == null || client.getFullName().isBlank()
                || client.getPhone() == null || client.getPhone().isBlank()
                || client.getEmail() == null || client.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "Усі поля клієнта є обов'язковими"
            );
        }
    }
}