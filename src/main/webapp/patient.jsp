<%@ page import="java.util.List" %>
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
    <%
    List<String> patientInfo = (List<String>) request.getAttribute("patientInfo");

    String[] labels = {
        "Patient ID",
        "Date of Birth",
        "SSN",
        "NHS Number",
        "Driver ID",
        "Title",
        "First Name",
        "Last Name",
        "Gender",
        "Ethnicity",
        "Race",
        "Marital Status",
        "Birth City",
        "Birth State",
        "Birth Country",
        "Address",
        "City",
        "State",
        "Postcode"
    };
    %>

    <dl>
    <%
    int count = Math.min(patientInfo.size(), labels.length);

    for (int i = 0; i < count; i++) {
    %>
        <dt><strong><%= labels[i] %></strong></dt>
        <dd><%= patientInfo.get(i) %></dd>
    <%
    }
    %>
    </dl>
</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>
