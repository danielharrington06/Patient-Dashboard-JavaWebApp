<%@ page import="uk.ac.ucl.model.Patient" %>
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
    <h2>Patient Record</h2>
    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null)
        {
    %>
        <p class="error"><%= errorMessage %></p>
    <%
        }
    %>
    <%
    Patient patientInfo = (Patient) request.getAttribute("patientInfo");
    List<String> columns = (List<String>) request.getAttribute("columnNames");
    %>
    <dl>
    <%
    for (int i = 0; i < columns.size(); i++) {
    %>
        <dt><strong><%= columns.get(i) %></strong></dt>
        <dd><%= patientInfo.get(columns.get(i)) %></dd>
    <%
    }
    %>
    </dl>
</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>
