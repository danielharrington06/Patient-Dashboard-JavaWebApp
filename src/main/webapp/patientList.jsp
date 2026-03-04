<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Patient Data App</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
    <h2>Patients:</h2>
    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null)
        {
    %>
        <p style="color: red;"><%= errorMessage %></p>
    <%
        }
    %>
    <ul>
        <%
        Map<String, String> patients = (Map<String, String>) request.getAttribute("patientNames");
        if (patients != null) {
            for (Map.Entry<String, String> entry : patients.entrySet()) {

                String id = entry.getKey();
                String patientName = entry.getValue();
                String href = "patient?id=" + id;
        %>
                <li>
                    <a href="<%= href %>"><%= patientName %></a>
                </li>
        <%
            }
        }
        %>
    </ul>
</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>
