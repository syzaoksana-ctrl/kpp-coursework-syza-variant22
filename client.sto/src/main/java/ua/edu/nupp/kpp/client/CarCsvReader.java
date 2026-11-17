package ua.edu.nupp.kpp.client;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
public class CarCsvReader {
    public static final Path DEFAULT_PATH = Path.of("sample-data", "cars.csv");

    public Car readFirstCar(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        if (lines.size() < 2) {
            throw new IllegalArgumentException("У файлі немає рядків даних");
        }

        return parseLine(lines.get(1), 2);
    }

    public Car importFirstCar(Path path, HttpCarServiceClient api) throws IOException {
        Car car = readFirstCar(path);
        api.addCar(car);
        return car;
    }

    public Car parseLine(String line, int lineNumber) {
        String[] parts = line.split(";", -1);

        if (parts.length != 5) {
            throw new IllegalArgumentException(
                    "Рядок " + lineNumber + ": очікується 5 полів, знайдено " + parts.length);
        }

        try {
            return new Car(
                    null,
                    Long.parseLong(parts[0].trim()),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim(),
                    Integer.parseInt(parts[4].trim())
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Рядок " + lineNumber + ": client_id та year мають бути цілими числами");
        }
    }

    public static void main(String[] args) throws IOException {
        Car car = new CarCsvReader().importFirstCar(DEFAULT_PATH, new HttpCarServiceClient());
        System.out.println("Надіслано на сервер: " + car.getVin());
    }
}