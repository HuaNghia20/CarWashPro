package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Booking;
import utils.DBContext;

public class BookingDAO {

    // =========================
    // THÊM BOOKING
    // =========================
    public boolean addBooking(Booking b) {

        String sql =
                "INSERT INTO Bookings "
                + "(CustomerID, LicensePlate, ServiceType, "
                + "BookingDate, BookingTime, BookingStatus, "
                + "Price, PaymentStatus) "
                + "VALUES (?,?,?,?,?,?,?,?)";

        try (
                Connection cn = DBContext.getConnection();
                PreparedStatement st = cn.prepareStatement(sql)) {

            st.setInt(1, b.getCustomerID());
            st.setString(2, b.getLicensePlate());
            st.setString(3, b.getServiceType());
            st.setString(4, b.getBookingDate());
            st.setString(5, b.getBookingTime());
            st.setString(6, b.getStatus());

            st.setDouble(7, b.getPrice());
            st.setString(8, b.getPaymentStatus());

            return st.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // LỊCH SỬ BOOKING
    // =========================
    public ArrayList<Booking> getBookingsByCustomer(int custID) {

        ArrayList<Booking> list = new ArrayList<>();

        String sql =
                "SELECT * "
                + "FROM Bookings "
                + "WHERE CustomerID = ? "
                + "ORDER BY BookingDate DESC";

        try (
                Connection cn = DBContext.getConnection();
                PreparedStatement st = cn.prepareStatement(sql)) {

            st.setInt(1, custID);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                Booking b = new Booking();

                b.setBookingID(rs.getInt("BookingID"));
                b.setCustomerID(rs.getInt("CustomerID"));

                b.setLicensePlate(
                        rs.getString("LicensePlate"));

                b.setServiceType(
                        rs.getString("ServiceType"));

                if (rs.getDate("BookingDate") != null) {
                    b.setBookingDate(
                            rs.getDate("BookingDate").toString());
                }

                if (rs.getString("BookingTime") != null) {
                    b.setBookingTime(
                            rs.getString("BookingTime"));
                }

                b.setStatus(
                        rs.getString("BookingStatus"));

                // Đọc giá thật từ DB
                b.setPrice(
                        rs.getDouble("Price"));

                // Đọc trạng thái thanh toán từ DB
                b.setPaymentStatus(
                        rs.getString("PaymentStatus"));

                list.add(b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // HỦY BOOKING
    // =========================
    public boolean cancelBooking(int bookingID) {

        String sql =
                "UPDATE Bookings "
                + "SET BookingStatus = 'Cancelled' "
                + "WHERE BookingID = ?";

        try (
                Connection cn = DBContext.getConnection();
                PreparedStatement st = cn.prepareStatement(sql)) {

            st.setInt(1, bookingID);

            return st.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // LẤY SLOT ĐÃ ĐẶT
    // =========================
    public List<String> getBusySlots(String bookingDate) {

        List<String> busySlots = new ArrayList<>();

        String sql =
                "SELECT BookingTime "
                + "FROM Bookings "
                + "WHERE CAST(BookingDate AS DATE) = ? "
                + "AND BookingStatus <> 'Cancelled'";

        try (
                Connection cn = DBContext.getConnection();
                PreparedStatement st = cn.prepareStatement(sql)) {

            st.setString(1, bookingDate);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                String slot = rs.getString("BookingTime");

                if (slot != null) {
                    busySlots.add(slot);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return busySlots;
    }
    public void updateExpiredBookings() {

    String sql =
        "UPDATE Bookings "
      + "SET BookingStatus = 'Completed' "
      + "WHERE BookingStatus = 'Pending' "
      + "AND ( "
      + "      BookingDate < CAST(GETDATE() AS DATE) "
      + "   OR ( "
      + "        BookingDate = CAST(GETDATE() AS DATE) "
      + "        AND LEFT(BookingTime,5) <= CONVERT(VARCHAR(5), GETDATE(), 108) "
      + "      ) "
      + "    )";

    try (
        Connection cn = DBContext.getConnection();
        PreparedStatement st = cn.prepareStatement(sql)
    ) {

        int rows = st.executeUpdate();
        System.out.println("Updated bookings: " + rows);

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
