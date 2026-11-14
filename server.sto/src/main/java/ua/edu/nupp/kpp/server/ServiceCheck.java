package ua.edu.nupp.kpp.server;
import java.time.LocalDate;
import java.util.List;
public class ServiceCheck {
    private static final boolean KEEP_CONTROL_RECORD = true;
    public static void main(String[] args) throws Exception {

        ClientService clientService = new ClientService();
        CarService carService = new CarService();
        ServiceOrderService orderService = new ServiceOrderService();
        WorkService workService = new WorkService();

        check("Порожнє ім'я клієнта", () ->
                clientService.add(new Client(null, "", "123456789", "test@test.com")));

        check("Порожній VIN автомобіля", () ->
                carService.add(new Car(null, 1L, "", "Toyota", "Camry", 2020)));

        check("F2 більше 100", () ->
                orderService.add(new ServiceOrder(
                        null, 1L, OrderState.CREATED, "Тест",
                        LocalDate.now(), 50, 120, 50, 0
                )));

        check("F3 менше 0", () ->
                RiskCalculator.checkFactor("F3", -1));

        check("Порожня назва роботи", () ->
                workService.add(new Work(null, 1L, "", 100, 2)));

        check("Неіснуючий клієнт", () ->
                carService.add(new Car(null, 999999L, "TESTVIN", "Toyota", "Camry", 2020)));

        check("Неіснуючий автомобіль", () ->
                orderService.add(new ServiceOrder(
                        null, 999999L, OrderState.CREATED, "Тест",
                        LocalDate.now(), 50, 50, 50, 0
                )));

        check("Неіснуюче замовлення", () ->
                workService.add(new Work(null, 999999L, "Тестова робота", 100, 2)));

        check("Зміна стану неіснуючого замовлення", () ->
                orderService.changeState(999999L, OrderState.DIAGNOSED));

        checkControlScenario(orderService);
        checkUpdate(orderService);
        checkLifecycle(orderService);
    }

    private static void checkControlScenario(ServiceOrderService orderService)
            throws Exception {

        ServiceOrderDAO dao = new ServiceOrderDAO();
        CarDAO carDAO = new CarDAO();

        List<Car> cars = carDAO.getAll();
        if (cars.isEmpty()) {
            System.out.println("НЕ ПЕРЕВІРЕНО: Немає автомобілів для контрольного сценарію");
            return;
        }
        long carId = cars.get(0).getId();

        ServiceOrder order = new ServiceOrder();
        order.setCarId(carId);
        order.setDescription("Контрольний сценарій: заміна гальмівних колодок");
        order.setCreatedAt(LocalDate.now().minusDays(15));
        order.setF1(80);
        order.setF2(60);

        orderService.add(order);

        List<ServiceOrder> all = dao.getAll();
        ServiceOrder saved = all.get(all.size() - 1);

        boolean ok = saved.getStatus() == OrderState.CREATED
                && Math.abs(saved.getF1() - 80.0) < 0.001
                && Math.abs(saved.getF2() - 60.0) < 0.001
                && Math.abs(saved.getF3() - 50.0) < 0.001
                && Math.abs(saved.getRiskIndex() - 65.5) < 0.001;

        System.out.println((ok ? "УСПІШНО" : "ПОМИЛКА")
                + ": Контрольний сценарій — id=" + saved.getId()
                + ", статус=" + saved.getStatus()
                + ", F1=" + saved.getF1()
                + ", F2=" + saved.getF2()
                + ", F3=" + saved.getF3()
                + ", I=" + saved.getRiskIndex()
                + " (очікується F3=50.0, I=65.5)");

        if (!KEEP_CONTROL_RECORD) {
            orderService.delete(saved.getId());
            System.out.println("Контрольний запис видалено");
        }
    }

    private static void checkUpdate(ServiceOrderService orderService)
            throws Exception {

        ServiceOrderDAO dao = new ServiceOrderDAO();

        List<ServiceOrder> orders = dao.getAll();

        if (orders.isEmpty()) {
            System.out.println(
                    "НЕ ПЕРЕВІРЕНО: Немає замовлень для тестування update()"
            );
            return;
        }

        ServiceOrder original = orders.get(0);

        OrderState oldStatus = original.getStatus();
        String oldDescription = original.getDescription();
        LocalDate oldDate = original.getCreatedAt();

        try {
            ServiceOrder updated = new ServiceOrder(
                    original.getId(),
                    original.getCarId(),
                    OrderState.COMPLETED,               // спроба змінити стан має ігноруватись
                    oldDescription + " (test)",
                    LocalDate.now().minusDays(10),      // спроба змінити дату має ігноруватись
                    original.getF1(),
                    original.getF2(),
                    0,
                    0
            );

            orderService.update(updated);

            ServiceOrder result = dao.getById(original.getId());

            double expectedF3 = RiskCalculator.calculateF3(oldDate, LocalDate.now());
            double expectedI = RiskCalculator.calculateIntegral(
                    result.getF1(), result.getF2(), expectedF3);

            if (result.getStatus() == oldStatus
                    && result.getCreatedAt().equals(oldDate)
                    && Math.abs(result.getF3() - expectedF3) < 0.01
                    && Math.abs(result.getRiskIndex() - expectedI) < 0.01) {

                System.out.println(
                        "УСПІШНО: Редагування замовлення — стан і дату збережено, показники перераховано"
                );

            } else {
                System.out.println(
                        "ПОМИЛКА: Некоректне оновлення замовлення"
                );
            }

        } finally {
            ServiceOrder restore = new ServiceOrder(
                    original.getId(),
                    original.getCarId(),
                    oldStatus,
                    oldDescription,
                    oldDate,
                    original.getF1(),
                    original.getF2(),
                    original.getF3(),
                    original.getRiskIndex()
            );

            orderService.update(restore);
        }
    }

    private static void checkLifecycle(ServiceOrderService orderService)
            throws Exception {

        ServiceOrderDAO dao = new ServiceOrderDAO();

        ServiceOrder target = null;
        for (ServiceOrder o : dao.getAll()) {
            if (o.getStatus() == OrderState.CREATED) {
                target = o;
                break;
            }
        }

        if (target == null) {
            System.out.println(
                    "НЕ ПЕРЕВІРЕНО: Немає замовлення в стані CREATED для перевірки життєвого циклу"
            );
            return;
        }

        final long id = target.getId();

        try {
            check("Пропуск стану (CREATED → COMPLETED)", () ->
                    orderService.changeState(id, OrderState.COMPLETED));

            check("Той самий стан (CREATED → CREATED)", () ->
                    orderService.changeState(id, OrderState.CREATED));

            orderService.changeState(id, OrderState.DIAGNOSED);
            OrderState afterFirst = dao.getById(id).getStatus();
            System.out.println((afterFirst == OrderState.DIAGNOSED ? "УСПІШНО" : "ПОМИЛКА")
                    + ": Перехід CREATED → DIAGNOSED, стан=" + afterFirst);

            check("Зворотний перехід (DIAGNOSED → CREATED)", () ->
                    orderService.changeState(id, OrderState.CREATED));

            orderService.changeState(id, OrderState.REPAIR);
            orderService.changeState(id, OrderState.COMPLETED);
            OrderState finalState = dao.getById(id).getStatus();
            System.out.println((finalState == OrderState.COMPLETED ? "УСПІШНО" : "ПОМИЛКА")
                    + ": Повний цикл до COMPLETED, стан=" + finalState);

            check("Вихід зі стану COMPLETED", () ->
                    orderService.changeState(id, OrderState.REPAIR));

        } finally {
            dao.updateStatus(id, OrderState.CREATED);
        }
    }

    @FunctionalInterface
    interface CheckedAction {
        void run() throws Exception;
    }

    private static void check(String name, CheckedAction operation) {
        try {
            operation.run();
            System.out.println("ПОМИЛКА: " + name + " не відхилено");

        } catch (IllegalArgumentException e) {
            System.out.println("УСПІШНО: " + name + " — " + e.getMessage());

        } catch (Exception e) {
            System.out.println("ІНША ПОМИЛКА: " + name + " — " + e.getMessage());
        }
    }
}