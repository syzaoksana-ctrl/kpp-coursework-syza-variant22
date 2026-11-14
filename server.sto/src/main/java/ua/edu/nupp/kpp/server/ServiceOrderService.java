package ua.edu.nupp.kpp.server;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
public class ServiceOrderService {
    private static final Logger LOG =
            Logger.getLogger(ServiceOrderService.class.getName());
    private final ServiceOrderDAO dao = new ServiceOrderDAO();
    private final CarDAO carDAO = new CarDAO();
    public List<ServiceOrder> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(ServiceOrder order) throws SQLException {
        if (order == null) {
            throw new IllegalArgumentException("Замовлення не може бути null");
        }

        order.setId(null);
        order.setStatus(OrderState.CREATED);
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDate.now());
        }

        validate(order);
        requireCar(order.getCarId());
        applyIndicators(order);

        dao.add(order);
        LOG.info("Додано сервісне замовлення: carId=" + order.getCarId()
                + ", стан=" + order.getStatus()
                + ", riskIndex=" + order.getRiskIndex());
    }

    public void update(ServiceOrder order) throws SQLException {
        if (order == null || order.getId() == null || order.getId() <= 0) {
            throw new IllegalArgumentException("Некоректний id замовлення");
        }

        ServiceOrder existing = dao.getById(order.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Замовлення не знайдено");
        }

        order.setStatus(existing.getStatus());
        order.setCreatedAt(existing.getCreatedAt());

        validate(order);
        requireCar(order.getCarId());
        applyIndicators(order);

        dao.update(order);
        LOG.info("Оновлено сервісне замовлення: id=" + order.getId()
                + ", riskIndex=" + order.getRiskIndex());
    }

    public void delete(long id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Некоректний id замовлення");
        }
        dao.delete(id);
        LOG.info("Видалено сервісне замовлення: id=" + id);
    }

    public void changeState(long id, OrderState newState) throws SQLException {
        if (id <= 0 || newState == null) {
            throw new IllegalArgumentException("Некоректні дані для зміни стану");
        }

        ServiceOrder order = dao.getById(id);
        if (order == null) {
            throw new IllegalArgumentException("Замовлення не знайдено");
        }
        OrderState oldState = order.getStatus();
        if (!isAllowedTransition(oldState, newState)) {
            LOG.warning("Недопустимий перехід стану: id=" + id
                    + ", " + oldState + " -> " + newState);

            throw new IllegalArgumentException(
                    "Перехід зі стану " + oldState
                            + " у стан " + newState + " заборонений");
        }

        dao.updateStatus(id, newState);
        LOG.info("Змінено стан замовлення: id=" + id
                + ", " + oldState + " -> " + newState);
    }

    public static boolean isAllowedTransition(OrderState from, OrderState to) {
        return (from == OrderState.CREATED && to == OrderState.DIAGNOSED)
                || (from == OrderState.DIAGNOSED && to == OrderState.REPAIR)
                || (from == OrderState.REPAIR && to == OrderState.COMPLETED);
    }

    static void validate(ServiceOrder order) {
        if (order == null
                || order.getCarId() == null
                || order.getCarId() <= 0
                || order.getDescription() == null
                || order.getDescription().isBlank()
                || order.getStatus() == null
                || order.getCreatedAt() == null) {
            throw new IllegalArgumentException("Некоректні дані замовлення");
        }

        RiskCalculator.checkFactor("F1", order.getF1());
        RiskCalculator.checkFactor("F2", order.getF2());
    }

    static void applyIndicators(ServiceOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Замовлення не може бути null");
        }

        double f3 = RiskCalculator.calculateF3(order.getCreatedAt(), LocalDate.now());
        order.setF3(f3);
        order.setRiskIndex(
                RiskCalculator.calculateIntegral(order.getF1(), order.getF2(), f3));
    }

    private void requireCar(long id) throws SQLException {
        if (id <= 0 || carDAO.getById(id) == null) {
            throw new IllegalArgumentException("Автомобіль не знайдено");
        }
    }
}