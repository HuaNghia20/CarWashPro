package dao;

import java.sql.*;
import java.util.ArrayList;
import model.Booking;
import utils.DBContext;

public class BookingDAO {
    // Thêm lịch đặt
    public boolean addBooking(Booking b) {
        String sql = "INSERT INTO Bookings (CustomerID, LicensePlate, ServiceType, BookingDate, BookingTime, Status) VALUES (?,?,?,?,?,?)";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, b.getCustomerID());
            st.setString(2, b.getLicensePlate());
            st.setString(3, b.getServiceType());
            st.setString(4, b.getBookingDate());
            st.setString(5, b.getBookingTime());
            st.setString(6, "Pending"); // Tình trạng mặc định
            return st.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }

    // Lấy lịch sử (để hiển thị bảng)
    public ArrayList<Booking> getBookingsByCustomer(int custID) {
        ArrayList<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM Bookings WHERE CustomerID = ? ORDER BY BookingDate DESC";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, custID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                // CHỈ TRUYỀN ĐÚNG 6 THAM SỐ (Bỏ VehicleID đi)
                Booking b = new Booking(
                    rs.getInt("BookingID"), 
                    rs.getInt("CustomerID"), 
                    rs.getString("LicensePlate"), 
                    rs.getString("BookingDate"), 
                    rs.getString("BookingTime"), 
                    rs.getString("Status")
                );
                b.setServiceType(rs.getString("ServiceType"));
                list.add(b);
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    // Hủy lịch
    public boolean deleteBooking(int bookingID) {
        String sql = "DELETE FROM Bookings WHERE BookingID = ?";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, bookingID);
            return st.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }
}