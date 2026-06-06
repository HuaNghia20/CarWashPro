<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="model.User, model.Car, model.Booking, java.util.ArrayList, java.time.LocalDate"%>
<% 
    // 1. Bảo vệ trang bằng Session
    User user = (User) session.getAttribute("USER"); 
    if(user == null) { 
        response.sendRedirect("login_page.jsp"); 
        return; 
    } 

    // 2. Lấy danh sách xe và lịch sử đặt từ Servlet
    ArrayList<Car> carList = (ArrayList<Car>) request.getAttribute("CAR_LIST");
    ArrayList<Booking> bookingList = (ArrayList<Booking>) request.getAttribute("BOOKING_LIST");

    // 3. Xử lý thông tin gói dịch vụ từ URL
    String serviceParam = request.getParameter("service");
    if(serviceParam == null || serviceParam.isEmpty()){
        serviceParam = (String) request.getAttribute("SERVICE"); // Fallback nếu lấy từ request
    }
    
    String serviceName = "";
    String servicePrice = "";
    String serviceDesc = "";

    if ("Premium".equals(serviceParam)) {
        serviceName = "Nâng Cao";
        servicePrice = "149.000 ₫";
        serviceDesc = "Bao gồm gói Cơ Bản, cộng thêm hút bụi toàn bộ nội thất, lau kính và khử mùi diệt khuẩn.";
    } else if ("Detail".equals(serviceParam)) {
        serviceName = "Chi Tiết";
        servicePrice = "299.000 ₫";
        serviceDesc = "Chăm sóc toàn diện: Vệ sinh khoang máy, bảo dưỡng da nội thất và đánh bóng sơn chuyên sâu.";
    } else {
        serviceParam = "Basic"; 
        serviceName = "Rửa Xe Cơ Bản";
        servicePrice = "99.000 ₫";
        serviceDesc = "Làm sạch bề mặt ngoài bằng bọt tuyết, xịt khô.";
    }

    // 4. Xử lý giới hạn lịch 7 ngày tới
    LocalDate today = LocalDate.now();
    LocalDate nextWeek = today.plusDays(7);
    String minDate = today.toString();      
    String maxDate = nextWeek.toString();   
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt Lịch - CarWash Pro</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">

    <style>
        :root {
            --bg-main: #0a0b0d;
            --bg-card: #14161a;
            --yellow-accent: #ffcc00;
            --border-color: #2a2d35;
            --text-main: #e0e4e8;
        }
        body { background: var(--bg-main); color: var(--text-main); font-family: 'Plus Jakarta Sans', sans-serif; padding: 40px 0; }
        .container-box { max-width: 800px; margin: 0 auto; } /* Tăng width để chứa bảng lịch sử đẹp hơn */
        
        .card-custom { 
            background: var(--bg-card); 
            border: 1px solid var(--border-color); 
            padding: 30px; 
            border-radius: 16px; 
            box-shadow: 0 10px 30px rgba(0,0,0,0.5); 
            margin-bottom: 25px;
        }
        
        .btn-link-back { color: #8a919e; text-decoration: none; font-weight: 600; transition: 0.3s; display: inline-block; margin-bottom: 20px; }
        .btn-link-back:hover { color: var(--yellow-accent); }
        
        .selected-package {
            border: 1px solid var(--yellow-accent);
            background: rgba(255, 204, 0, 0.05);
            padding: 20px;
            border-radius: 12px;
            text-align: center;
            margin-bottom: 25px;
            box-shadow: 0 0 15px rgba(255, 204, 0, 0.2);
        }

        .form-label { color: #8a919e; font-weight: 600; font-size: 0.9rem; margin-bottom: 8px; }
        .form-control, .form-select {
            background-color: #0d0f12;
            border: 1px solid var(--border-color);
            color: #fff !important; 
            padding: 12px 15px;
            border-radius: 8px;
            box-sizing: border-box;
        }
        .form-control:focus, .form-select:focus {
            background-color: #1a1c23;
            border-color: var(--yellow-accent);
            box-shadow: 0 0 8px rgba(255, 204, 0, 0.4);
        }
        
        /* Chỉnh màu icon lịch và đồng hồ thành trắng trên nền tối */
        input[type="date"]::-webkit-calendar-picker-indicator,
        input[type="time"]::-webkit-calendar-picker-indicator {
            filter: invert(1);
            cursor: pointer;
        }
        
        .btn-submit {
            background-color: var(--yellow-accent);
            color: #000;
            font-weight: 700;
            border: none;
            padding: 12px;
            border-radius: 8px;
            width: 100%;
            transition: 0.3s;
            margin-top: 15px;
        }
        .btn-submit:hover {
            background-color: #ffda33;
            box-shadow: 0 0 15px rgba(255, 204, 0, 0.4);
            transform: scale(1.02);
        }

        /* Table Lịch sử */
        .table-dark { --bs-table-bg: transparent; }
        .table thead th { color: var(--yellow-accent); border-bottom: 2px solid var(--border-color); text-transform: uppercase; font-size: 0.85rem;}
        .table tbody td { vertical-align: middle; border-bottom: 1px solid var(--border-color); color: #fff;}
    </style>
</head><div class="alert alert-warning">
    <i class="fa-solid fa-star"></i> Điểm tích lũy: <strong><%= user.getPoint() %></strong>
</div>
<body>

<div class="container container-box">
    
    <a href="${pageContext.request.contextPath}/index.jsp" class="btn-link-back">
        <i class="fa-solid fa-arrow-left me-2"></i>Quay lại trang chủ
    </a>

    <div class="card-custom">
        <h2 class="text-center fw-bold mb-4" style="color: var(--yellow-accent);">
            <i class="fa-solid fa-calendar-check me-2"></i>Tiến Hành Đặt Lịch
        </h2>

        <div class="selected-package">
            <h4 class="fw-bold" style="color: var(--yellow-accent);"><%= serviceName %></h4>
            <h5 class="fw-bold text-white mb-2"><%= servicePrice %></h5>
            <p class="text-white mb-0" style="font-size: 0.95rem; opacity: 0.9;"><%= serviceDesc %></p>
        </div>

        <form action="BookingServlet" method="POST">
            <input type="hidden" name="serviceType" value="<%= serviceParam %>">

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Người đặt lịch</label>
                    <input type="text" value="<%= user.getFullname() %>" class="form-control" readonly>
                </div>

                <div class="col-md-6 mb-3">
                    <label class="form-label">Chọn Xe (Biển số)</label>
                    <select name="licensePlate" class="form-select form-control" required>
                        <% if(carList != null && !carList.isEmpty()){ %>
                            <% for(Car c : carList){ %>
                                <option value="<%=c.getLicensePlate()%>">
                                    <%=c.getLicensePlate()%> - <%=c.getBrand()%> <%=c.getModel()%>
                                </option>
                            <% } %>
                        <% } else { %>
                            <option value="">Chưa có xe. Vui lòng thêm xe!</option>
                        <% } %>
                    </select>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Ngày Đặt (Trong 7 ngày tới)</label>
                    <input type="date" name="bookingDate" class="form-control" min="<%= minDate %>" max="<%= maxDate %>" required>
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label">Giờ Đặt</label>
                    <input type="time" name="bookingTime" class="form-control" min="01:00" max="23:59" required>
                </div>
            </div>

            <button type="submit" class="btn-submit">
                <i class="fa-solid fa-paper-plane me-2"></i>Xác Nhận Booking
            </button>
        </form>
    </div>

    <div class="card-custom" id="booking-history">
        <h4 class="fw-bold mb-4" style="color: var(--yellow-accent);">
            <i class="fa-solid fa-clock-rotate-left me-2"></i>Lịch Đã Đặt
        </h4>

        <div class="table-responsive">
            <table class="table table-dark table-hover mt-3">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Biển Số Xe</th>
                        <th>Gói Dịch Vụ</th>
                        <th>Giá Tiền</th>
                        <th>Ngày Đặt</th>
                        <th>Giờ</th>
                        <th>Trạng thái</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    if(bookingList != null && !bookingList.isEmpty()){
                        for(Booking b : bookingList){
                            String historyServiceName;
                            String historyPrice;

                            if("Basic".equals(b.getServiceType())){
                                historyServiceName = "Rửa Xe Cơ Bản";
                                historyPrice = "99.000 ₫";
                            }else if("Premium".equals(b.getServiceType())){
                                historyServiceName = "Nâng Cao";
                                historyPrice = "149.000 ₫";
                            }else{
                                historyServiceName = "Chi Tiết";
                                historyPrice = "299.000 ₫";
                            }

                            String time = b.getBookingTime();
                            if(time != null && time.length() >= 5){
                                time = time.substring(0,5);
                            }
                %>
                    <tr>
                        <td style="color: var(--yellow-accent); font-weight: 700;">#<%=b.getBookingID()%></td>
                        <td class="fw-bold"><span class="badge bg-secondary"><%=b.getLicensePlate()%></span></td>
                        <td><%=historyServiceName%></td>
                        <td style="color: var(--yellow-accent);"><%=historyPrice%></td>
                        <td><%=b.getBookingDate()%></td>
                        <td><%=time%></td>
                        
                        <td>
                            <% if("Đã thanh toán".equalsIgnoreCase(b.getStatus())) { %>
                                <span class="badge bg-success">Đã thanh toán</span>
                            <% } else { %>
                                <span class="badge bg-warning text-dark">Chưa thanh toán</span>
                            <% } %>
                        </td>
                        
                        <td>
                            <div class="d-flex gap-2">
                                <% if(!"Đã thanh toán".equalsIgnoreCase(b.getStatus())) { %>
                                    <a href="payment.jsp?id=<%=b.getBookingID()%>&service=<%=historyServiceName%>&price=<%=historyPrice%>" 
                                       class="btn btn-outline-success btn-sm">
                                        <i class="fa-solid fa-money-bill-wave"></i> Thanh toán
                                    </a>
                                <% } %>
                                
                                <a href="BookingServlet?action=delete&id=<%=b.getBookingID()%>" 
                                   class="btn btn-outline-danger btn-sm" onclick="return confirm('Bạn có chắc chắn muốn hủy lịch này không?');">
                                    <i class="fa-solid fa-trash"></i> Hủy
                                </a>
                            </div>
                        </td>
                    </tr>
                <%
                        }
                    } else {
                %>
                    <tr>
                        <td colspan="8" class="text-center py-4 text-muted">
                            Bạn chưa có lịch đặt nào.
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
