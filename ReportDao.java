/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import utils.DBContext;
/**
 *
 * @author ASUS
 */
public class ReportDAO {
    // Lấy tổng quan: Tổng khách, Tổng đơn, Tổng doanh thu
   public Map<String, Object> getGeneralStats() {
    Map<String, Object> stats = new HashMap<>();
    // XÓA MỌI ĐIỀU KIỆN 'WHERE' NẾU KHÔNG CẦN THIẾT
    // Dưới đây là cách lấy tổng toàn bộ hệ thống:
    String sql = "SELECT COUNT(CustomerID) as TotalCust, " +
                 "COUNT(BookingID) as TotalBookings, " +
                 "SUM(Price) as TotalRevenue " +
                 "FROM Bookings"; // Chỉ cần lấy từ bảng Bookings, không lọc người dùng
    
    try (Connection cn = DBContext.getConnection();
         PreparedStatement st = cn.prepareStatement(sql);
         ResultSet rs = st.executeQuery()) {
        if (rs.next()) {
            stats.put("totalCust", rs.getInt("TotalCust")); // Cẩn thận: COUNT CustomerID có thể trùng lặp
            stats.put("totalBookings", rs.getInt("TotalBookings"));
            stats.put("totalRevenue", rs.getDouble("TotalRevenue"));
        }
    } catch (Exception e) { e.printStackTrace(); }
    return stats;
}

    // Doanh thu theo từng tháng trong năm hiện tại
    public Map<Integer, Double> getRevenueByMonth() {
        Map<Integer, Double> revenueMap = new TreeMap<>();
        String sql = "SELECT MONTH(BookingDate) as Month, SUM(Price) as Rev " +
                     "FROM Bookings WHERE PaymentStatus = 'Paid' AND YEAR(BookingDate) = YEAR(GETDATE()) " +
                     "GROUP BY MONTH(BookingDate)";
        
        try (Connection cn = DBContext.getConnection();
             PreparedStatement st = cn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                revenueMap.put(rs.getInt("Month"), rs.getDouble("Rev"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return revenueMap;
    }
    // Hàm lấy tổng doanh thu từ tất cả các đơn hàng đã Completed
// Hàm lấy tổng doanh thu từ tất cả các đơn hàng đã Completed
    public double getTotalRevenue() {
        double total = 0;
       // Sửa 'Status' thành 'BookingStatus'
String sql = "SELECT SUM(Price) FROM Bookings WHERE BookingStatus = 'Completed'";
        
        try (Connection cn = DBContext.getConnection();
             PreparedStatement st = cn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            
            if (rs.next()) {
                total = rs.getDouble(1); // Lấy giá trị của cột đầu tiên (SUM)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    // Hàm lấy tổng số đơn hàng
    public int getTotalBookings() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM Bookings";
        
        try (Connection cn = DBContext.getConnection();
             PreparedStatement st = cn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            
            if (rs.next()) {
                count = rs.getInt(1); // Lấy giá trị của cột đầu tiên (COUNT)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
