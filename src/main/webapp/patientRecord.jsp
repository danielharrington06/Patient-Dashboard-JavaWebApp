<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Patient Data App</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
    <h2>Patient Record</h2>
    <p class="visually-hidden">Patient medical record details. This is all in english so should not be recognised as French.</p>
    <%
        String id = request.getParameter("id");
        String backHref = (String) session.getAttribute("lastListUrl");
        if (backHref == null) backHref = "/patientList";
    %>

    <div class="record-actions">
        <a href="<%= backHref %>" class="btn btn-secondary">← Back</a>
        <div class="record-actions-right">
            <a href="/editPatient?id=<%= id %>" class="btn btn-secondary">Edit Patient</a>
            <button type="button" class="btn btn-danger" onclick="document.getElementById('delete-modal').style.display='flex'">
                Delete Patient
            </button>

            <div id="delete-modal" class="modal-overlay">
                <div class="modal">
                    <h3 class="modal-title">Delete Patient</h3>
                    <p class="modal-body">
                        Are you sure you want to delete this patient? This action cannot be undone.
                    </p>
                    <div class="modal-actions">
                        <button type="button" class="btn btn-secondary"
                                onclick="document.getElementById('delete-modal').style.display='none'">
                            Cancel
                        </button>
                        <form method="POST" action="/deletePatient" style="display:inline">
                            <input type="hidden" name="id" value="<%= id %>"/>
                            <button type="submit" class="btn btn-danger">Yes, Delete</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
        <p class="error"><%= errorMessage %></p>
    <%
        }
    %>
    <%
        Map<String, String> patientRecord = (Map<String, String>) request.getAttribute("patientRecord");
        Map<String, String> columnLabels = (Map<String, String>) request.getAttribute("columnLabels");
    %>
    <dl>
        <% for (Map.Entry<String, String> entry : columnLabels.entrySet()) { %>
            <dt><%= entry.getValue() %></dt>
            <% String val = patientRecord.get(entry.getKey());%>
            <dd><%= val != null && !val.isEmpty() ? val : "—" %></dd>
        <% } %>
    </dl>
</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>