package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.Car;
import utils.DBContext;

public class CarDAO {

    public ArrayList<Car> getCars(int custid) {
        ArrayList<Car> list = new ArrayList<>();
        Connection cn = null;
        try {
            cn = DBContext.getConnection();
            if (cn != null) {
                String sql = "SELECT [VehicleID],[CustomerID],[LicensePlate],[Brand],[Model],[Color],[CreatedAt],[CarImage],[PlateImage] "
                           + "FROM [CarWashDB].[dbo].[Vehicles] WHERE CustomerID=?";
                PreparedStatement st = cn.prepareStatement(sql);
                st.setInt(1, custid);
                ResultSet table = st.executeQuery();
                while (table.next()) {
                    list.add(new Car(
                            table.getInt("VehicleID"),
                            table.getString("LicensePlate"),
                            table.getString("Model"),
                            table.getString("Brand"),
                            table.getString("Color"),
                            table.getDate("CreatedAt"),
                            custid,
                            table.getString("CarImage"),
                            table.getString("PlateImage")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (cn != null) cn.close(); } catch (Exception e) { }
        }
        return list;
    }

    // FIX: updateCar KHÔNG cập nhật LicensePlate
    // Biển số là định danh cố định của xe, không được thay đổi
    // Chỉ cho sửa: Brand, Model, Color và ảnh
    public int updateCar(String id, String brand, String model, String color,
                         String newCarImage, String newPlateImage) {
        Connection cn = null;
        try {
            cn = DBContext.getConnection();
            if (cn != null) {
                StringBuilder sql = new StringBuilder(
                    "UPDATE dbo.Vehicles SET Brand=?, Model=?, Color=?"
                );
                if (newCarImage != null)   sql.append(", CarImage=?");
                if (newPlateImage != null) sql.append(", PlateImage=?");
                sql.append(" WHERE VehicleID=?");

                PreparedStatement st = cn.prepareStatement(sql.toString());
                int idx = 1;
                st.setString(idx++, brand);
                st.setString(idx++, model);
                st.setString(idx++, color);
                if (newCarImage != null)   st.setString(idx++, newCarImage);
                if (newPlateImage != null) st.setString(idx++, newPlateImage);
                st.setInt(idx++, Integer.parseInt(id.trim()));

                return st.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (cn != null) cn.close(); } catch (Exception e) { }
        }
        return 0;
    }

    public boolean insertCar(Car car) {
        String sql = "INSERT INTO dbo.Vehicles (CustomerID, LicensePlate, Brand, Model, Color, CreatedAt, CarImage, PlateImage) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, car.getCustid());
            st.setString(2, car.getLicensePlate());
            st.setString(3, car.getBrand());
            st.setString(4, car.getModel());
            st.setString(5, car.getColor());
            st.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            st.setString(7, car.getCarImage());
            st.setString(8, car.getPlateImage());
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCar(int vehicleId) {
        String sql = "DELETE FROM dbo.Vehicles WHERE VehicleID = ?";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, vehicleId);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isLicensePlateExists(String licensePlate, int custId) {
        String sql = "SELECT COUNT(*) FROM dbo.Vehicles WHERE LicensePlate = ?";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, licensePlate);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Car> searchCars(int custid, String keyword) {
        ArrayList<Car> list = new ArrayList<>();
        String sql = "SELECT * FROM dbo.Vehicles WHERE CustomerID = ? AND (LicensePlate LIKE ? OR Brand LIKE ?)";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, custid);
            st.setString(2, "%" + keyword + "%");
            st.setString(3, "%" + keyword + "%");
            ResultSet table = st.executeQuery();
            while (table.next()) {
                list.add(new Car(
                        table.getInt("VehicleID"),
                        table.getString("LicensePlate"),
                        table.getString("Model"),
                        table.getString("Brand"),
                        table.getString("Color"),
                        table.getDate("CreatedAt"),
                        custid,
                        table.getString("CarImage"),
                        table.getString("PlateImage")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
