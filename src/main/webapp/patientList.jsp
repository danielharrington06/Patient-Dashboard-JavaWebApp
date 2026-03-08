<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.List" %>
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

    <%-- Search bar --%>
    <div class="search-card" style="margin-bottom: 1.5rem; max-width: 100%;">
        <form method="GET" action="/runsearch">
            <div class="search-row">
                <input class="search-input" type="text" name="searchstring"
                    placeholder="Search patients by name, city, state..."
                    value="<%= request.getParameter("searchstring") != null ? request.getParameter("searchstring") : "" %>"/>
                <input class="btn" type="submit" value="Search"/>
            </div>
            <div class="filter-row">
                <%-- Gender filter --%>
                <div class="filter-group">
                    <label class="filter-label">Gender</label>
                    <div class="filter-options">
                        <label class="filter-chip">
                            <input type="radio" name="gender" value=""
                                <%= request.getParameter("gender") == null || request.getParameter("gender").isEmpty() ? "checked" : "" %>>
                            Any
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="gender" value="M"
                                <%= "M".equals(request.getParameter("gender")) ? "checked" : "" %>>
                            Male
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="gender" value="F"
                                <%= "F".equals(request.getParameter("gender")) ? "checked" : "" %>>
                            Female
                        </label>
                    </div>
                </div>

                <%-- Alive filter --%>
                <div class="filter-group">
                    <label class="filter-label">Status</label>
                    <div class="filter-options">
                        <label class="filter-chip">
                            <input type="radio" name="alive" value=""
                                <%= request.getParameter("alive") == null || request.getParameter("alive").isEmpty() ? "checked" : "" %>>
                            Any
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="alive" value="true"
                                <%= "true".equals(request.getParameter("alive")) ? "checked" : "" %>>
                            Alive
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="alive" value="false"
                                <%= "false".equals(request.getParameter("alive")) ? "checked" : "" %>>
                            Deceased
                        </label>
                    </div>
                </div>

                <%-- Marital filter --%>
                <div class="filter-group">
                    <label class="filter-label">Marital Status</label>
                    <div class="filter-options">
                        <label class="filter-chip">
                            <input type="radio" name="marital" value=""
                                <%= request.getParameter("marital") == null || request.getParameter("marital").isEmpty() ? "checked" : "" %>>
                            Any
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="marital" value="M"
                                <%= "M".equals(request.getParameter("marital")) ? "checked" : "" %>>
                            Married
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="marital" value="S"
                                <%= "S".equals(request.getParameter("marital")) ? "checked" : "" %>>
                            Single
                        </label>
                        <label class="filter-chip">
                            <input type="radio" name="marital" value="-"
                                <%= "-".equals(request.getParameter("marital")) ? "checked" : "" %>>
                            Unknown
                        </label>
                    </div>
                </div>
                <%-- Race filter --%>
                <%
                    LinkedHashMap<String, String> raceOptions =
                        (LinkedHashMap<String, String>) request.getAttribute("raceOptions");
                    String raceFilter = (String) request.getAttribute("raceFilter");
                %>
                <div class="filter-group">
                    <label class="filter-label">Race</label>
                    <div class="filter-options">
                        <label class="filter-chip">
                            <input type="radio" name="race" value=""
                                <%= (raceFilter == null || raceFilter.isEmpty()) ? "checked" : "" %>>
                            Any
                        </label>
                        <% if (raceOptions != null) {
                            for (Map.Entry<String, String> opt : raceOptions.entrySet()) { %>
                            <label class="filter-chip">
                                <input type="radio" name="race" value="<%= opt.getKey() %>"
                                    <%= opt.getKey().equals(raceFilter) ? "checked" : "" %>>
                                <%= opt.getValue() %>
                            </label>
                        <% } } %>
                    </div>
                </div>

                <%-- Ethnicity filter --%>
                <%
                    LinkedHashMap<String, String> ethnicityOptions =
                        (LinkedHashMap<String, String>) request.getAttribute("ethnicityOptions");
                    String ethnicityFilter = (String) request.getAttribute("ethnicityFilter");
                %>
                <div class="filter-group">
                    <label class="filter-label">Ethnicity</label>
                    <div class="filter-options">
                        <label class="filter-chip">
                            <input type="radio" name="ethnicity" value=""
                                <%= (ethnicityFilter == null || ethnicityFilter.isEmpty()) ? "checked" : "" %>>
                            Any
                        </label>
                        <% if (ethnicityOptions != null) {
                            for (Map.Entry<String, String> opt : ethnicityOptions.entrySet()) { %>
                            <label class="filter-chip">
                                <input type="radio" name="ethnicity" value="<%= opt.getKey() %>"
                                    <%= opt.getKey().equals(ethnicityFilter) ? "checked" : "" %>>
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

    <%-- Declare all variables up front --%>
    <%
        Map<String, List<String>> patients = (Map<String, List<String>>) request.getAttribute("patientData");
        List<String> columnDisplayNames = (List<String>) request.getAttribute("columnDisplayNames");
        String sortKey = (String) request.getAttribute("sortKey");
        String sortDir = (String) request.getAttribute("sortDir");
        String searchTerm = request.getParameter("searchstring");

        String[][] headers = {
            {"First Name", "firstname"},
            {"Last Name",  "lastname"},
            {"Date of Birth", "birthdate"},
            {"Date of Death", "deathdate"},
            {"Gender", null},
            {"Marital", null},
            {"Race", null},
            {"Ethnicity", null},
            {"City", "city"},
            {"State", null}
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
                %>
                            <%
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
                    String fromParam = (searchTerm != null && !searchTerm.isEmpty()) ? "&from=" + searchTerm : "";
                    String href = "patientRecord?id=" + id + fromParam;
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

        String baseUrl = (searchTerm != null && !searchTerm.isEmpty())
            ? "/runsearch?searchstring=" + searchTerm
            : "/patientList?";
        if (sortKey != null && !sortKey.isEmpty()) {
            baseUrl += (baseUrl.endsWith("?") ? "" : "&") + "sort=" + sortKey + "&dir=" + (sortDir != null ? sortDir : "asc");
        }
        baseUrl += (baseUrl.endsWith("?") ? "" : "&") + "page=";

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