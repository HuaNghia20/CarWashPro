package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Booking;
import model.Voucher;
import utils.DBContext;

public class BookingDAO {

    public int addBookingAndGetId(Booking b) {
        String sql = "INSERT INTO Bookings (CustomerID, LicensePlate, ServiceType, BookingDate, BookingTime, BookingStatus, Price, PaymentStatus, VoucherID) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection cn = DBContext.getConnection(); 
             PreparedStatement st = cn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, b.getCustomerID());
            st.setString(2, b.getLicensePlate());
            st.setString(3, b.getServiceType());
            st.setString(4, b.getBookingDate());
            st.setString(5, b.getBookingTime());
            st.setString(6, b.getStatus());
            st.setDouble(7, b.getPrice());
            st.setString(8, b.getPaymentStatus());
            if (b.getVoucherID() != null) st.setInt(9, b.getVoucherID()); 
            else st.setNull(9, java.sql.Types.INTEGER);
            if (st.executeUpdate() > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    // HÀM MỚI ĐỂ LƯU BOOKING SAU KHI THANH TOÁN
    public void addBooking(Booking b) {
        String sql = "INSERT INTO Bookings (CustomerID, LicensePlate, ServiceType, BookingDate, BookingTime, BookingStatus, Price, PaymentStatus, VoucherID) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, b.getCustomerID());
            st.setString(2, b.getLicensePlate());
            st.setString(3, b.getServiceType());
            st.setString(4, b.getBookingDate());
            st.setString(5, b.getBookingTime());
            st.setString(6, b.getStatus());
            st.setDouble(7, b.getPrice());
            st.setString(8, b.getPaymentStatus());
            if (b.getVoucherID() != null) st.setInt(9, b.getVoucherID()); 
            else st.setNull(9, java.sql.Types.INTEGER);
            st.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void confirmPayment(int bookingID) {
        String sql = "UPDATE Bookings SET PaymentStatus = 'Paid', BookingStatus = 'Pending' WHERE BookingID = ?";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, bookingID);
            st.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public ArrayList<Booking> getBookingsByCustomer(int custID) {
        ArrayList<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM Bookings WHERE CustomerID = ? AND BookingStatus <> 'PaymentPending' ORDER BY BookingDate DESC";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, custID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Booking b = new Booking();
                b.setBookingID(rs.getInt("BookingID"));
                b.setLicensePlate(rs.getString("LicensePlate"));
                b.setServiceType(rs.getString("ServiceType"));
                b.setBookingDate(rs.getString("BookingDate"));
                b.setBookingTime(rs.getString("BookingTime"));
                b.setStatus(rs.getString("BookingStatus"));
                b.setPrice(rs.getDouble("Price"));
                b.setPaymentStatus(rs.getString("PaymentStatus"));
                list.add(b);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean cancelBooking(int bookingID) {
        String sql = "UPDATE Bookings SET BookingStatus = 'Cancelled' WHERE BookingID = ?";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, bookingID);
            return st.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public List<String> getBusySlots(String bookingDate) {
        List<String> busySlots = new ArrayList<>();
        String sql = "SELECT BookingTime FROM Bookings WHERE CAST(BookingDate AS DATE) = ? AND BookingStatus <> 'Cancelled' AND BookingStatus <> 'PaymentPending'";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, bookingDate);
            ResultSet rs = st.executeQuery();
            while (rs.next()) busySlots.add(rs.getString("BookingTime"));
        } catch (Exception e) { e.printStackTrace(); }
        return busySlots;
    }

    public void updateExpiredBookings() {
        String sql = "UPDATE Bookings SET BookingStatus = 'Completed' WHERE BookingStatus = 'Pending' AND (BookingDate < CAST(GETDATE() AS DATE) OR (BookingDate = CAST(GETDATE() AS DATE) AND LEFT(BookingTime,5) <= CONVERT(VARCHAR(5), GETDATE(), 108)))";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public List<Voucher> getAllActiveVouchers() {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM Vouchers WHERE ExpiryDate >= CAST(GETDATE() AS DATE)";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                list.add(new Voucher(rs.getInt("VoucherID"), rs.getString("Code"), rs.getInt("DiscountPercent"), rs.getString("ExpiryDate")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public double getDiscountPercent(int voucherID) {
        String sql = "SELECT DiscountPercent FROM Vouchers WHERE VoucherID = ?";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, voucherID);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getDouble("DiscountPercent");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}
