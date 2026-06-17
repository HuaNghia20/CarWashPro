package controller;

import dao.BookingDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.User;

@WebServlet("/BookingHistoryServlet")
public class BookingHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if(session == null
                || session.getAttribute("USER") == null){

            response.sendRedirect("login_page.jsp");
            return;
        }

        User user = (User) session.getAttribute("USER");

        BookingDAO dao = new BookingDAO();
        dao.updateExpiredBookings();
        
        String action = request.getParameter("action");

        if("cancel".equals(action)){

            int bookingID =
                    Integer.parseInt(request.getParameter("id"));

            dao.cancelBooking(bookingID);

            response.sendRedirect("BookingHistoryServlet");
            return;
        }

        request.setAttribute(
                "BOOKING_LIST",
                dao.getBookingsByCustomer(user.getCustID())
        );

        request.getRequestDispatcher("BookingHistory.jsp")
                .forward(request, response);
    }
}
