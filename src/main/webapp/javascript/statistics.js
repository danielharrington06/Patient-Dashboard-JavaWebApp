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