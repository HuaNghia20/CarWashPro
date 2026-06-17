<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="model.Car, model.User, java.util.ArrayList"%>
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
    <title>Quản Lý Phương Tiện - CarWash Pro</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        :root {
            --bg-main: #0a0b0d;
            --bg-card: #14161a;
            --border-color: #2a2d35;
            --yellow-accent: #ffcc00;
            --yellow-glow: rgba(255, 204, 0, 0.4);
            --text-main: #e0e4e8;
            --text-muted: #8a919e;
        }

        body {
            background-color: var(--bg-main);
            color: var(--text-main);
            font-family: 'Plus Jakarta Sans', sans-serif;
            padding: 40px 0;
        }

        .text-yellow { color: var(--yellow-accent) !important; }

        .btn-link-back {
            color: var(--text-muted);
            text-decoration: none;
            font-weight: 600;
            transition: 0.3s;
        }
        .btn-link-back:hover { color: var(--yellow-accent); }

        .card-custom {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
            height: 100%;
        }

        .form-label {
            color: var(--text-muted);
            font-weight: 600;
            font-size: 0.9rem;
            margin-bottom: 8px;
        }
        .form-control {
            background-color: #0d0f12;
            border: 1px solid var(--border-color);
            color: #fff !important;
            padding: 12px 15px;
            border-radius: 8px;
        }
        .form-control:focus {
            background-color: #1a1c23;
            border-color: var(--yellow-accent);
            color: #fff;
            box-shadow: 0 0 8px var(--yellow-glow);
        }
        .form-control::placeholder { color: #4a515e; }

        .btn-primary-custom {
            background-color: var(--yellow-accent);
            color: #000;
            font-weight: 700;
            border: none;
            padding: 10px 20px;
            border-radius: 8px;
            transition: 0.3s;
            width: 100%;
        }
        .btn-primary-custom:hover {
            background-color: #ffda33;
            box-shadow: 0 0 15px var(--yellow-glow);
            transform: scale(1.02);
        }
        .btn-search { width: auto; }

        .btn-action {
            padding: 4px 10px;
            font-size: 0.8rem;
            border-radius: 6px;
            font-weight: 600;
            text-decoration: none;
            display: inline-block;
            transition: 0.3s;
        }
        .btn-edit {
            background-color: rgba(255, 204, 0, 0.1);
            color: var(--yellow-accent);
            border: 1px solid var(--yellow-accent);
            cursor: pointer;
        }
        .btn-edit:hover { background-color: var(--yellow-accent); color: #000; }
        .btn-delete {
            background-color: rgba(255, 77, 77, 0.1);
            color: #ff4d4d;
            border: 1px solid #ff4d4d;
        }
        .btn-delete:hover { background-color: #ff4d4d; color: #fff; }

        .table {
            --bs-table-bg: transparent;
            color: var(--text-main);
            margin-top: 15px;
            font-size: 0.85rem;
        }
        .table thead th {
            background-color: #1d1f24 !important;
            color: var(--yellow-accent) !important;
            font-size: 0.75rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            border: none;
            padding: 12px 10px;
            white-space: nowrap;
        }
        .table tbody td {
            color: #ffffff !important;
            border-bottom: 1px solid var(--border-color) !important;
            padding: 12px 10px;
            vertical-align: middle;
        }
        .table tbody tr:hover td {
            background-color: rgba(255,204,0,0.05) !important;
        }

        /* Biển số xe - có thể click */
        .plate-badge {
            background-color: var(--yellow-accent);
            color: #000;
            font-weight: 800;
            padding: 4px 8px;
            font-size: 0.85rem;
            border-radius: 6px;
            display: inline-block;
            letter-spacing: 0.5px;
            white-space: nowrap;
            cursor: pointer;
            transition: 0.2s;
            border: 2px solid transparent;
        }
        .plate-badge:hover {
            background-color: #fff700;
            box-shadow: 0 0 12px var(--yellow-glow);
            transform: scale(1.05);
            border-color: #fff;
        }
        .plate-badge .click-hint {
            font-size: 0.6rem;
            font-weight: 400;
            display: block;
            text-align: center;
            opacity: 0.6;
            margin-top: 1px;
        }

        /* ====== MODAL XEM CHI TIẾT XE ====== */
        .modal-overlay {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.75);
            backdrop-filter: blur(4px);
            z-index: 9990;
            align-items: center;
            justify-content: center;
        }
        .modal-overlay.active {
            display: flex;
        }
        .car-detail-modal {
            background: #14161a;
            border: 1px solid var(--border-color);
            border-radius: 20px;
            width: 580px;
            max-width: 95vw;
            max-height: 90vh;
            overflow-y: auto;
            box-shadow: 0 25px 60px rgba(0,0,0,0.7), 0 0 40px rgba(255,204,0,0.08);
            animation: modalPop 0.25s cubic-bezier(0.34,1.56,0.64,1);
        }
        @keyframes modalPop {
            from { transform: scale(0.85); opacity: 0; }
            to   { transform: scale(1);    opacity: 1; }
        }
        .modal-header-custom {
            background: linear-gradient(135deg, #1d1f24 0%, #212530 100%);
            border-bottom: 1px solid var(--border-color);
            padding: 20px 25px;
            border-radius: 20px 20px 0 0;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .modal-body-custom { padding: 25px; }
        .modal-close-btn {
            background: rgba(255,255,255,0.05);
            border: 1px solid var(--border-color);
            color: var(--text-muted);
            width: 34px; height: 34px;
            border-radius: 8px;
            cursor: pointer;
            display: flex; align-items: center; justify-content: center;
            transition: 0.2s;
        }
        .modal-close-btn:hover { background: #ff4d4d; color: #fff; border-color: #ff4d4d; }

        /* Thông tin xe trong modal */
        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px;
            margin-bottom: 20px;
        }
        .info-item {
            background: #0d0f12;
            border: 1px solid var(--border-color);
            border-radius: 10px;
            padding: 12px 14px;
        }
        .info-item .label {
            font-size: 0.72rem;
            color: var(--text-muted);
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 4px;
        }
        .info-item .value {
            font-size: 0.95rem;
            color: #fff;
            font-weight: 700;
        }
        .info-item.full-width { grid-column: 1 / -1; }

        /* Ảnh xe trong modal */
        .images-section { margin-top: 5px; }
        .images-section .section-label {
            font-size: 0.75rem;
            font-weight: 700;
            color: var(--text-muted);
            text-transform: uppercase;
            letter-spacing: 0.8px;
            margin-bottom: 12px;
        }
        .car-images-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px;
        }
        .car-image-box {
            background: #0d0f12;
            border: 1px solid var(--border-color);
            border-radius: 12px;
            overflow: hidden;
            position: relative;
        }
        .car-image-box .img-label {
            position: absolute;
            top: 8px; left: 8px;
            background: rgba(0,0,0,0.7);
            color: var(--yellow-accent);
            font-size: 0.65rem;
            font-weight: 700;
            padding: 3px 8px;
            border-radius: 4px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            backdrop-filter: blur(4px);
        }
        .car-image-box img {
            width: 100%;
            height: 160px;
            object-fit: cover;
            display: block;
            cursor: zoom-in;
            transition: transform 0.3s;
        }
        .car-image-box img:hover { transform: scale(1.03); }
        .car-image-box .no-img {
            width: 100%; height: 160px;
            display: flex; flex-direction: column;
            align-items: center; justify-content: center;
            color: var(--text-muted);
            font-size: 0.8rem;
            gap: 8px;
        }
        .car-image-box .no-img i { font-size: 2rem; opacity: 0.3; }

        /* Lightbox xem ảnh to */
        .lightbox {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.92);
            z-index: 99999;
            align-items: center;
            justify-content: center;
            cursor: zoom-out;
        }
        .lightbox.active { display: flex; }
        .lightbox img {
            max-width: 90vw;
            max-height: 90vh;
            border-radius: 8px;
            box-shadow: 0 0 60px rgba(255,204,0,0.2);
        }
        .lightbox-close {
            position: fixed;
            top: 20px; right: 20px;
            background: rgba(255,255,255,0.1);
            border: none;
            color: #fff;
            width: 40px; height: 40px;
            border-radius: 50%;
            cursor: pointer;
            font-size: 1.2rem;
        }

        /* ====== PREVIEW ẢNH KHI CHỌN FILE ====== */
        .img-preview-wrap {
            position: relative;
            margin-top: 8px;
        }
        .img-preview {
            width: 100%;
            height: 100px;
            object-fit: cover;
            border-radius: 8px;
            border: 1px solid var(--border-color);
            display: none;
            background: #0d0f12;
        }
        .img-preview.has-img { display: block; }
        .img-preview-placeholder {
            width: 100%;
            height: 100px;
            border-radius: 8px;
            border: 1px dashed var(--border-color);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            color: var(--text-muted);
            font-size: 0.75rem;
            gap: 5px;
            background: #0d0f12;
        }
        .img-preview-placeholder i { font-size: 1.4rem; opacity: 0.4; }
        .img-preview-placeholder.hidden { display: none; }
        .remove-preview-btn {
            position: absolute;
            top: 5px; right: 5px;
            background: rgba(255,77,77,0.8);
            border: none;
            color: #fff;
            width: 22px; height: 22px;
            border-radius: 50%;
            font-size: 0.7rem;
            cursor: pointer;
            display: none;
            align-items: center;
            justify-content: center;
            transition: 0.2s;
        }
        .remove-preview-btn.visible { display: flex; }
        .remove-preview-btn:hover { background: #ff4d4d; }
    </style>
</head>
<body>

<!-- ====== MODAL XEM CHI TIẾT XE ====== -->
<div class="modal-overlay" id="carDetailOverlay" onclick="closeCarModal(event)">
    <div class="car-detail-modal" id="carDetailModal">
        <div class="modal-header-custom">
            <div>
                <div style="font-size:0.75rem; color:var(--text-muted); margin-bottom:4px; text-transform:uppercase; letter-spacing:0.8px;">Chi Tiết Phương Tiện</div>
                <div class="d-flex align-items-center gap-3">
                    <span class="plate-badge" id="mPlateHeader" style="cursor:default; font-size:1.1rem; padding:6px 14px;"></span>
                </div>
            </div>
            <button class="modal-close-btn" onclick="document.getElementById('carDetailOverlay').classList.remove('active')">
                <i class="fa-solid fa-xmark"></i>
            </button>
        </div>
        <div class="modal-body-custom">
            <!-- Thông tin -->
            <div class="info-grid">
                <div class="info-item">
                    <div class="label"><i class="fa-solid fa-hashtag me-1"></i>Mã Xe</div>
                    <div class="value text-yellow" id="mId"></div>
                </div>
                <div class="info-item">
                    <div class="label"><i class="fa-solid fa-calendar me-1"></i>Ngày Đăng Ký</div>
                    <div class="value" id="mDate"></div>
                </div>
                <div class="info-item">
                    <div class="label"><i class="fa-solid fa-industry me-1"></i>Hãng Xe</div>
                    <div class="value" id="mBrand"></div>
                </div>
                <div class="info-item">
                    <div class="label"><i class="fa-solid fa-car me-1"></i>Dòng Xe</div>
                    <div class="value" id="mModel"></div>
                </div>
                <div class="info-item full-width">
                    <div class="label"><i class="fa-solid fa-palette me-1"></i>Màu Sơn</div>
                    <div class="value" id="mColor"></div>
                </div>
            </div>

            <!-- Ảnh xe -->
            <div class="images-section">
                <div class="section-label"><i class="fa-solid fa-images me-2"></i>Hình Ảnh Phương Tiện</div>
                <div class="car-images-grid">
                    <!-- Ảnh tổng thể -->
                    <div class="car-image-box">
                        <div class="img-label">Tổng Thể</div>
                        <img id="mCarImg" src="" alt="Ảnh xe" onclick="openLightbox(this.src)"
                             onerror="this.style.display='none'; document.getElementById('mCarNoImg').style.display='flex';">
                        <div id="mCarNoImg" class="no-img" style="display:none;">
                            <i class="fa-solid fa-car-burst"></i>
                            <span>Chưa có ảnh</span>
                        </div>
                    </div>
                    <!-- Ảnh biển số -->
                    <div class="car-image-box">
                        <div class="img-label">Biển Số</div>
                        <img id="mPlateImg" src="" alt="Ảnh biển số" onclick="openLightbox(this.src)"
                             onerror="this.style.display='none'; document.getElementById('mPlateNoImg').style.display='flex';">
                        <div id="mPlateNoImg" class="no-img" style="display:none;">
                            <i class="fa-solid fa-image-slash"></i>
                            <span>Chưa có ảnh</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- ====== LIGHTBOX XEM ẢNH TO ====== -->
<div class="lightbox" id="lightbox" onclick="closeLightbox()">
    <button class="lightbox-close"><i class="fa-solid fa-xmark"></i></button>
    <img id="lightboxImg" src="" alt="">
</div>

<div class="container">
    <div class="d-flex flex-wrap justify-content-between align-items-end mb-4">
        <div>
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn-link-back d-block mb-3">
                <i class="fa-solid fa-arrow-left me-2"></i>Quay lại trang chủ
            </a>
            <h2 class="fw-bold mb-2 text-yellow"><i class="fa-solid fa-car-side me-3"></i>Quản Lý Danh Sách Xe</h2>
            <p class="text-light mb-0">Xin chào, <strong class="text-yellow"><%= user.getFullname()%></strong>. Dưới đây là danh sách phương tiện được liên kết với hồ sơ của bạn.</p>
        </div>

        <form action="search" method="GET" class="d-flex mt-3 mt-lg-0" style="width: 100%; max-width: 350px;">
            <input type="text" name="txt" class="form-control me-2" placeholder="Tìm biển số, hãng xe..." required>
            <button type="submit" class="btn-primary-custom btn-search"><i class="fa-solid fa-magnifying-glass"></i></button>
        </form>
    </div>

    <div class="row g-4">
        <!-- ====== FORM ĐĂNG KÝ / CHỈNH SỬA XE ====== -->
        <div class="col-lg-4">
            <div class="card-custom">
                <h5 id="formTitle" class="fw-bold text-yellow mb-4">
                    <i class="fa-solid fa-plus me-2"></i>Đăng Ký Xe Mới
                </h5>

                <form action="cars" method="POST" enctype="multipart/form-data">
                    <input type="hidden" id="vId" name="vehicleId" />

                    <div class="mb-3">
                        <label for="plate" class="form-label">
                            Biển số xe:
                            <span id="plateLockedNote" style="display:none; color:#ff4d4d; font-size:0.75rem; font-weight:400;">
                                <i class="fa-solid fa-lock me-1"></i>Không thể thay đổi
                            </span>
                        </label>
                        <%-- Khi thêm mới: input bình thường. Khi sửa: hiển thị badge + input hidden --%>
                        <input type="text" id="plate" name="licensePlate" class="form-control"
                               placeholder="VD: 30A-111.22" required />
                        <div id="plateLockedDisplay" style="display:none; background:#0d0f12; border:1px solid #2a2d35;
                             border-radius:8px; padding:12px 15px; display:none; align-items:center; gap:10px;">
                            <span id="plateBadgeDisplay" class="plate-badge" style="cursor:default; font-size:1rem;"></span>
                            <span style="color:#8a919e; font-size:0.8rem;">Biển số xe được cố định sau khi đăng ký</span>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="brand" class="form-label">Hãng xe (Brand):</label>
                        <input type="text" id="brand" name="brand" class="form-control" placeholder="VD: Toyota, Honda, BMW" required />
                    </div>

                    <div class="mb-3">
                        <label for="model" class="form-label">Dòng xe (Model):</label>
                        <input type="text" id="model" name="model" class="form-control" placeholder="VD: Vios, Civic, X5" required />
                    </div>

                    <div class="mb-3">
                        <label for="color" class="form-label">Màu sơn xe:</label>
                        <input type="text" id="color" name="color" class="form-control" placeholder="VD: Trắng, Đen, Đỏ" required />
                    </div>

                    <!-- Ảnh tổng thể + preview -->
                    <div class="mb-3">
                        <label class="form-label">Ảnh tổng thể xe:</label>
                        <input type="file" id="carImageInput" name="txtcarimage" class="form-control" accept="image/*"
                               onchange="previewImage(this, 'carPreview', 'carPlaceholder', 'removeCarBtn')">
                        <div class="img-preview-wrap">
                            <div class="img-preview-placeholder" id="carPlaceholder">
                                <i class="fa-solid fa-car"></i>
                                <span>Chọn ảnh tổng thể xe</span>
                            </div>
                            <img id="carPreview" class="img-preview" alt="Preview ảnh xe">
                            <button type="button" class="remove-preview-btn" id="removeCarBtn"
                                    onclick="clearPreview('carImageInput','carPreview','carPlaceholder','removeCarBtn')">
                                <i class="fa-solid fa-xmark"></i>
                            </button>
                        </div>
                    </div>

                    <!-- Ảnh biển số + preview -->
                    <div class="mb-4">
                        <label class="form-label">Ảnh cận biển số:</label>
                        <input type="file" id="plateImageInput" name="txtplateimage" class="form-control" accept="image/*"
                               onchange="previewImage(this, 'platePreview', 'platePlaceholder', 'removePlateBtn')">
                        <div class="img-preview-wrap">
                            <div class="img-preview-placeholder" id="platePlaceholder">
                                <i class="fa-solid fa-id-card"></i>
                                <span>Chọn ảnh cận biển số</span>
                            </div>
                            <img id="platePreview" class="img-preview" alt="Preview ảnh biển số">
                            <button type="button" class="remove-preview-btn" id="removePlateBtn"
                                    onclick="clearPreview('plateImageInput','platePreview','platePlaceholder','removePlateBtn')">
                                <i class="fa-solid fa-xmark"></i>
                            </button>
                        </div>
                    </div>

                    <button type="submit" id="btnSubmit" name="action" value="add" class="btn-primary-custom">
                        Đăng Ký Xe
                    </button>
                    <button type="button" id="btnCancel" onclick="resetToAddForm()"
                            style="display:none; margin-top:10px; width:100%; padding:10px; border-radius:8px;
                                   background:transparent; border:1px solid #2a2d35; color:#8a919e;
                                   font-weight:600; cursor:pointer; transition:0.2s;"
                            onmouseover="this.style.borderColor='#ff4d4d';this.style.color='#ff4d4d';"
                            onmouseout="this.style.borderColor='#2a2d35';this.style.color='#8a919e';">
                        <i class="fa-solid fa-xmark me-2"></i>Huỷ chỉnh sửa
                    </button>
                </form>
            </div>
        </div>

        <!-- ====== BẢNG DANH SÁCH XE ====== -->
        <div class="col-lg-8">
            <div class="card-custom">
                <%
                    ArrayList<Car> list = (ArrayList<Car>) request.getAttribute("CAR_LIST");
                    int totalCars = (list != null) ? list.size() : 0;
                %>
                <% if (request.getAttribute("error") != null) { %>
                <div class="alert" style="background-color: rgba(255, 77, 77, 0.1); border: 1px solid #ff4d4d; color: #ff4d4d; padding: 12px; border-radius: 8px; margin-bottom: 20px;">
                    <i class="fa-solid fa-circle-exclamation me-2"></i> <%= request.getAttribute("error") %>
                </div>
                <% } %>

                <div class="d-flex align-items-center mb-4">
                    <h5 class="fw-bold text-yellow mb-0 text-capitalize">Phương tiện đã liên kết</h5>
                    <span class="badge bg-secondary ms-3 px-3 py-2 rounded-pill"><%= totalCars %>&nbsp; Xe</span>
                </div>

                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead>
                            <tr>
                                <th>Mã Xe</th>
                                <th>Biển Số Xe</th>
                                <th>Hãng Xe</th>
                                <th>Dòng Xe</th>
                                <th>Màu Sắc</th>
                                <th>Ngày Đăng Ký</th>
                                <th class="text-center">Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (list != null && !list.isEmpty()) {
                                    for (Car c : list) {
                            %>
                            <tr>
                                <td class="fw-bold" style="color: var(--text-muted) !important;">#<%= c.getId() %></td>
                                <td>
                                    <%-- BIỂN SỐ CÓ THỂ CLICK ĐỂ XEM CHI TIẾT --%>
                                    <span class="plate-badge"
                                          onclick="showCarDetail(
                                              '<%= c.getId() %>',
                                              '<%= c.getLicensePlate() %>',
                                              '<%= c.getBrand() %>',
                                              '<%= c.getModel() %>',
                                              '<%= c.getColor() %>',
                                              '<%= c.getCreatedAt() %>',
                                              '<%= (c.getCarImage() != null ? c.getCarImage() : "") %>',
                                              '<%= (c.getPlateImage() != null ? c.getPlateImage() : "") %>'
                                          )"
                                          title="Click để xem chi tiết">
                                        <%= c.getLicensePlate() %>
                                        <span class="click-hint">▶ xem chi tiết</span>
                                    </span>
                                </td>
                                <td class="fw-bold"><%= c.getBrand() %></td>
                                <td><%= c.getModel() %></td>
                                <td><%= c.getColor() %></td>
                                <td style="color: var(--text-muted) !important;"><%= c.getCreatedAt() %></td>
                                <td class="text-center">
                                    <div class="d-flex justify-content: center gap-2">
                                        <button type="button" class="btn-action btn-edit"
                                                onclick="fillToForm('<%= c.getId() %>', '<%= c.getLicensePlate() %>', '<%= c.getBrand() %>', '<%= c.getModel() %>', '<%= c.getColor() %>')">
                                            Sửa
                                        </button>
                                        <a href="cars?action=delete&vehicleId=<%= c.getId() %>" class="btn-action btn-delete">Xóa</a>
                                    </div>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="7" class="text-center py-5" style="color: var(--text-muted) !important;">
                                    <i class="fa-solid fa-car-burst fa-2x mb-3 d-block"></i>
                                    Hệ thống chưa ghi nhận phương tiện nào.<br>
                                    Vui lòng sử dụng biểu mẫu bên cạnh để thêm xe!
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // ====== PREVIEW ẢNH KHI CHỌN FILE ======
    function previewImage(input, previewId, placeholderId, removeBtnId) {
        const preview = document.getElementById(previewId);
        const placeholder = document.getElementById(placeholderId);
        const removeBtn = document.getElementById(removeBtnId);

        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                preview.src = e.target.result;
                preview.classList.add('has-img');
                placeholder.classList.add('hidden');
                removeBtn.classList.add('visible');
            };
            reader.readAsDataURL(input.files[0]);
        }
    }

    function clearPreview(inputId, previewId, placeholderId, removeBtnId) {
        document.getElementById(inputId).value = '';
        const preview = document.getElementById(previewId);
        preview.src = '';
        preview.classList.remove('has-img');
        document.getElementById(placeholderId).classList.remove('hidden');
        document.getElementById(removeBtnId).classList.remove('visible');
    }

    // ====== MỞ MODAL CHI TIẾT XE KHI CLICK BIỂN SỐ ======
    function showCarDetail(id, plate, brand, model, color, date, carImg, plateImg) {
        document.getElementById('mId').innerText = '#' + id;
        document.getElementById('mPlateHeader').innerText = plate;
        document.getElementById('mBrand').innerText = brand;
        document.getElementById('mModel').innerText = model;
        document.getElementById('mColor').innerText = color;
        document.getElementById('mDate').innerText = date;

        // Reset trạng thái ảnh
        const mCarImg = document.getElementById('mCarImg');
        const mPlateImg = document.getElementById('mPlateImg');
        const mCarNoImg = document.getElementById('mCarNoImg');
        const mPlateNoImg = document.getElementById('mPlateNoImg');

        // Set ảnh tổng thể
        if (carImg && carImg.trim() !== '') {
            mCarImg.style.display = 'block';
            mCarNoImg.style.display = 'none';
            mCarImg.src = 'uploads/' + carImg;
        } else {
            mCarImg.style.display = 'none';
            mCarNoImg.style.display = 'flex';
        }

        // Set ảnh biển số
        if (plateImg && plateImg.trim() !== '') {
            mPlateImg.style.display = 'block';
            mPlateNoImg.style.display = 'none';
            mPlateImg.src = 'uploads/' + plateImg;
        } else {
            mPlateImg.style.display = 'none';
            mPlateNoImg.style.display = 'flex';
        }

        document.getElementById('carDetailOverlay').classList.add('active');
    }

    function closeCarModal(event) {
        if (event.target === document.getElementById('carDetailOverlay')) {
            document.getElementById('carDetailOverlay').classList.remove('active');
        }
    }

    // ====== LIGHTBOX XEM ẢNH TO ======
    function openLightbox(src) {
        document.getElementById('lightboxImg').src = src;
        document.getElementById('lightbox').classList.add('active');
    }
    function closeLightbox() {
        document.getElementById('lightbox').classList.remove('active');
    }
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            document.getElementById('lightbox').classList.remove('active');
            document.getElementById('carDetailOverlay').classList.remove('active');
        }
    });

    // ====== ĐIỀN THÔNG TIN VÀO FORM KHI CLICK NÚT SỬA ======
    function fillToForm(id, plate, brand, model, color) {
        document.getElementById('vId').value = id;
        document.getElementById('brand').value = brand;
        document.getElementById('model').value = model;
        document.getElementById('color').value = color;

        // LOCK biển số: ẩn input, hiện badge chỉ đọc
        const plateInput = document.getElementById('plate');
        const plateLockedDisplay = document.getElementById('plateLockedDisplay');
        const plateLockedNote = document.getElementById('plateLockedNote');
        const plateBadgeDisplay = document.getElementById('plateBadgeDisplay');

        plateInput.style.display = 'none';
        plateInput.removeAttribute('required'); // bỏ required để form submit được
        plateInput.value = plate;               // vẫn giữ value để server nhận nếu cần

        plateLockedDisplay.style.display = 'flex';
        plateBadgeDisplay.innerText = plate;
        plateLockedNote.style.display = 'inline';

        document.getElementById('formTitle').innerHTML = "<i class='fa-solid fa-gear me-2'></i>Chỉnh Sửa Xe";
        const btn = document.getElementById('btnSubmit');
        btn.name = 'action';
        btn.value = 'update';
        btn.innerText = 'Cập nhật thông tin xe';

        document.getElementById('btnCancel').style.display = 'block';
        document.getElementById('brand').scrollIntoView({ behavior: 'smooth', block: 'center' });
        clearPreview('carImageInput', 'carPreview', 'carPlaceholder', 'removeCarBtn');
        clearPreview('plateImageInput', 'platePreview', 'platePlaceholder', 'removePlateBtn');
    }

    // Khi reset về form Đăng Ký mới → mở khóa biển số lại
    function resetToAddForm() {
        const plateInput = document.getElementById('plate');
        plateInput.style.display = 'block';
        plateInput.setAttribute('required', 'required');
        plateInput.value = '';

        document.getElementById('plateLockedDisplay').style.display = 'none';
        document.getElementById('plateLockedNote').style.display = 'none';
        document.getElementById('vId').value = '';
        document.getElementById('brand').value = '';
        document.getElementById('model').value = '';
        document.getElementById('color').value = '';
        document.getElementById('formTitle').innerHTML = "<i class='fa-solid fa-plus me-2'></i>Đăng Ký Xe Mới";
        const btn = document.getElementById('btnSubmit');
        btn.name = 'action';
        document.getElementById('btnCancel').style.display = 'none';
        clearPreview('carImageInput', 'carPreview', 'carPlaceholder', 'removeCarBtn');
        clearPreview('plateImageInput', 'platePreview', 'platePlaceholder', 'removePlateBtn');
    }
</script>
</body>
</html>
