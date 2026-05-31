package controller;

import dao.UserDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.User;

@WebServlet(name = "login", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chỉ đơn thuần là ném người dùng về file giao diện khi họ gõ url /login
        request.getRequestDispatcher("/login_page.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Thiết lập bảng mã (Giống hệt register)
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            // Lấy email, password trong form login
            String email = request.getParameter("txtemail");
            String password = request.getParameter("txtpassword");
            
            UserDAO d = new UserDAO();
            User rs = d.getUser(email, password);
            
            if (rs == null) {
                // Đăng nhập thất bại
                String msg = "Email hoặc mật khẩu không chính xác!";
                request.setAttribute("ERROR", msg);
                request.getRequestDispatcher("/login_page.jsp").forward(request, response);
            } else {
                // Kiểm tra trạng thái tài khoản
                if (rs.isStatus()) {
                    // Lưu dữ liệu vào session để sử dụng tiếp
                    request.getSession().setAttribute("USER", rs);
                    
                    // Chuyển hướng về trang chủ
                    response.sendRedirect("index.jsp");
                } else {
                    String msg = "Tài khoản của bạn đã bị khóa hoặc vô hiệu hóa!";
                    request.setAttribute("ERROR", msg);
                    request.getRequestDispatcher("/login_page.jsp").forward(request, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("ERROR", "Lỗi kết nối cơ sở dữ liệu!");
            request.getRequestDispatcher("/login_page.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Login Controller";
    }
}