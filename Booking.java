package model;

public class Booking {
    
    private String serviceType;
    private int bookingID;
    private int customerID;
    private String licensePlate; // Chỉ dùng Biển số xe
    private String bookingDate;
    private String bookingTime;
    private String status;

    public Booking() {
    }

    // Constructor chuẩn với 6 tham số (Khớp 100% với BookingDAO)
    public Booking(int bookingID, int customerID, String licensePlate, String bookingDate, String bookingTime, String status) {
        this.bookingID = bookingID;
        this.customerID = customerID;
        this.licensePlate = licensePlate;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.status = status;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}