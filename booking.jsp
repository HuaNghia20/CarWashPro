<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="model.User, model.Car, model.Booking, java.util.ArrayList"%>
<%
    User user = (User) session.getAttribute("USER");
    if(user == null) {
        response.sendRedirect("login_page.jsp");
        return;
    }

    ArrayList<Car> carList =
            (ArrayList<Car>) request.getAttribute("CAR_LIST");

    String serviceParam =
            request.getParameter("service") != null
            ? request.getParameter("service")
            : (String) request.getAttribute("SERVICE");

    int maxDays = 7;

    if (user.getTierID() == 2) {
        maxDays = 10;
    } else if (user.getTierID() == 3) {
        maxDays = 12;
    } else if (user.getTierID() == 4) {
        maxDays = 14;
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt Lịch - CarWash Pro</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

    <style>
        :root{
            --bg-main:#0a0b0d;
            --bg-card:#14161a;
            --yellow-accent:#ffcc00;
            --border-color:#2a2d35;
            --text-main:#e0e4e8;
        }

        body{
            background:var(--bg-main);
            color:var(--text-main);
            font-family:sans-serif;
            padding:40px 0;
        }

        .container-box{
            max-width:800px;
            margin:0 auto;
        }

        .card-custom{
            background:var(--bg-card);
            border:1px solid var(--border-color);
            padding:30px;
            border-radius:16px;
            margin-bottom:25px;
        }

        .form-label{
            color:var(--yellow-accent);
            font-weight:700;
        }

        .form-control,
        .form-select{
            background:#0d0f12;
            border:1px solid var(--border-color);
            color:#fff !important;
            padding:12px;
        }

        .form-control:focus,
        .form-select:focus{
            border-color:var(--yellow-accent);
            box-shadow:0 0 8px rgba(255,204,0,0.3);
        }

        .form-select option{
            background:#14161a;
        }

        .btn-submit{
            background-color:var(--yellow-accent);
            color:#000;
            font-weight:700;
            border:none;
            padding:12px;
            width:100%;
            border-radius:8px;
            font-size:1rem;
            transition:0.2s;
        }

        .btn-submit:hover{
            background-color:#ffda33;
            transform:scale(1.01);
        }

        .btn-slot-custom{
            display:flex;
            align-items:center;
            justify-content:center;
            height:50px;
            font-size:0.85rem;
            border:1px solid var(--yellow-accent);
            color:#fff;
            border-radius:8px;
            transition:0.3s;
        }

        .btn-check:checked + .btn-slot-custom{
            background:var(--yellow-accent);
            color:#000;
            font-weight:bold;
        }

        .date-input-wrapper{
            position:relative;
        }

        #datePicker::-webkit-calendar-picker-indicator{
            position:absolute;
            top:0;
            left:0;
            right:0;
            bottom:0;
            width:100%;
            height:100%;
            cursor:pointer;
            background:transparent;
            color:transparent;
        }
    </style>
</head>

<body>

<%
    String msg = (String) session.getAttribute("MESSAGE");
    String err = (String) session.getAttribute("ERROR");

    if(msg != null){
%>

<div class="alert alert-success alert-dismissible fade show container-box"
     role="alert"
     style="margin-top:20px;">

    <i class="fa-solid fa-check-circle me-2"></i>
    <%= msg %>

    <button type="button"
            class="btn-close"
            data-bs-dismiss="alert">
    </button>

</div>

<%
        session.removeAttribute("MESSAGE");
    }

    if(err != null){
%>

<div class="alert alert-danger alert-dismissible fade show container-box"
     role="alert"
     style="margin-top:20px;">

    <i class="fa-solid fa-exclamation-circle me-2"></i>
    <%= err %>

    <button type="button"
            class="btn-close"
            data-bs-dismiss="alert">
    </button>

</div>

<%
        session.removeAttribute("ERROR");
    }
%>

<div class="container container-box">

    <a href="index.jsp"
       class="text-decoration-none text-secondary mb-3 d-inline-block">

        <i class="fa-solid fa-arrow-left me-2"></i>
        Quay lại

    </a>

    <div class="card-custom">

        <h2 class="text-center fw-bold mb-4"
            style="color:var(--yellow-accent);">

            <i class="fa-solid fa-calendar-check me-2"></i>
            Tiến Hành Đặt Lịch

        </h2>

        <form action="BookingServlet" method="POST">

            <input type="hidden"
                   name="serviceType"
                   value="<%= serviceParam %>">

            <div class="row">

                <div class="col-12 mb-3">

                    <label class="form-label">
                        Người đặt lịch
                    </label>

                    <input type="text"
       value="<%= user.getFullname() %>"
       class="form-control"
       readonly
       tabindex="-1"
       style="pointer-events:none;">

                </div>

            </div>

            <div class="row">

                <div class="col-md-6 mb-3">

                    <label class="form-label">
                        Chọn Xe
                    </label>

                    <% if(carList == null || carList.isEmpty()) { %>

                    <div class="alert"
                         style="background:rgba(255,77,77,0.1);
                                border:1px solid #ff4d4d;
                                color:#ff4d4d;
                                border-radius:8px;
                                padding:10px;">

                        <i class="fa-solid fa-triangle-exclamation me-2"></i>

                        Bạn chưa có xe nào.

                        <a href="cars" style="color:#ffcc00;">
                            Đăng ký xe tại đây
                        </a>

                    </div>

                    <% } else { %>

                    <select name="licensePlate"
                            class="form-select"
                            required>

                        <option value=""
                                disabled
                                selected>

                            -- Chọn biển số xe --

                        </option>

                        <% for(Car c : carList) { %>

                        <option value="<%= c.getLicensePlate() %>">

                            <%= c.getLicensePlate() %> -
                            <%= c.getBrand() %>
                            <%= c.getModel() %>

                        </option>

                        <% } %>

                    </select>

                    <% } %>

                </div>

                <div class="col-md-6 mb-3 date-input-wrapper">

                    <label class="form-label">

                        Ngày Đặt

                        <span style="color:var(--yellow-accent);">
                            (tối đa <%= maxDays %> ngày tới)
                        </span>

                    </label>

                    <input type="date"
                           name="bookingDate"
                           id="datePicker"
                           class="form-control"
                           min="<%= request.getAttribute("MIN_DATE") %>"
                           max="<%= request.getAttribute("MAX_DATE") %>"
                           required>

                </div>

            </div>

            <div class="mb-4">

                <label class="form-label">
                    Chọn khung giờ rửa xe
                </label>

                <div id="slot-container" class="row g-2">

                    <p class="fw-bold text-white">

                        <i class="fa-regular fa-calendar me-1"></i>

                        Vui lòng chọn ngày để xem lịch trống.

                    </p>

                </div>

            </div>

            <button type="submit"
                    class="btn-submit"
                    <%= (carList == null || carList.isEmpty())
                        ? "disabled"
                        : "" %>>

                <i class="fa-solid fa-check me-2"></i>

                Xác Nhận Booking

            </button>

        </form>

    </div>

</div>

<script>

document.getElementById('datePicker').addEventListener('change', function () {

    let date = this.value;
    let container = document.getElementById('slot-container');

    if (!date) return;

    container.innerHTML =
        '<p class="fw-bold text-white">' +
        '<i class="fa-solid fa-spinner fa-spin me-1"></i>' +
        'Đang tải khung giờ...' +
        '</p>';

    fetch('BookingServlet?action=getSlots&date=' + date)

        .then(res => res.text())

        .then(html => {
            container.innerHTML = html;
        })

        .catch(() => {
            container.innerHTML =
                '<p class="text-danger">' +
                'Lỗi tải dữ liệu. Vui lòng thử lại.' +
                '</p>';
        });
});

</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
