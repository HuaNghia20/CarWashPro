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

/**
 * @author Toanla
 */
@WebServlet(name = "CarServlet", urlPatterns = {"/cars"})
public class CarServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Kiểm tra bảo mật Session (TV1 nhiệm vụ bảo vệ trang)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect("login_page.jsp");
            return;
        }

        User user = (User) session.getAttribute("USER");
        CarDAO carDAO = new CarDAO();

        // 2. Xử lý nghiệp vụ XÓA XE (TV4) nhận request từ thẻ <a> trên giao diện cars.jsp
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            String idStr = request.getParameter("vehicleId");
            if (idStr != null && !idStr.trim().isEmpty()) {
                try {
                    int vehicleId = Integer.parseInt(idStr.trim());
                    carDAO.deleteCar(vehicleId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            // Xóa xong quay trở lại trang danh sách xe sạch sẽ
            response.sendRedirect("cars");
            return;
        }

        // 3. Mặc định lấy danh sách xe (Có thể có 0 xe, 1 xe, hoặc nhiều xe) đẩy sang cars.jsp hiển thị
        ArrayList<Car> carList = carDAO.getCars(user.getCustID());
        request.setAttribute("CAR_LIST", carList);
        
        // Forward sang file giao diện cars.jsp để render bảng dữ liệu
        request.getRequestDispatcher("cars.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Thiết lập bảng mã UTF-8 để không bị lỗi font Tiếng Việt (màu sơn, hãng xe) khi đẩy vào SQL Server
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect("login_page.jsp");
            return;
        }

        User user = (User) session.getAttribute("USER");
        CarDAO carDAO = new CarDAO();
        String action = request.getParameter("action");

        // Đọc dữ liệu nhập từ các ô input của form cars.jsp
        String plate = request.getParameter("licensePlate");
        String brand = request.getParameter("brand");
        String model = request.getParameter("model");
        String color = request.getParameter("color");

        if ("add".equals(action)) { 
            // Nghiệp vụ THÊM XE MỚI (TV3)
            // Khởi tạo thực thể Car (Id tự tăng trong DB nên truyền tạm là 0, ngày tạo tự sinh)
            Car car = new Car(0, plate, model, brand, color, null, user.getCustID());
            carDAO.insertCar(car);
            
        } else if ("update".equals(action)) { 
            // Nghiệp vụ CẬP NHẬT/SỬA XE (TV4)
            String vehicleId = request.getParameter("vehicleId");
            if (vehicleId != null && !vehicleId.trim().isEmpty()) {
                carDAO.updateCar(vehicleId, plate, brand, model, color);
            }
        }

        // Sau khi hoàn thành thêm hoặc sửa, chuyển hướng (Redirect) về doGet của CarServlet để cập nhật lại bảng
        response.sendRedirect("cars");
    }

    @Override
    public String getServletInfo() {
        return "Car Management Controller";
    }
}