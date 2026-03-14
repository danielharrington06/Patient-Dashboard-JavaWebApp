<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (request.getAttribute("patientCount") == null) {
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }
    
    int patientCount = (Integer) request.getAttribute("patientCount");
    String currentFile = (String) request.getAttribute("currentFile");

    Boolean exportSuccess = (Boolean) session.getAttribute("exportSuccess");
    String exportError = (String) session.getAttribute("exportError");
    String switchSuccess = (String) session.getAttribute("switchSuccess");
    String switchError = (String) session.getAttribute("switchError");
    session.removeAttribute("exportSuccess");
    session.removeAttribute("exportError");
    session.removeAttribute("switchSuccess");
    session.removeAttribute("switchError");
%>
<html lang="en">
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Patient Data App</title>
</head>
<body>
<jsp:include page="/header.jsp"/>

<div class="main">

    <div class="welcome-card welcome-card--full">
        <h2>Patient Data App</h2>
        <p>Currently viewing <strong><%= patientCount %></strong> patients.</p>

        <div class="home-cards">

            <a href="patientList?search=" class="home-card-link">
                <div class="home-card home-card--search">
                    <div class="home-card-title">Search Patients</div>
                    <div class="home-card-desc">Filter by name, gender, ethnicity and more</div>
                </div>
            </a>

            <a href="addPatient" class="home-card-link">
                <div class="home-card home-card--add">
                    <div class="home-card-title">Add Patient</div>
                    <div class="home-card-desc">Register a new patient record</div>
                </div>
            </a>

            <a href="statistics" class="home-card-link">
                <div class="home-card home-card--stats">
                    <div class="home-card-title">Statistics</div>
                    <div class="home-card-desc">Insights from across the dataset</div>
                </div>
            </a>

        </div>
    </div>

    <div class="search-card">
        <h3 class="dataset-heading">Select Dataset</h3>
        <div class="dataset-row">
            <form method="POST" action="/switchdataset">
                <div class="dataset-grid">
                    <%
                        String[] sizes = {"100", "1000", "10000", "100000"};
                        for (String size : sizes) {
                            String file = "data/patients" + size + ".csv";
                            boolean active = currentFile.equals(file);
                    %>
                    <label class="filter-chip dataset-chip <%= active ? "dataset-chip-active" : "" %>">
                        <input type="radio" name="datasize" value="<%= size %>"
                               onchange="this.form.submit()"
                               <%= active ? "checked" : "" %>>
                        <span class="dataset-chip-name"><%= size %> patients</span>
                    </label>
                    <% } %>
                </div>
            </form>

            <div style="display: flex; align-items: center; gap: 1rem;">
                <form method="POST" action="/exportJSON">
                    <button type="submit" class="btn btn-secondary">Export to JSON</button>
                </form>

                <% if (Boolean.TRUE.equals(exportSuccess)) { %>
                    <p class="success flash-msg" id="flash-msg">Exported successfully.</p>
                <% } %>
                <% if (exportError != null) { %>
                    <p class="error flash-msg" id="flash-msg">Export failed: <%= exportError %></p>
                <% } %>
                <% if (switchSuccess != null) { %>
                    <p class="success flash-msg" id="flash-msg">Switched to <%= switchSuccess %> patients.</p>
                <% } %>
                <% if (switchError != null) { %>
                    <p class="error flash-msg" id="flash-msg">Failed to switch dataset.</p>
                <% } %>
            </div>
        </div>
    </div>

</div>

<jsp:include page="/footer.jsp"/>

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
</body>
</html>