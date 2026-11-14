package ua.edu.nupp.kpp.server;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
public class WorkService {
    private static final Logger LOG =
            Logger.getLogger(WorkService.class.getName());
    private final WorkDAO dao = new WorkDAO();
    private final ServiceOrderDAO serviceOrderDAO = new ServiceOrderDAO();
    public List<Work> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(Work work) throws SQLException {
        validate(work);
        requireOrder(work.getOrderId());
        dao.add(work);
        LOG.info("Додано роботу: " + work.getName()
                + ", orderId=" + work.getOrderId());
    }

    public void update(Work work) throws SQLException {
        validate(work);
        requireOrder(work.getOrderId());
        dao.update(work);
        LOG.info("Оновлено роботу: id=" + work.getId());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено роботу: id=" + id);
    }

    static void validate(Work work) {
        if (work == null
                || work.getOrderId() == null || work.getOrderId() <= 0
                || work.getName() == null || work.getName().isBlank()
                || work.getPrice() < 0
                || work.getHours() <= 0) {
            throw new IllegalArgumentException(
                    "Некоректні дані роботи"
            );
        }
    }

    private void requireOrder(long id) throws SQLException {
        if (serviceOrderDAO.getById(id) == null) {
            throw new IllegalArgumentException("Замовлення не знайдено");
        }
    }
}