<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Patient Data App - Patients</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">

    <h2>Patients</h2>

    <%-- Search bar and filters --%>
    <div class="search-card" style="margin-bottom: 1.5rem; max-width: 100%;">
        <form method="GET" action="/runsearch">
            <div class="search-row">
                <input class="search-input" type="text" name="searchstring"
                    placeholder="Search patients by name, city, state..."
                    value="<%= request.getParameter("searchstring") != null ? request.getParameter("searchstring") : "" %>"/>
                <input class="btn" type="submit" value="Search"/>
                <a href="/patientList" class="btn btn-secondary">Reset All</a>
            </div>

            <%-- Retrieve filter data and reset URLs from request attributes --%>
            <%
                Map<String, String> resetUrls = (Map<String, String>) request.getAttribute("resetUrls");
                String paginationBaseUrl = (String) request.getAttribute("paginationBaseUrl");
                String gf = (String) request.getAttribute("genderFilter");
                String af = (String) request.getAttribute("aliveFilter");
                String mf = (String) request.getAttribute("maritalFilter");
                List<String> raceFilterList = (List<String>) request.getAttribute("raceFilterList");
                List<String> ethnicityFilterList = (List<String>) request.getAttribute("ethnicityFilterList");
                LinkedHashMap<String, String> raceOptions = (LinkedHashMap<String, String>) request.getAttribute("raceOptions");
                LinkedHashMap<String, String> ethnicityOptions = (LinkedHashMap<String, String>) request.getAttribute("ethnicityOptions");
                if (raceFilterList == null) raceFilterList = new ArrayList<>();
                if (ethnicityFilterList == null) ethnicityFilterList = new ArrayList<>();
            %>

            <div class="filter-row">

                <%-- Gender filter --%>
                <div class="filter-group">
                    <div class="filter-label-row">
                        <label class="filter-label">Gender</label>
                        <a href="<%= resetUrls.get("gender") %>" class="filter-clear-link">Reset</a>
                    </div>
                    <div class="filter-options">
                        <label class="filter-chip">
                            <input type="radio" name="gender" value=""
                                onchange="this.form.submit()"
                                <%= gf == null || gf.isEmpty() ? "checked" : "" %>>
                            Any
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="gender" value="M"
                                onchange="this.form.submit()"
                                <%= "M".equals(gf) ? "checked" : "" %>>
                            Male
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="gender" value="F"
                                onchange="this.form.submit()"
                                <%= "F".equals(gf) ? "checked" : "" %>>
                            Female
                        </label>
                    </div>
                </div>

                <%-- Status filter --%>
                <div class="filter-group">
                    <div class="filter-label-row">
                        <label class="filter-label">Status</label>
                        <a href="<%= resetUrls.get("alive") %>" class="filter-clear-link">Reset</a>
                    </div>
                    <div class="filter-options">
                        <label class="filter-chip">
                            <input type="radio" name="alive" value=""
                                onchange="this.form.submit()"
                                <%= af == null || af.isEmpty() ? "checked" : "" %>>
                            Any
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="alive" value="true"
                                onchange="this.form.submit()"
                                <%= "true".equals(af) ? "checked" : "" %>>
                            Alive
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="alive" value="false"
                                onchange="this.form.submit()"
                                <%= "false".equals(af) ? "checked" : "" %>>
                            Deceased
                        </label>
                    </div>
                </div>

                <%-- Marital filter --%>
                <div class="filter-group">
                    <div class="filter-label-row">
                        <label class="filter-label">Marital Status</label>
                        <a href="<%= resetUrls.get("marital") %>" class="filter-clear-link">Reset</a>
                    </div>
                    <div class="filter-options">
                        <label class="filter-chip">
                            <input type="radio" name="marital" value=""
                                onchange="this.form.submit()"
                                <%= mf == null || mf.isEmpty() ? "checked" : "" %>>
                            Any
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="marital" value="M"
                                onchange="this.form.submit()"
                                <%= "M".equals(mf) ? "checked" : "" %>>
                            Married
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="marital" value="S"
                                onchange="this.form.submit()"
                                <%= "S".equals(mf) ? "checked" : "" %>>
                            Single
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="marital" value="-"
                                onchange="this.form.submit()"
                                <%= "-".equals(mf) ? "checked" : "" %>>
                            Unknown
                        </label>
                    </div>
                </div>

                <%-- Race filter --%>
                <div class="filter-group">
                    <div class="filter-label-row">
                        <label class="filter-label">Race</label>
                        <a href="<%= resetUrls.get("race") %>" class="filter-clear-link">Reset</a>
                    </div>
                    <div class="filter-options">
                        <% if (raceOptions != null) {
                            for (Map.Entry<String, String> opt : raceOptions.entrySet()) { %>
                            <label class="filter-chip">
                                <input type="checkbox" name="race" value="<%= opt.getKey() %>"
                                    onchange="this.form.submit()"
                                    <%= raceFilterList.contains(opt.getKey()) ? "checked" : "" %>>
                                <%= opt.getValue() %>
                            </label>
                        <% } } %>
                    </div>
                </div>

                <%-- Ethnicity filter --%>
                <div class="filter-group">
                    <div class="filter-label-row">
                        <label class="filter-label">Ethnicity</label>
                        <a href="<%= resetUrls.get("ethnicity") %>" class="filter-clear-link">Reset</a>
                    </div>
                    <div class="filter-options">
                        <% if (ethnicityOptions != null) {
                            for (Map.Entry<String, String> opt : ethnicityOptions.entrySet()) { %>
                            <label class="filter-chip">
                                <input type="checkbox" name="ethnicity" value="<%= opt.getKey() %>"
                                    onchange="this.form.submit()"
                                    <%= ethnicityFilterList.contains(opt.getKey()) ? "checked" : "" %>>
                                <%= opt.getValue() %>
                            </label>
                        <% } } %>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <%-- Error message --%>
    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
        <p class="error"><%= errorMessage %></p>
    <%
        }
    %>

    <%-- Declare variables for table and pagination --%>
    <%
        Map<String, List<String>> patients = (Map<String, List<String>>) request.getAttribute("patientData");
        List<String> columnDisplayNames = (List<String>) request.getAttribute("columnDisplayNames");
        String sortKey = (String) request.getAttribute("sortKey");
        String sortDir = (String) request.getAttribute("sortDir");
        String searchTerm = request.getParameter("searchstring");
        String genderFilter = (String) request.getAttribute("genderFilter");
        String aliveFilter = (String) request.getAttribute("aliveFilter");
        String maritalFilter = (String) request.getAttribute("maritalFilter");

        String[][] headers = {
            {"First Name", "firstname"},
            {"Last Name",  "lastname"},
            {"Date of Birth", "birthdate"},
            {"Date of Death", "deathdate"},
            {"Gender", null},
            {"Marital", null},
            {"Race", null},
            {"Ethnicity", null},
            {"City", "city"}
        };

        if (patients != null && !patients.isEmpty()) {
    %>

    <%-- Sort status --%>
    <p class="sort-status">
        <% if (sortKey != null && !sortKey.isEmpty()) {
            String dirLabel = "desc".equals(sortDir) ? "descending" : "ascending";
            String activeLabel = sortKey;
            for (String[] h : headers) {
                if (sortKey.equals(h[1])) { activeLabel = h[0]; break; }
            }
        %>
            Sorted by <strong><%= activeLabel %></strong> (<%= dirLabel %>) —
            <a href="<%= (searchTerm != null && !searchTerm.isEmpty()) ? "/runsearch?searchstring=" + searchTerm : "/patientList" %>">clear sort</a>
        <% } else { %>
            Click a column header to sort
        <% } %>
    </p>

    <%-- Patient table --%>
    <div class="table-wrapper">
        <table class="patient-table">
            <thead>
                <tr>
                <%
                    for (String[] header : headers) {
                        String label = header[0];
                        String key = header[1];
                        if (key != null) {
                            boolean isActive = key.equals(sortKey);
                            String nextDir = (isActive && "asc".equals(sortDir)) ? "desc" : "asc";
                            String arrow = isActive ? ("asc".equals(sortDir) ? " ↑" : " ↓") : "";
                            String base = (searchTerm != null && !searchTerm.isEmpty())
                                ? "/runsearch?searchstring=" + searchTerm
                                : "/patientList?";
                            String sortUrl = base + (base.endsWith("?") ? "" : "&") + "sort=" + key + "&dir=" + nextDir;
                            String tooltipDir = (isActive && "asc".equals(sortDir)) ? "descending" : "ascending";
                            String tooltip = isActive
                                ? "Sort by " + label + " (" + tooltipDir + ")"
                                : "Sort by " + label;
                %>
                            <th class="sortable <%= isActive ? "sort-active" : "" %>"
                                onclick="window.location='<%= sortUrl %>'"
                                title="<%= tooltip %>">
                                <%= label %><%= arrow %>
                            </th>
                <%
                        } else {
                %>
                            <th><%= label %></th>
                <%
                        }
                    }
                %>
                </tr>
            </thead>
            <tbody>
            <%
                for (Map.Entry<String, List<String>> entry : patients.entrySet()) {
                    String id = entry.getKey();
                    List<String> data = entry.getValue();
                    String href = "patientRecord?id=" + id;
            %>
                <tr data-href="<%= href %>">
                    <% for (int i = 0; i < data.size(); i++) { %>
                        <td><%= data.get(i) %></td>
                    <% } %>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>

    <%-- Pagination --%>
    <%
        Integer currentPage = (Integer) request.getAttribute("currentPage");
        Integer totalPages = (Integer) request.getAttribute("totalPages");
        Integer totalPatients = (Integer) request.getAttribute("totalPatients");
        String baseUrl = paginationBaseUrl;

        if (currentPage != null && totalPages != null) {
    %>
    <div class="pagination">
        <span class="result-count">
            <%= totalPatients %> patient(s) found — page <%= currentPage %> of <%= totalPages %>
        </span>
        <div class="page-controls">
            <% if (currentPage > 1) { %>
                <a href="<%= baseUrl %><%= currentPage - 1 %>" class="btn btn-secondary btn-sm">← Previous</a>
            <% } %>

            <% for (int p = 1; p <= totalPages; p++) { %>
                <a href="<%= baseUrl %><%= p %>"
                   class="btn btn-sm <%= p == currentPage ? "" : "btn-secondary" %>">
                    <%= p %>
                </a>
            <% } %>

            <% if (currentPage < totalPages) { %>
                <a href="<%= baseUrl %><%= currentPage + 1 %>" class="btn btn-secondary btn-sm">Next →</a>
            <% } %>
        </div>
    </div>
    <%
        }
    %>

    <%
        } else if (errorMessage == null) {
    %>
        <p class="text-muted">No patients found.</p>
    <%
        }
    %>

</div>
<jsp:include page="/footer.jsp"/>
<script>
    document.querySelectorAll('tr[data-href]').forEach(row => {
        row.addEventListener('click', () => {
            window.location = row.dataset.href;
        });
    });
</script>
</body>
</html>
