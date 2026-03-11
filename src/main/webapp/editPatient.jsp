<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Map" %>
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

        java.util.Set<String> requiredFields = new java.util.HashSet<>(
            java.util.Arrays.asList("FIRST", "LAST", "BIRTHDATE", "GENDER", "SSN")
        );
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
        <% for (Map.Entry<String, String> entry : rawRecord.entrySet()) {
               String col = entry.getKey();
               String val = entry.getValue() != null ? entry.getValue() : "";
               boolean isRequired = requiredFields.contains(col);

               if ("ID".equals(col)) { %>
                <dt><%= columnLabels != null ? columnLabels.getOrDefault("ID", "Patient ID") : "Patient ID" %></dt>
                <dd><%= val %></dd>
        <%     continue;
           } %>
            <dt>
                <label for="field-<%= col %>">
                    <%= columnLabels != null && columnLabels.containsKey(col) ? columnLabels.get(col) : col %>
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

<script>
    const form = document.getElementById('edit-form');

    document.getElementById('field-SSN').addEventListener('input', function () {
        let v = this.value.replace(/\D/g, '');
        if (v.length > 5)      v = v.slice(0,3) + '-' + v.slice(3,5) + '-' + v.slice(5,9);
        else if (v.length > 3) v = v.slice(0,3) + '-' + v.slice(3);
        this.value = v;
    });

    function validateDates() {
        const birth = document.getElementById('field-BIRTHDATE').value;
        const death = document.getElementById('field-DEATHDATE').value;
        const errDeath = document.getElementById('err-DEATHDATE');
        if (birth && death && death < birth) {
            errDeath.textContent = 'Date of death cannot be before date of birth.';
            document.getElementById('field-DEATHDATE').setCustomValidity('invalid');
            return false;
        }
        errDeath.textContent = '';
        document.getElementById('field-DEATHDATE').setCustomValidity('');
        return true;
    }

    document.getElementById('field-BIRTHDATE').addEventListener('change', validateDates);
    document.getElementById('field-DEATHDATE').addEventListener('change', validateDates);

    form.addEventListener('submit', function (e) {
        let valid = true;

        const existingBanner = document.getElementById('validation-banner');
        if (existingBanner) existingBanner.remove();

        [
            { id: 'field-FIRST',     errId: 'err-FIRST',     msg: 'First name is required.' },
            { id: 'field-LAST',      errId: 'err-LAST',      msg: 'Last name is required.' },
            { id: 'field-BIRTHDATE', errId: 'err-BIRTHDATE', msg: 'Date of birth is required.' },
            { id: 'field-GENDER',    errId: 'err-GENDER',    msg: 'Gender is required.' },
            { id: 'field-SSN',       errId: 'err-SSN',       msg: 'SSN is required.' }
        ].forEach(({ id, errId, msg }) => {
            const el  = document.getElementById(id);
            const err = document.getElementById(errId);
            if (!el.value.trim()) {
                if (err) err.textContent = msg;
                el.classList.add('input-error');
                valid = false;
            } else {
                if (err) err.textContent = '';
                el.classList.remove('input-error');
            }
        });

        const today = new Date().toISOString().split('T')[0];

        const birthEl = document.getElementById('field-BIRTHDATE');
        const birthErr = document.getElementById('err-BIRTHDATE');
        if (birthEl.value) {
            if (birthEl.value > today) {
                birthErr.textContent = 'Date of birth cannot be in the future.';
                birthEl.classList.add('input-error');
                valid = false;
            } else if (birthEl.value < '1900-01-01') {
                birthErr.textContent = 'Date of birth must be on or after 01/01/1900.';
                birthEl.classList.add('input-error');
                valid = false;
            }
        }

        const deathEl = document.getElementById('field-DEATHDATE');
        const deathErr = document.getElementById('err-DEATHDATE');
        if (deathEl.value && deathEl.value > today) {
            deathErr.textContent = 'Date of death cannot be in the future.';
            deathEl.classList.add('input-error');
            valid = false;
        }

        const ssn = document.getElementById('field-SSN');
        const ssnErr = document.getElementById('err-SSN');
        if (ssn.value && !/^\d{3}-\d{2}-\d{4}$/.test(ssn.value)) {
            ssnErr.textContent = 'Format: 999-12-3456';
            ssn.classList.add('input-error');
            valid = false;
        }

        const zip = document.getElementById('field-ZIP');
        const zipErr = document.getElementById('err-ZIP');
        if (zip && zip.value && !/^\d{5}$/.test(zip.value)) {
            zipErr.textContent = 'ZIP must be exactly 5 digits.';
            zip.classList.add('input-error');
            valid = false;
        }

        if (!validateDates()) valid = false;

        if (!valid) {
            e.preventDefault();
            const banner = document.createElement('div');
            banner.id = 'validation-banner';
            banner.className = 'error';
            banner.style.marginBottom = 'var(--spacing-md)';
            banner.textContent = 'One or more fields have errors — please review and correct them before saving.';
            form.insertBefore(banner, form.firstChild);
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    });

    form.querySelectorAll('input, select').forEach(el => {
        el.addEventListener('input', () => {
            el.classList.remove('input-error');
            const errId = el.id.replace('field-', 'err-');
            const err = document.getElementById(errId);
            if (err) err.textContent = '';
        });
    });
</script>
</body>
</html>