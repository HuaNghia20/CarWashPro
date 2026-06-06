<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String bookingId = request.getParameter("id");
    String serviceName = request.getParameter("service");
    String price = request.getParameter("price");
    if(bookingId == null || bookingId.isEmpty()) { response.sendRedirect("BookingServlet"); return; }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh Toán - CarWash Pro</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        :root {
            --primary-yellow: #ffcc00;
            --bg-dark: #0a0b0d;
        }
        body { 
            background: radial-gradient(circle at center, #1a1c23 0%, #0a0b0d 100%);
            color: #fff;
            font-family: 'Plus Jakarta Sans', sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .payment-container {
            background: rgba(25, 27, 33, 0.7);
            backdrop-filter: blur(15px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            padding: 40px;
            border-radius: 24px;
            width: 100%;
            max-width: 420px;
            text-align: center;
            box-shadow: 0 20px 40px rgba(0,0,0,0.6);
        }
        .method-tabs {
            background: #000;
            padding: 5px;
            border-radius: 12px;
            margin-bottom: 25px;
        }
        .btn-method {
            border: none;
            color: #888;
            font-weight: 600;
            transition: all 0.3s;
            padding: 10px 20px;
        }
        .btn-method.active {
            background: var(--primary-yellow);
            color: #000;
            border-radius: 8px;
        }
        .qr-box {
            background: #fff;
            padding: 15px;
            border-radius: 20px;
            display: inline-block;
            margin: 20px 0;
        }
        .btn-action {
            padding: 12px 25px;
            border-radius: 12px;
            font-weight: 700;
            transition: 0.3s;
        }
        .btn-done { background: var(--primary-yellow); color: #000; border: none; }
        .btn-done:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(255,204,0,0.3); }
    </style>
</head>
<body>

<div class="payment-container">
    <h2 class="fw-800 mb-1">
    Thanh Toán <span style="color: #ffcc00;">#<%= bookingId %></span>
</h2>
    <div class="mb-4">
    <span style="color: var(--primary-yellow); font-weight: 700; font-size: 1.1rem;">
        <%= serviceName %>
    </span>
    <span class="text-white-50 mx-2">•</span>
    <span class="text-white fw-bold fs-5">
        <%= price %>
    </span>
</div>

    <div class="btn-group method-tabs w-100" role="group">
        <button class="btn btn-method active" id="tabTransfer" onclick="selectTab('transfer')">
            <i class="fa-solid fa-qrcode"></i> Chuyển khoản
        </button>
        <button class="btn btn-method" id="tabCash" onclick="selectTab('cash')">
            <i class="fa-solid fa-money-bill-1"></i> Tiền mặt
        </button>
    </div>

    <div id="contentTransfer">
        <div class="qr-box">
            <img src="https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=<%= bookingId %>" style="width:180px">
        </div>
       <p class="text-white fw-medium small px-3">Quét mã QR để thanh toán nhanh chóng qua ứng dụng ngân hàng.</p>
    </div>

    <div id="contentCash" style="display:none;">
        <div class="my-4 p-4 border border-warning rounded" style="background: rgba(255, 204, 0, 0.05);">
            <i class="fa-solid fa-store fa-3x text-warning mb-3"></i>
            <h5 class="text-white fw-bold mb-3">Thanh toán tại quầy</h5>
            <p class="text-white fw-bold mb-0" style="font-size: 1.05rem; line-height: 1.5;">
                Vui lòng mang theo mã <span class="text-warning">#<%= bookingId %></span> đến quầy thu ngân để thanh toán.
            </p>
        </div>
    </div>

    <div class="d-flex gap-3 mt-4">
        <a href="BookingServlet" class="btn btn-outline-secondary flex-grow-1 btn-action">Quay lại</a>
        <a href="BookingServlet?action=pay&id=<%= bookingId %>&price=<%= price %>" id="btnSubmit" class="btn btn-done flex-grow-1 btn-action">Đã thanh toán</a>
    </div>
</div>

<script>
    function selectTab(type) {
        document.getElementById('tabTransfer').classList.toggle('active', type === 'transfer');
        document.getElementById('tabCash').classList.toggle('active', type === 'cash');
        document.getElementById('contentTransfer').style.display = type === 'transfer' ? 'block' : 'none';
        document.getElementById('contentCash').style.display = type === 'cash' ? 'block' : 'none';
        document.getElementById('btnSubmit').innerText = type === 'transfer' ? 'Đã chuyển khoản' : 'Xác nhận tại quầy';
    }
</script>
</body>
</html>
