package controller;

import dao.BookingDAO;
import dao.CarDAO;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.Booking;
import model.User;

@WebServlet(name = "BookingServlet", urlPatterns = {"/BookingServlet"})
public class BookingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect("login_page.jsp");
            return;
        }

        String action = request.getParameter("action");
        BookingDAO bookingDAO = new BookingDAO();
        CarDAO carDAO = new CarDAO();
        User user = (User) session.getAttribute("USER");

        if ("getSlots".equals(action)) {
            String date = request.getParameter("date");
            List<String> busySlots = bookingDAO.getBusySlots(date);
            String[] allSlots = {"07:00 - 08:00", "08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00", "18:00 - 19:00", "19:00 - 20:00", "20:00 - 21:00", "21:00 - 22:00"};
            response.setContentType("text/html;charset=UTF-8");
            StringBuilder html = new StringBuilder();
            for (String slot : allSlots) {
                boolean disabled = busySlots.contains(slot);
                if (LocalDate.parse(date).equals(LocalDate.now()) && LocalTime.parse(slot.split("-")[0].trim()).isBefore(LocalTime.now())) disabled = true;
                String slotId = slot.replace(" ", "").replace("-", "");
                html.append("<div class='col-6 col-md-3'><input type='radio' class='btn-check' name='bookingTime' id='slot").append(slotId).append("' value='").append(slot).append("' ").append(disabled ? "disabled" : "").append(" required><label class='btn ").append(disabled ? "btn-secondary" : "btn-outline-warning").append(" w-100' for='slot").append(slotId).append("'>").append(slot).append("</label></div>");
            }
            response.getWriter().write(html.toString());
            return;
        }

        // ĐOẠN ĐƯỢC CHỈNH SỬA LẠI THEO YÊU CẦU CỦA BẠN:
        if ("pay".equals(action)) {
            String idStr = request.getParameter("id");
            try {
                if (idStr != null && !idStr.isEmpty()) {
                    int bookingId = Integer.parseInt(idStr);
                    
                    // Thực hiện gọi hàm cập nhật Database cũ của bạn
                    bookingDAO.confirmPayment(bookingId);
                    
                    // Lưu thông báo thành công vào Session để mang qua trang index.jsp
                    session.setAttribute("toastMessage", "Đã đặt lịch thành công!");
                    
                    // Chuyển hướng trực tiếp về trang index.jsp thay vì quay lại BookingServlet
                    response.sendRedirect("index.jsp");
                    return;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            // Đề phòng trường hợp lỗi hoặc không tìm thấy ID, chuyển về trang chủ an toàn
            response.sendRedirect("index.jsp");
            return;
        }

        LocalDate today = LocalDate.now();
        int maxDays = (user.getTierID() == 2) ? 10 : (user.getTierID() == 3) ? 12 : (user.getTierID() == 4) ? 14 : 7;
        request.setAttribute("MIN_DATE", today.toString());
        request.setAttribute("MAX_DATE", today.plusDays(maxDays).toString());
        request.setAttribute("SERVICE", request.getParameter("service") != null ? request.getParameter("service") : "Basic");
        request.setAttribute("CAR_LIST", carDAO.getCars(user.getCustID()));
        request.setAttribute("BOOKING_LIST", bookingDAO.getBookingsByCustomer(user.getCustID()));
        request.setAttribute("VOUCHER_LIST", bookingDAO.getAllActiveVouchers());
        request.getRequestDispatcher("Booking.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("USER");
        BookingDAO dao = new BookingDAO();

        try {
            Booking booking = new Booking();
            booking.setCustomerID(user.getCustID());
            booking.setLicensePlate(request.getParameter("licensePlate"));
            booking.setServiceType(request.getParameter("serviceType"));
            booking.setBookingDate(request.getParameter("bookingDate"));
            booking.setBookingTime(request.getParameter("bookingTime"));
            
            String vId = request.getParameter("voucherID");
            double discount = 0;
            if (vId != null && !vId.isEmpty()) {
                booking.setVoucherID(Integer.parseInt(vId));
                discount = dao.getDiscountPercent(Integer.parseInt(vId));
            }

            double price = booking.getServiceType().equalsIgnoreCase("Premium") ? 149000 : 
                           booking.getServiceType().equalsIgnoreCase("Detail") ? 299000 : 99000;
            booking.setPrice(price - (price * discount / 100));
            
            // LƯU TẠM THỜI VỚI TRẠNG THÁI PAYMENTPENDING
            booking.setStatus("PaymentPending"); 
            booking.setPaymentStatus("Unpaid");

            int newId = dao.addBookingAndGetId(booking);
            if (newId > 0) {
                response.sendRedirect("payment.jsp?id=" + newId + "&service=" + booking.getServiceType() + "&price=" + booking.getPrice());
            } else {
                session.setAttribute("ERROR", "Lỗi lưu Database.");
                response.sendRedirect("BookingServlet");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("BookingServlet");
        }
    }
}
