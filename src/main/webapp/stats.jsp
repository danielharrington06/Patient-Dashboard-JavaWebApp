<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Statistics</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
</head>
<body>
<jsp:include page="/header.jsp"/>

<div class="main">
    <h2 style="margin-bottom: 1.5rem;">Statistics</h2>

    <%-- ── Key Numbers ─────────────────────────────────────── --%>
    <h3 class="section-heading">Key Numbers</h3>
    <div class="stats-grid" style="margin-bottom: 2.5rem;">

        <div class="stat-card">
            <div class="stat-label">Total Patients</div>
            <div class="stat-value"><%= request.getAttribute("totalPatients") %></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Oldest Alive</div>
            <div class="stat-value"><%= request.getAttribute("oldestAliveAge") %><span style="font-size:1rem;font-weight:400;"> yrs</span></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Youngest Alive</div>
            <div class="stat-value"><%= request.getAttribute("youngestAliveAge") %><span style="font-size:1rem;font-weight:400;"> yrs</span></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Avg Age (Living)</div>
            <div class="stat-value"><%= request.getAttribute("averageAliveAge") %><span style="font-size:1rem;font-weight:400;"> yrs</span></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Avg Age at Death</div>
            <div class="stat-value"><%= request.getAttribute("averageAgeAtDeath") %><span style="font-size:1rem;font-weight:400;"> yrs</span></div>
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

    </div>

    <%-- ── Charts ─────────────────────────────────────────── --%>
    <h3 class="section-heading">Charts</h3>
    <div class="charts-grid">

        <div class="chart-card">
            <h3>Gender Split</h3>
            <canvas id="genderChart"></canvas>
        </div>

        <div class="chart-card">
            <h3>Marital Status</h3>
            <canvas id="maritalChart"></canvas>
        </div>

        <div class="chart-card">
            <h3>Ethnicity (Top <%= request.getAttribute("ethnicityTopN") %>)</h3>
            <canvas id="ethnicityChart"></canvas>
        </div>

        <div class="chart-card">
            <h3>Race</h3>
            <canvas id="raceChart"></canvas>
        </div>

    </div>
</div>

<%-- Data injected as JS variables directly — avoids HTML attribute quote-escaping issues --%>
<script>
    var chartData = {
        genderLabels:    <%= request.getAttribute("genderLabels") %>,
        genderValues:    <%= request.getAttribute("genderValues") %>,
        maritalLabels:   <%= request.getAttribute("maritalLabels") %>,
        maritalValues:   <%= request.getAttribute("maritalValues") %>,
        ethnicityLabels: <%= request.getAttribute("ethnicityLabels") %>,
        ethnicityValues: <%= request.getAttribute("ethnicityValues") %>,
        raceLabels:      <%= request.getAttribute("raceLabels") %>,
        raceValues:      <%= request.getAttribute("raceValues") %>
    };
</script>

<jsp:include page="/footer.jsp"/>

<script>
    const PALETTE = [
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

    makePie('genderChart',    chartData.genderLabels,    chartData.genderValues);
    makePie('maritalChart',   chartData.maritalLabels,   chartData.maritalValues);
    makePie('ethnicityChart', chartData.ethnicityLabels, chartData.ethnicityValues);
    makePie('raceChart',      chartData.raceLabels,      chartData.raceValues);
</script>

</body>
</html>