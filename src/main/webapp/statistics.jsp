<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Patient Statistics</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
</head>
<body>
<jsp:include page="/header.jsp"/>

<div class="main">
    <h2 class="page-title">Statistics</h2>

    <%-- Insights --%>
    <h3 class="section-heading">Insights</h3>
    <div class="stats-grid">

        <div class="stat-card">
            <div class="stat-label">Total Patients</div>
            <div class="stat-value"><%= request.getAttribute("totalPatients") %></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Living Patients</div>
            <div class="stat-value"><%= request.getAttribute("livingCount") %></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Oldest Alive</div>
            <div class="stat-value"><%= request.getAttribute("oldestAliveAge") %><span class="stat-unit"> yrs</span></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Youngest Alive</div>
            <div class="stat-value"><%= request.getAttribute("youngestAliveAge") %><span class="stat-unit"> yrs</span></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Avg Age (Living)</div>
            <div class="stat-value"><%= request.getAttribute("averageAliveAge") %><span class="stat-unit"> yrs</span></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Avg Age at Death</div>
            <div class="stat-value"><%= request.getAttribute("averageAgeAtDeath") %><span class="stat-unit"> yrs</span></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Distinct Cities</div>
            <div class="stat-value"><%= request.getAttribute("distinctCityCount") %></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Most Common City</div>
            <div class="stat-value-text"><%= request.getAttribute("mostCommonCity") %></div>
            <div class="stat-sub"><%= request.getAttribute("mostCommonCityCount") %> patients</div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Most Common Ethnicity</div>
            <div class="stat-value-text"><%= request.getAttribute("mostCommonEthnicity") %></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Most Common Race</div>
            <div class="stat-value-text"><%= request.getAttribute("mostCommonRace") %></div>
        </div>

    </div>

    <%-- Charts --%>
    <h3 class="section-heading section-heading--spaced">Charts</h3>
    <div class="charts-grid">

        <div class="chart-card">
            <h3>Gender Split</h3>
            <canvas id="genderChart"></canvas>
        </div>

        <div class="chart-card">
            <h3>Living vs Deceased</h3>
            <canvas id="livingChart"></canvas>
        </div>

        <div class="chart-card">
            <h3>Marital Status</h3>
            <canvas id="maritalChart"></canvas>
        </div>

        <div class="chart-card">
            <h3>Ethnicity (Top <%= request.getAttribute("ethnicityTopN") %>)</h3>
            <canvas id="ethnicityChart"></canvas>
        </div>

        <%-- ── Age Histogram — full width across 2 columns ── --%>
        <div class="chart-card chart-card--wide">
            <h3>Age Distribution (Living Patients)</h3>
            <canvas id="ageHistogram"></canvas>
        </div>

    </div>
</div>

<div id="chartDataJson" style="display:none;"><%= request.getAttribute("chartDataJson") %></div>

<jsp:include page="/footer.jsp"/>

<script>
    var chartData = JSON.parse(document.getElementById('chartDataJson').textContent);

    var PALETTE = [
        '#3b82f6', '#f59e0b', '#10b981', '#ef4444',
        '#8b5cf6', '#06b6d4', '#f97316', '#ec4899',
        '#84cc16', '#6366f1'
    ];

    function palette(n) {
        return Array.from({length: n}, function(_, i) { return PALETTE[i % PALETTE.length]; });
    }

    function makePie(id, labels, data) {
        new Chart(document.getElementById(id), {
            type: 'pie',
            data: {
                labels: labels,
                datasets: [{ data: data, backgroundColor: palette(data.length), borderWidth: 2 }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'bottom', labels: { padding: 14, font: { size: 12 } } },
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                var total = ctx.dataset.data.reduce(function(a, b) { return a + b; }, 0);
                                var pct = ((ctx.parsed / total) * 100).toFixed(1);
                                return ' ' + ctx.label + ': ' + ctx.parsed.toLocaleString() + ' (' + pct + '%)';
                            }
                        }
                    }
                }
            }
        });
    }

    function makeBar(id, labels, data) {
        new Chart(document.getElementById(id), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Patients',
                    data: data,
                    backgroundColor: palette(data.length),
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                return ' ' + ctx.parsed.y.toLocaleString() + ' patients';
                            }
                        }
                    }
                },
                scales: {
                    x: { grid: { display: false } },
                    y: { beginAtZero: true, ticks: { precision: 0 } }
                }
            }
        });
    }

    makePie('genderChart',    chartData.genderLabels,    chartData.genderValues);
    makePie('livingChart',    chartData.livingLabels,    chartData.livingValues);
    makePie('maritalChart',   chartData.maritalLabels,   chartData.maritalValues);
    makePie('ethnicityChart', chartData.ethnicityLabels, chartData.ethnicityValues);
    makeBar('ageHistogram',   chartData.ageHistogramLabels, chartData.ageHistogramValues);
</script>

</body>
</html>