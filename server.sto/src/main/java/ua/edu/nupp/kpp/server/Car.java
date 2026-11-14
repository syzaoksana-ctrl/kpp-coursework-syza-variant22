package ua.edu.nupp.kpp.server;

public class Car {
    private Long id;
    private Long clientId;
    private String vin;
    private String brand;
    private String model;
    private Integer year;
    public Car() {

    }
    public Car(Long id, Long clientId, String vin,
               String brand, String model, Integer year) {
        this.id = id;
        this.clientId = clientId;
        this.vin = vin;
        this.brand = brand;
        this.model = model;
        this.year = year;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getClientId() {
        return clientId;
    }
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    public String getVin() {
        return vin;
    }
    public void setVin(String vin) {
        this.vin = vin;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
}