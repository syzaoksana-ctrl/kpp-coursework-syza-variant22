package ua.edu.nupp.kpp.client;

public class ServiceOrder {
    private Long id;
    private long carId;
    private OrderState status;
    private String description;
    private String createdAt;
    private double f1;
    private double f2;
    private double f3;
    private double riskIndex;

    public ServiceOrder() {}
    public ServiceOrder(Long id, long carId, OrderState status, String description,
                        String createdAt, double f1, double f2, double f3, double riskIndex) {
        this.id = id; this.carId = carId; this.status = status;
        this.description = description; this.createdAt = createdAt;
        this.f1 = f1; this.f2 = f2; this.f3 = f3; this.riskIndex = riskIndex;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getCarId() { return carId; }
    public void setCarId(long carId) { this.carId = carId; }
    public OrderState getStatus() { return status; }
    public void setStatus(OrderState status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }
    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }
    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }
    public double getRiskIndex() { return riskIndex; }
    public void setRiskIndex(double riskIndex) { this.riskIndex = riskIndex; }
}
