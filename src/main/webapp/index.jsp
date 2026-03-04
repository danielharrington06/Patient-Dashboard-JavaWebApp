<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
</div>

<jsp:include page="/footer.jsp"/>
</body>
</html>