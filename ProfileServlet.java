package controller;

import dao.UserDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;

/**
 * @author Toanla
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hàm này có thể để trống hoặc giữ nguyên vì chúng ta xử lý riêng biệt ở doGet và doPost
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect("login_page.jsp");
            return;
        }
        
        // Tải thông tin mới nhất từ DB lên session trước khi hiển thị profile.jsp
        User user = (User) session.getAttribute("USER");
        UserDAO dao = new UserDAO();
        User latestInfo = dao.getUser(user.getEmail());
        session.setAttribute("USER", latestInfo);
        
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect("login_page.jsp");
            return;
        }

        User user = (User) session.getAttribute("USER");
        String fullname = request.getParameter("fullname");

        UserDAO dao = new UserDAO();
        boolean success = dao.updateProfile(user.getCustID(), fullname);

        if (success) {
            request.setAttribute("MESSAGE", "Cập nhật thông tin lý lịch thành công!");
        } else {
            request.setAttribute("ERROR", "Cập nhật thông tin thất bại!");
        }
        
        // Đồng bộ lại dữ liệu mới vào session USER
        session.setAttribute("USER", dao.getUser(user.getEmail()));
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Profile Controller";
    }
}