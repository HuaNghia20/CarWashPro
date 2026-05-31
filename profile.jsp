<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="model.User"%>
<%
    User user = (User) session.getAttribute("USER");
    if (user == null) {
        response.sendRedirect("login_page.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>CarWash Pro - Hồ Sơ Cá Nhân</title>

        <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

        <style>
            /* BẢNG MÀU ĐEN - VÀNG ĐỒNG BỘ */
            :root {
                --bg-main: #0a0b0d;
                --bg-card: #14161a;
                --border-color: #2a2d35;
                --yellow-accent: #ffcc00;
                --yellow-glow: rgba(255, 204, 0, 0.4);
                --text-main: #e0e4e8;
                --text-muted: #8a919e;
                --success: #10b981;
                --success-bg: rgba(16, 185, 129, 0.15);
            }

            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
                font-family: 'Plus Jakarta Sans', sans-serif;
            }

            body {
                background-color: var(--bg-main);
                color: var(--text-main);
                min-height: 100vh;
                display: flex;
                flex-direction: column;
            }

            /* --- NAVBAR (Đồng bộ) --- */
            .navbar-custom {
                background-color: #0d0f12;
                border-bottom: 1px solid var(--border-color);
                padding: 15px 0;
                position: sticky;
                top: 0;
                z-index: 100;
            }
            .navbar-brand {
                color: var(--yellow-accent) !important;
                font-weight: 900;
                font-size: 1.5rem;
                text-shadow: 0 0 15px var(--yellow-glow);
                letter-spacing: 1px;
            }
            .nav-link {
                color: var(--text-main) !important;
                font-weight: 500;
                margin-right: 15px;
                transition: 0.3s;
            }
            .nav-link:hover, .nav-link.active {
                color: var(--yellow-accent) !important;
                text-shadow: 0 0 8px var(--yellow-glow);
            }
            .btn-logout {
                background-color: transparent;
                color: #ff4d4d;
                border: 1px solid #ff4d4d;
                border-radius: 8px;
                transition: 0.3s;
                padding: 8px 20px;
                text-decoration: none;
                font-weight: bold;
                display: flex;
                align-items: center;
                gap: 6px;
            }
            .btn-logout:hover {
                background-color: #ff4d4d;
                color: #fff;
                box-shadow: 0 0 10px rgba(255, 77, 77, 0.5);
            }
            .btn-change-pass-nav {
                background-color: rgba(255, 255, 255, 0.1);
                color: var(--text-main);
                border: 1px solid var(--border-color);
                border-radius: 8px;
                padding: 8px 15px;
                text-decoration: none;
                font-weight: bold;
                transition: 0.3s;
                display: flex;
                align-items: center;
                gap: 6px;
            }
            .btn-change-pass-nav:hover {
                background-color: var(--yellow-accent);
                color: #000;
                box-shadow: 0 0 10px var(--yellow-glow);
                border-color: var(--yellow-accent);
            }

            /* --- PROFILE CONTAINER --- */
            .main-wrapper {
                flex: 1;
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                padding: 50px 20px;
                animation: slideUp 0.6s ease-out forwards;
            }

            @keyframes slideUp {
                from {
                    opacity: 0;
                    transform: translateY(20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            .profile-box {
                width: 100%;
                max-width: 600px;
                background: var(--bg-card);
                backdrop-filter: blur(20px);
                border-radius: 24px;
                border: 1px solid var(--border-color);
                box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
                padding: 40px;
            }

            .header {
                text-align: center;
                margin-bottom: 20px;
            }
            .avatar-circle {
                width: 90px;
                height: 90px;
                background: rgba(255, 204, 0, 0.1);
                border: 2px solid var(--yellow-accent);
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 2.5rem;
                color: var(--yellow-accent);
                margin: 0 auto 15px;
                box-shadow: 0 0 20px var(--yellow-glow);
            }
            .header h1 {
                font-size: 1.8rem;
                font-weight: 800;
                margin-bottom: 5px;
                color: var(--text-main);
            }
            .header p {
                color: var(--text-muted);
                font-size: 0.95rem;
            }

            /* --- HUY HIỆU RANK --- */
            /* --- HUY HIỆU RANK (MỚI THEO HÌNH ẢNH) --- */
            .tier-container {
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: 8px;
                margin-bottom: 30px;
            }
            .badge-tier {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 6px 20px;
                border-radius: 30px;
                font-weight: 800;
                font-size: 0.95rem;
                border: 1px solid;
                letter-spacing: 0.5px;
                text-transform: uppercase;
            }
            /* Hạng 1: Member (Màu xanh lam nhạt) */
            .rank-1-member {
                color: #38bdf8;
                border-color: rgba(56, 189, 248, 0.5);
                background: rgba(56, 189, 248, 0.1);
                box-shadow: 0 0 15px rgba(56, 189, 248, 0.3);
            }
            /* Hạng 2: Silver (Màu Bạc) */
            .rank-2-sil {
                color: #cbd5e1;
                border-color: rgba(203, 213, 225, 0.5);
                background: rgba(203, 213, 225, 0.08);
                box-shadow: 0 0 15px rgba(203, 213, 225, 0.3);
            }
            /* Hạng 3: Gold (Màu Vàng) */
            .rank-3-gold {
                color: #ffcc00;
                border-color: rgba(255, 204, 0, 0.6);
                background: rgba(255, 204, 0, 0.1);
                box-shadow: 0 0 15px var(--yellow-glow);
            }
            /* Hạng 4: Platinum (Màu Trắng Sáng/Bạch Kim) */
            .rank-4-plat {
                color: #f8fafc;
                border-color: rgba(248, 250, 252, 0.7);
                background: rgba(248, 250, 252, 0.15);
                box-shadow: 0 0 20px rgba(248, 250, 252, 0.5);
            }

            .tier-benefit {
                font-size: 0.85rem;
                color: var(--success);
                font-weight: 600;
                background: var(--success-bg);
                padding: 6px 15px;
                border-radius: 20px;
                margin-top: 5px;
            }

            /* --- MESSAGE SUCCESS --- */
            .msg-success {
                background: var(--success-bg);
                color: var(--success);
                padding: 14px;
                text-align: center;
                font-size: 0.95rem;
                font-weight: 600;
                border: 1px solid rgba(16, 185, 129, 0.3);
                border-radius: 12px;
                margin-bottom: 25px;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
            }

            /* --- FORM INPUTS --- */
            .form-group {
                display: flex;
                flex-direction: column;
                gap: 8px;
                margin-bottom: 22px;
            }
            label {
                font-size: 0.9rem;
                font-weight: 600;
                color: #cbd5e1;
            }
            .input-wrapper {
                position: relative;
                display: flex;
                align-items: center;
            }
            .input-wrapper i {
                position: absolute;
                left: 18px;
                color: var(--text-muted);
                font-size: 1.1rem;
                transition: color 0.3s ease;
            }

            input[type="text"] {
                width: 100%;
                padding: 14px 18px 14px 48px;
                background: #0d0f12;
                border: 1px solid var(--border-color);
                border-radius: 12px;
                color: #fff;
                font-size: 1rem;
                font-weight: 500;
                outline: none;
                transition: all 0.3s ease;
            }

            input:disabled {
                background: rgba(15, 23, 42, 0.3);
                color: #64748b;
                cursor: not-allowed;
                border-style: dashed;
            }
            input:disabled ~ i {
                color: #475569;
            }

            input:not(:disabled):focus {
                border-color: var(--yellow-accent);
                box-shadow: 0 0 10px var(--yellow-glow);
                background: #1a1c23;
            }
            input:not(:disabled):focus + i {
                color: var(--yellow-accent);
            }

            /* --- BUTTON LƯU THAY ĐỔI --- */
            button[type="submit"] {
                width: 100%;
                background: linear-gradient(135deg, var(--yellow-accent), #ffaa00);
                color: #000;
                border: none;
                padding: 16px;
                font-size: 1.1rem;
                font-weight: 800;
                border-radius: 12px;
                cursor: pointer;
                transition: all 0.3s ease;
                box-shadow: 0 8px 20px -5px rgba(255, 204, 0, 0.4);
                display: flex;
                justify-content: center;
                align-items: center;
                gap: 10px;
                margin-top: 15px;
            }
            button[type="submit"]:hover {
                transform: translateY(-2px);
                box-shadow: 0 12px 25px -5px rgba(255, 204, 0, 0.5);
                filter: brightness(1.05);
            }
        </style>
    </head>

    <body>

        <nav class="navbar navbar-expand-lg navbar-custom">
            <div class="container">
                <a class="navbar-brand d-flex align-items-center" href="index.jsp">
                    <i class="fa-solid fa-car me-2"></i>CarWash Pro
                </a>

                <div class="collapse navbar-collapse justify-content-end">
                    <ul class="navbar-nav me-4 align-items-center">
                        <li class="nav-item"><a class="nav-link" href="index.jsp"><i class="fa-solid fa-house me-1"></i> Trang Chủ</a></li>
                        <li class="nav-item"><a class="nav-link active" href="profile.jsp"><i class="fa-solid fa-user me-1"></i> Hồ Sơ</a></li>
                        <li class="nav-item"><a class="nav-link" href="cars"><i class="fa-solid fa-car-side me-1"></i> Quản Lý Xe</a></li>
                    </ul>

                    <div class="d-flex gap-3">
                        <a href="ChangePasswordServlet" class="btn-change-pass-nav">
                            <i class="fa-solid fa-lock"></i> Đổi Mật Khẩu
                        </a>
                        <a href="logout" class="btn-logout">
                            <i class="fa-solid fa-right-from-bracket"></i> Đăng Xuất
                        </a>
                    </div>
                </div>
            </div>
        </nav>

        <div class="main-wrapper">
            <div class="profile-box">

                <div class="header">
                    <div class="avatar-circle">
                        <i class="fa-solid fa-user-tie"></i>
                    </div>
                    <h1>Hồ Sơ Thành Viên</h1>
                    <p>Quản lý và cập nhật thông tin cá nhân của bạn</p>
                </div>

                <div class="tier-container">
                    <div class="badge-tier <%= (user.getTierID() == 1) ? "rank-1-cop" : ((user.getTierID() == 2) ? "rank-2-sil" : "rank-3-gold")%>">
                        <i class="fa-solid fa-crown"></i> 
                        <%= (user.getTierID() == 1) ? "Member" : ((user.getTierID() == 2) ? "Hạng Bạc (SILVER)" : "Hạng Vàng (GOLD)")%>
                    </div>
                    <span style="font-size: 0.8rem; color: var(--text-muted); font-style: italic;">
                        <i class="fa-solid fa-ranking-star" style="color: var(--yellow-accent);"></i> Hệ thống tự động nâng/hạ cấp theo điểm tích lũy
                    </span>
                </div>

                <% if (request.getAttribute("MESSAGE") != null) {%>
                <div class="msg-success">
                    <i class="fa-solid fa-circle-check"></i>
                    <%= request.getAttribute("MESSAGE")%>
                </div>
                <% }%>

                <form action="profile" method="POST">

                    <div class="form-group">
                        <label>Mã tài khoản (CustID)</label>
                        <div class="input-wrapper">
                            <input type="text" value="#<%= user.getCustID()%>" disabled />
                            <i class="fa-solid fa-id-badge"></i>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Địa chỉ Email (Tài khoản đăng nhập)</label>
                        <div class="input-wrapper">
                            <input type="text" value="<%= user.getEmail()%>" disabled />
                            <i class="fa-solid fa-envelope"></i>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Họ và Tên</label>
                        <div class="input-wrapper">
                            <input type="text" name="fullname" value="<%= user.getFullname()%>" required placeholder="Nhập họ và tên mới..." />
                            <i class="fa-solid fa-user-pen text-yellow"></i>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Điểm Khách Hàng</label>
                        <div class="input-wrapper">
                            <input type="text" value="<%= (user != null) ? user.getPoint() : 0%> điểm" disabled />
                            <i class="fa-solid fa-star"></i>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Thứ Hạng Hiện Tại</label>
                        <div class="input-wrapper">
                            <input type="text" value="<%= (user.getTierID() == 1) ? "Member" : ((user.getTierID() == 2) ? "SILVER" : "GOLD")%>" disabled />
                            <i class="fa-solid fa-medal"></i>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Ví Khách Hàng</label>
                        <div class="input-wrapper">
                            <input type="text" value="<%= (user != null) ? user.getWallet() : 0%> VNĐ" disabled />
                            <i class="fa-solid fa-wallet"></i>
                        </div>
                    </div>

                    <button type="submit">                    
                        <i class="fa-solid fa-floppy-disk"></i> Lưu Thay Đổi
                    </button>
                </form>

            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
