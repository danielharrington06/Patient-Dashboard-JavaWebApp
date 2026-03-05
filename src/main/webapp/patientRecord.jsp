<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">   
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Patient Data App</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
    <h2>Patient Record</h2>
    <%
    String fromSearch = request.getParameter("from");
    String backHref = (fromSearch != null && !fromSearch.isEmpty())
        ? "/runsearch?searchstring=" + fromSearch
        : "/patientList";
    %>
    <a href="<%= backHref %>" class="btn btn-secondary" style="margin-bottom: 1rem;">← Back to patients</a>
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
    LinkedHashMap<String, String> patientRecord = (LinkedHashMap<String, String>) request.getAttribute("patientRecord");
    List<String> columns = (List<String>) request.getAttribute("columnNames");
    %>
    <dl>
    <%
        for (Map.Entry<String, String> entry : patientRecord.entrySet()) {
    %>
        <dt><%= entry.getKey() %></dt>
        <dd><%= entry.getValue() != null && !entry.getValue().isEmpty() ? entry.getValue() : "—" %></dd>
    <%
    }
    %>
    </dl>
</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>
