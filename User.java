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
public class User {
    private int custID;
    private String fullname;
    private String phone;
    private String email;
    //private String address;
    private int tierID;
    //private int totalBooking;
    private Date createAt;
    private boolean status;
    private String password;
    private int point;
    private String rank;
    private double wallet;

    public User() {
    }

    public User(int custID, String fullname, String phone, String email, int tierID, Date createAt, boolean status, String password) {
        this.custID = custID;
        this.fullname = fullname;
        this.phone = phone;
        this.email = email;
        this.tierID = tierID;
        this.createAt = createAt;
        this.status = status;
        this.password = password;
    }

    public User(int custID, String fullname, String phone, String email, int tierID, Date createAt, boolean status, String password, int point, String rank, double wallet) {
        this.custID = custID;
        this.fullname = fullname;
        this.phone = phone;
        this.email = email;
        this.tierID = tierID;
        this.createAt = createAt;
        this.status = status;
        this.password = password;
        this.point = point;
        this.rank = rank;
        this.wallet = wallet;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    public int getCustID() {
        return custID;
    }

    public void setCustID(int custID) {
        this.custID = custID;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTierID() {
        return tierID;
    }

    public void setTierID(int tierID) {
        this.tierID = tierID;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPassword() {
return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}