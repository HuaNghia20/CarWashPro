<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="model.User, model.Car, model.Booking, model.Voucher, java.util.ArrayList"%>
<%
    User user = (User) session.getAttribute("USER");
    if(user == null) {
        response.sendRedirect("login_page.jsp");
        return;
    }

    ArrayList<Car> carList = (ArrayList<Car>) request.getAttribute("CAR_LIST");
    ArrayList<Voucher> voucherList = (ArrayList<Voucher>) request.getAttribute("VOUCHER_LIST");
    
    String serviceParam = request.getParameter("service") != null 
                          ? request.getParameter("service") 
                          : (String) request.getAttribute("SERVICE");

    int maxDays = 7;
    if (user != null) {
        if (user.getTierID() == 2) maxDays = 10;
        else if (user.getTierID() == 3) maxDays = 12;
        else if (user.getTierID() == 4) maxDays = 14;
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tiến Hành Đặt Lịch - CarWash Pro</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    
    <style>
        :root {
            --bg-main: #0a0b0d;
            --bg-card: #14161a;
            --border-color: #2a2d35;
            --yellow-accent: #ffcc00;
            --input-bg: #0d0f12;
            --text-main: #e0e4e8;
        }
        
        body { 
            background-color: var(--bg-main);
            color: var(--text-main);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 40px 20px;
        }

        .container-box { 
            max-width: 800px; 
            margin: 0 auto;
        }

        .btn-return {
            color: #8a919e;
            font-weight: 600;
            font-size: 0.95rem;
            transition: color 0.2s;
            display: inline-flex;
            align-items: center;
            text-decoration: none;
        }
        .btn-return:hover {
            color: var(--yellow-accent);
        }

        .card-custom { 
            background-color: var(--bg-card);
            border: 1px solid var(--border-color); 
            padding: 35px 40px; 
            border-radius: 16px; 
            margin-top: 20px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.5);
        }

        .form-label { 
            color: var(--yellow-accent); 
            font-weight: 700; 
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .form-control, .form-select { 
            background: var(--input-bg) !important; 
            border: 1px solid var(--border-color); 
            color: #ffffff !important; 
            padding: 12px 16px; 
            border-radius: 8px;
            font-weight: 500;
            font-size: 0.95rem;
            transition: border-color 0.2s;
            appearance: none;
            -webkit-appearance: none;
        }
        
        .form-control:focus, .form-select:focus {
            border-color: var(--yellow-accent);
            box-shadow: 0 0 8px var(--yellow-glow);
            outline: none;
        }

        .form-control[readonly] {
            background: rgba(255, 255, 255, 0.03) !important;
            border-color: var(--border-color);
            color: #8a919e !important;
        }

        .select-wrapper {
            position: relative;
        }
        .select-wrapper::after {
            content: "\f107";
            font-family: "Font Awesome 6 Free";
            font-weight: 900;
            position: absolute;
            right: 16px;
            top: 50%;
            transform: translateY(-50%);
            color: var(--yellow-accent);
            pointer-events: none;
        }

        .form-select option {
            background-color: #14161a;
            color: #ffffff;
        }

        .badge-limit {
            font-size: 0.8rem;
            color: var(--yellow-accent);
            font-weight: normal;
            text-transform: none;
        }

        .date-input-wrapper { position: relative; }
        .date-input-wrapper::after {
            content: "\f133";
            font-family: "Font Awesome 6 Free";
            font-weight: 900;
            position: absolute;
            right: 16px;
            top: 50%;
            transform: translateY(-50%);
            color: var(--yellow-accent);
            pointer-events: none;
        }
        #datePicker::-webkit-calendar-picker-indicator { 
            position: absolute; top: 0; left: 0; right: 0; bottom: 0; 
            width: 100%; height: 100%; cursor: pointer; 
            background: transparent; color: transparent; 
            z-index: 2;
        }

        /* LƯỚI KHUNG GIỜ GỌN GÀNG, KHÔNG BỊ TO */
        .slot-col {
            padding: 4px !important;
        }

        .slot-btn-label {
            display: flex;
            align-items: center;
            justify-content: center;
            background: #0d0f12;
            border: 1px solid var(--border-color);
            color: #ffffff;
            padding: 10px 0 !important;
            border-radius: 8px;
            font-size: 0.9rem !important;
            font-weight: 600;
            white-space: nowrap;
            cursor: pointer;
            transition: 0.2s;
            width: 100%;
        }

        .btn-check:not(:disabled) + .slot-btn-label:hover {
            border-color: var(--yellow-accent);
            color: var(--yellow-accent);
        }

        .btn-check:checked + .slot-btn-label {
            background: var(--yellow-accent) !important;
            color: #000000 !important;
            border-color: var(--yellow-accent) !important;
            font-weight: 700;
        }

        .btn-check:disabled + .slot-btn-label {
            background: rgba(255, 255, 255, 0.02) !important;
            border-color: rgba(255, 255, 255, 0.05) !important;
            color: rgba(255, 255, 255, 0.2) !important;
            text-decoration: line-through;
            cursor: not-allowed;
        }

        .btn-submit { 
            background-color: var(--yellow-accent); 
            color: #000000 !important; 
            font-weight: 700; 
            font-size: 1rem;
            border: none; 
            padding: 14px; 
            width: 100%; 
            border-radius: 8px; 
            transition: background-color 0.2s;
        }
        .btn-submit:hover:not(:disabled) { 
            background-color: #ffe033;
        }

        .hint-slot-text { color: #8a919e; font-size: 0.9rem; padding: 15px 0; margin: 0; }
    </style>
</head>
<body>

<div class="container container-box">
    <a href="index.jsp" class="btn-return">
        <i class="fa-solid fa-arrow-left me-2"></i> Quay lại trang chủ
    </a>

    <div class="card-custom">
        <h2 class="text-center fw-bold mb-4" style="color: var(--yellow-accent);">
            <i class="fa-solid fa-calendar-check me-2"></i> Tiến Hành Đặt Lịch
        </h2>

        <form action="BookingServlet" method="POST">
            <input type="hidden" name="serviceType" value="<%= serviceParam != null ? serviceParam : "Basic" %>">

            <div class="row mb-3">
                <div class="col-12">
                    <label class="form-label">Người đặt lịch</label>
                    <input type="text" value="<%= user.getFullname() %>" class="form-control" readonly tabindex="-1" style="pointer-events:none;">
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-6 mb-3 mb-md-0">
                    <label class="form-label">Chọn Xe</label>
                    <% if(carList == null || carList.isEmpty()) { %>
                        <div class="alert alert-danger p-2 small">
                            Bạn chưa có xe. <a href="cars" class="text-warning fw-bold">Đăng ký tại đây</a>
                        </div>
                    <% } else { %>
                        <div class="select-wrapper">
                            <select name="licensePlate" class="form-select" required>
                                <option value="" disabled selected>-- Chọn biển số xe --</option>
                                <% for(Car c : carList) { %>
                                    <option value="<%= c.getLicensePlate() %>"><%= c.getLicensePlate() %> - <%= c.getBrand() %></option>
                                <% } %>
                            </select>
                        </div>
                    <% } %>
                </div>
                
                <div class="col-md-6 date-input-wrapper">
                    <label class="form-label d-flex justify-content-between align-items-center">
                        <span>Ngày Đặt</span>
                        <span class="badge-limit">(tối đa <%= maxDays %> ngày tới)</span>
                    </label>
                    <input type="date" name="bookingDate" id="datePicker" class="form-control" min="<%= request.getAttribute("MIN_DATE") %>" max="<%= request.getAttribute("MAX_DATE") %>" required>
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-12">
                    <label class="form-label">Chọn Voucher ưu đãi</label>
                    <div class="select-wrapper">
                        <select name="voucherID" class="form-select">
                            <option value="">-- Không sử dụng voucher --</option>
                            <% if(voucherList != null) {
                                for(Voucher v : voucherList) { %>
                                    <option value="<%= v.getVoucherID() %>"><%= v.getCode() %> (Giảm <%= v.getDiscountPercent() %>%)</option>
                            <% } } %>
                        </select>
                    </div>
                </div>
            </div>

            <div class="mb-4">
                <label class="form-label">Chọn khung giờ rửa xe</label>
                <div id="slot-container" class="row p-2 rounded border border-secondary border-opacity-20 m-0" style="background: #0d0f12;">
                    <p class="hint-slot-text text-center w-100">Vui lòng chọn ngày để xem lịch trống.</p>
                </div>
            </div>

            <button type="submit" class="btn-submit" id="submitBtn">
                <i class="fa-solid fa-check me-2"></i> Xác Nhận Booking
            </button>
        </form>
    </div>
</div>

<script>
document.getElementById('datePicker').addEventListener('change', function () {
    let date = this.value; let container = document.getElementById('slot-container'); if (!date) return;
    container.innerHTML = '<p class="hint-slot-text text-center w-100"><i class="fa-solid fa-spinner fa-spin me-2"></i> Đang tải dữ liệu...</p>';
    fetch('BookingServlet?action=getSlots&date=' + date)
        .then(res => res.text()).then(html => { 
            let luxuryHtml = html.replace(/col-6 col-md-3/g, 'col-6 col-md-3 slot-col')
                                 .replace(/btn btn-secondary/g, 'slot-btn-label')
                                 .replace(/btn btn-outline-warning/g, 'slot-btn-label');
            container.innerHTML = luxuryHtml; 
        }).catch(err => { container.innerHTML = '<p class="hint-slot-text text-center text-danger w-100">Lỗi tải khung giờ lịch trống.</p>'; });
});
</script>
</body>
</html>
