<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Patient Data App - Patients</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
    <h2>Patients</h2>

    <%-- Search bar --%>
    <div class="search-card" style="margin-bottom: 1.5rem;">
        <form method="GET" action="/runsearch">
            <div class="search-row">
                <input class="search-input" type="text" name="searchstring"
                       placeholder="Search patients by name, city, state..."
                       value="<%= request.getParameter("searchstring") != null ? request.getParameter("searchstring") : "" %>"/>
                <input class="btn" type="submit" value="Search"/>
            </div>
        </form>
    </div>

    <%-- Error message --%>
    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
        <p class="error"><%= errorMessage %></p>
    <%
        }
    %>

    <%-- Patient table --%>
    <%
        Map<String, List<String>> patients = (Map<String, List<String>>) request.getAttribute("patientData");
        if (patients != null && !patients.isEmpty()) {
    %>
    <div class="table-wrapper">
        <table class="patient-table">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Date of Birth</th>
                    <th>Gender</th>
                    <th>City</th>
                    <th>State</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
            <%
                for (Map.Entry<String, List<String>> entry : patients.entrySet()) {
                    String id = entry.getKey();
                    List<String> data = entry.getValue();
                    // expects list: [fullName, dob, gender, city, state]
                    String href = "patient?id=" + id;
            %>
                <tr>
                    <td><%= data.get(0) %></td>
                    <td><%= data.get(1) %></td>
                    <td><%= data.get(2) %></td>
                    <td><%= data.get(3) %></td>
                    <td><%= data.get(4) %></td>
                    <td><a href="<%= href %>" class="btn btn-sm">View more...</a></td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
    <%
        } else if (errorMessage == null) {
    %>
        <p class="text-muted">No patients found.</p>
    <%
        }
    %>

    <%-- Result count --%>
    <%
        if (patients != null) {
    %>
        <p class="result-count"><%= patients.size() %> patient(s) found</p>
    <%
        }
    %>
</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>