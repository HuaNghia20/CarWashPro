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
@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/ChangePasswordServlet"})
public class ChangePasswordServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect("login_page.jsp");
            return;
        }
        request.getRequestDispatcher("changePassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect("login_page.jsp");
            return;
        }

        User user = (User) session.getAttribute("USER");
        
        String oldPass = request.getParameter("oldPassword").trim();
        String newPass = request.getParameter("newPassword").trim();
        String confirmPass = request.getParameter("confirmPassword").trim();

        if (!user.getPassword().trim().equals(oldPass)) {
            request.setAttribute("ERROR", "Mật khẩu cũ hiện tại không chính xác!");
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            
        } else if (oldPass.equals(newPass)) {
            request.setAttribute("ERROR", "Mật khẩu mới không được trùng với mật khẩu cũ hiện tại!");
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            
        } else if (!newPass.equals(confirmPass)) {
            request.setAttribute("ERROR", "Xác nhận mật khẩu mới không trùng khớp!");
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            
        } else {
            UserDAO dao = new UserDAO();
            if (dao.changePassword(user.getCustID(), newPass)) {
                
                session.invalidate(); 
                
                HttpSession newSession = request.getSession(true);
                newSession.setAttribute("SUCCESS_MSG", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại bằng mật khẩu mới.");
                
                response.sendRedirect("login_page.jsp");
                return;
                
            } else {
                request.setAttribute("ERROR", "Thay đổi mật khẩu thất bại do lỗi hệ thống SQL!");
                request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Change Password Controller";
    }
}