<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="model.Booking"%>
<%@page import="model.User"%>

<%
    User user = (User) session.getAttribute("USER");

    if(user == null){
        response.sendRedirect("login_page.jsp");
        return;
    }

    ArrayList<Booking> bookingList =
            (ArrayList<Booking>) request.getAttribute("BOOKING_LIST");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lịch Sử Booking</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        :root{
            --bg-main:#0a0b0d;
            --bg-card:#14161a;
            --yellow:#ffcc00;
            --border:#2a2d35;
            --text:#e0e4e8;
        }

        body{
            background:var(--bg-main);
            color:var(--text);
            padding:40px;
        }

        .card-custom{
            background:var(--bg-card);
            border:1px solid var(--border);
            border-radius:16px;
            padding:25px;
        }

        .title{
            color:var(--yellow);
            font-weight:bold;
            text-align:center;
            margin-bottom:25px;
        }

        table{
            color:white !important;
        }

        th{
            color:var(--yellow);
        }

        .btn-back{
            text-decoration:none;
            color:#999;
            margin-bottom:20px;
            display:inline-block;
        }

        .status-pending{
            color:orange;
            font-weight:bold;
        }

        .status-confirmed{
            color:limegreen;
            font-weight:bold;
        }

        .status-completed{
             color:#32cd32;
            font-weight:bold;
        }
        .status-cancelled{
            color:red;
            font-weight:bold;
        }

        .payment-paid{
            color:limegreen;
            font-weight:bold;
        }

        .payment-unpaid{
            color:orange;
            font-weight:bold;
        }
    </style>
</head>

<body>

<div class="container">

    <a href="index.jsp" class="btn-back">
        ← Quay lại trang chủ
    </a>

    <div class="card-custom">

        <h1 class="title">
            Lịch Sử Đặt Lịch
        </h1>

        <div class="table-responsive">

            <table class="table table-dark table-hover table-bordered align-middle">

                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Biển số xe</th>
                        <th>Dịch vụ</th>
                        <th>Ngày đặt</th>
                        <th>Giờ đặt</th>
                        <th>Giá</th>
                        <th>Trạng thái</th>
                        <th>Thanh toán</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>

                <tbody>

                <%
                    if(bookingList != null && !bookingList.isEmpty()){

                        for(Booking b : bookingList){
                %>

                    <tr>

                        <td><%= b.getBookingID() %></td>

                        <td><%= b.getLicensePlate() %></td>

                        <td><%= b.getServiceType() %></td>

                        <td><%= b.getBookingDate() %></td>

                        <td><%= b.getBookingTime() %></td>

                        <td>
                            <%= String.format("%,.0f", b.getPrice()) %> VNĐ
                        </td>

                        <!-- TRẠNG THÁI -->
                        <td>

                            <%
                                String status = b.getStatus();

                                if("Pending".equalsIgnoreCase(status)){
                            %>

                                <span class="status-pending">
                                    Pending
                                </span>

                            <%
                                }else if("Confirmed".equalsIgnoreCase(status)){
                            %>

                                <span class="status-confirmed">
                                    Confirmed
                                </span>

                            <%
                                }else if("Completed".equalsIgnoreCase(status)){
                            %>

                                <span class="status-completed">
                                    Completed
                                </span>

                            <%
                                }else if("Cancelled".equalsIgnoreCase(status)){
                            %>

                                <span class="status-cancelled">
                                    Cancelled
                                </span>

                            <%
                                }
                            %>

                        </td>

                        <!-- THANH TOÁN -->
                        <td>

                            <%
                                String payment = b.getPaymentStatus();

                                if("Paid".equalsIgnoreCase(payment)){
                            %>

                                <span class="payment-paid">
                                    Paid
                                </span>

                            <%
                                }else{
                            %>

                                <span class="payment-unpaid">
                                    Unpaid
                                </span>

                            <%
                                }
                            %>

                        </td>

                        <!-- THAO TÁC -->
                        <td>

                            <%
                                if("Pending".equalsIgnoreCase(b.getStatus())){
                            %>

                                <a href="BookingHistoryServlet?action=cancel&id=<%=b.getBookingID()%>"
                                   class="btn btn-danger btn-sm"
                                   onclick="return confirm('Bạn có chắc muốn hủy lịch này?');">

                                    Hủy lịch

                                </a>

                            <%
                                }else if("Completed".equalsIgnoreCase(b.getStatus())){
                            %>

                                <span class="status-completed">
                                    Đã hoàn thành
                                </span>

                            <%
                                }else if("Cancelled".equalsIgnoreCase(b.getStatus())){
                            %>

                                <span class="status-cancelled">
                                    Đã hủy
                                </span>

                            <%
                                }else if("Confirmed".equalsIgnoreCase(b.getStatus())){
                            %>

                                <span class="status-confirmed">
                                    Đã xác nhận
                                </span>

                            <%
                                }
                            %>

                        </td>

                    </tr>

                <%
                        }
                    }else{
                %>

                    <tr>
                        <td colspan="9" class="text-center">
                            Chưa có lịch đặt nào.
                        </td>
                    </tr>

                <%
                    }
                %>

                </tbody>

            </table>

        </div>

    </div>

</div>

</body>
</html>
