package model;

public class Booking {
    
    private int bookingID;
    private int customerID;
    private String licensePlate;
    private String serviceType;
    private String bookingDate;
    private String bookingTime;
    private String status;         // Trạng thái xử lý (Pending, Confirmed, ...)
    private double price;          // Thêm: Giá tiền
    private String paymentStatus;  // Thêm: Trạng thái thanh toán (Paid, Unpaid)

    public Booking() {
    }

    // Constructor đầy đủ (có thêm price và paymentStatus)
    public Booking(int bookingID, int customerID, String licensePlate, String serviceType, 
                   String bookingDate, String bookingTime, String status, double price, String paymentStatus) {
        this.bookingID = bookingID;
        this.customerID = customerID;
        this.licensePlate = licensePlate;
        this.serviceType = serviceType;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.status = status;
        this.price = price;
        this.paymentStatus = paymentStatus;
    }

    // --- Getters và Setters ---

    public int getBookingID() { return bookingID; }
    public void setBookingID(int bookingID) { this.bookingID = bookingID; }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getBookingTime() { return bookingTime; }
    public void setBookingTime(String bookingTime) { this.bookingTime = bookingTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
