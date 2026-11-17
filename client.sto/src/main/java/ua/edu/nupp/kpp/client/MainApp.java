package ua.edu.nupp.kpp.client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
public class MainApp extends Application {
    private final HttpCarServiceClient api = new HttpCarServiceClient();
    private TableView<Client> clientsTable;
    private TableView<Car> carsTable;
    private TableView<ServiceOrder> ordersTable;
    private TableView<Work> worksTable;
    private ComboBox<Client> carClientBox;
    private ComboBox<Car> orderCarBox;
    private ComboBox<ServiceOrder> workOrderBox;
    @Override
    public void start(Stage stage) {

        TabPane tabs = new TabPane(
                clientsTab(),
                carsTab(),
                ordersTab(),
                worksTab()
        );

        tabs.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldTab, newTab) -> refreshAll());

        stage.setTitle("Сервісне обслуговування автомобілів");
        stage.setScene(new Scene(tabs, 1100, 700));
        stage.show();
    }

    private Tab clientsTab() {

        TableView<Client> table = new TableView<>();
        clientsTable = table; // для refreshAll

        table.getColumns().add(column("ID", "id"));
        table.getColumns().add(column("ПІБ", "fullName"));
        table.getColumns().add(column("Телефон", "phone"));
        table.getColumns().add(column("Email", "email"));

        TextField fullName = new TextField();
        fullName.setPromptText("ПІБ клієнта");

        TextField phone = new TextField();
        phone.setPromptText("Телефон");

        TextField email = new TextField();
        email.setPromptText("Email");

        final Long[] selectedId = {null};

        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {

                    if (newValue != null) {

                        selectedId[0] = newValue.getId();

                        fullName.setText(newValue.getFullName());
                        phone.setText(newValue.getPhone());
                        email.setText(newValue.getEmail() == null ? "" : newValue.getEmail());
                    }
                });

        Button add = new Button("Додати клієнта");

        add.setOnAction(e -> safe(() -> {

            if (fullName.getText().isBlank()
                    || phone.getText().isBlank()
                    || email.getText().isBlank()) {

                throw new RuntimeException("Заповніть усі поля клієнта");
            }

            Client client = new Client(
                    null,
                    fullName.getText().trim(),
                    phone.getText().trim(),
                    email.getText().trim()
            );

            api.addClient(client);

            show(table, api.getClients());

            selectedId[0] = null;
            table.getSelectionModel().clearSelection();

            fullName.clear();
            phone.clear();
            email.clear();
        }));

        Button update = new Button("Змінити");

        update.setOnAction(e -> safe(() -> {

            if (selectedId[0] == null) {
                throw new RuntimeException("Виберіть запис");
            }

            if (fullName.getText().isBlank()
                    || phone.getText().isBlank()
                    || email.getText().isBlank()) {

                throw new RuntimeException("Заповніть усі поля клієнта");
            }

            Client client = new Client(
                    selectedId[0],
                    fullName.getText().trim(),
                    phone.getText().trim(),
                    email.getText().trim()
            );

            api.updateClient(client);

            show(table, api.getClients());

            selectedId[0] = null;
            table.getSelectionModel().clearSelection();

            fullName.clear();
            phone.clear();
            email.clear();
        }));

        Button delete = new Button("Видалити");

        delete.setOnAction(e -> safe(() -> {

            Client selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                throw new RuntimeException("Виберіть запис");
            }

            api.deleteClient(selected.getId());

            show(table, api.getClients());

            selectedId[0] = null;
            table.getSelectionModel().clearSelection();

            fullName.clear();
            phone.clear();
            email.clear();
        }));

        HBox form = new HBox(10,
                fullName, phone, email, add, update, delete
        );

        return tab("Клієнти", table, api::getClients, form);
    }

    private Tab carsTab() {

        TableView<Car> table = new TableView<>();
        carsTable = table; // для refreshAll

        table.getColumns().add(column("ID", "id"));
        table.getColumns().add(column("VIN", "vin"));
        table.getColumns().add(column("Марка", "brand"));
        table.getColumns().add(column("Модель", "model"));
        table.getColumns().add(column("Рік", "year"));

        ComboBox<Client> clientBox = new ComboBox<>();
        carClientBox = clientBox; // для refreshAll
        clientBox.setPromptText("Оберіть клієнта");
        clientBox.setPrefWidth(180);

        setDisplay(clientBox, Client::getFullName);

        TextField vin = new TextField();
        vin.setPromptText("VIN");

        TextField brand = new TextField();
        brand.setPromptText("Марка");

        TextField model = new TextField();
        model.setPromptText("Модель");

        TextField year = new TextField();
        year.setPromptText("Рік");

        final Long[] selectedId = {null};

        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {

                    if (newValue != null) {

                        selectedId[0] = newValue.getId();

                        vin.setText(newValue.getVin());
                        brand.setText(newValue.getBrand());
                        model.setText(newValue.getModel());
                        year.setText(String.valueOf(newValue.getYear()));

                        clientBox.getItems().stream()
                                .filter(c -> c.getId() == newValue.getClientId())
                                .findFirst()
                                .ifPresent(clientBox::setValue);
                    }
                });

        safe(() -> clientBox.setItems(
                FXCollections.observableArrayList(api.getClients())
        ));

        Button add = new Button("Додати автомобіль");

        add.setOnAction(e -> safe(() -> {

            Client client = clientBox.getValue();

            if (client == null) {
                throw new RuntimeException("Оберіть клієнта");
            }

            if (vin.getText().isBlank()
                    || brand.getText().isBlank()
                    || model.getText().isBlank()
                    || year.getText().isBlank()) {

                throw new RuntimeException("Заповніть усі поля автомобіля");
            }

            int carYear = Integer.parseInt(year.getText().trim());

            Car car = new Car(
                    null,
                    client.getId(),
                    vin.getText().trim(),
                    brand.getText().trim(),
                    model.getText().trim(),
                    carYear
            );

            api.addCar(car);

            show(table, api.getCars());

            selectedId[0] = null;
            table.getSelectionModel().clearSelection();

            vin.clear();
            brand.clear();
            model.clear();
            year.clear();
            clientBox.setValue(null);
        }));

        Button update = new Button("Змінити");

        update.setOnAction(e -> safe(() -> {

            if (selectedId[0] == null) {
                throw new RuntimeException("Виберіть запис");
            }

            Client client = clientBox.getValue();

            if (client == null) {
                throw new RuntimeException("Оберіть клієнта");
            }

            if (vin.getText().isBlank()
                    || brand.getText().isBlank()
                    || model.getText().isBlank()
                    || year.getText().isBlank()) {

                throw new RuntimeException("Заповніть усі поля автомобіля");
            }

            int carYear = Integer.parseInt(year.getText().trim());

            Car car = new Car(
                    selectedId[0],
                    client.getId(),
                    vin.getText().trim(),
                    brand.getText().trim(),
                    model.getText().trim(),
                    carYear
            );

            api.updateCar(car);

            show(table, api.getCars());

            selectedId[0] = null;
            table.getSelectionModel().clearSelection();

            vin.clear();
            brand.clear();
            model.clear();
            year.clear();
            clientBox.setValue(null);
        }));

        Button delete = new Button("Видалити");

        delete.setOnAction(e -> safe(() -> {

            Car selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                throw new RuntimeException("Виберіть запис");
            }

            api.deleteCar(selected.getId());

            show(table, api.getCars());

            selectedId[0] = null;
            table.getSelectionModel().clearSelection();

            vin.clear();
            brand.clear();
            model.clear();
            year.clear();
            clientBox.setValue(null);
        }));

        HBox form = new HBox(10,
                clientBox, vin, brand, model, year, add, update, delete
        );

        return tab("Автомобілі", table, api::getCars, form);
    }

    private Tab ordersTab() {

        TableView<ServiceOrder> table = new TableView<>();
        ordersTable = table; // для refreshAll

        table.getColumns().add(column("ID", "id"));
        table.getColumns().add(column("ID автомобіля", "carId"));
        table.getColumns().add(column("Статус", "status"));
        table.getColumns().add(column("Опис", "description"));
        table.getColumns().add(column("F1", "f1"));
        table.getColumns().add(column("F2", "f2"));
        table.getColumns().add(column("F3", "f3"));
        table.getColumns().add(column("Ризик", "riskIndex"));

        ComboBox<Car> carBox = new ComboBox<>();
        orderCarBox = carBox; // для refreshAll
        carBox.setPromptText("Оберіть автомобіль");
        carBox.setPrefWidth(180);

        setDisplay(carBox,
                car -> car.getBrand() + " " + car.getModel()
                        + " (ID: " + car.getId() + ")");

        TextArea description = new TextArea();
        description.setPromptText("Опис несправності");
        description.setPrefRowCount(2);
        description.setPrefWidth(200);

        TextField f1 = new TextField();
        f1.setPromptText("F1");

        TextField f2 = new TextField();
        f2.setPromptText("F2");

        ComboBox<OrderState> statusBox = new ComboBox<>(
                FXCollections.observableArrayList(OrderState.values())
        );

        statusBox.setPromptText("Новий статус");

        final Long[] selectedId = {null};

        safe(() -> carBox.setItems(
                FXCollections.observableArrayList(api.getCars())
        ));

        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {

                    if (newValue != null) {

                        selectedId[0] = newValue.getId();

                        description.setText(newValue.getDescription());
                        f1.setText(String.valueOf(newValue.getF1()));
                        f2.setText(String.valueOf(newValue.getF2()));

                        carBox.getItems().stream()
                                .filter(c -> c.getId() == newValue.getCarId())
                                .findFirst()
                                .ifPresent(carBox::setValue);

                        statusBox.setValue(newValue.getStatus());
                    }
                });

        Button add = new Button("Додати замовлення");

        add.setOnAction(e -> safe(() -> {

            Car car = carBox.getValue();

            if (car == null) {
                throw new RuntimeException("Оберіть автомобіль");
            }

            if (description.getText().isBlank()
                    || f1.getText().isBlank()
                    || f2.getText().isBlank()) {

                throw new RuntimeException("Заповніть усі поля замовлення");
            }

            double factor1 = parseNumber(f1.getText());
            double factor2 = parseNumber(f2.getText());

            ServiceOrder order = new ServiceOrder(
                    null,
                    car.getId(),
                    OrderState.CREATED,
                    description.getText().trim(),
                    "",
                    factor1,
                    factor2,
                    0,
                    0
            );

            api.addOrder(order);

            show(table, api.getOrders());

            selectedId[0] = null;
            table.getSelectionModel().clearSelection();

            description.clear();
            f1.clear();
            f2.clear();
            carBox.setValue(null);
            statusBox.setValue(null);
        }));

        Button update = new Button("Змінити");

        update.setOnAction(e -> safe(() -> {

            if (selectedId[0] == null) {
                throw new RuntimeException("Виберіть запис");
            }

            Car car = carBox.getValue();

            if (car == null) {
                throw new RuntimeException("Оберіть автомобіль");
            }

            if (description.getText().isBlank()
                    || f1.getText().isBlank()
                    || f2.getText().isBlank()) {

                throw new RuntimeException("Заповніть усі поля замовлення");
            }

            ServiceOrder selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                throw new RuntimeException("Виберіть запис");
            }

            double factor1 = parseNumber(f1.getText());
            double factor2 = parseNumber(f2.getText());

            ServiceOrder order = new ServiceOrder(
                    selectedId[0],
                    car.getId(),
                    selected.getStatus(),
                    description.getText().trim(),
                    "",
                    factor1,
                    factor2,
                    0,
                    0
            );

            api.updateOrder(order);

            show(table, api.getOrders());

            selectedId[0] = null;
            table.getSelectionModel().clearSelection();

            description.clear();
            f1.clear();
            f2.clear();
            carBox.setValue(null);
            statusBox.setValue(null);
        }));

        Button delete = new Button("Видалити");

        delete.setOnAction(e -> safe(() -> {

            ServiceOrder selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                throw new RuntimeException("Виберіть запис");
            }

            api.deleteOrder(selected.getId());

            show(table, api.getOrders());

            selectedId[0] = null;
            table.getSelectionModel().clearSelection();

            description.clear();
            f1.clear();
            f2.clear();
            carBox.setValue(null);
            statusBox.setValue(null);
        }));

        Button changeStatus = new Button("Змінити статус");

        changeStatus.setOnAction(e -> safe(() -> {

            ServiceOrder selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                throw new RuntimeException("Оберіть замовлення в таблиці");
            }

            OrderState state = statusBox.getValue();

            if (state == null) {
                throw new RuntimeException("Оберіть статус");
            }

            api.transitionOrder(selected.getId(), state);

            show(table, api.getOrders());

            selectedId[0] = null;
            statusBox.setValue(null);
            table.getSelectionModel().clearSelection();

            description.clear();
            f1.clear();
            f2.clear();
            carBox.setValue(null);
        }));

        HBox form = new HBox(10,
                carBox, description, f1, f2, add, update, delete
        );

        HBox statusForm = new HBox(10,
                new Label("Зміна статусу:"),
                statusBox,
                changeStatus
        );

        VBox content = new VBox(10, form, statusForm);

        return tab("Замовлення", table, api::getOrders, content);
    }

    private Tab worksTab() {

        TableView<Work> table = new TableView<>();
        worksTable = table; // для refreshAll

        table.getColumns().add(column("ID", "id"));
        table.getColumns().add(column("ID замовлення", "orderId"));
        table.getColumns().add(column("Назва", "name"));
        table.getColumns().add(column("Ціна", "price"));
        table.getColumns().add(column("Години", "hours"));

        ComboBox<ServiceOrder> orderBox = new ComboBox<>();
        workOrderBox = orderBox; // для refreshAll
        orderBox.setPromptText("Оберіть замовлення");
        orderBox.setPrefWidth(180);

        setDisplay(orderBox,
                order -> "Замовлення №" + order.getId());

        TextField name = new TextField();
        name.setPromptText("Назва роботи");

        TextField price = new TextField();
        price.setPromptText("Ціна");

        TextField hours = new TextField();
        hours.setPromptText("Години");

        final Long[] selectedId = {null};

        safe(() -> orderBox.setItems(
                FXCollections.observableArrayList(api.getOrders())
        ));

        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {

                    if (newValue != null) {

                        selectedId[0] = newValue.getId();

                        name.setText(newValue.getName());
                        price.setText(String.valueOf(newValue.getPrice()));
                        hours.setText(String.valueOf(newValue.getHours()));

                        orderBox.getItems().stream()
                                .filter(o -> o.getId() == newValue.getOrderId())
                                .findFirst()
                                .ifPresent(orderBox::setValue);
                    }
                });

        Button add = new Button("Додати роботу");

        add.setOnAction(e -> safe(() -> {

            ServiceOrder order = orderBox.getValue();

            if (order == null) {
                throw new RuntimeException("Оберіть замовлення");
            }

            if (name.getText().isBlank()
                    || price.getText().isBlank()
                    || hours.getText().isBlank()) {

                throw new RuntimeException("Заповніть усі поля роботи");
            }

            double workPrice = parseNumber(price.getText());
            double workHours = parseNumber(hours.getText());

            Work work = new Work(
                    null,
                    order.getId(),
                    name.getText().trim(),
                    workPrice,
                    workHours
            );

            api.addWork(work);

            show(table, api.getWorks());

            selectedId[0] = null;
            table.getSelectionModel().clearSelection();

            name.clear();
            price.clear();
            hours.clear();
            orderBox.setValue(null);
        }));

        Button update = new Button("Змінити");

        update.setOnAction(e -> safe(() -> {

            if (selectedId[0] == null) {
                throw new RuntimeException("Виберіть запис");
            }

            ServiceOrder order = orderBox.getValue();

            if (order == null) {
                throw new RuntimeException("Оберіть замовлення");
            }

            if (name.getText().isBlank()
                    || price.getText().isBlank()
                    || hours.getText().isBlank()) {

                throw new RuntimeException("Заповніть усі поля роботи");
            }

            Work work = new Work(
                    selectedId[0],
                    order.getId(),
                    name.getText().trim(),
                    parseNumber(price.getText()),
                    parseNumber(hours.getText())
            );

            api.updateWork(work);

            show(table, api.getWorks());

            selectedId[0] = null;
            table.getSelectionModel().clearSelection();

            name.clear();
            price.clear();
            hours.clear();
            orderBox.setValue(null);
        }));

        Button delete = new Button("Видалити");

        delete.setOnAction(e -> safe(() -> {

            Work selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                throw new RuntimeException("Виберіть запис");
            }

            api.deleteWork(selected.getId());

            show(table, api.getWorks());

            selectedId[0] = null;
            table.getSelectionModel().clearSelection();

            name.clear();
            price.clear();
            hours.clear();
            orderBox.setValue(null);
        }));

        HBox form = new HBox(10,
                orderBox, name, price, hours, add, update, delete
        );

        return tab("Роботи", table, api::getWorks, form);
    }

    private void refreshClients() {
        show(clientsTable, api.getClients());
    }

    private void refreshCars() {
        show(carsTable, api.getCars());
    }

    private void refreshOrders() {
        show(ordersTable, api.getOrders());
    }

    private void refreshWorks() {
        show(worksTable, api.getWorks());
    }

    private void refreshAll() {
        safe(() -> {
            refreshClients();
            refreshCars();
            refreshOrders();
            refreshWorks();

            carClientBox.setItems(FXCollections.observableArrayList(api.getClients()));
            orderCarBox.setItems(FXCollections.observableArrayList(api.getCars()));
            workOrderBox.setItems(FXCollections.observableArrayList(api.getOrders()));
        });
    }

    private <T> Tab tab(
            String title,
            TableView<T> table,
            Supplier<List<T>> loader,
            Node form) {

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        Button refresh = new Button("Оновити");

        refresh.setOnAction(e ->
                safe(() -> show(table, loader.get()))
        );

        VBox box = new VBox(10, table, form, refresh);
        box.setPadding(new Insets(10));

        VBox.setVgrow(table, Priority.ALWAYS);

        Tab tab = new Tab(title, box);
        tab.setClosable(false);

        Platform.runLater(() ->
                safe(() -> show(table, loader.get()))
        );

        return tab;
    }

    private <S> TableColumn<S, Object> column(
            String title,
            String property) {

        TableColumn<S, Object> col = new TableColumn<>(title);

        col.setCellValueFactory(
                new PropertyValueFactory<>(property)
        );

        return col;
    }

    private <T> void show(TableView<T> table, List<T> items) {
        table.setItems(FXCollections.observableArrayList(items));
    }

    private <T> void setDisplay(
            ComboBox<T> box,
            Function<T, String> display) {

        box.setConverter(new StringConverter<>() {

            @Override
            public String toString(T item) {
                return item == null ? "" : display.apply(item);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });
    }

    private double parseNumber(String value) {
        return Double.parseDouble(
                value.trim().replace(',', '.')
        );
    }

    private void safe(Runnable action) {

        try {
            action.run();

        } catch (RuntimeException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setHeaderText("Помилка");
            alert.setContentText(e.getMessage());

            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}