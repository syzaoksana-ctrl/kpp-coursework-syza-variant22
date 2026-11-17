package ua.edu.nupp.kpp.client;

public class Work {
    private Long id;
    private long orderId;
    private String name;
    private double price;
    private double hours;

    public Work() {}
    public Work(Long id, long orderId, String name, double price, double hours) {
        this.id = id; this.orderId = orderId; this.name = name;
        this.price = price; this.hours = hours;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }
}
