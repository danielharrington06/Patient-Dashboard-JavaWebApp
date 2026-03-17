<header class="site-header">
    <div class="header-inner">
        <a href="/" class="site-title">Patient Data App</a>
        <nav class="nav">
            <a href="/home" class="${activePage == 'home' ? 'nav-active' : ''}">Home</a>
            <a href="/patientList" class="${activePage == 'patients' ? 'nav-active' : ''}">All Patients</a>
            <a href="/addPatient" class="${activePage == 'add' ? 'nav-active' : ''}">Add Patient</a>
            <a href="/statistics" class="${activePage == 'statistics' ? 'nav-active' : ''}">Statistics</a>
        </nav>
    </div>
</header>