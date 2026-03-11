<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.net.URLDecoder" %>
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
        String fromEncoded = request.getParameter("from") != null
            ? request.getParameter("from")
            : (String) request.getAttribute("from");
        if (fromEncoded == null) fromEncoded = "";
        String fromParam = fromEncoded;

        String cancelHref = "/patientRecord?id=" + id + (fromParam.isEmpty() ? "" : "&from=" + fromParam);
    %>

    <div class="record-actions">
        <a href="<%= cancelHref %>" class="btn btn-secondary">← Cancel</a>
        <div class="record-actions-right">
            <button type="submit" form="edit-form" class="btn">Save Changes</button>
        </div>
    </div>

    <% String errorMessage = (String) request.getAttribute("errorMessage");
       if (errorMessage != null) { %>
        <p class="error"><%= errorMessage %></p>
    <% } %>

    <%
        LinkedHashMap<String, String> rawRecord =
            (LinkedHashMap<String, String>) request.getAttribute("rawRecord");
    %>

    <form id="edit-form" method="POST" action="/editPatient" novalidate>
        <input type="hidden" name="id"   value="<%= id %>"/>
        <input type="hidden" name="from" value="<%= fromParam %>"/>

        <dl>
        <% for (Map.Entry<String, String> entry : rawRecord.entrySet()) {
               String col = entry.getKey();
               String val = entry.getValue() != null ? entry.getValue() : "";
               // Patient ID is not editable
               if ("ID".equals(col)) { %>
                <dt>Patient ID</dt>
                <dd><%= val %></dd>
        <%     continue;
           }
           // formatted display name
           String label = col; // will be replaced by model's formatColumnName output
        %>
            <dt><label for="field-<%= col %>"><%= col %></label></dt>
            <dd>
            <% if ("BIRTHDATE".equals(col) || "DEATHDATE".equals(col)) { %>
                <input class="record-input" type="date" id="field-<%= col %>" name="<%= col %>"
                       value="<%= val %>"
                       max="<%= java.time.LocalDate.now().toString() %>"
                       min="1900-01-01"
                       <%= "BIRTHDATE".equals(col) ? "required" : "" %>>
                <span class="field-error" id="err-<%= col %>"></span>
            <% } else if ("GENDER".equals(col)) { %>
                <select class="record-input" id="field-<%= col %>" name="<%= col %>" required>
                    <option value="">— Select —</option>
                    <option value="M" <%= "M".equals(val) ? "selected" : "" %>>Male</option>
                    <option value="F" <%= "F".equals(val) ? "selected" : "" %>>Female</option>
                </select>
                <span class="field-error" id="err-GENDER"></span>
            <% } else if ("MARITAL".equals(col)) { %>
                <select class="record-input" id="field-<%= col %>" name="<%= col %>">
                    <option value="">— Select —</option>
                    <option value="M" <%= "M".equals(val) ? "selected" : "" %>>Married</option>
                    <option value="S" <%= "S".equals(val) ? "selected" : "" %>>Single</option>
                </select>
            <% } else if ("PREFIX".equals(col)) { %>
                <select class="record-input" id="field-<%= col %>" name="<%= col %>">
                    <option value="">— None —</option>
                    <option value="Mr."  <%= "Mr.".equals(val)  ? "selected" : "" %>>Mr.</option>
                    <option value="Mrs." <%= "Mrs.".equals(val) ? "selected" : "" %>>Mrs.</option>
                    <option value="Ms."  <%= "Ms.".equals(val)  ? "selected" : "" %>>Ms.</option>
                    <option value="Dr."  <%= "Dr.".equals(val)  ? "selected" : "" %>>Dr.</option>
                    <option value="Prof."<%= "Prof.".equals(val)? "selected" : "" %>>Prof.</option>
                </select>
            <% } else { %>
                <input class="record-input" type="text" id="field-<%= col %>" name="<%= col %>"
                       value="<%= val %>"
                       <%= "FIRST".equals(col) || "LAST".equals(col) || "SSN".equals(col) ? "required" : "" %>>
                <% if ("SSN".equals(col)) { %><span class="field-hint">Format: 999-12-3456</span><% } %>
                <% if ("ZIP".equals(col)) { %><span class="field-hint">5-digit ZIP, optionally +4</span><% } %>
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
    const today = new Date().toISOString().split('T')[0];

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

        const ssn = document.getElementById('field-SSN');
        const ssnErr = document.getElementById('err-SSN');
        if (ssn.value && !/^\d{3}-\d{2}-\d{4}$/.test(ssn.value)) {
            ssnErr.textContent = 'SSN must be in format 999-12-3456.';
            ssn.classList.add('input-error');
            valid = false;
        }

        const zip = document.getElementById('field-ZIP');
        const zipErr = document.getElementById('err-ZIP');
        if (zip && zip.value && !/^\d{5}(-\d{4})?$/.test(zip.value)) {
            zipErr.textContent = 'ZIP must be 5 digits, optionally +4.';
            zip.classList.add('input-error');
            valid = false;
        }

        if (!validateDates()) valid = false;
        if (!valid) e.preventDefault();
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