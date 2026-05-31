/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.Car;
import utils.DBContext;

public class CarDAO {
    // Lấy danh sách xe của 1 khách hàng (TV3)
    public ArrayList<Car> getCars(int custid){
        ArrayList<Car> list=new ArrayList<>();
        Connection cn=null;
        try {
            cn=DBContext.getConnection();
            if(cn!=null){
                String sql = "SELECT [VehicleID],[CustomerID],[LicensePlate],[Brand],[Model],[Color],[CreatedAt]\n"
                        + "  FROM [CarWashDB].[dbo].[Vehicles]\n"
                        + "  WHERE CustomerID=? ";
                PreparedStatement st=cn.prepareStatement(sql);
                st.setInt(1, custid);
                ResultSet table=st.executeQuery();
                if(table!=null){
                    while(table.next()){
                        int id=table.getInt("VehicleID");
                        String licensePlate=table.getString("LicensePlate");
                        String model=table.getString("Model");
                        String brand=table.getString("Brand");
                        String color=table.getString("Color");
                        Date d=table.getDate("CreatedAt");
                        Car v=new Car(id, licensePlate, model, brand, color, d, custid);
                        list.add(v);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(cn!=null) cn.close(); } catch(Exception e) {}
        }
        return list;
    }

    // Sửa thông tin xe (TV4)
    public int updateCar(String id, String licenseplate, String brand, String model, String color){
        int result=0;
        Connection cn=null;
        try {
           cn=DBContext.getConnection();
           if(cn!=null){
               String sql = "update dbo.Vehicles\n"
                       + "set LicensePlate=?,Brand=?,Model=?,Color=?\n"
                       + "where VehicleID=?";
               PreparedStatement st=cn.prepareStatement(sql);
               st.setString(1, licenseplate);
               st.setString(2, brand);
               st.setString(3, model);
               st.setString(4, color);
               st.setInt(5, Integer.parseInt(id.trim()));
               result=st.executeUpdate();
           }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(cn!=null) cn.close(); } catch(Exception e) {}
        }
        return result;
    }

    // --- CODE THÊM MỚI CHO WORKSHOP 1 ---

    // Thêm xe mới (TV3)
    public boolean insertCar(Car car) {
        String sql = "INSERT INTO dbo.Vehicles (CustomerID, LicensePlate, Brand, Model, Color, CreatedAt) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = DBContext.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, car.getCustid());
            st.setString(2, car.getLicensePlate());
            st.setString(3, car.getBrand());
            st.setString(4, car.getModel());
            st.setString(5, car.getColor());
            st.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa xe (TV4)
    public boolean deleteCar(int vehicleId) {
        String sql = "DELETE FROM dbo.Vehicles WHERE VehicleID = ?";
        try (Connection cn = DBContext.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, vehicleId);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm xe của khách hàng theo biển số hoặc hãng xe (TV4)
    public ArrayList<Car> searchCars(int custid, String keyword) {
        ArrayList<Car> list = new ArrayList<>();
        String sql = "SELECT * FROM dbo.Vehicles WHERE CustomerID = ? AND (LicensePlate LIKE ? OR Brand LIKE ?)";
        try (Connection cn = DBContext.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, custid);
            st.setString(2, "%" + keyword + "%");
            st.setString(3, "%" + keyword + "%");
            try (ResultSet table = st.executeQuery()) {
                while (table.next()) {
                    list.add(new Car(
                        table.getInt("VehicleID"),
                        table.getString("LicensePlate"),
                        table.getString("Model"),
                        table.getString("Brand"),
                        table.getString("Color"),
                        table.getDate("CreatedAt"),
                        custid
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
