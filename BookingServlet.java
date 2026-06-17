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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect("login_page.jsp");
            return;
        }

        String action = request.getParameter("action");

        // ================= AJAX LOAD SLOT =================
        if ("getSlots".equals(action)) {

            String date = request.getParameter("date");

            BookingDAO dao = new BookingDAO();
            List<String> busySlots = dao.getBusySlots(date);

            String[] allSlots = {
                "07:00 - 08:00",
                "08:00 - 09:00",
                "09:00 - 10:00",
                "10:00 - 11:00",
                "13:00 - 14:00",
                "14:00 - 15:00",
                "15:00 - 16:00",
                "16:00 - 17:00",
                "18:00 - 19:00",
                "19:00 - 20:00",
                "20:00 - 21:00",
                "21:00 - 22:00"
            };

            response.setContentType("text/html;charset=UTF-8");

            StringBuilder html = new StringBuilder();

            for (String slot : allSlots) {

                boolean isBusy = busySlots.contains(slot);
                boolean isPast = false;

                try {
                    LocalDate selectedDate = LocalDate.parse(date);

                    if (selectedDate.equals(LocalDate.now())) {

                        String startTime = slot.split("-")[0].trim();

                        LocalTime slotStart = LocalTime.parse(startTime);

                        if (slotStart.isBefore(LocalTime.now())) {
                            isPast = true;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean disabled = isBusy || isPast;

                String slotId = slot.replace(" ", "")
                        .replace("-", "");

                html.append("<div class='col-6 col-md-3'>");

                html.append("<input type='radio' class='btn-check' ")
                        .append("name='bookingTime' ")
                        .append("id='slot").append(slotId).append("' ")
                        .append("value='").append(slot).append("' ")
                        .append(disabled ? "disabled" : "")
                        .append(" required>");

                html.append("<label class='btn btn-slot-custom ")
                        .append(disabled
                                ? "btn-secondary"
                                : "btn-outline-warning")
                        .append(" w-100' ")
                        .append("for='slot").append(slotId).append("' ")
                        .append("style='")
                        .append(disabled
                                ? "cursor:not-allowed;opacity:0.5;"
                                : "")
                        .append("'>")
                        .append(slot)
                        .append("</label>");

                html.append("</div>");
            }

            response.getWriter().write(html.toString());
            return;
        }

        // ================= HỦY BOOKING =================
        User user = (User) session.getAttribute("USER");

        BookingDAO bookingDAO = new BookingDAO();

        CarDAO carDAO = new CarDAO();
        if ("delete".equals(action)) {

            String id = request.getParameter("id");

            if (id != null && !id.trim().isEmpty()) {

                bookingDAO.cancelBooking(Integer.parseInt(id));

                session.setAttribute(
                        "MESSAGE",
                        "Đã hủy lịch đặt thành công!"
                );

                response.sendRedirect("BookingServlet");
                return;
            }
        }

        // ================= LOAD TRANG BOOKING =================
        LocalDate today = LocalDate.now();

        int maxDays = 7;

        if (user.getTierID() == 2) {
            maxDays = 10;
        } else if (user.getTierID() == 3) {
            maxDays = 12;
        } else if (user.getTierID() == 4) {
            maxDays = 14;
        }

        request.setAttribute("MIN_DATE", today.toString());

        request.setAttribute(
                "MAX_DATE",
                today.plusDays(maxDays).toString()
        );

        request.setAttribute(
                "SERVICE",
                request.getParameter("service") != null
                        ? request.getParameter("service")
                        : "Basic"
        );

        request.setAttribute(
                "CAR_LIST",
                carDAO.getCars(user.getCustID())
        );

        request.setAttribute(
                "BOOKING_LIST",
                bookingDAO.getBookingsByCustomer(user.getCustID())
        );

        request.getRequestDispatcher("Booking.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("USER") == null) {

            response.sendRedirect("login_page.jsp");
            return;
        }

        User user = (User) session.getAttribute("USER");

        try {

            Booking booking = new Booking();

            booking.setCustomerID(user.getCustID());

            booking.setLicensePlate(
                    request.getParameter("licensePlate"));

            booking.setServiceType(
                    request.getParameter("serviceType"));

            booking.setBookingDate(
                    request.getParameter("bookingDate"));

            booking.setBookingTime(
                    request.getParameter("bookingTime"));

            // ================= CHẶN ĐẶT GIỜ ĐÃ QUA =================
            LocalDate bookingDate
                    = LocalDate.parse(booking.getBookingDate());

            if (bookingDate.equals(LocalDate.now())) {

                String startTime
                        = booking.getBookingTime()
                                .split("-")[0]
                                .trim();

                LocalTime slotStart
                        = LocalTime.parse(startTime);

                if (slotStart.isBefore(LocalTime.now())) {

                    session.setAttribute(
                            "ERROR",
                            "Không thể đặt lịch vào khung giờ đã qua."
                    );

                    response.sendRedirect("BookingServlet");
                    return;
                }
            }

            booking.setStatus("Pending");

            // ================= GIÁ DỊCH VỤ =================
            String service = booking.getServiceType();

            if (service != null) {

                if (service.equalsIgnoreCase("Basic")) {
                    booking.setPrice(99000);
                } else if (service.equalsIgnoreCase("Premium")) {
                    booking.setPrice(149000);
                } else if (service.equalsIgnoreCase("Detail")) {
                    booking.setPrice(299000);
                } else {
                    booking.setPrice(0);
                }
            }

            booking.setPaymentStatus("Unpaid");

            BookingDAO dao = new BookingDAO();

            if (dao.addBooking(booking)) {

                session.setAttribute(
                        "MESSAGE",
                        "Đặt lịch thành công!"
                );

                response.sendRedirect("BookingServlet");

            } else {

                session.setAttribute(
                        "ERROR",
                        "Lỗi lưu Database."
                );

                response.sendRedirect("BookingServlet");
            }

        } catch (Exception e) {

            e.printStackTrace();

            session.setAttribute(
                    "ERROR",
                    "Đã xảy ra lỗi hệ thống: "
                    + e.getMessage()
            );

            response.sendRedirect("BookingServlet");
        }
    }
}
