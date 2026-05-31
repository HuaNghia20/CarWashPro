package controller;

import dao.UserDAO;
import dao.CarDAO; 
import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig; // BẮT BUỘC PHẢI CÓ
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.User;
import model.Car; 

@WebServlet(name="register", urlPatterns={"/register"})
@MultipartConfig // BẮT BUỘC PHẢI CÓ ĐỂ ĐỌC ĐƯỢC FORM CÓ TẢI ẢNH
public class register extends HttpServlet {
   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // --- 1. LẤY THÔNG TIN CÁ NHÂN ---
        String fullname = request.getParameter("txtfullname");
        String email = request.getParameter("txtemail");
        String password = request.getParameter("txtpassword");
        String phone = request.getParameter("txtphone"); // Đã lấy SĐT

        // --- 2. LẤY THÔNG TIN PHƯƠNG TIỆN ---
        String plate = request.getParameter("txtplate");
        String brand = request.getParameter("txtbrand");
        String model = request.getParameter("txtmodel");
        String color = request.getParameter("txtcolor");
        
        UserDAO userDAO = new UserDAO();
        CarDAO carDAO = new CarDAO();
        
        User found = userDAO.getUser(email);
        if(found == null){
            
            // Đóng gói User
            User c = new User();
            c.setFullname(fullname);
            c.setEmail(email);
            c.setPassword(password);
            c.setPhone(phone); // Đã lưu SĐT
            c.setStatus(true);
            c.setTierID(1); 
            c.setCreateAt(new Date(System.currentTimeMillis()));
            
            // Đẩy User xuống Database
            int rs = userDAO.createNewUser(c);
            
            if(rs >= 1){
                // --- ĐĂNG KÝ USER THÀNH CÔNG -> TIẾN HÀNH LƯU XE ---
                User newUser = userDAO.getUser(email);
                
                if (newUser != null && plate != null && !plate.trim().isEmpty()) {
                    Car car = new Car(0, plate, model, brand, color, null, newUser.getCustID());
                    carDAO.insertCar(car);
                }
                
                response.sendRedirect("login_page.jsp");
            }
            else {
                // In lỗi SQL Server ra màn hình đỏ nếu thất bại
                response.getWriter().print("<div style='background:#241a1a; color:#e74c3c; padding:20px; text-align:center; font-family:sans-serif;'>");
                response.getWriter().print("<h2>LỖI TỪ CƠ SỞ DỮ LIỆU:</h2>");
                response.getWriter().print("<p>" + UserDAO.lastError + "</p>");
                response.getWriter().print("</div>");
            }
        } else {
            String msg = "Email này đã được đăng ký trong hệ thống!";
            request.setAttribute("ERROR", msg);
            request.getRequestDispatcher("/register_page.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.sendRedirect("register_page.jsp"); 
    } 

    @Override
    public String getServletInfo() {
        return "Register with Car and Multipart support";
    }
}