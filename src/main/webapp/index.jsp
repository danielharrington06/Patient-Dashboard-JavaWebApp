<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="uk.ac.ucl.model.ModelFactory" %>
<%@ page import="uk.ac.ucl.model.Model" %>
<%
    Model model = ModelFactory.getModel();
    String currentFile = model.getCurrentDataFile().replace("\\", "/");
%>
<html lang="en">
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Patient Data App</title>
</head>
<body>
<jsp:include page="/header.jsp"/>

<div class="main">
    <div class="welcome-card">
        <h2>Welcome to the Patient Data App</h2>
        <p>Use the links below or the navigation above to get started.</p>
        <div class="quick-links">
            <a href="patientList" class="btn">View All Patients</a>
            <a href="search.jsp" class="btn btn-secondary">Search</a>
        </div>
    </div>

    <div class="search-card" style="margin-top: 2rem;">
        <h3 style="margin-bottom: 1rem;">Select Dataset</h3>
        <div style="display: flex; align-items: center; gap: 1.5rem; flex-wrap: wrap;">
            <form method="POST" action="/switchdataset">
                <div class="filter-options" style="gap: 1rem;">
                    <% for (String size : new String[]{"100", "1000", "10000", "100000"}) {
                        String file = "data/patients" + size + ".csv";
                        boolean active = currentFile.equals(file);
                    %>
                    <label class="filter-chip dataset-chip <%= active ? "dataset-chip-active" : "" %>">
                        <input type="radio" name="datasize" value="<%= size %>"
                            onchange="this.form.submit()"
                            <%= active ? "checked" : "" %>>
                        <%= size %> patients
                    </label>
                    <% } %>
                </div>
            </form>

            <form method="POST" action="/exportJSON">
                <button type="submit" class="btn btn-primary">Export to JSON</button>
            </form>
            
            <%
                Boolean exportSuccess = (Boolean) session.getAttribute("exportSuccess");
                String exportError = (String) session.getAttribute("exportError");
                String switchSuccess = (String) session.getAttribute("switchSuccess");
                String switchError = (String) session.getAttribute("switchError");
                session.removeAttribute("exportSuccess");
                session.removeAttribute("exportError");
                session.removeAttribute("switchSuccess");
                session.removeAttribute("switchError");
            %>
            <% if (Boolean.TRUE.equals(exportSuccess)) { %>
                <p class="success" id="flash-msg">Data exported to data/patients.json successfully.</p>
            <% } %>
            <% if (exportError != null) { %>
                <p class="error" id="flash-msg">Export failed: <%= exportError %></p>
            <% } %>
            <% if (switchSuccess != null) { %>
                <p class="success" id="flash-msg">Dataset switched to <%= switchSuccess %> patients.</p>
            <% } %>
            <% if (switchError != null) { %>
                <p class="error" id="flash-msg">Failed to switch dataset: <%= switchError %></p>
            <% } %>

            <script>
                const msg = document.getElementById("flash-msg");
                if (msg) {
                    setTimeout(() => {
                        msg.style.transition = "opacity 0.5s ease";
                        msg.style.opacity = "0";
                        setTimeout(() => msg.remove(), 500);
                    }, 2500);
                }
            </script>

        </div>
    </div>
</div>

<jsp:include page="/footer.jsp"/>
</body>
</html>