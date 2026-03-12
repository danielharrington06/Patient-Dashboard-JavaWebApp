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
    </div>
</div>

<jsp:include page="/footer.jsp"/>
</body>
</html>