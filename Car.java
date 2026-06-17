/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;

/**
 *
 * @author pc
 */
public class Car {
    private int id;
    private String licensePlate;
    private String model;
    private String brand;
    private String color;
    private Date createdAt;
    private int custid;
    private String carImage;
    private String plateImage;
 
    // Constructor đầy đủ 9 tham số (dùng khi có ảnh)
    public Car(int id, String licensePlate, String model, String brand, String color,
               Date createdAt, int custid, String carImage, String plateImage) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.model = model;
        this.brand = brand;
        this.color = color;
        this.createdAt = createdAt;
        this.custid = custid;
        this.carImage = carImage;
        this.plateImage = plateImage;
    }
 
    // Constructor 7 tham số (không có ảnh) — ĐÃ SỬA: set null thay vì dùng biến chưa khai báo
    public Car(int id, String licensePlate, String model, String brand, String color,
               Date createdAt, int custid) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.model = model;
        this.brand = brand;
        this.color = color;
        this.createdAt = createdAt;
        this.custid = custid;
        this.carImage = null;   // FIX: bản cũ dùng this.carImage = carImage → lỗi
        this.plateImage = null; // FIX: tương tự
    }
 
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
 
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
 
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
 
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
 
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
 
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
 
    public int getCustid() { return custid; }
    public void setCustid(int custid) { this.custid = custid; }
 
    public String getCarImage() { return carImage; }
    public void setCarImage(String carImage) { this.carImage = carImage; }
 
    public String getPlateImage() { return plateImage; }
    public void setPlateImage(String plateImage) { this.plateImage = plateImage; }
}
