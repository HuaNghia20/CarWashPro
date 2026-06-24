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

    // ==========================================
    // 1. THÊM BOOKING MỚI
    // ==========================================
    public boolean addBooking(Booking b) {
       String sql = "INSERT INTO Bookings (CustomerID, VehicleID, ServiceType, BookingDate, BookingTime, BookingStatus, Price, PaymentStatus, CreatedAt) VALUES (?,?,?,?,?,?,?,?,GETDATE())";

        try (Connection cn = DBContext.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {

            st.setInt(1, b.getCustomerID());
            st.setInt(2, b.getVehicleID());
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

    
       // ==========================================
    // 2. LẤY TOÀN BỘ LỊCH SỬ BOOKING CỦA KHÁCH HÀNG (ĐÃ SỬA LỖI JOIN)
    // ==========================================
    public ArrayList<Booking> getBookingsByCustomer(int custID) {
        ArrayList<Booking> list = new ArrayList<>();
        // Áp dụng JOIN tương tự như hàm getBookingsByCustomerAndDate
        String sql = "SELECT b.*, v.LicensePlate "
                   + "FROM Bookings b "
                   + "JOIN Vehicles v ON b.VehicleID = v.VehicleID "
                   + "WHERE b.CustomerID = ? "
                   + "ORDER BY b.BookingDate DESC, b.BookingID DESC";

        try (Connection cn = DBContext.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {

            st.setInt(1, custID);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Booking b = new Booking();
                b.setBookingID(rs.getInt("BookingID"));
                b.setCustomerID(rs.getInt("CustomerID"));
                
                // Lấy VehicleID và LicensePlate
                b.setVehicleID(rs.getInt("VehicleID"));
                b.setLicensePlate(rs.getString("LicensePlate"));
                
                b.setServiceType(rs.getString("ServiceType"));

                if (rs.getDate("BookingDate") != null) {
                    b.setBookingDate(rs.getDate("BookingDate").toString());
                }
                if (rs.getString("BookingTime") != null) {
                    b.setBookingTime(rs.getString("BookingTime"));
                }

                b.setStatus(rs.getString("BookingStatus"));
                b.setPrice(rs.getDouble("Price"));
                b.setPaymentStatus(rs.getString("PaymentStatus"));

                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==========================================
    // 3. LỌC LỊCH SỬ BOOKING THEO KHOẢNG NGÀY
    // ==========================================
    public ArrayList<Booking> getBookingsByCustomerAndDate(int custID, String startDate, String endDate) {
        ArrayList<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, v.LicensePlate "
               + "FROM Bookings b "
               + "JOIN Vehicles v ON b.VehicleID = v.VehicleID "
               + "WHERE b.CustomerID = ? "
               + "ORDER BY b.BookingDate DESC, b.BookingID DESC";

        try (Connection cn = DBContext.getConnection();
         PreparedStatement st = cn.prepareStatement(sql)) {

        st.setInt(1, custID);
        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            Booking b = new Booking();
            b.setBookingID(rs.getInt("BookingID"));
            b.setCustomerID(rs.getInt("CustomerID"));
            
            b.setVehicleID(rs.getInt("VehicleID")); // Set ID xe
            b.setLicensePlate(rs.getString("LicensePlate")); // Set Biển số để in ra màn hình
            
            b.setServiceType(rs.getString("ServiceType"));
            // ... các set thuộc tính khác ...
            list.add(b);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}

    // ==========================================
    // 4. HỦY BOOKING VÀ TỰ ĐỘNG HOÀN TIỀN / TẶNG VOUCHER (THEO ẢNH LOGIC)
    // ==========================================
  // ==========================================
    // 4. HỦY BOOKING VÀ TỰ ĐỘNG HOÀN TIỀN / TẶNG VOUCHER
    // ==========================================
    public String cancelAndRefundBooking(int bookingID, int custID) {
        // Đã sửa lỗi SQL: Dùng LEFT(BookingTime, 5) để lấy giờ bắt đầu trước khi ép kiểu
        String sqlCheck = "SELECT CustomerID, Price, PaymentStatus, BookingStatus, "
            + "DATEDIFF(MINUTE, GETDATE(), CAST(CONCAT(CAST(BookingDate AS DATE), ' ', LEFT(BookingTime, 5)) AS DATETIME)) AS MinutesLeft "
            + "FROM Bookings WHERE BookingID = ?";
        
        String sqlCancel  = "UPDATE Bookings SET BookingStatus='Cancelled', PaymentStatus=? WHERE BookingID=? AND CustomerID=?";
        String sqlRefund  = "UPDATE Customers SET Wallet = Wallet + ? WHERE CustomerID = ?";
        String sqlVoucher = "INSERT INTO Vouchers (CustomerID, Code, DiscountPercent, ExpireAt) VALUES (?,?,?,?)";

        Connection cn = null;
        try {
            cn = DBContext.getConnection();
            if (cn == null) return "Lỗi kết nối cơ sở dữ liệu.";
            
            cn.setAutoCommit(false); // Kích hoạt Transaction an toàn dữ liệu

            int ownerID = -1, t = 0; 
            double price = 0;
            String paymentStatus = "", bookingStatus = "";

            // --- Đọc dữ liệu kiểm tra đơn đặt ---
            try (PreparedStatement st = cn.prepareStatement(sqlCheck)) {
                st.setInt(1, bookingID);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        ownerID       = rs.getInt("CustomerID");
                        price         = rs.getDouble("Price");
                        paymentStatus = rs.getString("PaymentStatus");
                        bookingStatus = rs.getString("BookingStatus");
                        t             = rs.getInt("MinutesLeft");
                    }
                }
            }

            // Kiểm tra tính hợp lệ cơ bản
            if (ownerID == -1)      { cn.rollback(); return "Không tìm thấy lịch đặt."; }
            if (ownerID != custID)  { cn.rollback(); return "Bạn không có quyền hủy lịch đặt này."; }
            if ("Cancelled".equalsIgnoreCase(bookingStatus) || "Completed".equalsIgnoreCase(bookingStatus)) {
                cn.rollback(); return "Lịch đặt này đã được xử lý hoặc đã hoàn thành, không thể hủy.";
            }

            // --- Áp dụng cây điều kiện phân hoạch hoàn tiền ---
            double refundPercent = 0.0;
            boolean giveVoucher = false;

            if (t > 1800) {
                refundPercent = 1.00;        // T > 1800 (Còn > 30 tiếng) -> Hoàn 100%
            } else if (t > 1440) {
                refundPercent = 0.75;        // 1440 < T <= 1800 (Còn 24-30 tiếng) -> Hoàn 75%
            } else if (t > 720) {
                refundPercent = 0.50;        // 720 < T <= 1440 (Còn 12-24 tiếng) -> Hoàn 50%
            } else if (t > 120) {
                refundPercent = 0.25;        // 120 < T <= 720 (Còn 2-12 tiếng) -> Hoàn 25%
            } else if (t > 20) {
                refundPercent = 0.00;        // 20 < T <= 120 (Còn 20 phút - 2 tiếng) -> Hoàn 0%
            } else {
                refundPercent = 0.00;        // T <= 20 (Còn <= 20 phút, hoặc đã quá giờ) -> Hoàn 0% + Tặng mã 10%
                giveVoucher = true;
            }

            boolean isPaid       = "Paid".equalsIgnoreCase(paymentStatus);
            double  refundAmount = isPaid ? price * refundPercent : 0;
            String  nextPayment  = (isPaid && refundAmount > 0) ? "Refunded" : paymentStatus;

            // 1. Cập nhật trạng thái lịch đặt sang 'Cancelled'
            try (PreparedStatement st = cn.prepareStatement(sqlCancel)) {
                st.setString(1, nextPayment);
                st.setInt(2, bookingID);
                st.setInt(3, custID);
                st.executeUpdate();
            }

            // 2. Thực hiện cộng tiền hoàn lại vào Ví tài khoản của khách
            if (refundAmount > 0) {
                try (PreparedStatement st = cn.prepareStatement(sqlRefund)) {
                    st.setDouble(1, refundAmount);
                    st.setInt(2, ownerID);
                    st.executeUpdate();
                }
            }

            // 3. Khởi tạo và cấp tặng voucher giảm giá 10%
            String voucherCode = null;
            if (giveVoucher) {
                voucherCode = "SAVE10-" + bookingID + "-" + (System.currentTimeMillis() % 100000);
                try (PreparedStatement st = cn.prepareStatement(sqlVoucher)) {
                    st.setInt(1, ownerID);
                    st.setString(2, voucherCode);
                    st.setInt(3, 10);
                    st.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
                    st.executeUpdate();
                }
            }

            cn.commit(); 

            // Trả về thông điệp
            int pct = (int) (refundPercent * 100);
            if (giveVoucher) {
                return "Đã hủy lịch. Do thời gian tới giờ hẹn còn dưới 20 phút nên không hoàn tiền, tặng bạn mã giảm 10%: " + voucherCode;
            }
            if (refundAmount > 0) {
                return "Đã hủy lịch. Hệ thống đã hoàn lại " + pct + "% tiền (" + String.format("%,.0f", refundAmount) + " VNĐ) vào ví của bạn!";
            }
            if (isPaid) {
                return "Đã hủy lịch thành công. Theo quy định khung giờ này mức hoàn tiền là 0%.";
            }
            return "Đã hủy lịch đặt thành công (Hệ thống không hoàn tiền do đơn chưa thanh toán).";

        } catch (Exception e) {
            if (cn != null) {
                try { cn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return "Lỗi hệ thống khi xử lý hủy lịch.";
        } finally {
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    // ==========================================
    // 5. CHECK-IN ĐƠN ĐẶT LỊCH
    // ==========================================
    public boolean checkInBooking(int bookingID) {
        String sql = "UPDATE Bookings "
                   + "SET BookingStatus = 'Checked-in' "
                   + "WHERE BookingID = ? AND BookingStatus IN ('Pending', 'Confirmed')";

        try (Connection cn = DBContext.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {

            st.setInt(1, bookingID);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // 6. XÁC NHẬN HOÀN TẤT THANH TOÁN QR / TẠI QUẦY
    // ==========================================
    public int completePayment(int bookingID) {
    String sqlInfo = "SELECT CustomerID, Price FROM Bookings WHERE BookingID = ?";
    String sqlPay  = "UPDATE Bookings SET PaymentStatus='Paid', BookingStatus='Confirmed' "
                   + "WHERE BookingID=? AND PaymentStatus<>'Paid'";

    Connection cn = null;
    try {
        cn = DBContext.getConnection();
        cn.setAutoCommit(false);

        int customerID = -1;
        double price = 0;
        try (PreparedStatement st = cn.prepareStatement(sqlInfo)) {
            st.setInt(1, bookingID);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    customerID = rs.getInt("CustomerID");
                    price = rs.getDouble("Price");
                }
            }
        }

        int rows;
        try (PreparedStatement st = cn.prepareStatement(sqlPay)) {
            st.setInt(1, bookingID);
            rows = st.executeUpdate();
        }

        int earnedPoints = 0;
        if (rows > 0 && customerID != -1) {
            earnedPoints = new LoyaltyDAO().earnPointsOnPayment(cn, customerID, price);
        }

        cn.commit();
        return earnedPoints; // số điểm vừa cộng (0 nếu đơn đã Paid trước đó)

    } catch (Exception e) {
        if (cn != null) {
            try { cn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
        }
        e.printStackTrace();
        return -1; // lỗi
    } finally {
        if (cn != null) {
            try { cn.setAutoCommit(true); cn.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }
}

    // Tính tổng chi tiêu
    // Tính tổng chi tiêu (Đã tối ưu hóa bằng lệnh SUM của SQL và sửa lỗi tên cột)
    public int getTotalSpentByCustomer(int custID) {
        // Dùng đúng cột PaymentStatus và giá trị 'Paid'
        String sql = "SELECT SUM(Price) AS TotalSpent FROM Bookings WHERE CustomerID = ? AND PaymentStatus = 'Paid'";
        try (Connection cn = DBContext.getConnection(); 
             PreparedStatement st = cn.prepareStatement(sql)) {
            
            st.setInt(1, custID);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("TotalSpent"); // SQL tự cộng tổng tiền các đơn đã thanh toán
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return 0;
    }
    
    
    // Cập nhật hạng thành viên
    public void updateTier(int custID, int tierID) {
        String sql = "UPDATE Customers SET TierID = ? WHERE CustomerID = ?";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, tierID);
            st.setInt(2, custID);
            st.executeUpdate();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    // ==========================================
    // 7. LẤY KHUNG GIỜ ĐÃ BẬN TRONG NGÀY
    // ==========================================
    public List<String> getBusySlots(String bookingDate) {
        List<String> busySlots = new ArrayList<>();
        String sql = "SELECT BookingTime FROM Bookings "
                   + "WHERE CAST(BookingDate AS DATE) = ? "
                   + "AND BookingStatus <> 'Cancelled'";

        try (Connection cn = DBContext.getConnection();
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

    // ==========================================
    // 8. TỰ ĐỘNG QUÉT LỊCH QUÁ HẠN SANG COMPLETED
    // ==========================================
    public void updateExpiredBookings() {
        String sql = "UPDATE Bookings "
                   + "SET BookingStatus = 'Completed' "
                   + "WHERE BookingStatus = 'Pending' "
                   + "AND ( "
                   + "    BookingDate < CAST(GETDATE() AS DATE) "
                   + "    OR ( "
                   + "        BookingDate = CAST(GETDATE() AS DATE) "
                   + "        AND LEFT(BookingTime,5) <= CONVERT(VARCHAR(5), GETDATE(), 108) "
                   + "      ) "
                   + " )";

        try (Connection cn = DBContext.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {

            int rows = st.executeUpdate();
            if (rows > 0) {
                System.out.println("Updated expired bookings: " + rows);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    
    public int addBookingAndGetId(Booking b) {
        String sql = "INSERT INTO Bookings (CustomerID, VehicleID, ServiceType, BookingDate, BookingTime, BookingStatus, Price, PaymentStatus, VoucherID) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection cn = DBContext.getConnection(); 
             PreparedStatement st = cn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, b.getCustomerID());
            st.setInt(2, b.getVehicleID());
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
    
    public List<Voucher> getAllActiveVouchers() {
    List<Voucher> list = new ArrayList<>();
    // Thêm điều kiện Quantity > 0 để không hiện các voucher đã hết
    String sql = "SELECT * FROM Vouchers WHERE ExpiryDate >= CAST(GETDATE() AS DATE) AND Quantity > 0";
    
    try (Connection cn = DBContext.getConnection(); 
         PreparedStatement st = cn.prepareStatement(sql); 
         ResultSet rs = st.executeQuery()) {
         
        while (rs.next()) {
            list.add(new Voucher(
                rs.getInt("VoucherID"), 
                rs.getString("Code"), 
                rs.getInt("DiscountPercent"), 
                rs.getString("ExpiryDate"), 
                rs.getInt("PointCost"),    // Phải để trong ngoặc kép
                rs.getInt("Quantity"),     // Phải để trong ngoặc kép
                rs.getString("Description") // Thêm Description nếu bạn đã thêm vào DB
            ));
        }
    } catch (Exception e) { 
        e.printStackTrace(); 
    }
    return list;
}
     
     public void confirmPayment(int bookingID) {
        String sql = "UPDATE Bookings SET PaymentStatus = 'Paid', BookingStatus = 'Pending' WHERE BookingID = ?";
        try (Connection cn = DBContext.getConnection(); PreparedStatement st = cn.prepareStatement(sql)) {
            st.setInt(1, bookingID);
            st.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
 public ArrayList<Booking> getAllBookings() {
        ArrayList<Booking> list = new ArrayList<>();
        // Dùng JOIN để lấy LicensePlate từ bảng Vehicles
        String sql = "SELECT b.*, v.LicensePlate " +
                     "FROM Bookings b " +
                     "LEFT JOIN Vehicles v ON b.VehicleID = v.VehicleID " +
                     "ORDER BY b.BookingDate DESC"; 
        
        try (Connection cn = DBContext.getConnection();
             PreparedStatement st = cn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            
            while (rs.next()) {
                list.add(new Booking(
                    rs.getInt("BookingID"),
                    rs.getString("LicensePlate") != null ? rs.getString("LicensePlate") : "N/A",
                    rs.getString("ServiceType"),
                    rs.getString("BookingDate"),
                    rs.getString("BookingTime"),
                    rs.getDouble("Price"),
                    rs.getString("BookingStatus")// Đảm bảo cột trong DB tên là Status
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. CẬP NHẬT TRẠNG THÁI
    public boolean updateBookingStatus(int bookingID, String status) {
        String sql = "UPDATE Bookings SET Status = ? WHERE BookingID = ?";
        try (Connection cn = DBContext.getConnection();
             PreparedStatement st = cn.prepareStatement(sql)) {
            st.setString(1, status);
            st.setInt(2, bookingID);
            return st.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    
}
