<header class="site-header">
    <div class="header-inner">
        <span class="site-title">Patient Data App</span>
        <nav class="nav">
            <a href="/">Home</a>
            <a href="/patientList">Patients</a>
            <a href="/addPatient?from=<%= request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "") %>">Add Patient</a>
        </nav>
    </div>
</header>