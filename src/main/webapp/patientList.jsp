<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">
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
            <%
                List<String> columnDisplayNames = (List<String>) request.getAttribute("columnDisplayNames");
            %>
            <thead>
                <tr>
                    <% for (String colName : columnDisplayNames) { %>
                        <th><%= colName %></th>
                    <% } %>
                </tr>
            </thead>
            <tbody>
            <%
                String search = request.getParameter("searchstring");
                for (Map.Entry<String, List<String>> entry : patients.entrySet()) {
                    String id = entry.getKey();
                    List<String> data = entry.getValue();
                    String fromParam = (search != null && !search.isEmpty()) ? "&from=" + search : "";
                    String href = "patientRecord?id=" + id + fromParam;
            %>
                <tr data-href="<%= href %>">
                    <td><%= data.get(0) %></td>
                    <td><%= data.get(1) %></td>
                    <td><%= data.get(2) %></td>
                    <td><%= data.get(3) %></td>
                    <td><%= data.get(4) %></td>
                    <td><%= data.get(5) %></td>
                    <td><%= data.get(6) %></td>
                    <td><%= data.get(7) %></td>
                    <td><%= data.get(8) %></td>
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
            Integer currentPage = (Integer) request.getAttribute("currentPage");
            Integer totalPages = (Integer) request.getAttribute("totalPages");
            Integer totalPatients = (Integer) request.getAttribute("totalPatients");
            String searchTerm = request.getParameter("searchstring");
            String baseUrl = (searchTerm != null && !searchTerm.isEmpty())
                ? "/runsearch?searchstring=" + searchTerm + "&page="
                : "/patientList?page=";
            if (currentPage != null && totalPages != null) {
        %>
        <div class="pagination">
            <span class="result-count">
                <%= totalPatients %> patient(s) found — page <%= currentPage %> of <%= totalPages %>
            </span>
            <div class="page-controls">
                <% if (currentPage > 1) { %>
                    <a href="<%= baseUrl %><%= currentPage - 1 %>" class="btn btn-secondary btn-sm">← Previous</a>
                <% } %>

                <% for (int p = 1; p <= totalPages; p++) { %>
                    <a href="<%= baseUrl %><%= p %>"
                    class="btn btn-sm <%= p == currentPage ? "" : "btn-secondary" %>">
                        <%= p %>
                    </a>
                <% } %>

                <% if (currentPage < totalPages) { %>
                    <a href="<%= baseUrl %><%= currentPage + 1 %>" class="btn btn-secondary btn-sm">Next →</a>
                <% } %>
            </div>
        </div>
        <%
            }
        }
    %>
</div>
<jsp:include page="/footer.jsp"/>
<script>
    document.querySelectorAll('tr[data-href]').forEach(row => {
        row.addEventListener('click', () => {
            window.location = row.dataset.href;
        });
    });
</script>
</body>
</html>