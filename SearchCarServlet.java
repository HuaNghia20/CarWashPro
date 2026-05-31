/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.CarDAO;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Car;
import model.User;

@WebServlet(name = "search", urlPatterns = {"/search"})
public class SearchCarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect("login_page.jsp");
            return;
        }

        User user = (User) session.getAttribute("USER");
        String keyword = request.getParameter("txt"); // Nhận tham số từ form ô tìm kiếm
        
        CarDAO carDAO = new CarDAO();
        ArrayList<Car> listResult = carDAO.searchCars(user.getCustID(), keyword);

        request.setAttribute("SEARCH_RESULT", listResult);
        request.setAttribute("KEYWORD", keyword);
        request.getRequestDispatcher("search.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Đồng bộ chuyển hướng dữ liệu doPost sang doGet để xử lý hiển thị giao diện kết quả search mẫu sạch sẽ
        String keyword = request.getParameter("txt");
        response.sendRedirect("search?txt=" + keyword);
    }
}