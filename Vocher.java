/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author TRAN HUYNH QUANG
 */
public class Voucher {
    private int voucherID;
    private String code;
    private int discountPercent;
    private String expiryDate;

    public Voucher() {
    }

    
    // Constructor phải đúng thứ tự này:
    public Voucher(int voucherID, String code, int discountPercent, String expiryDate) {
        this.voucherID = voucherID;
        this.code = code;
        this.discountPercent = discountPercent;
        this.expiryDate = expiryDate;
    }

    public int getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(int voucherID) {
        this.voucherID = voucherID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    
}
