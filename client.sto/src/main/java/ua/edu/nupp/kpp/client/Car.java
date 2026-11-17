package ua.edu.nupp.kpp.client;

public class Car {
    private Long id;
    private long clientId;
    private String vin;
    private String brand;
    private String model;
    private int year;

    public Car() {}
    public Car(Long id, long clientId, String vin, String brand, String model, int year) {
        this.id = id; this.clientId = clientId; this.vin = vin;
        this.brand = brand; this.model = model; this.year = year;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getClientId() { return clientId; }
    public void setClientId(long clientId) { this.clientId = clientId; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}