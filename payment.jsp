<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String bookingId = request.getParameter("id");
    String serviceName = request.getParameter("service");
    String priceStr = request.getParameter("price");
    
    if(bookingId == null || bookingId.isEmpty()) { 
        response.sendRedirect("BookingServlet"); 
        return; 
    }

    String formattedPrice = "";
    try {
        if(priceStr != null) {
            double amount = Double.parseDouble(priceStr);
            java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###");
            java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            formatter.setDecimalFormatSymbols(symbols);
            formattedPrice = formatter.format(amount) + " VND";
        }
    } catch (Exception e) {
        formattedPrice = priceStr + " VND"; 
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh Toán - CarWash Pro</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    
    <style>
        :root {
            --bg-main: #0a0b0d;
            --bg-card: #14161a;
            --border-color: #2a2d35;
            --yellow-accent: #ffcc00;
            --text-main: #e0e4e8;
        }
        
        body { 
            background-color: var(--bg-main);
            color: var(--text-main); 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            display: flex; align-items: center; justify-content: center; margin: 0; padding: 40px 20px;
        }

        .luxury-workspace { display: flex; gap: 25px; max-width: 1140px; width: 100%; align-items: stretch; }

        .side-panel {
            flex: 1; background-color: var(--bg-card); border: 1px solid var(--border-color);
            border-radius: 12px; display: flex; flex-direction: column; justify-content: space-between;
            box-shadow: 0 8px 20px rgba(0,0,0,0.4); padding: 30px 24px;
        }

        .panel-image-container {
            width: 100%; height: 160px; overflow: hidden; border-radius: 8px; margin-bottom: 20px;
        }
        .panel-image-container img { width: 100%; height: 100%; object-fit: cover; filter: brightness(75%); }

        .panel-body { padding: 0; flex: 1; display: flex; flex-direction: column; justify-content: space-between; }

        .payment-container {
            width: 400px; background-color: var(--bg-card); border: 1px solid var(--border-color);
            border-radius: 12px; padding: 30px 24px; text-align: center; box-shadow: 0 8px 20px rgba(0,0,0,0.4);
            display: flex; flex-direction: column; justify-content: space-between; gap: 20px;
        }

        .royal-title { font-size: 1.6rem; font-weight: 700; color: var(--yellow-accent); margin: 0; }
        .side-title { font-size: 1.2rem; font-weight: 700; color: var(--yellow-accent); margin-bottom: 15px; border-bottom: 1px solid var(--border-color); padding-bottom: 8px; }

        .badge-service {
            background-color: #0d0f12; border: 1px solid var(--border-color);
            color: var(--yellow-accent); padding: 4px 15px; border-radius: 4px; font-weight: 700; font-size: 0.85rem;
        }
        .price-display { font-size: 1.8rem; font-weight: 700; color: #ffffff; }

        /* KHÔI PHỤC HIỆU ỨNG TIA LÀSER QUÉT MÃ QR NHƯ CŨ */
        .qr-wrapper { position: relative; display: inline-block; }
        .qr-box { background: #ffffff; padding: 16px; border-radius: 16px; display: inline-block; border: 1px solid var(--border-color); }
        
        .qr-wrapper::after {
            content: ""; position: absolute; left: 12px; right: 12px; height: 3px; background: linear-gradient(90deg, transparent, var(--yellow-accent), transparent);
            box-shadow: 0 0 12px var(--yellow-accent), 0 0 6px var(--yellow-accent); animation: scanLaser 3.2s linear infinite;
        }
        @keyframes scanLaser { 0% { top: 12px; opacity: 0; } 12% { opacity: 1; } 88% { opacity: 1; } 100% { top: calc(100% - 12px); opacity: 0; } }

        .account-card { background: #0d0f12; border: 1px solid var(--border-color); border-radius: 8px; padding: 12px 18px; }
        .info-row { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid rgba(255, 255, 255, 0.05); }
        .info-row:last-child { border-bottom: none; padding-bottom: 0; }
        .info-row:first-child { padding-top: 0; }

        .info-label { font-size: 0.85rem; color: #8a919e; }
        .info-value { font-size: 0.95rem; font-weight: 700; color: #ffffff; }
        .info-value.name { color: var(--yellow-accent); text-transform: uppercase; }
        .info-value.bank { color: #3a91ff; font-weight: 700; }
        .info-value.system { color: #2ecc71; }

        .btn-copy {
            background: rgba(255, 204, 0, 0.15); border: 1px solid rgba(255, 204, 0, 0.3); color: var(--yellow-accent);
            padding: 1px 6px; border-radius: 4px; font-size: 0.7rem; margin-left: 6px; cursor: pointer;
        }

        .hint-text { color: #8a919e; font-size: 0.85rem; line-height: 1.5; margin: 0; }

        .btn-action { padding: 12px; border-radius: 8px; font-weight: 700; font-size: 0.9rem; transition: 0.2s; }
        .btn-back { background: transparent; color: #ffffff; border: 1px solid var(--border-color); }
        .btn-back:hover { background: rgba(255, 255, 255, 0.05); }
        .btn-done { background-color: var(--yellow-accent); color: #000000 !important; border: none; }
        .btn-done:hover { background-color: #ffe033; }

        .luxury-list { list-style: none; padding: 0; margin: 0; text-align: left; }
        .luxury-list li { font-size: 0.85rem; color: #cbd1db; margin-bottom: 15px; display: flex; align-items: flex-start; line-height: 1.5; }
        .luxury-list li i { color: var(--yellow-accent); margin-right: 10px; margin-top: 3px; font-size: 0.9rem; }

        .panel-footer-note { padding-top: 15px; border-top: 1px dashed var(--border-color); font-size: 0.8rem; color: #8a919e; text-align: left; }
        .hotline-box { background: #0d0f12; border: 1px solid var(--border-color); padding: 10px; border-radius: 8px; display: flex; align-items: center; gap: 10px; }

        .copy-toast {
            position: absolute; top: 15px; left: 50%; transform: translateX(-50%) translateY(-10px); background: #1a1c23; border: 1px solid var(--yellow-accent); color: #fff;
            padding: 8px 16px; border-radius: 8px; font-size: 0.8rem; font-weight: 600; z-index: 10; box-shadow: 0 4px 15px rgba(0,0,0,0.5); opacity: 0; visibility: hidden; transition: 0.3s;
        }
        .copy-toast.show { opacity: 1; visibility: visible; transform: translateX(-50%) translateY(0); }

        @media (max-width: 992px) { .luxury-workspace { flex-direction: column; height: auto; align-items: center; } .side-panel { width: 100%; max-width: 400px; } body { overflow-y: auto; padding: 20px 15px; } }
    </style>
</head>
<body>

<div class="luxury-workspace">
    
    <!-- CÁNH TRÁI: ĐẶC QUYỀN -->
    <div class="side-panel">
        <div class="panel-image-container">
            <img src="https://images.unsplash.com/photo-1616788494707-ec28f08d05a1?q=80&w=600&auto=format&fit=crop" alt="Car Detail">
        </div>
        <div class="panel-body">
            <div>
                <h4 class="side-title">Đặc Quyền VIP</h4>
                <ul class="luxury-list">
                    <li><i class="fa-solid fa-gem"></i><span>Phục vụ ưu tiên tại khu vực sảnh chờ sảnh rửa riêng biệt.</span></li>
                    <li><i class="fa-solid fa-sparkles"></i><span>Sử dụng bọt phủ Polyme bảo vệ sơn xe tốt hơn.</span></li>
                    <li><i class="fa-solid fa-user-tie"></i><span>Kỹ thuật viên lành nghề trực tiếp rửa và kiểm tra xe.</span></li>
                    <li><i class="fa-solid fa-wind"></i><span>Khử mùi làm sạch không khí Ozone khoang nội thất.</span></li>
                </ul>
            </div>
            <div class="panel-footer-note">
                <p class="mb-0"><i class="fa-solid fa-crown me-2 text-warning"></i>Tích lũy điểm thành viên ngay sau khi hoàn thành.</p>
            </div>
        </div>
    </div>

    <!-- KHỐI TRUNG TÂM: THANH TOÁN -->
    <div class="payment-container">
        <div>
            <div id="copyToast" class="copy-toast"><i class="fa-solid fa-circle-check text-warning me-2"></i>Đã sao chép số tài khoản</div>
            <h2 class="royal-title mb-3">XÁC NHẬN THANH TOÁN</h2>
            <div class="d-flex flex-column align-items-center gap-1 mb-3">
                <span class="badge-service"><i class="fa-solid fa-shield-halved me-2"></i>GÓI: <%= serviceName %></span>
                <div class="price-display mt-2"><%= formattedPrice %></div>
            </div>
            <div class="qr-wrapper mb-3">
                <div class="qr-box"><img src="qr-payment.png" style="width:140px; height:140px; object-fit: contain; display: block;"></div>
            </div>
            
            <div class="account-card text-start mb-2">
                <div class="info-row"><span class="info-label">Ngân hàng</span><span class="info-value bank">MB BANK</span></div>
                <div class="info-row"><span class="info-label">Chủ tài khoản</span><span class="info-value name">Ngô Minh Toàn</span></div>
                <div class="info-row"><span class="info-label">Số tài khoản</span>
                    <div><span class="info-value" id="stkValue">0375814577</span><button type="button" class="btn-copy" onclick="copySTK()"><i class="fa-regular fa-copy"></i></button></div>
                </div>
                <div class="info-row"><span class="info-label">Hệ thống</span><span class="info-value system">Tự động 24/7</span></div>
            </div>
            <p class="hint-text">Vui lòng chuyển đúng thông tin tài khoản trên. Hệ thống sẽ duyệt lịch tự động ngay sau khi nhận được giao dịch.</p>
        </div>
        
        <div class="d-flex gap-3 mt-auto">
            <a href="BookingServlet" class="btn btn-back flex-grow-1 btn-action d-flex align-items-center justify-content-center"><i class="fa-solid fa-arrow-left me-2"></i>Quay lại</a>
            <a href="BookingServlet?action=pay&id=<%= bookingId %>" id="btnSubmit" class="btn btn-done flex-grow-1 btn-action d-flex align-items-center justify-content-center">Đã chuyển khoản<i class="fa-solid fa-arrow-right ms-2"></i></a>
        </div>
    </div>

    <!-- CÁNH PHẢI: CAM KẾT -->
    <div class="side-panel">
        <div class="panel-image-container">
            <img src="https://images.unsplash.com/photo-1607860108855-64acf2078ed9?q=80&w=600&auto=format&fit=crop" alt="Car Wash">
        </div>
        <div class="panel-body">
            <div>
                <h4 class="side-title">Cam Kết Chất Lượng</h4>
                <ul class="luxury-list">
                    <li><i class="fa-solid fa-clock-rotate-left"></i><span>Hoàn tiền 100% nếu quý khách không hài lòng về chất lượng.</span></li>
                    <li><i class="fa-solid fa-shield-car"></i><span>Bảo hiểm an toàn tài sản xe của bạn khi thực hiện dịch vụ.</span></li>
                    <li><i class="fa-solid fa-bell-concierge"></i><span>Đội ngũ nhân viên sẵn sàng tiếp nhận ý kiến đóng góp 24/7.</span></li>
                    <li><i class="fa-solid fa-certificate"></i><span>Cam kết sử dụng các sản phẩm hóa chất chính hãng, an toàn.</span></li>
                </ul>
            </div>
            <div class="panel-footer-note">
                <div class="hotline-box">
                    <i class="fa-solid fa-headset text-warning fs-5"></i>
                    <div class="text-start">
                        <div style="font-size: 0.7rem; color: #8a919e;">Đường dây nóng hỗ trợ:</div>
                        <div style="font-size: 0.85rem; font-weight: 700; color: var(--yellow-accent);">1900 8888 (Phím 9)</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    function copySTK() {
        const stkText = document.getElementById("stkValue").innerText;
        navigator.clipboard.writeText(stkText).then(() => {
            const copyToast = document.getElementById("copyToast"); copyToast.classList.add("show");
            setTimeout(() => { copyToast.classList.remove("show"); }, 1600);
        }).catch(err => { console.error(err); });
    }
</script>
</body>
</html>
