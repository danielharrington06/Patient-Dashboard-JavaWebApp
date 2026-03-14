<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Arrays" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Patient Data App - Edit Patient</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
    <h2>Edit Patient</h2>

    <%
        String id = request.getParameter("id") != null
            ? request.getParameter("id")
            : (String) request.getAttribute("patientId");

        Set<String> requiredFields = new HashSet<>(Arrays.asList("FIRST", "LAST", "BIRTHDATE", "GENDER", "SSN"));
    %>

    <div class="record-actions">
        <a href="/patientRecord?id=<%= id %>" class="btn btn-secondary">← Cancel</a>
        <div class="record-actions-right">
            <button type="submit" form="edit-form" class="btn">Save Changes</button>
        </div>
    </div>

    <% String errorMessage = (String) request.getAttribute("errorMessage");
       if (errorMessage != null) { %>
        <div class="error" style="margin-bottom: var(--spacing-md);">
            <strong>Please fix the following errors:</strong> <%= errorMessage %>
        </div>
    <% } %>

    <p class="text-muted" style="margin-bottom: var(--spacing-md);">
        Fields marked <span style="color: var(--colour-error)">*</span> are required.
    </p>

    <%
        LinkedHashMap<String, String> rawRecord = (LinkedHashMap<String, String>) request.getAttribute("rawRecord");
        LinkedHashMap<String, String> columnLabels = (LinkedHashMap<String, String>) request.getAttribute("columnLabels");
    %>

    <form id="edit-form" method="POST" action="/editPatient" novalidate>
        <input type="hidden" name="id" value="<%= id %>"/>

        <dl>
        <% for (Map.Entry<String, String> entry : columnLabels.entrySet()) {
            String col = entry.getKey();
            String label = entry.getValue();
            String val = rawRecord != null && rawRecord.get(col) != null ? rawRecord.get(col) : "";
            boolean isRequired = requiredFields.contains(col);

            if ("ID".equals(col)) { %>
                <dt>Patient ID</dt>
                <dd><%= val %></dd>
        <%     continue;
        } %>
            <dt>
                <label for="field-<%= col %>">
                    <%= label %>
                    <% if (isRequired) { %><span style="color: var(--colour-error)">*</span><% } %>
                </label>
            </dt>
            <dd>
            <% if ("BIRTHDATE".equals(col) || "DEATHDATE".equals(col)) { %>
                <input class="record-input" type="date" id="field-<%= col %>" name="<%= col %>"
                    value="<%= val %>"
                    <%= isRequired ? "required" : "" %>>
                <span class="field-error" id="err-<%= col %>"></span>
            <% } else if ("GENDER".equals(col)) { %>
                <select class="record-input" id="field-<%= col %>" name="<%= col %>" required>
                    <option value="">— Select —</option>
                    <option value="M" <%= "M".equals(val) ? "selected" : "" %>>Male</option>
                    <option value="F" <%= "F".equals(val) ? "selected" : "" %>>Female</option>
                </select>
                <span class="field-error" id="err-<%= col %>"></span>
            <% } else if ("MARITAL".equals(col)) { %>
                <select class="record-input" id="field-<%= col %>" name="<%= col %>">
                    <option value="">— Select —</option>
                    <option value="M" <%= "M".equals(val) ? "selected" : "" %>>Married</option>
                    <option value="S" <%= "S".equals(val) ? "selected" : "" %>>Single</option>
                </select>
                <span class="field-error" id="err-<%= col %>"></span>
            <% } else if ("PREFIX".equals(col)) { %>
                <select class="record-input" id="field-<%= col %>" name="<%= col %>">
                    <option value="">— None —</option>
                    <option value="Mr."  <%= "Mr.".equals(val)  ? "selected" : "" %>>Mr.</option>
                    <option value="Mrs." <%= "Mrs.".equals(val) ? "selected" : "" %>>Mrs.</option>
                    <option value="Ms."  <%= "Ms.".equals(val)  ? "selected" : "" %>>Ms.</option>
                    <option value="Dr."  <%= "Dr.".equals(val)  ? "selected" : "" %>>Dr.</option>
                    <option value="Prof."<%= "Prof.".equals(val)? "selected" : "" %>>Prof.</option>
                </select>
                <span class="field-error" id="err-<%= col %>"></span>
            <% } else { %>
                <input class="record-input" type="text" id="field-<%= col %>" name="<%= col %>"
                    value="<%= val %>"
                    <%= isRequired ? "required" : "" %>>
                <span class="field-error" id="err-<%= col %>"></span>
            <% } %>
            </dd>
        <% } %>
        </dl>
    </form>
</div>
<jsp:include page="/footer.jsp"/>

<script src="/validateForm.js"></script>
<script>setupFormValidation('edit-form');</script>
</body>
</html>