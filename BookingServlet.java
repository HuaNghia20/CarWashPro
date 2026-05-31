package controller;

import dao.BookingDAO;
import dao.CarDAO;
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

        // 1. Chức năng hủy lịch
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            String id = request.getParameter("id");
            if (id != null) {
                bookingDAO.deleteBooking(Integer.parseInt(id));
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
            
            // QUAN TRỌNG: Dùng LicensePlate thay vì VehicleID để khớp với Database
            booking.setLicensePlate(request.getParameter("licensePlate"));
            
            booking.setServiceType(request.getParameter("serviceType"));
            booking.setBookingDate(request.getParameter("bookingDate"));
            booking.setBookingTime(request.getParameter("bookingTime"));
            booking.setStatus("Pending");

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