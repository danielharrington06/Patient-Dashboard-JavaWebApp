<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Patient Statistics</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
<jsp:include page="/header.jsp"/>

<div class="main">
    <h2 class="page-title">Statistics</h2>

    <%-- Insights --%>
    <div class="stats-grid">

        <div class="stat-card card">
            <div class="stat-label">Total Patients</div>
            <div class="stat-value"><%= request.getAttribute("totalPatients") %></div>
        </div>

        <div class="stat-card card">
            <div class="stat-label">Living Patients</div>
            <div class="stat-value"><%= request.getAttribute("livingCount") %></div>
        </div>

        <div class="stat-card card">
            <div class="stat-label">Oldest Alive</div>
            <div class="stat-value"><%= request.getAttribute("oldestAliveAge") %><span class="stat-unit"> yrs</span></div>
        </div>

        <div class="stat-card card">
            <div class="stat-label">Youngest Alive</div>
            <div class="stat-value"><%= request.getAttribute("youngestAliveAge") %><span class="stat-unit"> yrs</span></div>
        </div>

        <div class="stat-card card">
            <div class="stat-label">Avg Age (Living)</div>
            <div class="stat-value"><%= request.getAttribute("averageAliveAge") %><span class="stat-unit"> yrs</span></div>
        </div>

        <div class="stat-card card">
            <div class="stat-label">Avg Age at Death</div>
            <div class="stat-value"><%= request.getAttribute("averageAgeAtDeath") %><span class="stat-unit"> yrs</span></div>
        </div>

        <div class="stat-card card">
            <div class="stat-label">Distinct Cities</div>
            <div class="stat-value"><%= request.getAttribute("distinctCityCount") %></div>
        </div>

        <div class="stat-card card">
            <div class="stat-label">Most Common City</div>
            <div class="stat-value-text"><%= request.getAttribute("mostCommonCity") %></div>
            <div class="stat-sub"><%= request.getAttribute("mostCommonCityCount") %> patients</div>
        </div>

        <div class="stat-card card">
            <div class="stat-label">Most Common Ethnicity</div>
            <div class="stat-value-text"><%= request.getAttribute("mostCommonEthnicity") %></div>
        </div>

        <div class="stat-card card">
            <div class="stat-label">Most Common Race</div>
            <div class="stat-value-text"><%= request.getAttribute("mostCommonRace") %></div>
        </div>

    </div>

    <%-- Charts --%>
    <h3 class="section-heading section-heading--spaced">Charts</h3>
    <div class="charts-grid">

        <div class="chart-card card">
            <h3>Gender Split</h3>
            <canvas id="genderChart"></canvas>
        </div>

        <div class="chart-card card">
            <h3>Marital Status</h3>
            <canvas id="maritalChart"></canvas>
        </div>

        <div class="chart-card card">
            <h3>Living vs Deceased</h3>
            <canvas id="livingChart"></canvas>
        </div>

        <div class="chart-card card">
            <h3>Ethnicity (Top <%= request.getAttribute("ethnicityTopN") %>)</h3>
            <canvas id="ethnicityChart"></canvas>
        </div>

        <%-- ── Age Histogram — full width across 2 columns ── --%>
        <div class="chart-card card chart-card--wide">
            <h3>Age Distribution (Living Patients)</h3>
            <canvas id="ageHistogram"></canvas>
        </div>

    </div>
</div>

<%-- hidden content so it can be accessed by the script without an error as IDE was giving errors for methods like using %{} or JSP tags--%>
<div id="chartDataJson" style="display:none;"><%= request.getAttribute("chartDataJson") %></div>

<jsp:include page="/footer.jsp"/>

<script src="/javascript/statistics.js"></script>

</body>
</html>