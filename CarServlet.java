package controller;

import dao.CarDAO;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import model.Car;
import model.User;

@WebServlet(name = "CarServlet", urlPatterns = {"/cars"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize      = 1024 * 1024 * 10,
        maxRequestSize   = 1024 * 1024 * 50)
public class CarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect("login_page.jsp");
            return;
        }
        User user = (User) session.getAttribute("USER");
        CarDAO carDAO = new CarDAO();

        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            String idStr = request.getParameter("vehicleId");
            if (idStr != null && !idStr.trim().isEmpty()) {
                try { carDAO.deleteCar(Integer.parseInt(idStr.trim())); }
                catch (NumberFormatException e) { e.printStackTrace(); }
            }
            response.sendRedirect("cars");
            return;
        }

        request.setAttribute("CAR_LIST", carDAO.getCars(user.getCustID()));
        request.getRequestDispatcher("cars.jsp").forward(request, response);
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
        CarDAO carDAO = new CarDAO();
        String action = request.getParameter("action");

        String plate = request.getParameter("licensePlate");
        String brand = request.getParameter("brand");
        String model = request.getParameter("model");
        String color = request.getParameter("color");

        if ("add".equals(action)) {
            // Kiểm tra biển số đã tồn tại trong toàn hệ thống chưa
            if (carDAO.isLicensePlateExists(plate, user.getCustID())) {
                request.setAttribute("error", "Biển số " + plate + " đã tồn tại trong hệ thống!");
                request.setAttribute("CAR_LIST", carDAO.getCars(user.getCustID()));
                request.getRequestDispatcher("cars.jsp").forward(request, response);
            } else {
                String carFileName   = saveImage(request, "txtcarimage");
                String plateFileName = saveImage(request, "txtplateimage");
                Car car = new Car(0, plate, model, brand, color, null,
                                  user.getCustID(), carFileName, plateFileName);
                carDAO.insertCar(car);
                response.sendRedirect("cars");
            }

        } else if ("update".equals(action)) {
            String vehicleId = request.getParameter("vehicleId");
            // FIX: KHÔNG truyền plate vào updateCar — biển số không được thay đổi
            String newCarImage   = saveImage(request, "txtcarimage");
            String newPlateImage = saveImage(request, "txtplateimage");
            carDAO.updateCar(vehicleId, brand, model, color, newCarImage, newPlateImage);
            response.sendRedirect("cars");
        }
    }

    private String saveImage(HttpServletRequest request, String partName)
            throws IOException, ServletException {
        Part part = request.getPart(partName);
        if (part == null || part.getSubmittedFileName() == null
                || part.getSubmittedFileName().trim().isEmpty()
                || part.getSize() == 0) {
            return null;
        }
        String fileName   = System.currentTimeMillis() + "_" + part.getSubmittedFileName();
        String uploadPath = getServletContext().getRealPath("/uploads");
        java.io.File uploadDir = new java.io.File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();
        part.write(uploadPath + java.io.File.separator + fileName);
        return fileName;
    }
}
