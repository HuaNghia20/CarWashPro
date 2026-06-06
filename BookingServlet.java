package controller;

import dao.BookingDAO;
import dao.CarDAO;
import dao.UserDAO; // 1. Import UserDAO
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.*;

@WebServlet(name = "BookingServlet", urlPatterns = {"/BookingServlet"})
public class BookingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect("login_page.jsp");
            return;
        }

        User user = (User) session.getAttribute("USER");
        BookingDAO bookingDAO = new BookingDAO();
        CarDAO carDAO = new CarDAO();
        UserDAO userDAO = new UserDAO(); // Khởi tạo UserDAO

        // 1. Chức năng Thao tác (Hủy lịch / Thanh toán)
        String action = request.getParameter("action");
        if (action != null) {
            String id = request.getParameter("id");
            if (id != null && !id.trim().isEmpty()) {
                int bookingId = Integer.parseInt(id.trim());
                
                if ("delete".equals(action)) {
                    bookingDAO.deleteBooking(bookingId);
                } else if ("pay".equals(action)) {
                    // 1. Cập nhật trạng thái thanh toán
                    bookingDAO.updatePaymentStatus(bookingId);
                    
                    // 2. Tính điểm và cập nhật Tier
                    String priceStr = request.getParameter("price");
                    if (priceStr != null) {
                        int amount = parsePriceToInt(priceStr);
                        int pointsEarned = amount / 1000;
                        
                        // Cộng điểm vào DB
                        userDAO.addPoints(user.getCustID(), pointsEarned);
                        
                        // TÍCH HỢP LOGIC NÂNG HẠNG (Tier)
                        // Lấy tổng chi tiêu của khách để xét hạng
                        int totalSpent = bookingDAO.getTotalSpentByCustomer(user.getCustID());
                        int newTierId = 1; // Mặc định Member
                        if (totalSpent >= 15000000) newTierId = 4; // Platinum
                        else if (totalSpent >= 6000000) newTierId = 3; // Gold
                        else if (totalSpent >= 2000000) newTierId = 2; // Silver
                        
                        // Cập nhật Tier vào DB nếu hạng thay đổi
                        if (newTierId != user.getTierID()) {
                            userDAO.updateTier(user.getCustID(), newTierId);
                            user.setTierID(newTierId);
                        }
                        
                        // Cập nhật session
                        user.setPoint(user.getPoint() + pointsEarned);
                        session.setAttribute("USER", user);
                    }
                }
                
                response.sendRedirect("BookingServlet");
                return; 
            }
        }

        // 2. Lấy gói dịch vụ
        String service = request.getParameter("service");
        if (service == null) {
            service = "Basic";
        }
        request.setAttribute("SERVICE", service);

        // 3. Lấy dữ liệu Xe và Lịch sử đưa lên giao diện
        request.setAttribute("CAR_LIST", carDAO.getCars(user.getCustID()));
        request.setAttribute("BOOKING_LIST", bookingDAO.getBookingsByCustomer(user.getCustID()));
        
        request.getRequestDispatcher("Booking.jsp").forward(request, response);
    }

    // Hàm phụ trợ bóc tách số từ chuỗi giá tiền
    private int parsePriceToInt(String priceStr) {
        try {
            String clean = priceStr.replaceAll("[^0-9]", "");
            return Integer.parseInt(clean);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect("login_page.jsp");
            return;
        }

        User user = (User) session.getAttribute("USER");
        
        try {
            Booking booking = new Booking();
            booking.setCustomerID(user.getCustID());
            booking.setLicensePlate(request.getParameter("licensePlate"));
            booking.setServiceType(request.getParameter("serviceType"));
            booking.setBookingDate(request.getParameter("bookingDate"));
            booking.setBookingTime(request.getParameter("bookingTime"));
            booking.setStatus("Chưa thanh toán");

            BookingDAO dao = new BookingDAO();
            boolean result = dao.addBooking(booking);
            
            if (result) {
                response.sendRedirect("BookingServlet");
            } else {
                request.setAttribute("ERROR", "Không thể tạo booking do lỗi Database.");
                doGet(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("ERROR", e.getMessage());
            doGet(request, response);
        }
    }
}
